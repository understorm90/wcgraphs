package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import java.util.Arrays;

/**
 * This class is used to find all distances from a given vertex v, stored in variable dist. If dist[i] = -1, it means that v and i
 * are not connected.
 */
public class Dist implements VisitBFS {

	protected int[] distances;
	protected int far;
	protected int start;

	public Dist(int nn, int start) {
		distances = new int[nn];
		this.start = start;
	}

	@Override
	public boolean atVisitedArc(int v, int w) {
		if (distances[w] == -1) {
			distances[w] = distances[v] + 1;
			far = w;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int[] atStartVisit() {
		Arrays.fill(distances, -1);
		distances[start] = 0;
		far = start;
		return new int[]{start};
	}

	@Override
	public void atEndVisit() {
		// not implemented
	}
}
