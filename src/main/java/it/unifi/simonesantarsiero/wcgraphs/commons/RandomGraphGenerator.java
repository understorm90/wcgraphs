package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import it.unimi.dsi.webgraph.ArcListASCIIGraph;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.EXT_TSV;

public class RandomGraphGenerator {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(RandomGraphGenerator.class);

    //(the number of edges) / (the number of nodes)
    private int nVertices;
    private int mEdges;
    private ArrayList<Pair<Integer, Integer>> edges;

    public static final int DEFAULT_WINDOW_SIZE = 7;
    public static final int DEFAULT_MAX_REF_COUNT = 3;
    public static final int DEFAULT_MIN_INTERVAL_LENGTH = 4;
    public static final int DEFAULT_ZETA_K = 3;

    public RandomGraphGenerator(int vertices) {
        nVertices = vertices;
        mEdges = (int)(Math.random() * (vertices * vertices - 1)) + 1;
        edges = null;

        generate();
    }

    public int getVertices() {
        return nVertices;
    }

    public int getEdges() {
        return mEdges;
    }

    public List<Pair<Integer, Integer>> getListOfEdges() {
        return edges;
    }

    public double getDensity() {
        return (double)mEdges/ nVertices;
    }

    /**
     * Fissato n e un valore m minore di n^2, per m volte ripeti:
     * seleziona due interi x e y casuali tra 0 e n-1;
     * aggiungi l'arco da x a y se non esiste già.
     * Alla fine gli archi vengono ordinati in ordine crescente, partendo dallo 0.
     */
    private void generate() {
        edges = new ArrayList<>();
        int i = 0;
        while(i < mEdges) {
            int currentFirstVertex = getRandomVertex();
            int currentSecondVertex = getRandomVertex();

            if(!existsEdge(edges, currentFirstVertex, currentSecondVertex)) {
                edges.add(new Pair<>(currentFirstVertex, currentSecondVertex));
                i++;
            }
        }
        Collections.sort(edges);
    }

    private int getRandomVertex() {
        return (int) (Math.random() * nVertices);
    }

    private boolean existsEdge(ArrayList<Pair<Integer, Integer>> edges, int v1, int v2) {
        boolean exists = false;
        for (Pair<Integer, Integer> pair : edges) {
            if (pair.getFirst() == v1 && pair.getSecond() == v2) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    public void writeToFileTSV(String name) {
        if (edges == null) {
            return;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(name + EXT_TSV));
            for (Pair<Integer, Integer> pair : edges) {
                bw.write(Integer.toString(pair.getFirst()));
                bw.write("\t");
                bw.write(Integer.toString(pair.getSecond()));
                bw.write("\n");
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }
    }

    @Override
    public String toString() {
        if (edges == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Pair<Integer, Integer> edge : edges) {
            builder.append(edge.getFirst())
                    .append(" ")
                    .append(edge.getSecond())
                    .append("\n");
        }
        return builder.toString();
    }

//    @SuppressWarnings("deprecation")
    public void generateFilesGOP(String source) {
        ImmutableGraph graph;
        try {
            graph = ArcListASCIIGraph.loadSequential(source);

            String dest = source.substring(0, source.length() - 4);
            int flags = 0;
            BVGraph.store( graph, dest, DEFAULT_WINDOW_SIZE, DEFAULT_MAX_REF_COUNT, DEFAULT_MIN_INTERVAL_LENGTH, DEFAULT_ZETA_K, flags, null );

        } catch (IOException e) {
            LOGGER.error("IOException", e);
        }
    }
}
