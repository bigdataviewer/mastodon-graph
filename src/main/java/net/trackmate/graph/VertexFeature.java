package net.trackmate.graph;

import net.trackmate.graph.FeatureRegistry.DuplicateKeyException;
import net.trackmate.graph.features.FeatureCleanup;
import net.trackmate.graph.features.NotifyFeatureValueChange;
import net.trackmate.graph.features.UndoFeatureMap;

/**
 * TODO
 *
 * @param <M>
 * @param <V>
 * @param <F>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public abstract class VertexFeature< M, V extends Vertex< ? >, F extends FeatureValue< ? > >
{
	private final String key;

	/**
	 * Unique ID. These IDs are generated by FeatureRegistry, starting from 0.
	 * As long as there are not excessively many VertexFeatures, the ID can be
	 * used as an index to look up features in a list instead of a map.
	 */
	private final int id;

	protected VertexFeature( final String key ) throws DuplicateKeyException
	{
		this.key = key;
		this.id = FeatureRegistry.getUniqueVertexFeatureId( key );
		FeatureRegistry.registerVertexFeature( this );
	}

	public String getKey()
	{
		return key;
	}

	/*
	 * Following part is for the graph to create feature maps, initialize
	 * features, serialize, etc...
	 */

	protected abstract M createFeatureMap( final ReadOnlyGraph< V, ? > graph );

	public abstract F createFeatureValue( V vertex, GraphFeatures< V, ? > graphFeatures );

	protected abstract FeatureCleanup< V > createFeatureCleanup( M featureMap );

	public abstract UndoFeatureMap< V > createUndoFeatureMap( M featureMap );

	public int getUniqueFeatureId()
	{
		return id;
	}

	protected static class NotifyValueChange< V extends Vertex< ? > > implements NotifyFeatureValueChange
	{
		private final GraphFeatures< V, ? > graphFeatures;

		private final VertexFeature< ?, V, ? > feature;

		private final V vertex;

		public NotifyValueChange( final GraphFeatures< V, ? > graphFeatures, final VertexFeature< ?, V, ? > feature, final V vertex )
		{
			this.graphFeatures = graphFeatures;
			this.feature = feature;
			this.vertex = vertex;
		}

		@Override
		public void notifyBeforeFeatureChange()
		{
			graphFeatures.notifyBeforeFeatureChange( feature, vertex );
		}
	}

	@Override
	public int hashCode()
	{
		return id;
	}

	@Override
	public boolean equals( final Object obj )
	{
		return obj instanceof VertexFeature
				&& ( ( VertexFeature< ?, ?, ? > ) obj ).key.equals( key );
	}

	@Override
	public String toString()
	{
		return getClass().getName() + "(\"" + key + "\")";
	}
}
