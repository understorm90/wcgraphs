package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

/**
 * Visits a graph and sets the maximum eccentricity in the SCC of the starting vertex.
 */
public class DistWithSCC extends Dist {

	private int[] scc;
	protected int eccInSCC;

	public DistWithSCC(int nn, int start, int[] scc) {
		super(nn, start);
		this.scc = scc;
	}

	@Override
	public boolean atVisitedArc(int v, int w) {
		if (distances[w] == -1) {
			distances[w] = distances[v] + 1;
			far = w;
			if (scc[w] == scc[start]) {
				eccInSCC = distances[w];
			}
			return true;
		} else {
			return false;
		}
	}

}
