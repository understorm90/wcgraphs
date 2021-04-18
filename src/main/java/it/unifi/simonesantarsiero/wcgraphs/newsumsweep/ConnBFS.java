package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import java.util.Arrays;

/**
 * This class stores what vertices are visited by a BFS.
 */
public class ConnBFS implements VisitBFS {

	protected boolean[] visited;
	private int[] start;

	public ConnBFS(int nn, int[] start) {
		visited = new boolean[nn];
		this.start = start;
	}

	@Override
	public boolean atVisitedArc(int v, int w) {
		if (visited[w]) {
			return false;
		} else {
			visited[w] = true;
			return true;
		}
	}

	@Override
	public int[] atStartVisit() {
		Arrays.fill(visited, false);
		for (int v : start) {
			visited[v] = true;
		}
		return start;
	}

	@Override
	public void atEndVisit() {
		// not implemented
	}
}
