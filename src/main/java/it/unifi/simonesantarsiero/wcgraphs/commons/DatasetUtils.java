package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import it.unimi.dsi.webgraph.ArcListASCIIGraph;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.RandomGraphGenerator.*;
import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

/**
 * Un dataset scaricato da fonti come ad esempio SNAP, per essere utilizzato correttamente nel progetto,
 * ha bisogno di essere "pulito". I requisiti sono:
 * <p>
 * - il file non deve contenere commenti;
 * - ogni riga deve contenere solo coppie di vertici (archi) separati da spazi/tab;
 * - le righe sono ordinate in ordine crescente;
 * - il nodo più piccolo deve partire da 0;
 * - file .tsv (necessario per algoritmo di Akiba et al.);
 * - file .arcs (necessario per creare i file successivi);
 * - file .graph, .offsets, .properties (necessari per WebGraph e gli altri algoritmi).
 */
public class DatasetUtils {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DatasetUtils.class);
    private static final String EXT_TXT = ".txt";
    private static final String EDGE_SEPARATOR = "\t";
    private static final String LINE_SEPARATOR = System.lineSeparator();

    public static void main(String[] args) {
        new DatasetUtils();
    }

    public DatasetUtils() {
        List<String> list = new ArrayList<>(getPathsOfGraphsAvailableInDirectory(
                System.getProperty("user.dir") + FILE_SEPARATOR + DATASETS_PATH, EXT_TXT));

        for (String file : list) {
            LOGGER.info("{}\n", file);
            List<String> edges = readFile(file + EXT_TXT);
            java.util.Comparator<String> compareByFirstNode =
                    java.util.Comparator.comparing(node -> Integer.parseInt(node.split(EDGE_SEPARATOR)[0]));
            edges.sort(compareByFirstNode);
            writeFile(file + EXT_ARCS, String.join(LINE_SEPARATOR, edges));
            writeFile(file + EXT_TSV, String.join(LINE_SEPARATOR, edges));
            generateFilesForWebgraph(file);
        }
    }

    /**
     * Creates 3 files (.graph, .offsets, .properties) required for WebGraph algorithm.
     *
     * @param basename the basename of the graph.
     */
    public static void generateFilesForWebgraph(String basename) {
        ImmutableGraph graph;
        try {
            graph = ArcListASCIIGraph.loadSequential(basename + EXT_ARCS);
            BVGraph.store(graph,
                    basename,
                    DEFAULT_WINDOW_SIZE,
                    DEFAULT_MAX_REF_COUNT,
                    DEFAULT_MIN_INTERVAL_LENGTH,
                    DEFAULT_ZETA_K,
                    0,
                    null);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }

    private List<String> readFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return readAllLines(reader);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private void writeFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> readAllLines(BufferedReader reader) throws IOException {
        List<String> content = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            if (isValid(line)) {
                String[] edges = line.split("\\s+"); // any number of white spaces
                content.add(edges[0] + EDGE_SEPARATOR + edges[1]);
            }
        }
        return content;
    }

    // removing commented lines
    private boolean isValid(String line) {
        return !line.contains("#");
    }
}
