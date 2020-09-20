package it.unifi.simonesantarsiero.wcgraphs.akibajava;

import it.unifi.simonesantarsiero.wcgraphs.commons.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GraphDiameter {

	private static final Logger LOGGER = LoggerFactory.getLogger(GraphDiameter.class);

	private static final int NUM_DEFAULT_DOUBLE_SWEEP = 10;
	private int nVertices;
	private int diameter;
	private int numBFS;
	private long time;

	public GraphDiameter() {
		nVertices = 0;
		diameter = 0;
		numBFS = 0;
		time = 0;
	}

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
		int []scc = new int[nVertices];
		int []ord = new int[nVertices];
		int []low = new int[nVertices];
		boolean []in = new boolean[nVertices];
		for(int i = 0; i < nVertices; i++) {
			scc[i] = -1;
			ord[i] = -1;
			low[i] = -1;
			in[i] = false;
		}
		Stack<Integer> s = new Stack<>();
		Stack<Pair<Integer, Integer>> dfs = new Stack<>();

		for (int i = 0; i < nVertices; i++) {
			if (ord[i] != -1) {
				continue;
			}

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
					while (true) {
						int w = s.peek();

						s.pop();
						in[w] = false;
						scc[w] = numScc;

						if (v == w) {
							break;
						}
					}

					numScc++;
				}
			}
		}
		return scc;
	}

	public int getDiameter(List<Pair<Integer, Integer>> edges) {
		return getDiameter(edges, NUM_DEFAULT_DOUBLE_SWEEP);
	}

	public int getDiameter(List<Pair<Integer, Integer>> edges, int numDoubleSweep) {
		// Prepare the adjacency list
		// LinkedList<Integer> graph[];  non è type-safe quando vado a fare la new
		ArrayList<LinkedList<Integer>> graph; // array di linkedlist
		ArrayList<LinkedList<Integer>> rgraph;

		// serve per trovare quanti vertici ha il grafo, per poi dimensionare graph e rgraph
		for (Pair<Integer, Integer> edge : edges) {
			int from = edge.getFirst();
			int to = edge.getSecond();

			nVertices = Math.max(nVertices, from + 1);
			nVertices = Math.max(nVertices, to + 1);
		}

		// resize
		graph = new ArrayList<>(nVertices);
		for (int i = 0; i < nVertices; i++) {
			graph.add(new LinkedList<>());
		}
		rgraph =  new ArrayList<>(nVertices);
		for (int i = 0; i < nVertices; i++) {
			rgraph.add(new LinkedList<>());
		}

		for (Pair<Integer, Integer> edge : edges) {
			int from = edge.getFirst();
			int to = edge.getSecond();

			graph.get(from).add(to);
			rgraph.get(to).add(from);
		}

		// Decompose the graph into strongly connected components
		time = -getTime();
		int []scc = getSCC(graph);

		// Compute the diameter lower bound by the double sweep algorithm
		int qs;
		int qt;
		int []dist = new int[nVertices];
		int []queue = new int[nVertices];
		for(int i = 0; i < nVertices; i++) {
			dist[i] = -1;
			queue[i] = -1;
		}

		for (int i = 0; i < numDoubleSweep; i++) {
			int start = getRandom(); // random selected vertex v

			// forward BFS
			qs = qt = 0;
			dist[start] = 0;
			queue[qt++] = start;

			while (qs < qt) {
				int v = queue[qs++];

				for (int j = 0; j < graph.get(v).size(); j++) {
					if (dist[graph.get(v).get(j)] < 0) {
						dist[graph.get(v).get(j)] = dist[v] + 1;
						queue[qt++] = graph.get(v).get(j);
					}
				}
			}

			for (int j = 0; j < qt; j++) {
				dist[queue[j]] = -1;
			}

			// backward BFS
			start = queue[qt - 1];
			qs = qt = 0;
			dist[start] = 0;
			queue[qt++] = start;

			while (qs < qt) {
				int v = queue[qs++];

				for (int j = 0; j < rgraph.get(v).size(); j++) {
					if (dist[rgraph.get(v).get(j)] < 0) {
						dist[rgraph.get(v).get(j)] = dist[v] + 1;
						queue[qt++] = rgraph.get(v).get(j);
					}
				}
			}

			diameter = Math.max(diameter, dist[queue[qt - 1]]);

			for (int j = 0; j < qt; j++) {
				dist[queue[j]] = -1;
			}

		}

		// Order vertices
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
			Pair<Long, Integer> pl = new Pair<>((long)(scc[v] << 32) - indegree * outdegree, v);
			order.set(v, pl);
		}

		Collections.sort(order);

		// Examine every vertex
		int []ecc = new int[nVertices];
		for(int i = 0; i < nVertices; i++) {
			ecc[i] = nVertices;
		}

		for (int i = 0; i < nVertices; i++) {
			int u = order.get(i).getSecond();

			if (ecc[u] <= diameter) {
				continue;
			}

			// Refine the eccentricity upper bound
			int ub = 0;
			ArrayList<Pair<Integer, Integer>> neighbors = new ArrayList<>();

			for (int j = 0; j < graph.get(u).size(); j++) {
				Pair<Integer, Integer> p = new Pair<>(scc[graph.get(u).get(j)], ecc[graph.get(u).get(j)] + 1);
				neighbors.add(p);
			}

			Collections.sort(neighbors);

			for (int j = 0; j < neighbors.size(); ) {
				int component = neighbors.get(j).getFirst();
				int lb = nVertices;

				for (; j < neighbors.size(); j++) {
					if (neighbors.get(j).getFirst() != component) {
						break;
					}
					lb = Math.min(lb, neighbors.get(j).getSecond());
				}

				ub = Math.max(ub, lb);

				if (ub > diameter) {
					break;
				}
			}

			if (ub <= diameter) {
				ecc[u] = ub;
				continue;
			}

			// Conduct a BFS and update bounds
			numBFS++;
			qs = qt = 0;
			dist[u] = 0;
			queue[qt++] = u;

			while (qs < qt) {
				int v = queue[qs++];

				for (int j = 0; j < graph.get(v).size(); j++) {
					if (dist[graph.get(v).get(j)] < 0) {
						dist[graph.get(v).get(j)] = dist[v] + 1;
						queue[qt++] = graph.get(v).get(j);
					}
				}
			}

			ecc[u] = dist[queue[qt - 1]];
			diameter = Math.max(diameter, ecc[u]);

			for (int j = 0; j < qt; j++) {
				dist[queue[j]] = -1;
			}

			qs = qt = 0;
			dist[u] = 0;
			queue[qt++] = u;

			while (qs < qt) {
				int v = queue[qs++];

				ecc[v] = Math.min(ecc[v], dist[v] + ecc[u]);

				for (int j = 0; j < rgraph.get(v).size(); j++) {
					// only inside an SCC
					if (dist[rgraph.get(v).get(j)] < 0 && scc[rgraph.get(v).get(j)] == scc[u]) {
						dist[rgraph.get(v).get(j)] = dist[v] + 1;
						queue[qt++] = rgraph.get(v).get(j);
					}
				}
			}

			for (int j = 0; j < qt; j++) {
				dist[queue[j]] = -1;
			}

		}

		time += getTime();

		return diameter;
	}

	public int getDiameter(String filename) {
		return getDiameter(filename, NUM_DEFAULT_DOUBLE_SWEEP);
	}

	public int getDiameter(String filename, int numDoubleSweep) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));

			ArrayList<Pair<Integer, Integer>> edges = new ArrayList<>();

			String currentLine;
			String []currentVertices;
			while ((currentLine = br.readLine()) != null) {
				currentVertices = currentLine.split("\t");
				int currentFirstVertex = Integer.parseInt(currentVertices[0]);
				int currentSecondVertex = Integer.parseInt(currentVertices[1]);
				edges.add(new Pair<>(currentFirstVertex, currentSecondVertex));
			}
			br.close();
			return getDiameter(edges, numDoubleSweep);

		} catch (IOException e) {
			LOGGER.error("IOException", e);
		}
		return -1;
	}

	public int getNumVertices() {
		return nVertices;
	}

	public int getNumBFS() {
		return numBFS;
	}

	public double getTimeElapsed() {
		return time / 1000d;
	}
}
