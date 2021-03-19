package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class NewSumSweep extends AlgorithmStrategy {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(NewSumSweep.class);

    public static void main(String[] args) {
        NewSumSweepDir.disableLogger();

        AlgorithmStrategy algorithm = new NewSumSweep();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, NewSumSweep.class.getCanonicalName());
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
            String graphName = getGraphName(filename);
            loader.printFilename(graphName);

            ImmutableGraph graph;
            try {
                graph = ImmutableGraph.load(filename);

                long time = -System.currentTimeMillis();

                NewSumSweepDir nss = new NewSumSweepDir(graph);
                nss.runAuto();

                time += System.currentTimeMillis();

                setResults(graphName, graph.numNodes(), nss.getD(), nss.getIterD(), time / 1000d);

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
        NewSumSweepDir.disableLogger();
    }
}
