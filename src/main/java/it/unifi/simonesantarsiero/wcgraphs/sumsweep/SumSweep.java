package it.unifi.simonesantarsiero.wcgraphs.sumsweep;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import graph.Dir;
import graph.GraphTypes;
import it.unifi.simonesantarsiero.wcgraphs.commons.Algorithm;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.slf4j.LoggerFactory;
import utilities.Utilities;

import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

//rinominato le variabili in java style
// Replace the synchronized class "Stack" by an unsynchronized one such as "Deque".
//rimpiazzati i println con i logger
public class SumSweep implements Algorithm {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(SumSweep.class);

    private Map<String, Object> mapResult;

    public static void main(String[] args) {
        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, SumSweep.class.getCanonicalName());
                return;
            }
            new SumSweep(args[0], true);
        } else {
            new SumSweep("", false);
        }
    }

    public SumSweep(String datasetPath, boolean runningFromTerminal) {
        Utilities.verb = 0;

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

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS,VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            loader.printFilename(filename);

            Dir graph = Dir.load(datasetsPath + filename, GraphTypes.ADJLIST, Utilities.loadMethod);
            graph.transformIntoBiggestWCC();
            int nNodes = graph.getNN();
            alg.distances.SumSweepDir g = new alg.distances.SumSweepDir(graph);
            g.runAuto();
            double elapsedTime = g.graph.getElapsedTime() / 1000d;

            mapResult = new HashMap<>();
            mapResult.put(VALUE_DATASET, filename);
            mapResult.put(VALUE_NN, nNodes);
            mapResult.put(VALUE_DIAMETER, g.getD());
            mapResult.put(VALUE_NUM_OF_BFS, g.getIterD());
            mapResult.put(VALUE_TIME, elapsedTime);

            loader.printValues(mapResult);
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

