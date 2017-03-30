package org.mastodon.undo;

import java.util.ArrayList;

import org.mastodon.graph.Vertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

import gnu.trove.map.TIntObjectArrayMap;

// TODO: truncating earliest UndoableEdits in the list to avoid growing without bounds
/**
 * Data structure that manages a list of undoable edits.
 * <p>
 * As itself, this class only deals with general undoable edits, that need to
 * implement {@link UndoableEdit}. Derived classes may offer more specific
 * undoable edits.
 */
public class UndoableEditList extends Pool< UndoableEditRef, ByteMappedElement >
{
	protected final UndoDataStack dataStack;

	protected final ArrayList< UndoableEdit > nonRefEdits;

	/**
	 * Index in pool where the next {@link UndoableEdit} is to be
	 * recorded. (This is not simply the end of the list because of Redo ...)
	 */
	protected int nextEditIndex;

	public UndoableEditList( final int initialCapacity )
	{
		this( initialCapacity, new Factory() );
	}

	private UndoableEditList( final int initialCapacity, final Factory f )
	{
		super( initialCapacity, f );
		f.pool = this;

		dataStack = new UndoDataStack( 1024 * 1024 * 32 );
		nonRefEdits = new ArrayList<>();
	}

	public void setUndoPoint()
	{
		final UndoableEditRef ref = createRef();
		if ( nextEditIndex > 0 )
			get( nextEditIndex - 1, ref ).setUndoPoint( true );
		releaseRef( ref );
	}

	public void undo()
	{
		final UndoableEditRef ref = createRef();
		boolean first = true;
		for ( int i = nextEditIndex - 1; i >= 0; --i )
		{
			final UndoableEdit edit = get( i, ref );
			if ( edit.isUndoPoint() && !first )
				break;
			edit.undo();
			--nextEditIndex;
			first = false;
		}
		releaseRef( ref );
	}

	public void redo()
	{
		final UndoableEditRef ref = createRef();
		for ( int i = nextEditIndex; i < size(); ++i )
		{
			final UndoableEdit edit = get( i, ref );
			edit.redo();
			++nextEditIndex;
			if ( edit.isUndoPoint() )
				break;
		}
		releaseRef( ref );
	}

	protected UndoableEditRef get( final int index, final UndoableEditRef ref )
	{
		super.getObject( index, ref );
		return ref;
	}

	@Override
	public UndoableEditRef create( final UndoableEditRef ref )
	{
		if ( nextEditIndex < size() )
			clearFromIndex( nextEditIndex, ref );
		super.create( ref );
		++nextEditIndex;
		return ref;
	}

	private void clearFromIndex( final int fromIndex, final UndoableEditRef ref )
	{
		for ( int i = super.size() - 1; i >= fromIndex; --i )
		{
			getObject( i, ref );
			ref.clear();
			super.delete( ref );
		}
	}

	private static class Factory implements PoolObject.Factory< UndoableEditRef, ByteMappedElement >
	{
		private UndoableEditList pool;

		@Override
		public int getSizeInBytes()
		{
			return UndoableEditRef.SIZE_IN_BYTES;
		}

