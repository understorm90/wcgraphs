package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.martiansoftware.jsap.JSAPException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;
import it.unimi.dsi.webgraph.Transform;
import it.unimi.dsi.webgraph.algo.StronglyConnectedComponents;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * This class implements the directed SumSweep algorithm, as explained in the article
 * Borassi et al, Fast Diameter and Radius Computation in Real-World Graphs.
 */
public class NewSumSweepDir {

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger( NewSumSweepDir.class );

	private int[] lF;
	private int[] uF;
	private int[] lB;
	private int[] uB;
	private int[] totDistF;
	private int[] totDistB;
	private boolean[] accRadius;
	private int radius;
	private int diameter;
	private int diametralVertex;
	private int radialVertex;
	private int iterR;
	private int iterD;
	private int iter;
	private ImmutableGraph graph;
	private ImmutableGraph revgraph;
	private ImmutableGraph sccDag;
	private ImmutableGraph revsccDag;
	private StronglyConnectedComponents scc;
	private IntArrayList sumSweepResults;
	private int[] ccBonus;
	private IntArrayList[] edgesThroughSCCF;
	private IntArrayList[] edgesThroughSCCB;

	private int stillToDo;
	private int lastImprovement = 0;

	/**
	 * @return the radius computed
	 */
	public int getR() {return radius;}
	/**
	 * @return the diameter of the graph
	 */
	public int getD() {return diameter;}
	/**
	 * @return the number of iterations to compute the radius
	 */
	public int getIterR() {return iterR;}
	/**
	 * @return the number of iterations to compute the diameter
	 */
	public int getIterD() {return iterD;}
	/**
	 * @return a diametral vertex
	 */
	public int getDv() {return diametralVertex;}

	/**
	 * @return a radial vertex
	 */
	public int getRv() {return radialVertex;}

	private void calculateEdgesThroughSCCF(int[] arcs) {
		int v = 0;
		int w;
		LazyIntIterator iterator;
		edgesThroughSCCF = new IntArrayList[scc.numberOfComponents];

		for (int i = 0; i < scc.numberOfComponents; i++) {

			edgesThroughSCCF[i] = new IntArrayList();
			while (v < graph.numNodes() && scc.component[v] == i) {
				iterator = graph.successors(v);
				while ((w = iterator.nextInt()) != -1 ) {

					if (scc.component[w] != scc.component[v] && (arcs[2 * scc.component[w]] == -1 || (revgraph.outdegree(v) + graph.outdegree(w)
					> revgraph.outdegree(arcs[2 * scc.component[w]]) + graph.outdegree(arcs[2 * scc.component[w] + 1])))) {

						arcs[2 * scc.component[w]] = v;
						arcs[2 * scc.component[w] + 1] = w;
					}
				}
				v++;
			}
			iterator = sccDag.successors(i);
			while ((w = iterator.nextInt()) != -1 ) {
				edgesThroughSCCF[i].add(arcs[2 * w]);
				edgesThroughSCCF[i].add(arcs[2 * w + 1]);
				arcs[2 * w] = -1;
				arcs[2 * w + 1] = -1;
			}
		}
	}

	private void calculateEdgesThroughSCCB(int[] arcs) {
		int v = 0;
		int w;
		LazyIntIterator iterator;
		edgesThroughSCCB = new IntArrayList[scc.numberOfComponents];

		v = 0;
		for (int i = 0; i < scc.numberOfComponents; i++) {
			edgesThroughSCCB[i] = new IntArrayList();
			while (v < graph.numNodes() && scc.component[v] == i) {
				iterator = revgraph.successors(v);
				while ((w = iterator.nextInt()) != -1 ) {
					if (scc.component[w] != scc.component[v] && (arcs[2 * scc.component[w]] == -1 || (graph.outdegree(v) + revgraph.outdegree(w)
					> graph.outdegree(arcs[2 * scc.component[w]]) + revgraph.outdegree(arcs[2 * scc.component[w] + 1])))) {
						arcs[2 * scc.component[w]] = v;
						arcs[2 * scc.component[w] + 1] = w;
					}
				}
				v++;
			}
			iterator = revsccDag.successors(i);
			while ((w = iterator.nextInt()) != -1 ) {
				edgesThroughSCCB[i].add(arcs[2 * w]);
				edgesThroughSCCB[i].add(arcs[2 * w + 1]);
				arcs[2 * w] = -1;
				arcs[2 * w + 1] = -1;
			}
		}
	}

