package org.mastodon.graph.ref;

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObject;

public class AbstractVertexPool<
			V extends AbstractVertex< V, E, T >,
			E extends AbstractEdge< E, ?, ? >,
			T extends MappedElement >
		extends Pool< V, T >
{
	// TODO make it private again when we do not need this anymore.
	protected AbstractNonSimpleEdgePool< E, ?, ? > edgePool;

	public AbstractVertexPool(
			final int initialCapacity,
			final PoolObject.Factory< V, T > vertexFactory )
	{
		super( initialCapacity, vertexFactory );
	}

	public void linkEdgePool( final AbstractNonSimpleEdgePool< E, ?, ? > edgePool )
	{
		this.edgePool = edgePool;
	}

	@Override
	public V createRef()
	{
		final V vertex = super.createRef();
		if ( edgePool != null )
			vertex.linkEdgePool( edgePool );
		return vertex;
	}

	@Override
	public V create( final V vertex )
	{
		return super.create( vertex );
	}

	public void delete( final V vertex )
	{
		if ( edgePool != null )
			edgePool.deleteAllLinkedEdges( vertex );
		deleteByInternalPoolIndex( vertex.getInternalPoolIndex() );
	}
}
