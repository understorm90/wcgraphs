package it.unifi.simonesantarsiero.wcgraphs.webgraph;

import ch.qos.logback.classic.Logger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.LazyIntIterator;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.EXT_TSV;

// Crea file .tsv a partire da file .graph
public class WebGraphDecoder {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(WebGraphDecoder.class);

    public static void main(String[] args) {
        WebGraphDecoder.decode(args[0]);
    }

    public static void decode(String basename) {

        try {
            ImmutableGraph graph = ImmutableGraph.load(basename);
            BufferedWriter bw = new BufferedWriter(new FileWriter(basename + EXT_TSV));

            int nNodes = graph.numNodes();
            LOGGER.info("Vertices: {}\n", nNodes);
            LOGGER.info("Edges: {}\n", graph.numArcs());

            int nEdges = 0;
            for (int v = 0; v < nNodes; ++v) {
                LazyIntIterator successors = graph.successors(v);
                for (int i = 0; i < graph.outdegree(v); ++i) {
                    int w = successors.nextInt();
                    bw.write(Integer.toString(v));
                    bw.write("\t");
                    bw.write(Integer.toString(w));
                    bw.write("\n");
                    ++nEdges;
                }
            }
            bw.flush();
            bw.close();
            LOGGER.info("Output Edges: {}\n", nEdges);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }
    }
}