	/**
	 * Fills the variables edgesThroughSCCF and edgesThroughSCCB by storing an edge in the graph for each pair of connected SCCs
	 */
	private void findEdgesThroughSCC() {

		int[] arcs = new int[2 * scc.numberOfComponents];
		Arrays.fill(arcs, -1);

		calculateEdgesThroughSCCF(arcs);
		calculateEdgesThroughSCCB(arcs);
	}

	/**
	 * Instantiates this object using the biggest weakly connected component of the given graph.
	 * @param g the input graph.
	 */
	public NewSumSweepDir(ImmutableGraph g) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IOException, JSAPException {
		graph = g;
		LOGGER.trace("Size: {} nodes, {} edges.", graph.numNodes(), graph.numArcs());
		graph = NewImmutableGraph.transformIntoBiggestWCC(graph, "_test");
		revgraph = Transform.transpose(graph);
		LOGGER.trace("Main WCC size: {} nodes, {} edges.", graph.numNodes(), graph.numArcs());

		// findScc
		scc = StronglyConnectedComponents.compute(graph, false, null);

		// CollapseVertices
		int[] newNumbers = NewImmutableGraph.getNewNumbers(scc.component, scc.numberOfComponents);
		IntArrayList[] newAdj = NewImmutableGraph.permuteVertices(graph, newNumbers);
		graph = NewImmutableGraph.createNewGraph(newAdj);
		sccDag = NewImmutableGraph.collapseVertices(scc.component, scc.numberOfComponents, newNumbers, graph);

		// findScc
		scc = StronglyConnectedComponents.compute(graph, false, null);

		revsccDag = Transform.transpose(sccDag);
		revgraph = Transform.transpose(graph);
		computeAccRadius(graph);

		totDistF = new int[graph.numNodes()];
		totDistB  = new int[graph.numNodes()];
		lF = new int[graph.numNodes()];
		lB = new int[graph.numNodes()];
		uF = new int[graph.numNodes()];
		uB = new int[graph.numNodes()];
		Arrays.fill(uF, graph.numNodes() + 1);
		Arrays.fill(uB, graph.numNodes() + 1);

		LOGGER.debug("Strongly connected components: {}.", scc.numberOfComponents);

		iter = 0;
		iterR = -1;
		iterD = -1;
		radius = graph.numNodes();
		stillToDo = graph.numNodes();
		ccBonus = new int[scc.computeSizes().length];
		findEdgesThroughSCC();
	}

	// trova maxSizeCC
	private int getMaxSizeCC(int[] size) {
		int max = 0;
		int maxSizeCC = 0;
		for (int i = 0; i < size.length; i++) {
			if (size[i] > max) {
				max = size[i];
				maxSizeCC = i;
			}
		}
		return maxSizeCC;
	}

	/**
	 * Fills the array accRadius with all vertices that are candidates to be radial vertices
	 */
	public void computeAccRadius(ImmutableGraph myGraph) {

		IntArrayList vertInMaxSizeCC = new IntArrayList();

		final int[] size = scc.computeSizes();
		int maxSizeCC = getMaxSizeCC(size);

		for (int v = 0; v < myGraph.numNodes(); v++) {
			if (size[scc.component[v]] == size[maxSizeCC]) {
				vertInMaxSizeCC.add(v);
			}
		}

		ConnBFS visit = new ConnBFS(myGraph.numNodes(), vertInMaxSizeCC.toIntArray());

		NewImmutableGraph.performBBFS(Transform.transpose(myGraph), visit);
		accRadius = visit.visited;
	}

	/**
	 * Prints some data
	 * @param visitF
	 * @param visitB
	 */
	public void printVisitData(Dist visitF, Dist visitB) {

		int visitedF = 0;
		int visitedB = 0;
		int visitedA = 0;
		for (int v = 0; v < graph.numNodes(); v++) {
			if (visitF.distances[v] >= 0) {
				visitedF++;
			}
			if (visitB.distances[v] >= 0) {
				visitedB++;
			}
			if (visitF.distances[v] >= 0 && visitB.distances[v] >= 0) {
				visitedA++;
			}
		}
		LOGGER.trace("(visited F: {}, visited B: {}, always visited: {}).\n", visitedF, visitedB, visitedA);
	}

