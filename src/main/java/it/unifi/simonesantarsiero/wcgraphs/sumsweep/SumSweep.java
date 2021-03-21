package it.unifi.simonesantarsiero.wcgraphs.sumsweep;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import graph.Dir;
import graph.GraphTypes;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.slf4j.LoggerFactory;
import utilities.Utilities;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.EXT_GRAPH;
import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.USAGE_ERROR_MESSAGE;

//rinominato le variabili in java style
// Replace the synchronized class "Stack" by an unsynchronized one such as "Deque".
//rimpiazzati i println con i logger
public class SumSweep extends AlgorithmStrategy {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(SumSweep.class);

    public static void main(String[] args) {
        AlgorithmStrategy algorithm = new SumSweep();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, SumSweep.class.getCanonicalName());
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
        Utilities.verb = 0;

        DatasetLogger loader = new DatasetLogger(LOGGER);
        for (String filename : list) {
            String graphName = getGraphName(filename);
            loader.printFilename(graphName);

            Dir graph = Dir.load(filename, GraphTypes.ADJLIST, Utilities.loadMethod);
            int nNodes = graph.getNN();
            long mEdges = graph.getNE();
            graph.transformIntoBiggestWCC();
            alg.distances.SumSweepDir g = new alg.distances.SumSweepDir(graph);
            g.runAuto();
            double elapsedTime = g.graph.getElapsedTime() / 1000d;

            setResults(graphName, nNodes, mEdges, g.getD(), g.getIterD(), elapsedTime);

            loader.printValues(mapResult);
        }
        LOGGER.info("\n\n");
    }

    @Override
    public void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}

