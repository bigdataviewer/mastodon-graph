package org.mastodon.graph.ref;

import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.graph.Vertex;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

/**
 * TODO: javadoc
 *
 * @param <V>
 * @param <E>
 * @param <T>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractVertex< V extends AbstractVertex< V, E, T >, E extends AbstractEdge< E, ?, ? >, T extends MappedElement >
		extends PoolObject< V, T >
		implements Vertex< E >
{
	protected static final int FIRST_IN_EDGE_INDEX_OFFSET = 0;
	protected static final int FIRST_OUT_EDGE_INDEX_OFFSET = FIRST_IN_EDGE_INDEX_OFFSET + INDEX_SIZE;
	protected static final int SIZE_IN_BYTES = FIRST_OUT_EDGE_INDEX_OFFSET + INDEX_SIZE;

	protected AbstractVertex( final AbstractVertexPool< V, ?, T > pool )
	{
		super( pool );
	}

	protected int getFirstInEdgeIndex()
	{
		return access.getIndex( FIRST_IN_EDGE_INDEX_OFFSET );
	}

	protected void setFirstInEdgeIndex( final int index )
	{
		access.putIndex( index, FIRST_IN_EDGE_INDEX_OFFSET );
	}

	protected int getFirstOutEdgeIndex()
	{
		return access.getIndex( FIRST_OUT_EDGE_INDEX_OFFSET );
	}

	protected void setFirstOutEdgeIndex( final int index )
	{
		access.putIndex( index, FIRST_OUT_EDGE_INDEX_OFFSET );
	}

	@Override
	protected void setToUninitializedState()
	{
		setFirstInEdgeIndex( -1 );
		setFirstOutEdgeIndex( -1 );
	}

	private AbstractEdgePool< E, ?, ? > edgePool;

	private IncomingEdges< E > incomingEdges;

	private OutgoingEdges< E > outgoingEdges;

	private AllEdges< E > edges;

	@Override
	public IncomingEdges< E > incomingEdges()
	{
		return incomingEdges;
	}

	@Override
	public OutgoingEdges< E > outgoingEdges()
	{
		return outgoingEdges;
	}

	@Override
	public AllEdges< E > edges()
	{
		return edges;
	}

	void linkEdgePool( final AbstractEdgePool< E, ?, ? > edgePool )
	{
		if ( this.edgePool != edgePool )
		{
			this.edgePool = edgePool;
			incomingEdges = new IncomingEdges<>( this, edgePool );
			outgoingEdges = new OutgoingEdges<>( this, edgePool );
			edges = new AllEdges<>( this, edgePool );
		}
	}
}