	/**
	 * Prints some data computed during the SumSweep.
	 */
	public void printData() {
		int toDoUntilR = 0;
		int toDoUntilDF = 0;
		int toDoUntilDB = 0;

		for (int i = 0; i < graph.numNodes(); i++) {
			if (lF[i] < radius && totDistF[i] >= 0 && accRadius[i]) {
				toDoUntilR++;
			}
			if (uF[i] > diameter && totDistF[i] >= 0) {
				toDoUntilDF++;
			}
			if (uB[i] > diameter && totDistB[i] >= 0) {
				toDoUntilDB++;
			}
		}
		if (iterR == -1) {
			LOGGER.trace("    Approximated radius: {} (still to do: {})\n", radius, toDoUntilR);
		} else {
			LOGGER.trace("    Radius: {}\n", radius);
		}
		if (iterD == -1) {
			LOGGER.trace("    Approximated diameter: {} (still to do F: {}, still to do B: {})\n", diameter, toDoUntilDF, toDoUntilDB);
		} else {
			LOGGER.trace("    Diameter: {}\n", diameter);
		}

		int currentValue = Math.min(toDoUntilDF, toDoUntilDB) + toDoUntilR;
		lastImprovement = stillToDo - currentValue;
		stillToDo = currentValue;
	}

	/**
	 * Updates, if necessary, the approximated values of R and D, after a forward eccentricity has been computed.
	 * @param v the vertex of which the forward eccentricity is computed.
	 */
	private void checkNewEccF(int v) {

		if (lF[v] < radius && accRadius[v]) {
			radius = lF[v];
			radialVertex = v;
		}
		if (lF[v] > diameter) {
			diameter = lF[v];
			diametralVertex = v;
		}
	}

	/**
	 * Updates, if necessary, the approximated values of R and D, after a backward eccentricity has been computed.
	 * @param v the vertex of which the forward eccentricity is computed.
	 */
	private void checkNewEccB(int v) {
		if (lB[v] > diameter) {
			diameter = lB[v];
			diametralVertex = v;
		}
	}

	/**
	 * Finds the best pivot vertices for a SingleCCUpperBound
	 * @return an array containing in position i the pivot of component i
	 */
	private int[] findBestPivot() {
		int[] pivot = new int[scc.numberOfComponents];
		int j = 0;
		long currentBest;
		long current;

		for (int i = 0; i < pivot.length; i++) {
			currentBest = 4 * (long)graph.numNodes();
			while (j < graph.numNodes() && scc.component[j] == i) {
				current = (lF[j] + lB[j]);

				if (totDistF[j] < 0) {
					current += graph.numNodes();
				}
				if (totDistB[j] < 0) {
					current += graph.numNodes();
				}

				if (currentBest > current || (currentBest == current && totDistF[j] + totDistB[j] < totDistF[pivot[i]] + totDistB[pivot[i]])) {
					currentBest = current;
					pivot[i] = j;
				}
				j++;
			}
		}
		return pivot;
	}


	/**
	 * Finds the best vertex to run a stepSumSweepBoth (that is, the vertex such that lF(i) + lB(i) is minimum).
	 * @return the best vertex to perform a stepSumSweepBoth
	 */
	private int findPivotForSingleCCDiam(int[] pivot) {
		int[] sccBonus = computeSCCBonus();
		return pivot[Utilities.argMax(sccBonus, sccBonus)];
	}

	/** Computes a step of the SumSweep, performing a backward BFS. Updates the bounds.
	 * @param start the starting vertex
	 */
	private void stepSumSweepBackward(int start) {

		if (start == -1) {
			return;
		}

		LOGGER.trace("Using backward BFS from {} ", start);

		Dist visit = new Dist(graph.numNodes(), start);
		int visited = 0;

		totDistB[start] = -2;
		NewImmutableGraph.performBBFS(revgraph, visit);

		for (int v = 0; v < graph.numNodes(); v++) {
			if (visit.distances[v] >= 0) {
				visited++;
			}
		}
		LOGGER.trace("(visited: {}).\n", visited);

		int eccB;

		iter++;
		eccB = visit.distances[visit.far];

		lB[start] = eccB;
		uB[start] = eccB;

		checkNewEccB(start);

		for (int i = 0; i < graph.numNodes(); i++) {
			if (totDistF[i] >= 0) {

				lF[i] = Math.max(lF[i], visit.distances[i]);

				if (visit.distances[i] != -1) {
					totDistF[i] += visit.distances[i];
				}
			}
		}
	}

