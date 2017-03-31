package org.mastodon.graph.ref;

import org.mastodon.features.Features;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

public class AbstractEdgeWithFeaturesPool<
			E extends AbstractEdgeWithFeatures< E, V, ?, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			T extends MappedElement >
		extends AbstractEdgePool< E, V, T >
{
	Features< E > features;

	public AbstractEdgeWithFeaturesPool(
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
	public void delete( final E edge )
	{
		edge.features.delete( edge );
		super.delete( edge );
	}
}
