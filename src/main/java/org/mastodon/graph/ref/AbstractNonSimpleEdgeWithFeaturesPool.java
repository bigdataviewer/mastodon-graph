package org.mastodon.graph.ref;

import org.mastodon.features.Features;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

public class AbstractNonSimpleEdgeWithFeaturesPool< 
		E extends AbstractNonSimpleEdgeWithFeatures< E, V, T >,
		V extends AbstractVertex< V, ?, ? >,
		T extends MappedElement >
	extends AbstractNonSimpleEdgePool< E, V, T >
{

	private Features< E > features;

	public AbstractNonSimpleEdgeWithFeaturesPool(
			final int initialCapacity,
			final PoolObject.Factory< E, T > edgeFactory,
			final AbstractVertexPool< V, ?, ? > vertexPool )
	{
		super( initialCapacity, edgeFactory, vertexPool );
	}

	public void linkFeatures( final Features< E > features )
	{
		this.features = features;
	}

	@Override
	public E createRef()
	{
		final E edge = super.createRef();
		edge.features = features;
		return edge;
	}

	@Override
	public void delete( final E edge )
	{
		edge.features.delete( edge );
		super.delete( edge );
	}

}