	/**
	 * Check if the lower and upper bounds computed are equal for some vertices. Updates eccentricity if necessary.
	 */
	private void checkNewBounds() {
		for (int i = 0; i < graph.numNodes(); i++) {
			if (totDistF[i] >= 0 && lF[i] == uF[i]) {
				totDistF[i] = -1;
				checkNewEccF(i);
			}
			if (totDistB[i] >= 0 && lB[i] == uB[i]) {
				totDistB[i] = -1;
				checkNewEccB(i);
			}
		}
	}

	/** Computes a step of the SumSweep, performing a forward BFS. Updates the bounds.
	 * @param start the starting vertex
	 */
	private void stepSumSweepForward(int start) {
		LOGGER.trace("Using forward BFS from {} ", start);

		if (start == -1) {
			return;
		}

		Dist visit = new Dist(graph.numNodes(), start);
		int visited = 0;

		totDistF[start] = -2;
		NewImmutableGraph.performBFS(graph, visit);

		for (int v = 0; v < graph.numNodes(); v++) {
			if (visit.distances[v] >= 0) {
				visited++;
			}
		}

		LOGGER.trace("(visited: {}).\n", visited);
		int eccF;

		iter++;
		eccF = visit.distances[visit.far];

		lF[start] = eccF;
		uF[start] = eccF;

		totDistF[start] = -1;

		checkNewEccF(start);

		for (int i = 0; i < graph.numNodes(); i++) {

			if (totDistB[i] >= 0) {

				lB[i] = Math.max(lB[i], visit.distances[i]);

				if (visit.distances[i] != -1) {
					totDistB[i] += visit.distances[i];
				}
			}
		}
	}

	/**
	 * Performs a BFS and updates all required bounds
	 *
	 * @param start   the starting vertex
	 * @param forward if true, the BFS is performed forward.
	 * @return a DistWithSCC containing all necessary information.
	 */
	private DistWithSCC singleBFS(int start, boolean forward) {
		DistWithSCC visit = new DistWithSCC(graph.numNodes(), start, scc.component);
		int ecc;
		if (forward) {
			NewImmutableGraph.performBFS(graph, visit);
			ecc = visit.distances[visit.far];

			totDistF[start] = -2;
			lF[start] = ecc;
			uF[start] = ecc;
			checkNewEccF(start);
		} else {
			NewImmutableGraph.performBBFS(revgraph, visit);
			ecc = visit.distances[visit.far];

			totDistB[start] = -2;
			lB[start] = ecc;
			uB[start] = ecc;
			checkNewEccB(start);
		}
		return visit;
	}

