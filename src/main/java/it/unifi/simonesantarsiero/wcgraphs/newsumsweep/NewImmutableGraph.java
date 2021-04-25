package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import com.martiansoftware.jsap.JSAPException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import it.unimi.dsi.logging.ProgressLogger;
import it.unimi.dsi.webgraph.*;
import it.unimi.dsi.webgraph.algo.ParallelBreadthFirstVisit;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class NewImmutableGraph {

	private static int[] map;

	private static final String ARCS_EXTENSION = ".arcs";

	private NewImmutableGraph() {
	}

	/**
	 * @return the vertex with maximum out-degree.
	 */
	public static int maxOutDegVert(ImmutableGraph g) {
		int max = 0;
		for (int v = 1; v < g.numNodes(); v++) {
			if (g.outdegree(v) > g.outdegree(max)) {
				max = v;
			}
		}
		return max;
	}

	public static int maxOutDegree(ImmutableGraph g) {
		return g.outdegree(maxOutDegVert(g));
	}

	public static ImmutableGraph createNewGraph(IntArrayList[] arcs) {
		ArrayListMutableGraph mutableGraph = new ArrayListMutableGraph(arcs.length);

		for (int i = 0; i < arcs.length; i++) {
			IntArrayList list = arcs[i];
			for (int j = 0; j < list.size(); j++) {
				mutableGraph.addArc(i, list.getInt(j));
			}
		}
		return mutableGraph.immutableView();
	}

	public static int[] getNewNumbers(int[] groups, int nGroups) {
		int[] newNumbers = new int[groups.length];
		int[] sizes = new int[nGroups];
		int[] cumulativeSizes = new int[nGroups];
		cumulativeSizes[0] = 0;

		for (int i = 0; i < groups.length; i++) {
			sizes[groups[i]]++;
		}

		for (int i = 1; i < cumulativeSizes.length; i++) {
			cumulativeSizes[i] = cumulativeSizes[i - 1] + sizes[i - 1];
		}

		for (int v = 0; v < groups.length; v++) {
			newNumbers[v] = cumulativeSizes[groups[v]]++;
		}

		return newNumbers;
	}

	public static IntArrayList[] permuteVertices(ImmutableGraph g, int[] permutation) {
		int nn = g.numNodes();

		IntArrayList[] newAdj = new IntArrayList[nn];
		for (int i = 0; i < nn; i++) {
			newAdj[i] = new IntArrayList();
		}
		for (int i = 0; i < nn; i++) {

			LazyIntIterator iterator = g.successors(i);

			for (int v; (v = iterator.nextInt()) != -1; ) {
				newAdj[permutation[i]].add(permutation[v]);
			}
		}
		return newAdj;
	}

	/**
	 * Collapses all vertices in the same group, and returns the obtained graph. Furthermore, vertices in this graph are sorted according
	 * to the groups
	 */
	public static ImmutableGraph collapseVertices(int[] groups, int nGroups, int[] newNumbers, ImmutableGraph g) {

		ArrayListMutableGraph mutableGraph = new ArrayListMutableGraph(nGroups);

		int[] newGroups = new int[newNumbers.length];
		boolean[] alreadyAdded = new boolean[nGroups];
		IntArrayList toReset = new IntArrayList();
		int v = 0;
		int w;

		for (int i = 0; i < newNumbers.length; i++) {
			newGroups[newNumbers[i]] = groups[i];
		}

		LazyIntIterator iter;
		for (int i = 0; i < newGroups.length; i++) {

			while (v < newGroups.length && newGroups[v] == i) {
				iter = g.successors(v);

				while ((w = iter.nextInt()) != -1) {
					if (newGroups[v] != newGroups[w] && !alreadyAdded[newGroups[w]]) {
						mutableGraph.addArc(newGroups[v], newGroups[w]);
						alreadyAdded[newGroups[w]] = true;
						toReset.add(newGroups[w]);
					}
				}
				v++;
			}
			while (!toReset.isEmpty()) {
				alreadyAdded[toReset.popInt()] = false;
			}
		}
		return mutableGraph.immutableView();
	}

	// Metodi di Dir
	public static void storeGraphFile(ImmutableGraph graph, CharSequence basename, int[] map) throws IOException {

		final PrintStream ps = new PrintStream(new FastBufferedOutputStream(new FileOutputStream(basename + ARCS_EXTENSION)), false, StandardCharsets.US_ASCII.toString());

		int d;
		int s;
		int[] successor;
		for (final NodeIterator nodeIterator = graph.nodeIterator(); nodeIterator.hasNext();) {
			s = nodeIterator.nextInt();
			d = nodeIterator.outdegree();
			successor = nodeIterator.successorArray();
			for(int i = 0; i < d; i++) {
				int nodeFrom = s;
				int nodeTo = successor[i];
				if (map[nodeFrom] != -1 && map[nodeTo] != -1) {

					nodeFrom = map[nodeFrom];
					nodeTo = map[nodeTo];
					ps.println((nodeFrom) + "\t" + (nodeTo));
				}
			}
		}
		ps.close();
	}

	/**
	 * @throws JSAPException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 */
	// SumSweep - GraphConverter
	public static void convertAsciiToWebgraph(String basename) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IOException, JSAPException {

		String bvinput1 = "-g ArcListASCIIGraph " + basename + ARCS_EXTENSION + " " + basename;
		BVGraph.main(bvinput1.split(" "));
	}

	/**
	 * Removes from this graph every node which is not in the biggest weakly connected component.
	 */
	// SumSweep - graph.Dir
	public static ImmutableGraph transformIntoBiggestWCC(ImmutableGraph graph, String basename) throws IOException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, JSAPException {

		String wccSuffix = "_WCC";
		basename += wccSuffix;

		//findWCC
		ImmutableGraph symGraph = Transform.symmetrize(graph);
		NewImmutableGraph.getLargestComponent(symGraph, 0, null); // mettere new ProgressLogger() per stampare

		NewImmutableGraph.storeGraphFile(graph, basename, NewImmutableGraph.map);
		NewImmutableGraph.convertAsciiToWebgraph(basename);

		return ImmutableGraph.load(basename);
	}

	/**
	 * Returns the largest connected components of a symmetric graph.
	 */
	// WebGraph - ConnectedComponents
	// L'ho modificato perché mi serve la mappa per fare il processo inverso
	public static ImmutableGraph getLargestComponent(final ImmutableGraph symGraph, final int threads, final ProgressLogger pl) {
		ParallelBreadthFirstVisit visit = new ParallelBreadthFirstVisit(symGraph, threads, false, pl);
		visit.visitAll();
		final AtomicIntegerArray visited = visit.marker;
		final int numberOfComponents = visit.round + 1;
		final int[] component = new int[visited.length()];
		final int[] componentSizes = new int [numberOfComponents];
		map = new int[symGraph.numNodes()];
		int largestCC = 0;
		int largestCCSize = Integer.MIN_VALUE;

		for (int i = component.length; i-- != 0;) {
			component[i] = visited.get(i);
			componentSizes[component[i]]++;
		}
		for (int i = 0; i < componentSizes.length; i++) {
			if (componentSizes[i] > largestCCSize) {
				largestCC = i;
				largestCCSize = componentSizes[i];
			}
		}

		for (int i = symGraph.numNodes(); i-- != 0;) {
			if (component[i] == largestCC) {
				map[i] = --largestCCSize;
			} else {
				map[i] = -1;
			}
		}

		return Transform.map(symGraph, map, pl);
	}

	/**
	 * Runs a BFS, performing at each step the routines specified by visit.
	 * @param visit a class containing all routines used by the BFS.
	 */
	public static void performBFS(ImmutableGraph g, VisitBFS visit) {
		LazyIntIterator iter;

		int v;
		int w;

		int[] q = visit.atStartVisit();
		int startQ = 0;
		int endQ = q.length;
		q = IntArrays.ensureCapacity(q, g.numNodes());

		while (endQ > startQ) {
			v = q[startQ++];
			iter = g.successors(v);

			while((w = iter.nextInt() ) != -1) {
				if (visit.atVisitedArc(v, w)) {
					q[endQ++] = w;
				}
			}
		}
		visit.atEndVisit();
	}

	public static void performBFSGivenQ(ImmutableGraph g, VisitBFS visit) {
		LazyIntIterator iter;

		int v;
		int w;

		int[] q = visit.atStartVisit();
		q = IntArrays.ensureCapacity(q, g.numNodes() + 2);

		while (q[1] > q[0]) {
			v = q[q[0]++];
			iter = g.successors(v);

			while((w = iter.nextInt() ) != -1) {
				if (visit.atVisitedArc(v, w)) {
					q[q[1]++] = w;
				}
			}
		}
		visit.atEndVisit();
	}

	public static void performBBFSGivenQ(ImmutableGraph revG, VisitBFS visit) {
		performBFSGivenQ(revG, visit);
	}

	public static void performBBFS(ImmutableGraph revgraph, VisitBFS visit) {
		performBFS(revgraph, visit);
	}
}
