package it.unifi.simonesantarsiero.wcgraphs.webgraph;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.algo.SumSweepDirectedDiameterRadius;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class WebGraph extends AlgorithmStrategy {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(WebGraph.class);

    public static void main(String[] args) {
        AlgorithmStrategy algorithm = new WebGraph();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, WebGraph.class.getCanonicalName());
                return;
            }
            algorithm.setDatasetFile(args[0]);
        } else {
            algorithm.setDatasetsFromSNAP();
        }
        algorithm.compute();
    }

    @Override
    public String getDatasetFileExtension() {
        return EXT_GRAPH;
    }

    @Override
    public void compute() {
        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            loader.printFilename(filename);

            ImmutableGraph graph;
            try {
                graph = ImmutableGraph.load(filename);

                long time = -System.currentTimeMillis();

                final SumSweepDirectedDiameterRadius ss = new SumSweepDirectedDiameterRadius(graph, SumSweepDirectedDiameterRadius.OutputLevel.ALL, null, null);
                ss.compute();

                time += System.currentTimeMillis();

                mapResult = new HashMap<>();
                mapResult.put(VALUE_DATASET, getGraphName(filename));
                mapResult.put(VALUE_NN, graph.numNodes());
                mapResult.put("arcs", graph.numArcs());
                mapResult.put(VALUE_DIAMETER, ss.getDiameter());
                mapResult.put(VALUE_NUM_OF_BFS, ss.getDiameterIterations());
                mapResult.put("radius", ss.getRadius());
                mapResult.put(VALUE_TIME, time / 1000d);

                loader.printValues(mapResult);

            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
        }
        LOGGER.info("\n\n");
    }

    @Override
    public void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}
