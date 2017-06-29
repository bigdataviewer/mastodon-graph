package org.mastodon.graph.nonsimple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.TestEdge;
import org.mastodon.graph.TestGraph;
import org.mastodon.graph.TestVertex;

public class NonSimpleGraphTest
{

	private TestGraph graph;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new TestGraph();
	}

	@Test
	public void testSingleBranch()
	{
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 5; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
	}

	@Test
	public void testBranchY()
	{
		// Make a long branch.
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
		final TestVertex middle = vlist.get( vlist.size() / 2 );

		// Branch from its middle vertex in Y shape (merge event).
		TestVertex source = graph.addVertex().init( vlist.size() );
		TestVertex target = null;
		vlist.add( source );
		for ( int i = 1; i < 4; i++ )
		{
			target = graph.addVertex().init( vlist.size() );
			vlist.add( target );
			final TestEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
		graph.addEdge( target, middle );
	}

	@Test
	public void testBranchingLambda()
	{
		// Make a long branch.
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}

		// Branch from its middle vertex in lambda shape (split event).
		final TestVertex middle = vlist.get( vlist.size() / 2 );
		TestVertex source = middle;
		for ( int i = 0; i < vlist.size() / 2; i++ )
		{
			final TestVertex target = graph.addVertex().init( vlist.size() + i );
			vlist.add( target );
			final TestEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
	}

	@Test
	public void testMultipleEdges()
	{
		final TestVertex s = graph.addVertex().init( 0 );
		final TestVertex t = graph.addVertex().init( 1 );

		// Add 10 edges betwen the same 2 vertices.
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		for ( int i = 0; i < 10; i++ )
		{
			final TestEdge e = graph.addEdge( s, t );
			elist.add( e );
		}

		// Remove them one by one.
		for ( int i = 0; i < elist.size(); i++ )
		{
			final TestEdge e = graph.getEdge( s, t );
			assertNotNull( "There still should be at least one edge between source and target.", e );
			graph.remove( e );
		}
		assertNull( "There should be no edge left between source and target.", graph.getEdge( s, t ) );
	}

	@Test
	public void testMultipleEdgeIterator()
	{
		final TestVertex s = graph.addVertex().init( 0 );
		final TestVertex t = graph.addVertex().init( 1 );
		final TestVertex t2 = graph.addVertex().init( 2 );

		// Add 10 edges betwen the same 2 vertices.
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		for ( int i = 0; i < 10; i++ )
		{
			final TestEdge e = graph.addEdge( s, t );
			elist.add( e );
		}
		// And an edge to another target.
		graph.addEdge( s, t2 );

		final Iterator< TestEdge > edges = graph.getEdges( s, t );
		int nedges = 0;
		TestEdge previous = null;
		while ( edges.hasNext() )
		{
			nedges++;
			final TestEdge e = edges.next();

			// Test non equality with previous edge.
			if ( null != previous )
			{
				assertNotEquals( "Iterated edges should be all difference", previous, e );
			}
			else
			{
				previous = graph.edgeRef();
			}
			previous.refTo( e );

			// Check that edge iterated was indeed added to the graph.
			assertTrue( "Iterated edge is unexpected.", elist.contains( e ) );

			// Are source and target ok?
			assertEquals( "Edge source is unexpected.", s, e.getSource() );
			assertEquals( "Edge target is unexpected.", t, e.getTarget() );
			elist.remove( e );
		}
		assertEquals( "Did not iterate over the expected number of edges.", 10, nedges );
		assertTrue( "Not all edges have been iterated.", elist.isEmpty() );

		/*
		 * Test reuse of iterator ref.
		 */

		final int hashCode = edges.hashCode();
		final Iterator< TestEdge > edges2 = graph.getEdges( s, t, edges );
		assertEquals( "Did not reuse iterator reference.", hashCode, edges2.hashCode() );

		/*
		 * Test iterator with removal.
		 */

		while ( edges2.hasNext() )
		{
			edges2.next();
			edges2.remove();
		}

		assertNull( "All edges between source and target should have been removed.", graph.getEdge( s, t ) );

		/*
		 * Test reuse of iterator ref.
		 */

		final Iterator< TestEdge > edges3 = graph.getEdges( s, t2, edges );
		assertEquals( "Did not reuse iterator reference.", hashCode, edges3.hashCode() );

		/*
		 * Test single edge.
		 */

		while ( edges3.hasNext() )
		{
			final TestEdge e = edges3.next();
			assertEquals( "Unexpected edge.", graph.getEdge( s, t2 ), e );
		}

	}

}