	/**
	 * Upper bounds the eccentricity of all pivot vertices
	 *
	 * @param visitDistF
	 * @param visitDistB
	 * @param visitInSCCF
	 * @param visitInSCCB
	 * @param pivot
	 * @return an array containing in position 2*i an upper bound on the forward eccentricity of the pivot of the SCC i,
	 * in position 2*i+1 an upper bound on the backward eccentricity of the same pivot.
	 */
	private int[] findEccPivot(Dist visitDistF, Dist visitDistB, DistMultipleInSCC visitInSCCF, DistMultipleInSCC visitInSCCB, int[] pivot) {

		int[] ecc = new int[2 * sccDag.numNodes()];
		int[] eccFalse = new int[2 * sccDag.numNodes()];
		int w;
		int j;
		LazyIntIterator iterator;

		for (int v = 0; v < sccDag.numNodes(); v++) {

			if (visitDistF.distances[pivot[v]] != -1) {
				eccFalse[2 * v] = 0;
			} else {
				eccFalse[2 * v] = visitInSCCF.eccStart[v];
				iterator = sccDag.successors(v);
				for (j = 0; j < sccDag.outdegree(v); j++) {
					w = iterator.nextInt();
					if (visitDistF.distances[pivot[w]] == -1) {
						int temp1 = eccFalse[2 * v];
						int temp2 = eccFalse[2 * w];
						int temp3 = edgesThroughSCCF[v].getInt(2 * j);
						int temp4 = edgesThroughSCCF[v].getInt(2 * j + 1);
						eccFalse[2 * v] = Math.max(temp1, temp2 + visitInSCCF.dist[temp3] + 1 + visitInSCCB.dist[temp4]);
					}
				}
			}
			if (visitDistB.distances[pivot[v]] == -1) {
				ecc[2 * v] = visitInSCCF.eccStart[v];
				iterator = sccDag.successors(v);

				for (j = 0; j < sccDag.outdegree(v); j++) {
					w = iterator.nextInt();

					ecc[2 * v] = Math.max(ecc[2 * v], ecc[2 * w] + visitInSCCF.dist[edgesThroughSCCF[v].getInt(2 * j)] + 1 + visitInSCCB.dist[edgesThroughSCCF[v].getInt(2 * j + 1)]);
				}
			} else {

				ecc[2 * v] = Math.max(visitDistB.distances[pivot[v]] + visitDistF.distances[visitDistF.far], visitInSCCF.eccStart[v]);
				iterator = sccDag.successors(v);
				for (j = 0; j < sccDag.outdegree(v); j++) {
					w = iterator.nextInt();
					if (visitDistF.distances[pivot[w]] == -1) {
						int temp1 = ecc[2 * v];
						int temp2 = eccFalse[2 * w];
						int temp3 = edgesThroughSCCF[v].getInt(2 * j);
						int temp4 = edgesThroughSCCF[v].getInt(2 * j + 1);
						int temp5 = visitInSCCF.dist[temp3];
						int temp6 = Math.max(temp1, temp2 + temp5 + 1 + visitInSCCB.dist[temp4]);
						ecc[2 * v] = temp6;
					}
				}
			}
			ecc[2 * v] = Math.min(ecc[2 * v], uF[pivot[v]]);
			eccFalse[2 * v] = Math.min(eccFalse[2 * v], uF[pivot[v]]);
		}

		for (int v = sccDag.numNodes() - 1; v >= 0; v--) {
			if (visitDistB.distances[pivot[v]] != -1) {
				eccFalse[2 * v + 1] = 0;
			} else {
				eccFalse[2 * v + 1] = visitInSCCB.eccStart[v];
				iterator = revsccDag.successors(v);
				for (j = 0; j < revsccDag.outdegree(v); j++) {
					w = iterator.nextInt();
					if (visitDistB.distances[pivot[w]] == -1) {
						eccFalse[2 * v + 1] = Math.max(eccFalse[2 * v + 1], eccFalse[2 * w + 1] + visitInSCCF.dist[edgesThroughSCCB[v].getInt(2 * j)] + 1 + visitInSCCB.dist[edgesThroughSCCB[v].getInt(2 * j + 1)]);
					}
				}
			}
			if (visitDistF.distances[pivot[v]] == -1) {

				ecc[2 * v + 1] = visitInSCCB.eccStart[v];
				iterator = revsccDag.successors(v);

				for (j = 0; j < revsccDag.outdegree(v); j++) {
					w = iterator.nextInt();
					ecc[2 * v + 1] = Math.max(ecc[2 * v + 1], ecc[2 * w + 1] + visitInSCCB.dist[edgesThroughSCCB[v].getInt(2 * j)] + 1 + visitInSCCF.dist[edgesThroughSCCB[v].getInt(2 * j + 1)]);
				}
			} else {
				ecc[2 * v + 1] = Math.max(visitDistF.distances[pivot[v]] + visitDistB.distances[visitDistB.far], visitInSCCB.eccStart[v]);
				iterator = revsccDag.successors(v);
				for (j = 0; j < revsccDag.outdegree(v); j++) {
					w = iterator.nextInt();
					if (visitDistF.distances[pivot[w]] == -1) {
						ecc[2 * v + 1] = Math.max(ecc[2 * v + 1], eccFalse[2 * w + 1] + visitInSCCB.dist[edgesThroughSCCB[v].getInt(2 * j)] + 1 + visitInSCCF.dist[edgesThroughSCCB[v].getInt(2 * j + 1)]);
					}
				}
			}
			ecc[2 * v + 1] = Math.min(ecc[2 * v + 1], uB[pivot[v]]);
			eccFalse[2 * v + 1] = Math.min(eccFalse[2 * v + 1], uB[pivot[v]]);
		}
		return ecc;
	}

