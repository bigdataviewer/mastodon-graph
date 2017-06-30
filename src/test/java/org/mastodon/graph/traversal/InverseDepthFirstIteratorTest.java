package org.mastodon.graph.traversal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefCollections;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.algorithm.traversal.InverseDepthFirstIterator;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectVertex;
import org.mastodon.graph.traversal.GraphsForTests.GraphTestBundle;

/**
 *
 * @author Jean=Yves Tinevez &ltjeanyves.tinevez@gmail.com&gt
 */
public class InverseDepthFirstIteratorTest
{

	@Test
	public void testStraightLinePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.straightLinePoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 5 );
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testStraightLineStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.straightLineStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 5 );
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testForkStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.forkStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 1 );
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testForkPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.forkPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 1 );
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testDiamondPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.diamondPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 3 );
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 1 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testDiamondStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.diamondStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 3 );
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 1 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testLoopStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.loopStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 1 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testLoopPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.loopPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 1 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testSingleEdgePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.singleEdgePoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 1 );
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testSingleEdgeStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.singleEdgeStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 1 );
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testSingleVertexStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.singleVertexStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testSingleVertexPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.singleVertexPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testWpExampleVertexPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.wpExamplePoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 6 ); // G
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testWpExampleVertexStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.wpExampleStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 6 ); // G
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testTwoComponentsVertexStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.twoComponentsStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 6 ); // G
		final InverseDepthFirstIterator< ObjectVertex< Integer >, ObjectEdge< Integer >> it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< ObjectVertex< Integer >> expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< ObjectVertex< Integer >> eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testTwoComponentsVertexPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.twoComponentsPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 6 ); // G
		final InverseDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new InverseDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}
}
