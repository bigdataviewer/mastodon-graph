package org.mastodon.graph.ref;

import java.util.Iterator;

import org.mastodon.graph.Edges;

public class IncomingEdges< E extends AbstractEdge< E, ?, ? > > implements Edges< E >
{
	private final AbstractVertex< ?, ?, ? > vertex;
	private final AbstractNonSimpleEdgePool< E, ?, ? > edgePool;

	private IncomingEdgesIterator iterator;

	public IncomingEdges(
			final AbstractVertex< ?, ?, ? > vertex,
			final AbstractNonSimpleEdgePool< E, ?, ? > edgePool )
	{
		this.vertex = vertex;
		this.edgePool = edgePool;

		iterator = null;
	}

	@Override
	public int size()
	{
		int numEdges = 0;
		int edgeIndex = vertex.getFirstInEdgeIndex();
		if ( edgeIndex >= 0 )
		{
			final E edge = edgePool.createRef();
			while ( edgeIndex >= 0 )
			{
				++numEdges;
				edgePool.getObject( edgeIndex, edge );
				edgeIndex = edge.getNextTargetEdgeIndex();
			}
			edgePool.releaseRef( edge );
		}
		return numEdges;
	}

	@Override
	public boolean isEmpty()
	{
		return vertex.getFirstInEdgeIndex() < 0;
	}

	@Override
	public E get( final int i )
	{
		return get( i, edgePool.createRef() );
	}

	// garbage-free version
	@Override
	public E get( int i, final E edge )
	{
		int edgeIndex = vertex.getFirstInEdgeIndex();
		edgePool.getObject( edgeIndex, edge );
		while( i-- > 0 )
		{
			edgeIndex = edge.getNextTargetEdgeIndex();
			edgePool.getObject( edgeIndex, edge );
		}
		return edge;

	}

	@Override
	public IncomingEdgesIterator iterator()
	{
		if ( iterator == null )
			iterator = new IncomingEdgesIterator();
		else
			iterator.reset();
		return iterator;
	}

	@Override
	public IncomingEdgesIterator safe_iterator()
	{
		return new IncomingEdgesIterator();
	}

	public class IncomingEdgesIterator implements Iterator< E >
	{
		private int edgeIndex;

		private final E edge;

		public IncomingEdgesIterator()
		{
			this.edge = edgePool.createRef();
			reset();
		}

		public void reset()
		{
			edgeIndex = vertex.getFirstInEdgeIndex();
		}

		@Override
		public boolean hasNext()
		{
			return edgeIndex >= 0;
		}

		@Override
		public E next()
		{
			edgePool.getObject( edgeIndex, edge );
			edgeIndex = edge.getNextTargetEdgeIndex();
			return edge;
		}

		@Override
		public void remove()
		{
			edgePool.delete( edge );
		}
	}
}
