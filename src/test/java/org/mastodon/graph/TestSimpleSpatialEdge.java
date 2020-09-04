package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.pool.ByteMappedElement;

public class TestSimpleSpatialEdge extends AbstractListenableEdge< TestSimpleSpatialEdge, TestSimpleSpatialVertex, TestSimpleSpatialEdgePool, ByteMappedElement >
{

	public TestSimpleSpatialEdge( final TestSimpleSpatialEdgePool pool )
	{
		super( pool );
	}

	public TestSimpleSpatialEdge init()
	{
		initDone();
		return this;
	}


	@Override
	public String toString()
	{
		final TestSimpleSpatialVertex v = this.vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "se(" );
		getSource( v );
		sb.append( v.getId() );
		sb.append( " -> " );
		getTarget( v );
		sb.append( v.getId() );
		sb.append( ")" );
		this.vertexPool.releaseRef( v );
		return sb.toString();
	}
}