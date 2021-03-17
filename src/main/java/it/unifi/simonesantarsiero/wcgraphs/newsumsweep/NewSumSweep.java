package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.Algorithm;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class NewSumSweep extends Algorithm {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(NewSumSweep.class);

    public static void main(String[] args) {
        NewSumSweepDir.disableLogger();

        Algorithm algorithm = new NewSumSweep();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, NewSumSweep.class.getCanonicalName());
                return;
            }
            algorithm.setDatasetFile(args[0], true);
        } else {
            algorithm.setDatasetFile("", false);
        }
        algorithm.compute();
    }

    @Override
    public String getFileExtension() {
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
                graph = ImmutableGraph.load(datasetsPath + filename);

                long time = -System.currentTimeMillis();

                NewSumSweepDir nss = new NewSumSweepDir(graph);
                nss.runAuto();

                time += System.currentTimeMillis();

                mapResult = new HashMap<>();
                mapResult.put(VALUE_DATASET, filename);
                mapResult.put(VALUE_NN, graph.numNodes());
                mapResult.put("arcs", graph.numArcs());
                mapResult.put(VALUE_DIAMETER, nss.getD());
                mapResult.put(VALUE_NUM_OF_BFS, nss.getIterD());
                mapResult.put("radius", nss.getR());
                mapResult.put(VALUE_TIME, time / 1000d);

                loader.printValues(mapResult);

            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
        }
        LOGGER.info("\n\n");
    }

    public static void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}
