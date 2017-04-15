package org.mastodon.revisedundo;

import static org.mastodon.pool.ByteUtils.INT_SIZE;
import static org.mastodon.pool.ByteUtils.SHORT_SIZE;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.properties.undo.PropertyUndoRedoStack;
import org.mastodon.revisedundo.ByteArrayUndoRedoStack.ByteArrayRef;
import org.mastodon.revisedundo.attributes.Attribute;
import org.mastodon.revisedundo.attributes.AttributeSerializer;
import org.mastodon.revisedundo.edits.GenericUndoableEditType;
import org.mastodon.revisedundo.edits.SetAttributeType;
import org.mastodon.revisedundo.edits.SetPropertyType;

/**
 * A undo/redo stack of undoable edits made to a listenable graph.
 *
 * @param <V>
 *            the type of vertex.
 * @param <E>
 *            the type of edges.
 *
 * @author Tobias Pietzsch
 */
public class GraphUndoRedoStack<
			V extends Vertex< E >,
			E extends Edge< V > >
		extends UndoRedoStack
{
	protected final ListenableGraph< V, E > graph;

	protected final UndoIdBimap< V > vertexUndoIdBimap;

	protected final UndoIdBimap< E > edgeUndoIdBimap;

	protected final AttributeSerializer< V > vertexSerializer;

	protected final AttributeSerializer< E > edgeSerializer;

	protected final ByteArrayUndoRedoStack dataStack;

	public GraphUndoRedoStack(
			final int initialCapacity,
			final ListenableGraph< V, E > graph,
			final AttributeSerializer< V > vertexSerializer,
			final AttributeSerializer< E > edgeSerializer,
			final UndoIdBimap< V > vertexUndoIdBimap,
			final UndoIdBimap< E > edgeUndoIdBimap )
	{
		super( initialCapacity );
		this.graph = graph;
		this.vertexSerializer = vertexSerializer;
		this.edgeSerializer = edgeSerializer;
		this.vertexUndoIdBimap = vertexUndoIdBimap;
		this.edgeUndoIdBimap = edgeUndoIdBimap;

		dataStack = new ByteArrayUndoRedoStack( 1024 * 1024 * 32 );
	}

	public Recorder< V > createAddVertexRecorder()
	{
		return new AddVertexType( this );
	}

	public Recorder< V > createRemoveVertexRecorder()
	{
		return new RemoveVertexType( this );
	}

	public Recorder< E > createAddEdgeRecorder()
	{
		return new AddEdgeType( this );
	}

	public Recorder< E > createRemoveEdgeRecorder()
	{
		return new RemoveEdgeType( this );
	}

	public Recorder< V > createSetVertexAttributeRecorder( final Attribute< V > attribute )
	{
		return new SetAttributeType<>( attribute.getUndoSerializer(), vertexUndoIdBimap, dataStack, this );
	}

	public Recorder< E > createSetEdgeAttributeRecorder( final Attribute< E > attribute )
	{
		return new SetAttributeType<>( attribute.getUndoSerializer(), edgeUndoIdBimap, dataStack, this );
	}

	public Recorder< V > createSetVertexPropertyRecorder( final PropertyUndoRedoStack< V > propertyUndoRedoStack )
	{
		return new SetPropertyType<>( propertyUndoRedoStack, vertexUndoIdBimap, dataStack, this );
	}

	public Recorder< E > createSetEdgePropertyRecorder( final PropertyUndoRedoStack< E > propertyUndoRedoStack )
	{
		return new SetPropertyType<>( propertyUndoRedoStack, edgeUndoIdBimap, dataStack, this );
	}

	public < T extends UndoableEdit > Recorder< T > createGenericUndoableEditRecorder()
	{
		return new GenericUndoableEditType<>( this );
	}

	private class AddVertexType extends AbstractUndoableEditType implements Recorder< V >
	{
		private final AddRemoveVertexRecord addRemoveVertex;

		private final int size;

		private final ByteArrayRef ref;

		public AddVertexType( final UndoRedoStack undoRedoStack )
		{
			super( undoRedoStack );
			addRemoveVertex = new AddRemoveVertexRecord();
			size = addRemoveVertex.getBufferSize();
			ref = dataStack.createRef();
		}

		@Override
		public void record( final V vertex )
		{
			recordType();
			addRemoveVertex.initAdd( vertex, dataStack.record( size, ref ) );
		}

		@Override
		public void undo()
		{
			addRemoveVertex.doRemoveVertex( dataStack.undo( size, ref ) );
		}

		@Override
		public void redo()
		{
			addRemoveVertex.doAddVertex( dataStack.redo( size, ref ) );
		}
	}

	private class RemoveVertexType extends AbstractUndoableEditType implements Recorder< V >
	{
		private final AddRemoveVertexRecord addRemoveVertex;

		private final int size;

		private final ByteArrayRef ref;

		public RemoveVertexType( final UndoRedoStack undoRedoStack )
		{
			super( undoRedoStack );
			addRemoveVertex = new AddRemoveVertexRecord();
			size = addRemoveVertex.getBufferSize();
			ref = dataStack.createRef();
		}

		@Override
		public void record( final V vertex )
		{
			recordType();
			addRemoveVertex.initRemove( vertex, dataStack.record( size, ref ) );
		}

		@Override
		public void undo()
		{
			addRemoveVertex.doAddVertex( dataStack.undo( size, ref ) );
		}

		@Override
		public void redo()
		{
			addRemoveVertex.doRemoveVertex( dataStack.redo( size, ref ) );
		}
	}

	private class AddRemoveVertexRecord
	{
		private final byte[] data;

		private final int bufferSize;

		private final static int VERTEX_ID_OFFSET = 0;
		private final static int DATA_OFFSET = VERTEX_ID_OFFSET + INT_SIZE;

		public AddRemoveVertexRecord()
		{
			data = new byte[ vertexSerializer.getNumBytes() ];
			bufferSize = DATA_OFFSET + vertexSerializer.getNumBytes();
		}

		public void initAdd( final V vertex, final ByteArrayRef buffer )
		{
			final int vi = vertexUndoIdBimap.getId( vertex );
			buffer.putInt( VERTEX_ID_OFFSET, vi );
		}

		public void initRemove( final V vertex, final ByteArrayRef buffer )
		{
			final int vi = vertexUndoIdBimap.getId( vertex );
			vertexSerializer.getBytes( vertex, data );

			buffer.putInt( VERTEX_ID_OFFSET, vi );
			buffer.putBytes( DATA_OFFSET, data );
		}

		public void doRemoveVertex( final ByteArrayRef buffer )
		{
			final V vref = graph.vertexRef();

			final int vi = buffer.getInt( VERTEX_ID_OFFSET );
			final V vertex = vertexUndoIdBimap.getObject( vi, vref );
			vertexSerializer.getBytes( vertex, data );
			buffer.putBytes( DATA_OFFSET, data );
			graph.remove( vertex );

			graph.releaseRef( vref );
		}

		public void doAddVertex( final ByteArrayRef buffer )
		{
			final V ref = graph.vertexRef();

			final int vi = buffer.getInt( VERTEX_ID_OFFSET );
			final V vertex = graph.addVertex( ref );
			vertexUndoIdBimap.put( vertex, vi );
			buffer.getBytes( DATA_OFFSET, data );
			vertexSerializer.setBytes( vertex, data );
			vertexSerializer.notifySet( vertex );

			graph.releaseRef( ref );
		}

		public int getBufferSize()
		{
			return bufferSize;
		}
	}

	private class AddEdgeType extends AbstractUndoableEditType implements Recorder< E >
	{
		private final AddRemoveEdgeRecord addRemoveEdge;

		private final int size;

		private final ByteArrayRef ref;

		public AddEdgeType( final UndoRedoStack undoRedoStack )
		{
			super( undoRedoStack );
			addRemoveEdge = new AddRemoveEdgeRecord();
			size = addRemoveEdge.getBufferSize();
			ref = dataStack.createRef();
		}

		@Override
		public void record( final E edge )
		{
			recordType();
			addRemoveEdge.initAdd( edge, dataStack.record( size, ref ) );
		}

		@Override
		public void undo()
		{
			addRemoveEdge.doRemoveEdge( dataStack.undo( size, ref ) );
		}

		@Override
		public void redo()
		{
			addRemoveEdge.doAddEdge( dataStack.redo( size, ref ) );
		}
	}

	private class RemoveEdgeType extends AbstractUndoableEditType implements Recorder< E >
	{
		private final AddRemoveEdgeRecord addRemoveEdge;

		private final int size;

		private final ByteArrayRef ref;

		public RemoveEdgeType( final UndoRedoStack undoRedoStack )
		{
			super( undoRedoStack );
			addRemoveEdge = new AddRemoveEdgeRecord();
			size = addRemoveEdge.getBufferSize();
			ref = dataStack.createRef();
		}

		@Override
		public void record( final E edge )
		{
			recordType();
			addRemoveEdge.initRemove( edge, dataStack.record( size, ref ) );
		}

		@Override
		public void undo()
		{
			addRemoveEdge.doAddEdge( dataStack.undo( size, ref ) );
		}

		@Override
		public void redo()
		{
			addRemoveEdge.doRemoveEdge( dataStack.redo( size, ref ) );
		}
	}

	private class AddRemoveEdgeRecord
	{
		private final byte[] data;

		private final int bufferSize;

		private final static int EDGE_ID_OFFSET = 0;
		private final static int SOURCE_VERTEX_ID_OFFSET = EDGE_ID_OFFSET + INT_SIZE;
		private final static int TARGET_VERTEX_ID_OFFSET = SOURCE_VERTEX_ID_OFFSET + INT_SIZE;
		private final static int SOURCE_OUT_INDEX_OFFSET = TARGET_VERTEX_ID_OFFSET + INT_SIZE;
		private final static int TARGET_IN_INDEX_OFFSET = SOURCE_OUT_INDEX_OFFSET + SHORT_SIZE;
		private final static int DATA_OFFSET = TARGET_IN_INDEX_OFFSET + SHORT_SIZE;

		public AddRemoveEdgeRecord()
		{
			data = new byte[ edgeSerializer.getNumBytes() ];
			bufferSize = DATA_OFFSET + edgeSerializer.getNumBytes();
		}

		public void initAdd( final E edge, final ByteArrayRef buffer )
		{
			final int ei = edgeUndoIdBimap.getId( edge );
			buffer.putInt( EDGE_ID_OFFSET, ei );
		}

		public void initRemove( final E edge, final ByteArrayRef buffer )
		{
			final V vref = graph.vertexRef();

			final int ei = edgeUndoIdBimap.getId( edge );
			final int si = vertexUndoIdBimap.getId( edge.getSource( vref ) );
			final int ti = vertexUndoIdBimap.getId( edge.getTarget( vref ) );
			final int sOutIndex = edge.getSourceOutIndex();
			final int tInIndex = edge.getTargetInIndex();
			buffer.putInt( EDGE_ID_OFFSET, ei );
			buffer.putInt( SOURCE_VERTEX_ID_OFFSET, si );
			buffer.putInt( TARGET_VERTEX_ID_OFFSET, ti );
			buffer.putShort( SOURCE_OUT_INDEX_OFFSET, ( short ) sOutIndex );
			buffer.putShort( TARGET_IN_INDEX_OFFSET, ( short ) tInIndex );

			edgeSerializer.getBytes( edge, data );
			buffer.putBytes( DATA_OFFSET, data );

			graph.releaseRef( vref );
		}

		public void doRemoveEdge( final ByteArrayRef buffer )
		{
			final E ref = graph.edgeRef();
			final V vref = graph.vertexRef();

			final int ei = buffer.getInt( EDGE_ID_OFFSET );
			final E edge = edgeUndoIdBimap.getObject( ei, ref );

			final int si = vertexUndoIdBimap.getId( edge.getSource( vref ) );
			final int ti = vertexUndoIdBimap.getId( edge.getTarget( vref ) );
			final int sOutIndex = edge.getSourceOutIndex();
			final int tInIndex = edge.getTargetInIndex();
			buffer.putInt( SOURCE_VERTEX_ID_OFFSET, si );
			buffer.putInt( TARGET_VERTEX_ID_OFFSET, ti );
			buffer.putShort( SOURCE_OUT_INDEX_OFFSET, ( short ) sOutIndex );
			buffer.putShort( TARGET_IN_INDEX_OFFSET, ( short ) tInIndex );

			edgeSerializer.getBytes( edge, data );
			buffer.putBytes( DATA_OFFSET, data );

			graph.remove( edge );

			graph.releaseRef( ref );
			graph.releaseRef( vref );
		}

		public void doAddEdge( final ByteArrayRef buffer )
		{
			final E ref = graph.edgeRef();
			final V vref1 = graph.vertexRef();
			final V vref2 = graph.vertexRef();

			final int ei = buffer.getInt( EDGE_ID_OFFSET );
			final int si = buffer.getInt( SOURCE_VERTEX_ID_OFFSET );
			final int ti = buffer.getInt( TARGET_VERTEX_ID_OFFSET );
			final int sOutIndex = buffer.getShort( SOURCE_OUT_INDEX_OFFSET );
			final int tInIndex = buffer.getShort( TARGET_IN_INDEX_OFFSET );

			final V source = vertexUndoIdBimap.getObject( si, vref1 );
			final V target = vertexUndoIdBimap.getObject( ti, vref2 );
			final E edge = graph.insertEdge( source, sOutIndex, target, tInIndex, ref );
			edgeUndoIdBimap.put( edge, ei );

			buffer.getBytes( DATA_OFFSET, data );
			edgeSerializer.setBytes( edge, data );
			edgeSerializer.notifySet( edge );

			graph.releaseRef( ref );
			graph.releaseRef( vref1 );
			graph.releaseRef( vref2 );
		}

		public int getBufferSize()
		{
			return bufferSize;
		}
	}
}