		@Override
		public UndoableEditRef createEmptyRef()
		{
			return new UndoableEditRef(	pool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@SuppressWarnings( { "unchecked", "rawtypes" } )
		@Override
		public Class< UndoableEditRef > getRefClass()
		{
			return UndoableEditRef.class;
		}
	};

	/**
	 * Represents a specific sub-type of {@link UndoableEdit},
	 * identified by a unique index.
	 *
	 * @param <T>
	 *            the {@link UndoableEdit} type.
	 */
	public interface UndoableEditType< T extends UndoableEdit >
	{
		/**
		 * Get the unique index associated to {@code T}.
		 *
		 * @return the unique index associated to T.
		 */
		public int typeIndex();

		/**
		 * Create a {@link UndoableEdit} of type {@code T}.
		 *
		 * @param ref
		 *            the {@link UndoableEditRef} that will use the created
		 *            {@code T}.
		 * @return a new {@code T}.
		 */
		public T createInstance( final UndoableEditRef ref );

		public boolean isInstance( final UndoableEditRef ref );
	}

	/**
	 * Abstract base class for {@link UndoableEdit}s that has an
	 * {@link UndoableEditRef} to which it forwards.
	 * <p>
	 * This acts as a facet of a polymorphic {@link UndoableEditRef},
	 * representing one particular derived type. Each {@link UndoableEditRef}
	 * caches a collection of {@link AbstractUndoableEdit} for the specific
	 * types it has represented.
	 */
	protected abstract class AbstractUndoableEdit implements UndoableEdit
	{
		protected final UndoableEditRef ref;

		protected final byte typeIndex;

		protected AbstractUndoableEdit( final UndoableEditRef ref, final int typeIndex )
		{
			this.ref = ref;
			this.typeIndex = ( byte ) typeIndex;
		}

		public void init()
		{
			ref.setIsUndoPointField( false );
			ref.setTypeIndex( typeIndex );
		}

		@Override
		public boolean isUndoPoint()
		{
			return ref.getIsUndoPointField();
		}

		@Override
		public void setUndoPoint( final boolean isUndoPoint )
		{
			ref.setIsUndoPointField( isUndoPoint );
		}
	}

	private int idgen = 0;

	private final TIntObjectArrayMap< UndoableEditType< ? > > undoableEditTypes = new TIntObjectArrayMap<>();

	/**
	 * Abstract base class for the {@link UndoableEditType}s of this {@link UndoableEditList}.
	 *
	 * @param <T>
	 *            the {@link AbstractUndoableEdit} type.
	 */
	protected abstract class UndoableEditTypeImp< T extends AbstractUndoableEdit > implements UndoableEditType< T >
	{
		private final int typeIndex;

		public UndoableEditTypeImp()
		{
			typeIndex = idgen++;
			undoableEditTypes.put( typeIndex, this );
		}

		@Override
		public int typeIndex()
		{
			return typeIndex;
		}

		@Override
		public abstract T createInstance( final UndoableEditRef ref );


		@Override
		public boolean isInstance( final UndoableEditRef ref )
		{
			return ref != null && ref.getTypeIndex() == typeIndex;
		}
	}

	UndoableEditType< ? > getUndoableEditType( final byte typeIndex )
	{
		return undoableEditTypes.get( typeIndex );
	}

	/*
	 * =========================================================================
	 *
	 *                        recording specific edits
	 *
	 * =========================================================================
	 */

	/**
	 * Record any {@link UndoableEdit}. (The method is named {@code recordOther}
	 * because derived classes provide more specific {@code record} methods, for
	 * example {@link GraphUndoableEditList#recordAddVertex(Vertex)}.)
	 *
	 * @param undoableEdit
	 *            the edit to record.
	 */
	public void recordOther( final UndoableEdit undoableEdit )
	{
		final UndoableEditRef ref = createRef();
		create( ref ).getEdit( other ).init( undoableEdit );
		releaseRef( ref );
	}

	private final OtherType other = new OtherType();

	private class OtherType extends UndoableEditTypeImp< Other >
	{
		@Override
		public Other createInstance( final UndoableEditRef ref )
		{
			return new Other( ref, typeIndex() );
		}
	}

	private class Other extends AbstractUndoableEdit
	{
		Other( final UndoableEditRef ref, final int typeIndex )
		{
			super( ref, typeIndex );
		}

		public void init( final UndoableEdit edit )
		{
			super.init();
			ref.setDataIndex( nonRefEdits.size() );
			nonRefEdits.add( edit );
		}

		@Override
		public void redo()
		{
			nonRefEdits.get( ( int ) ref.getDataIndex() ).redo();
		}

		@Override
		public void undo()
		{
			nonRefEdits.get( ( int ) ref.getDataIndex() ).undo();
		}

		@Override
		public void setUndoPoint( final boolean isUndoPoint )
		{
			nonRefEdits.get( ( int ) ref.getDataIndex() ).setUndoPoint( isUndoPoint );
		}

		@Override
		public boolean isUndoPoint()
		{
			return nonRefEdits.get( ( int ) ref.getDataIndex() ).isUndoPoint();
		}

		@Override
		public void clear()
		{
			// THIS ONLY WORKS BECAUSE OF HOW clearFromIndex() IS IMPLEMENTED!
			nonRefEdits.remove( nonRefEdits.size() - 1 );
		}
	}
}
