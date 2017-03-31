package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.ByteMappedElement;

public class TestEdge extends AbstractEdge< TestEdge, TestVertex, TestEdgePool, ByteMappedElement >
{
	protected static final int SIZE_IN_BYTES = AbstractEdge.SIZE_IN_BYTES;

	public final AbstractEdgePool< TestEdge, TestVertex, ByteMappedElement > creatingPool;

	protected TestEdge( final TestEdgePool pool )
	{
		super( pool );
		creatingPool = pool;
	}

	@Override
	public String toString()
	{
		final TestVertex v = this.vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "e(" );
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
