package org.mastodon.graph.algorithm.traversal;

import org.mastodon.collection.RefList;
import org.mastodon.collection.RefStack;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.RootFinder;

/**
 * A Cross-component, Depth-first iterator, that iterates through a whole graph,
 * traversing edges only following their direction.
 * <p>
 * With {@code A -> B}, the iterator will move from A to B, but not from B to A.
 * <p>
 * This iterator is a cross-component iterator, meaning that when the iteration
 * through a connected-component of the graph is finished, the iterator jumps to
 * the next component automatically. This ensures that all the vertices of the
 * graph are iterated exactly once with this iterator.
 * <p>
 * The order in which the connected-components are iterated can be specified by
 * using the constructor that specifies an iterable over the collection of
 * roots, using a list with the desired order. For this iterator to operate
 * properly and indeed iterate through all the vertices of the graph exactly
 * once, the specified collection of roots must include all the roots of the
 * graph, that is: all the vertices that have no incoming edges.
 * <p>
 * Within a single connected-component, the order of iteration is depth-first.
 * The iterator only jumps to another component only when all the vertices of
 * the currently iterated component have been iterated.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the type of the graph vertices iterated.
 * @param <E>
 *            the type of the graph edges iterated.
 */
public class DepthFirstCrossComponentIterator< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphIteratorAlgorithm< V, E >
{

	private final RefStack< V > stack;

	/**
	 * Creates a depth-first, cross-component iterator starting from the
	 * specified vertex.
	 * <p>
	 * The collection of roots is determined automatically at creation.
	 *
	 * @param start
	 *            the vertex to start iteration with.
	 * @param graph
	 *            the graph to iterate over.
	 */
	public DepthFirstCrossComponentIterator( final V start, final Graph< V, E > graph )
	{
		this( start, graph, RootFinder.getRoots( graph ) );
	}

	/**
	 * Creates a depth-first, cross-component iterator starting from the
	 * specified vertex, using the specified collection of roots to jump across
	 * connected-components.
	 * <p>
	 * The order in which the connected-components are iterated can be specified
	 * by using this constructor, using a list with the desired order. For this
	 * iterator to operate properly and indeed iterate through all the vertices
	 * of the graph exactly once, the specified collection of roots must include
	 * all the roots of the graph, that is: all the vertices that have no
	 * incoming edges.
	 *
	 * @param start
	 *            the vertex to start iteration with.
	 * @param graph
	 *            the graph to iterate over.
	 * @param roots
	 *            an iterable over the collection of roots.
	 */
	public DepthFirstCrossComponentIterator( final V start, final Graph< V, E > graph, final Iterable< V > roots )
	{
		super( graph );
		stack = createVertexStack();
		final RefList< V > list = createVertexList();
		for ( final V root : roots )
		{
			if ( !root.equals( start ) )
				list.add( root );
		}
		// Inverse list order
		for ( int i = list.size() - 1; i >= 0; i-- )
			stack.push( list.get( i ) );

		stack.push( start );
		fetchNext();
		visited.add( start );
	}

	@Override
	protected Iterable< E > neighbors( final V vertex )
	{
		return vertex.outgoingEdges();
	}

	@Override
	protected V fetch( final V ref )
	{
		return stack.pop( ref );
	}

	@Override
	protected void toss( final V vertex )
	{
		stack.push( vertex );
	}

	@Override
	protected boolean canFetch()
	{
		return !stack.isEmpty();
	}

	static < V extends Vertex< E >, E extends Edge< V > > DepthFirstIterator< V, E > create( final V root, final Graph< V, E > graph )
	{
		return new DepthFirstIterator< V, E >( root, graph );
	}
}