	/**
	 * Performs a step using the singleCCUpperBound technique.
	 */
	private void singleCCUpperBound() {
		int[] mScc = scc.component;
		int[] pivot = findBestPivot();
		int start = findPivotForSingleCCDiam(pivot);

		if (start == -1) {
			return;
		}

		LOGGER.trace("Using all CC upper bound from {} ", start);

		DistWithSCC visitDistF = singleBFS(start, true);
		DistWithSCC visitDistB = singleBFS(start, false);

		printVisitData(visitDistF, visitDistB);

		DistMultipleInSCC visitInSCCF = new DistMultipleInSCC(graph.numNodes(), mScc);
		visitInSCCF.run(graph, revgraph, pivot, start, visitDistF.eccInSCC, true);
		DistMultipleInSCC visitInSCCB = new DistMultipleInSCC(graph.numNodes(), mScc);
		visitInSCCB.run(graph, revgraph, pivot, start, visitDistB.eccInSCC, false);

		int[] eccPivot = findEccPivot(visitDistF, visitDistB, visitInSCCF, visitInSCCB, pivot);

		for (int v = 0; v < graph.numNodes(); v++) {

			if (visitDistF.distances[v] >= 0 && visitDistB.distances[v] >= 0) {
				uF[v] = Math.min(uF[v], eccPivot[2 * mScc[v]] + visitDistB.distances[v]);
				uB[v] = Math.min(uB[v], eccPivot[2 * mScc[v] + 1] + visitDistF.distances[v]);
			} else {
				uF[v] = Math.min(uF[v], eccPivot[2 * mScc[v]] + visitInSCCB.dist[v]);
				uB[v] = Math.min(uB[v], eccPivot[2 * mScc[v] + 1] + visitInSCCF.dist[v]);
			}
		}

		if (scc.numberOfComponents == 1) {
			iter += 2;
		} else {
			iter += 3;
		}
	}

	/**
	 * Runs the SumSweep with the best parameters, according to our experiments.
	 */
	public void runAuto() {
		run(NewImmutableGraph.maxOutDegVert(graph), 6);
	}

	/**
	 * @return the results obtained during the initial SumSweep. In position i, there is the diameter approximation after i steps.
	 */
	public IntArrayList getSumSweepResults() {
		return sumSweepResults;
	}

	/**
	 * Perform some steps of the SumSweep heuristic.
	 * @param start the starting vertex of the first BFS.
	 * @param initialSumSweepIter the number of steps performed.
	 */
	public void sumSweep(int start, int initialSumSweepIter) {
		String bfsComplete = "BFS {} complete!\n";

		initialSumSweepIter = Math.min(initialSumSweepIter, graph.numNodes());
		stepSumSweepForward(start);
		LOGGER.trace(bfsComplete, iter);

		sumSweepResults = new IntArrayList();
		sumSweepResults.add(diameter);

		for (int i = 1; i < initialSumSweepIter; i++) {
			if (i % 2 == 0) {
				stepSumSweepForward(Utilities.argMax(totDistF, lF));
			} else {
				stepSumSweepBackward(Utilities.argMax(totDistB, lB));
			}
			sumSweepResults.add(diameter);
			LOGGER.trace(bfsComplete, iter);
		}

		int[] lFRad = new int[graph.numNodes()];
		for (int k = 0; k < graph.numNodes(); k++) {
			if (accRadius[k]) {
				lFRad[k] = lF[k];
			} else {
				lFRad[k] = -1;
			}
		}
		stepSumSweepForward(Utilities.argMin(totDistF, lFRad));
		LOGGER.trace(bfsComplete, iter);
	}

	/**
	 * @return an array containing, in position i, the number of open vertices in component i.
	 */
	private int[] computeSCCBonus() {
		int stillToDoF = 0;
		int stillToDoB = 0;
		int[] toReturn = new int[scc.numberOfComponents];

		for (int x = 0; x < ccBonus.length; x++) {
			toReturn[x] = 0;
		}

		for (int x = 0; x < graph.numNodes(); x++) {
			if (uF[x] > diameter) {
				stillToDoF++;
			} else if (uB[x] > diameter) {
				stillToDoB++;
			}
		}

		if (stillToDoF < stillToDoB) {

			for (int x = 0; x < graph.numNodes(); x++) {
				if ((lF[x] < radius && accRadius[x]) || uF[x] > diameter) {
					toReturn[scc.component[x]]++;
				}
			}
		} else {
			for (int x = 0; x < graph.numNodes(); x++) {
				if ((lF[x] < radius && accRadius[x]) || uB[x] > diameter) {
					toReturn[scc.component[x]]++;
				}
			}
		}
		return toReturn;
	}

