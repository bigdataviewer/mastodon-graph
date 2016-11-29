package org.mastodon.graph.ref;

import java.util.Iterator;

import org.mastodon.graph.Edges;

public class OutgoingEdges< E extends AbstractEdge< E, ?, ? > > implements Edges< E >
{
	private final AbstractVertex< ?, ?, ? > vertex;
	private final AbstractNonSimpleEdgePool< E, ?, ? > edgePool;

	private OutgoingEdgesIterator iterator;

	public OutgoingEdges(
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
		int edgeIndex = vertex.getFirstOutEdgeIndex();
		if ( edgeIndex >= 0 )
		{
			final E edge = edgePool.createRef();
			while ( edgeIndex >= 0 )
			{
				++numEdges;
				edgePool.getObject( edgeIndex, edge );
				edgeIndex = edge.getNextSourceEdgeIndex();
			}
			edgePool.releaseRef( edge );
		}
		return numEdges;
	}

	@Override
	public boolean isEmpty()
	{
		return vertex.getFirstOutEdgeIndex() < 0;
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
		int edgeIndex = vertex.getFirstOutEdgeIndex();
		edgePool.getObject( edgeIndex, edge );
		while( i-- > 0 )
		{
			edgeIndex = edge.getNextSourceEdgeIndex();
			edgePool.getObject( edgeIndex, edge );
		}
		return edge;

	}

	@Override
	public OutgoingEdgesIterator iterator()
	{
		if ( iterator == null )
			iterator = new OutgoingEdgesIterator();
		else
			iterator.reset();
		return iterator;
	}

	@Override
	public OutgoingEdgesIterator safe_iterator()
	{
		return new OutgoingEdgesIterator();
	}

	public class OutgoingEdgesIterator implements Iterator< E >
	{
		private int edgeIndex;

		private final E edge;

		public OutgoingEdgesIterator()
		{
			this.edge = edgePool.createRef();
			reset();
		}

		public void reset()
		{
			edgeIndex = vertex.getFirstOutEdgeIndex();
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
			edgeIndex = edge.getNextSourceEdgeIndex();
			return edge;
		}

		@Override
		public void remove()
		{
			edgePool.delete( edge );
		}
	}
}
