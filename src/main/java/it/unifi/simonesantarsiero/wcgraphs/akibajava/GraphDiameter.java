package it.unifi.simonesantarsiero.wcgraphs.akibajava;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.Pair;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class GraphDiameter {

	private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GraphDiameter.class);

	private static final int NUM_DEFAULT_DOUBLE_SWEEP = 10;
	private int nVertices;
	private int diameter;
	private int numBFS;
	private long time;
	private ArrayList<Pair<Integer, Integer>> edges;

	private long getTime() {
		return System.currentTimeMillis();
	}

	private int getRandom() {
		int x = 123456789;
		int w = 88675123;
		int t;

		t = x ^ (x << 11);
		w = (w ^ (w >> 19)) ^ (t ^ (t >> 8));

		return w % nVertices;
	}

	private int[] getSCC(ArrayList<LinkedList<Integer>> graph) {
		int numVisit = 0;
		int numScc = 0;
		int[] scc = new int[nVertices];
		int[] ord = new int[nVertices];
		int[] low = new int[nVertices];
		boolean[] in = new boolean[nVertices];
		Arrays.fill(scc, -1);
		Arrays.fill(ord, -1);
		Arrays.fill(low, -1);
		Arrays.fill(in, false);

		Deque<Integer> s = new ArrayDeque<>();
		Deque<Pair<Integer, Integer>> dfs = new ArrayDeque<>();

		for (int i = 0; i < nVertices; i++) {

			if (ord[i] == -1) {

				dfs.push(new Pair<>(i, -1));

				while (!dfs.isEmpty()) {
					int v = dfs.peek().getFirst();
					int index = dfs.peek().getSecond();

					dfs.pop();

					if (index == -1) {
						numVisit++;
						ord[v] = numVisit;
						low[v] = numVisit;
						s.push(v);
						in[v] = true;
					} else {
						low[v] = Math.min(low[v], low[graph.get(v).get(index)]);
					}

					for (index++; index < graph.get(v).size(); index++) {
						int w = graph.get(v).get(index);

						if (ord[w] == -1) {
							dfs.push(new Pair<>(v, index));
							dfs.push(new Pair<>(w, -1));
							break;
						} else if (in[w]) {
							low[v] = Math.min(low[v], ord[w]);
						}
					}

					if (index == graph.get(v).size() && low[v] == ord[v]) {

						int w;
						do {
							w = s.peek();

							s.pop();
							in[w] = false;
							scc[w] = numScc;
						} while (v != w);

						numScc++;
					}
				}
			}
		}
		return scc;
	}

	// serve per trovare quanti vertici ha il grafo, per poi dimensionare graph e rgraph
	private int getNumVerticesFromListOfEdges(List<Pair<Integer, Integer>> edges) {
		int vertices = 0;
		for (Pair<Integer, Integer> edge : edges) {
			int from = edge.getFirst();
			int to = edge.getSecond();

			vertices = Math.max(vertices, from + 1);
			vertices = Math.max(vertices, to + 1);
		}
		return vertices;
	}

	private ArrayList<LinkedList<Integer>> resize(int nVertices) {
		ArrayList<LinkedList<Integer>> graph = new ArrayList<>(nVertices);
		for (int i = 0; i < nVertices; i++) {
			graph.add(new LinkedList<>());
		}
		return graph;
	}

	private int calculateUpperBound(List<Pair<Integer, Integer>> neighbors, int nVertices, int diameter) {
		int ub = 0;
		int j = 0;

		while (j < neighbors.size()) {
			int component = neighbors.get(j).getFirst();
			int lb = nVertices;

			while (j < neighbors.size() && neighbors.get(j).getFirst() == component) {
				lb = Math.min(lb, neighbors.get(j).getSecond());
				j++;
			}

			ub = Math.max(ub, lb);

			if (ub > diameter) {
				return ub;
			}
		}
		return ub;
	}

	private int bfs(ArrayList<LinkedList<Integer>> graphOrReversedGraph, int start, int[] dist, int[] queue) {
		int qs;
		int qt;

		qs = qt = 0;
		dist[start] = 0;
		queue[qt++] = start;

		while (qs < qt) {
			int v = queue[qs++];

			for (int j = 0; j < graphOrReversedGraph.get(v).size(); j++) {
				if (dist[graphOrReversedGraph.get(v).get(j)] < 0) {
					dist[graphOrReversedGraph.get(v).get(j)] = dist[v] + 1;
					queue[qt++] = graphOrReversedGraph.get(v).get(j);
				}
			}
		}
		return qt;
	}

	private int bfsWithScc(ArrayList<LinkedList<Integer>> rgraph, int start, int[] dist, int[] queue, int[] scc, int[] ecc) {
		int qt;
		int qs;

		qs = qt = 0;
		dist[start] = 0;
		queue[qt++] = start;

		while (qs < qt) {
			int v = queue[qs++];

			ecc[v] = Math.min(ecc[v], dist[v] + ecc[start]);

			for (int j = 0; j < rgraph.get(v).size(); j++) {
				// only inside an SCC
				if (dist[rgraph.get(v).get(j)] < 0 && scc[rgraph.get(v).get(j)] == scc[start]) {
					dist[rgraph.get(v).get(j)] = dist[v] + 1;
					queue[qt++] = rgraph.get(v).get(j);
				}
			}
		}
		return qt;
	}

	private void cleanDistArray(int qt, int[] dist, int[] queue) {
		for (int j = 0; j < qt; j++) {
			dist[queue[j]] = -1;
		}
	}

	private void doubleSweep(int numDoubleSweep, ArrayList<LinkedList<Integer>> graph, ArrayList<LinkedList<Integer>> rgraph, int[] dist, int[] queue) {
		int qt;
		for (int i = 0; i < numDoubleSweep; i++) {

			// forward BFS
			qt = bfs(graph, getRandom(), dist, queue); // random selected vertex v

			cleanDistArray(qt, dist, queue);

			// backward BFS
			qt = bfs(rgraph, queue[qt - 1], dist, queue);

			diameter = Math.max(diameter, dist[queue[qt - 1]]);

			cleanDistArray(qt, dist, queue);

		}
	}

	private ArrayList<Pair<Long, Integer>> getOrderedVertices(ArrayList<LinkedList<Integer>> graph, ArrayList<LinkedList<Integer>> rgraph, int[] scc) {
		ArrayList<Pair<Long, Integer>> order = new ArrayList<>(Collections.nCopies(nVertices, new Pair<>((long) -1, -1)));

		for (int v = 0; v < nVertices; v++) {
			int indegree = 0;
			int outdegree = 0;

			for (int i = 0; i < rgraph.get(v).size(); i++) {
				if (scc[rgraph.get(v).get(i)] == scc[v]) {
					indegree++;
				}
			}

			for (int i = 0; i < graph.get(v).size(); i++) {
				if (scc[graph.get(v).get(i)] == scc[v]) {
					outdegree++;
				}
			}

			// SCC : reverse topological order
			// inside an SCC : decreasing order of the product of the indegree and outdegree for vertices in the same SCC
			Pair<Long, Integer> pl = new Pair<>((long) (scc[v]) - indegree * outdegree, v);
			order.set(v, pl);
		}

		Collections.sort(order);
		return order;
	}

	public int getDiameter(List<Pair<Integer, Integer>> edges, int numDoubleSweep) {
		// Prepare the adjacency list
		// LinkedList<Integer> graph[];  non è type-safe quando vado a fare la new
		ArrayList<LinkedList<Integer>> graph;
		ArrayList<LinkedList<Integer>> rgraph;

		nVertices = getNumVerticesFromListOfEdges(edges);
		graph = resize(nVertices);
		rgraph = resize(nVertices);

		for (Pair<Integer, Integer> edge : edges) {
			int from = edge.getFirst();
			int to = edge.getSecond();

			graph.get(from).add(to);
			rgraph.get(to).add(from);
		}

		// Decompose the graph into strongly connected components
		time = -getTime();
		int[] scc = getSCC(graph);

		// Compute the diameter lower bound by the double sweep algorithm
		int qt;
		int[] dist = new int[nVertices];
		int[] queue = new int[nVertices];
		for (int i = 0; i < nVertices; i++) {
			dist[i] = -1;
			queue[i] = -1;
		}

		doubleSweep(numDoubleSweep, graph, rgraph, dist, queue);

		// Order vertices
		ArrayList<Pair<Long, Integer>> order = getOrderedVertices(graph, rgraph, scc);

		// Examine every vertex
		int[] ecc = new int[nVertices];
		for (int i = 0; i < nVertices; i++) {
			ecc[i] = nVertices;
		}

		for (int i = 0; i < nVertices; i++) {
			int u = order.get(i).getSecond();

			if (ecc[u] > diameter) {

				// Refine the eccentricity upper bound
				ArrayList<Pair<Integer, Integer>> neighbors = new ArrayList<>();

				for (int j = 0; j < graph.get(u).size(); j++) {
					Pair<Integer, Integer> p = new Pair<>(scc[graph.get(u).get(j)], ecc[graph.get(u).get(j)] + 1);
					neighbors.add(p);
				}

				Collections.sort(neighbors);

				int ub = calculateUpperBound(neighbors, nVertices, diameter);

				if (ub <= diameter) {
					ecc[u] = ub;
				} else {
					// Conduct a BFS and update bounds
					numBFS++;

					qt = bfs(graph, u, dist, queue);

					ecc[u] = dist[queue[qt - 1]];
					diameter = Math.max(diameter, ecc[u]);

					cleanDistArray(qt, dist, queue);

					qt = bfsWithScc(rgraph, u, dist, queue, scc, ecc);

					cleanDistArray(qt, dist, queue);
				}
			}
		}

		time += getTime();

		return diameter;
	}

	public int getDiameter(String filename) {
		return getDiameter(filename, NUM_DEFAULT_DOUBLE_SWEEP);
	}

	public int getDiameter(String filename, int numDoubleSweep) {
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			edges = new ArrayList<>();

			String currentLine;
			String[] currentVertices;
			while ((currentLine = br.readLine()) != null) {
				currentVertices = currentLine.split("\t");
				int currentFirstVertex = Integer.parseInt(currentVertices[0]);
				int currentSecondVertex = Integer.parseInt(currentVertices[1]);
				edges.add(new Pair<>(currentFirstVertex, currentSecondVertex));
			}
			return getDiameter(edges, numDoubleSweep);

		} catch (Exception e) {
			LOGGER.error("Exception", e);
		}
		return -1;
	}

	public int getNumVertices() {
		return nVertices;
	}

	public int getNumEdges() {
		return edges.size();
	}

	public int getNumBFS() {
		return numBFS;
	}

	public double getTimeElapsed() {
		return time / 1000d;
	}
}