	/**
	 * Computes the diameter and the radius of a graph with the SumSweep technique. To do so, it performs some steps
	 * of the SumSweep heuristic, then it starts bounding the eccentricities of the remaining vertices. In the meantime,
	 * it keeps a lower bound on the diameter and an upper bound on the radius. As soon as the lower bound on the diameter
	 * is as high as the biggest upper bound on the eccentricity, the diameter is found. Similarly, the radius is found
	 * when the upper bound on the radius is as low as the smallest lower bound on the eccentricity.
	 * The results are stored in the variables R and D, and they can be obtained using getR() and getD().
	 * @param start the starting vertex for the first BFS of the SumSweep heuristic
	 * @param initialSumSweepIter the number of iterations of the first SumSweep heuristic.
	 */
	public void run(int start, int initialSumSweepIter) {

		int v;
		int w1;
		int w2;
		int i;
		int[] cost = {1, 1, 1, 1, 1};
		double[] points = new double[5];

		LOGGER.trace("Initial SumSweep started!\n");
		sumSweep(start, initialSumSweepIter);

		Arrays.fill(points, graph.numNodes());

		printData();
		while (true) {

			int[] lFRad = new int[graph.numNodes()];
			for (int x = 0; x < graph.numNodes(); x++) {
				if (accRadius[x]) {
					lFRad[x] = lF[x];
				} else {
					lFRad[x] = -1;
				}
			}

			w1 = Utilities.argMax(uF, totDistF);
			w2 = Utilities.argMax(uB, totDistB);
			v = Utilities.argMin(lFRad, totDistF);

			if (iterR == -1 && (v == -1 || lF[v] >= radius)) {
				iterR = iter;
			}
			if (iterD == -1 && (w1 == -1 || uF[w1] <= diameter || w2 == -1 || uB[w2] <= diameter)) {
				iterD = iter;
			}
			if (iterD != -1 && iterR != -1) {
				break;
			}
			if (LOGGER.isTraceEnabled()) {
				DecimalFormat df = new DecimalFormat("0.00");
				LOGGER.trace("BFS {}: {} {} {} {} {}. ", iter, df.format(points[0]), df.format(points[1]), df.format(points[2]), df.format(points[3]), df.format(points[4]));
			}

			if (iterD != -1) {
				points[0] = -100;
				points[1] = -100;
			}
			if (iterR != -1) {
				points[2] = -100;
			}

			i = Utilities.argMax(points);

			switch (i) {
				case 0: // ONLY DIAM
					singleCCUpperBound();
					break;
				case 1: // ONLY DIAM
					stepSumSweepForward(w1);
					break;
				case 2: // ONLY RAD
					stepSumSweepForward(v);
					break;
				case 3: // BOTH
					stepSumSweepBackward(w2);
					break;
				case 4:
					stepSumSweepBackward(Utilities.argMax(totDistB, uB));
				break;
			}
			checkNewBounds();

			printData();
			points[i] = ((double)lastImprovement / cost[i]);
			for (int j = 1; j < points.length; j++) {
				points[(i + j) % points.length] = points[(i + j) % points.length] + 2.0 / iter;
			}
		}
		LOGGER.debug("Radius:   {} ({} iterations).\n", getR(), getIterR());
		LOGGER.debug("Diameter: {} ({} iterations).\n", getD(), getIterD());
	}

	public int getlF(int i) {return lF[i];}
	public int getuF(int i) {return uF[i];}
	public int getlB(int i) {return lB[i];}
	public int getuB(int i) {return uB[i];}

	public ImmutableGraph getGraph() { return graph; }
	public ImmutableGraph getRevGraph() { return revgraph; }
	public ImmutableGraph getSccDag() { return sccDag; }
	public ImmutableGraph getRevSccDag() { return revsccDag; }

	public IntArrayList[] getEdgesThroughSCCF() { return edgesThroughSCCF; }
	public StronglyConnectedComponents getScc() { return scc; }

	public static void disableLogger() {
		LOGGER.setLevel(Level.toLevel("error"));
	}
}
