package it.unifi.simonesantarsiero.wcgraphs.webgraph;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.Algorithm;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.algo.SumSweepDirectedDiameterRadius;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class WebGraph implements Algorithm {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(WebGraph.class);

    private Map<String, Object> mapResult;

    public static void main(String[] args) {
        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, WebGraph.class.getCanonicalName());
                return;
            }
            new WebGraph(args[0], true);
        } else {
            new WebGraph("", false);
        }
    }

    public WebGraph(String datasetPath, boolean runningFromTerminal) {
        String workingDirectory = System.getProperty("user.dir");
        String datasetsPath;
        List<String> list;

        if (runningFromTerminal) {
            datasetsPath = workingDirectory + SLASH;

            list = new ArrayList<>();
            list.add(datasetPath);
        } else {
            datasetsPath = workingDirectory + DATASETS_PATH;

            list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_GRAPH);
        }

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            loader.printFilename(filename);

            ImmutableGraph graph;
            try {
                graph = ImmutableGraph.load(datasetsPath + filename);

                long time = -System.currentTimeMillis();

                final SumSweepDirectedDiameterRadius ss = new SumSweepDirectedDiameterRadius(graph, SumSweepDirectedDiameterRadius.OutputLevel.ALL, null, null);
                ss.compute();

                time += System.currentTimeMillis();

                mapResult = new HashMap<>();
                mapResult.put(VALUE_DATASET, filename);
                mapResult.put(VALUE_NN, graph.numNodes());
                mapResult.put("arcs", graph.numArcs());
                mapResult.put(VALUE_DIAMETER, ss.getDiameter());
                mapResult.put(VALUE_NUM_OF_BFS, ss.getDiameterIterations());
                mapResult.put("radius", ss.getRadius());
                mapResult.put(VALUE_TIME, time / 1000d);

                loader.printValues(mapResult);

            } catch (IOException e) {
                LOGGER.error("IOException", e);
            }
        }
        LOGGER.info("\n\n");
    }

    @Override
    public Map<String, Object> getResults() {
        return mapResult;
    }

    public static void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}
