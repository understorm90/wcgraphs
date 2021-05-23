package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class RandomGraphGenerator {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(RandomGraphGenerator.class);
    public static final String EMPTY_STRING = "";

    private static final int MAX_DENSITY = 100;
    private static final int MAX_VERTICES = 10000;

    private final int nVertices;
    private final int mEdges;
    private ArrayList<Pair<Integer, Integer>> edges;

    public static final int DEFAULT_WINDOW_SIZE = 7;
    public static final int DEFAULT_MAX_REF_COUNT = 3;
    public static final int DEFAULT_MIN_INTERVAL_LENGTH = 4;
    public static final int DEFAULT_ZETA_K = 3;
    private final Random r = new Random();

    public RandomGraphGenerator(int vertices, int edges) {
        nVertices = vertices;
        mEdges = edges;
        this.edges = null;

        generate();
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
        return r.nextInt(nVertices);
    }

    private boolean existsEdge(ArrayList<Pair<Integer, Integer>> edges, int v1, int v2) {
        for (Pair<Integer, Integer> pair : edges) {
            if (pair.getFirst() == v1 && pair.getSecond() == v2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a file (with extension <samp>.tsv</samp>) where each row represents a pair of edges of the graph.
     *
     * @return a String containing the basename of the graph.
     */
    public String writeToFileTSV() {
        if (edges == null) {
            return EMPTY_STRING;
        }
        String basename;
        File dir = new File(RANDOM_GENERATED_DATASETS_PATH);
        if (!dir.exists() && !dir.mkdirs()) {
            basename = EMPTY_STRING;
        } else {
            basename = RANDOM_GENERATED_DATASETS_PATH;
        }

        basename += "random-graph-v" + nVertices + "-e" + mEdges;
        try (
                BufferedWriter bw1 = new BufferedWriter(new FileWriter(basename + EXT_TSV));
                BufferedWriter bw2 = new BufferedWriter(new FileWriter(basename + EXT_ARCS));
        ) {
            for (Pair<Integer, Integer> pair : edges) {
                bw1.write(Integer.toString(pair.getFirst()));
                bw1.write("\t");
                bw1.write(Integer.toString(pair.getSecond()));
                bw1.write("\n");

                bw2.write(Integer.toString(pair.getFirst()));
                bw2.write("\t");
                bw2.write(Integer.toString(pair.getSecond()));
                bw2.write("\n");
            }
            bw1.flush();
            bw2.flush();
            return basename;
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return EMPTY_STRING;
    }

    @Override
    public String toString() {
        if (edges == null) {
            return EMPTY_STRING;
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

    public static void main(String[] args) {
        for (int density = 1; density < MAX_DENSITY; density += 10) {
            for (int nVertices = 1000; nVertices < MAX_VERTICES; nVertices += 1000) {
                int mEdges = density * nVertices;

                RandomGraphGenerator randomGraphGenerator = new RandomGraphGenerator(nVertices, mEdges);
                String basename = randomGraphGenerator.writeToFileTSV();
                DatasetUtils.generateFilesForWebgraph(basename);
            }
        }
    }
}
