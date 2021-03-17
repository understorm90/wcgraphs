package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import it.unimi.dsi.webgraph.ImmutableGraph;
import visit.DistMultipleInSCC;

public class NewDistMultipleInSCC extends DistMultipleInSCC {
    public NewDistMultipleInSCC(int nn, int[] scc) {
        super(nn, scc);
    }

    public void run(ImmutableGraph g, ImmutableGraph revG, int[] pivot, int toAvoid, int eccToAvoid, boolean forward) {
        for (int v : pivot) {
            if (scc[v] != scc[toAvoid]) {
                start = v;
                if (forward) {
                    NewImmutableGraph.performBFSGivenQ(g, this);
                } else {
                    NewImmutableGraph.performBBFSGivenQ(revG, this);
                }
            } else {
                eccStart[scc[toAvoid]] = eccToAvoid;
            }
        }
    }
}
