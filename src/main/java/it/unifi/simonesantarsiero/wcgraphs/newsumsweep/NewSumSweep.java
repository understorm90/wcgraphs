package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unimi.dsi.webgraph.ImmutableGraph;
import org.slf4j.LoggerFactory;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class NewSumSweep extends AlgorithmStrategy {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(NewSumSweep.class);
    private int maxOutDegVert;

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
        DatasetLogger loader = new DatasetLogger(LOGGER);
        for (String filename : list) {
            String graphName = getGraphName(filename);
            loader.printFilename(graphName);

            ImmutableGraph graph;
            try {
                graph = ImmutableGraph.load(filename);
                maxOutDegVert = NewImmutableGraph.maxOutDegree(graph);

                long time = -System.currentTimeMillis();

                NewSumSweepDir nss = new NewSumSweepDir(graph);
                nss.runAuto();

                time += System.currentTimeMillis();

                setResults(graphName, graph.numNodes(), graph.numArcs(), nss.getD(), nss.getIterD(), time / 1000d);

                loader.printValues(mapResult);

            } catch (Exception e) {
                LOGGER.error("Exception", e);
            }
        }
        LOGGER.info("\n\n");
    }

    @Override
    public void setResults(String datasetName, int nVertices, long mEdges, int diameter, int nBFSs, double elapsedTime) {
        super.setResults(datasetName, nVertices, mEdges, diameter, nBFSs, elapsedTime);
        String regex;
        if ("/".equals(FILE_SEPARATOR)) {
            regex = ".*/";
        } else {
            regex = ".*\\\\";
        }
        String datasetPathWithoutLastFileSeparator = DATASETS_PATH.substring(0, DATASETS_PATH.length() - 1);
        mapResult.put(VALUE_DATASET_SOURCE, datasetPathWithoutLastFileSeparator.replaceAll(regex, ""));
        mapResult.put(VALUE_MAX_DEGREE, maxOutDegVert);
    }

    @Override
    public void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
        NewSumSweepDir.disableLogger();
    }
}
