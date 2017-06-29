package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class ListenableTestVertexPool extends AbstractListenableVertexPool< ListenableTestVertex, ListenableTestEdge, ByteMappedElement >
{

	static class ListenableTestVertexLayout extends AbstractVertexLayout
	{
		final IntField id = intField();
		final IntField timepoint = intField();
	}

	static ListenableTestVertexLayout layout = new ListenableTestVertexLayout();

	final IntAttribute< ListenableTestVertex > id;

	final IntAttribute< ListenableTestVertex > timepoint;

	public ListenableTestVertexPool( final int initialCapacity )
	{
		super( initialCapacity, layout, ListenableTestVertex.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id, this );
		timepoint = new IntAttribute<>( layout.timepoint, this );

	}

	@Override
	protected ListenableTestVertex createEmptyRef()
	{
		return new ListenableTestVertex( this );
	}
}