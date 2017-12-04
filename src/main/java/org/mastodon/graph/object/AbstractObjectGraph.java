package org.mastodon.graph.object;

import java.util.Collection;
import java.util.Collections;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.wrap.RefCollectionWrapper;
import org.mastodon.graph.Edges;
import org.mastodon.graph.Graph;

public abstract class AbstractObjectGraph< V extends AbstractObjectVertex< V, E >, E extends AbstractObjectEdge< E, V > > implements Graph< V, E >
{

	public interface Factory< V, E >
	{
		public V createVertex();

		public E createEdge( V source, V target );
	}

	private final Factory< V, E > factory;

	private final Collection< V > vertices;

	private final Collection< E > edges;

	private final RefCollectionWrapper< V > unmodifiableVertices;

	private final RefCollectionWrapper< E > unmodifiableEdges;

	protected AbstractObjectGraph( final Factory< V, E > factory, final Collection< V > vertices, final Collection< E > edges )
	{
		this.factory = factory;
		this.vertices = vertices;
		this.edges = edges;
		unmodifiableVertices = new RefCollectionWrapper<>( Collections.unmodifiableCollection( vertices ) );
		unmodifiableEdges = new RefCollectionWrapper<>( Collections.unmodifiableCollection( edges ) );
	}

	@Override
	public V addVertex()
	{
		final V vertex = factory.createVertex();
		vertices.add( vertex );
		return vertex;
	}

	@Override
	public V addVertex( final V ref )
	{
		return addVertex();
	}

	@Override
	public E addEdge( final V source, final V target )
	{
		final E edge = factory.createEdge( source, target );
		source.outgoing.edges.add( edge );
		target.incoming.edges.add( edge );
		edges.add( edge );
		return edge;
	}

	@Override
	public E addEdge( final V source, final V target, final E ref )
	{
		return addEdge( source, target );
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex )
	{
		final E edge = factory.createEdge( source, target );
		source.outgoing.edges.add( Math.min( Math.max( 0, sourceOutIndex ), source.outgoing.size() ), edge );
		target.incoming.edges.add( Math.min( Math.max( 0, targetInIndex ), target.incoming.size() ), edge );
		edges.add( edge );
		return edge;
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex, final E ref )
	{
		return insertEdge( source, sourceOutIndex, target, targetInIndex );
	}

	@Override
	public E getEdge( final V source, final V target )
	{
		for ( final E edge : source.outgoing )
			if ( target.incoming.edges.contains( edge ) )
				return edge;
		return null;
	}

	@Override
	public E getEdge( final V source, final V target, final E ref )
	{
		return getEdge( source, target );
	}

	@Override
	public Edges< E > getEdges( final V source, final V target, final V ref )
	{
		return getEdges( source, target );
	}

	@Override
	public Edges< E > getEdges( final V source, final V target )
	{
		return new ObjectEdgesSourceToTarget<>( source, target, edges );
	}

	@Override
	public void remove( final V vertex )
	{
		if ( vertices.remove( vertex ) )
		{
			for ( final E edge : vertex.incoming )
			{
				edge.getSource().outgoing.edges.remove( edge );
				edges.remove( edge );
			}
			for ( final E edge : vertex.outgoing )
			{
				edge.getTarget().incoming.edges.remove( edge );
				edges.remove( edge );
			}
		}
 	}

	@Override
	public void remove( final E edge )
	{
		if ( edges.remove( edge ) )
		{
			edge.getSource().outgoing.edges.remove( edge );
			edge.getTarget().incoming.edges.remove( edge );
		}
	}

	@Override
	public RefCollection< V > vertices()
	{
		return unmodifiableVertices;
	}

	@Override
	public RefCollection< E > edges()
	{
		return unmodifiableEdges;
	}

	@Override
	public V vertexRef()
	{
		return null;
	}

	@Override
	public E edgeRef()
	{
		return null;
	}

	@Override
	public void releaseRef( final V ref )
	{}

	@Override
	public void releaseRef( final E ref )
	{}
}
