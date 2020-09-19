package it.unifi.simonesantarsiero.wcgraphs.akibajava;

import ch.qos.logback.classic.Level;
import it.unifi.simonesantarsiero.wcgraphs.commons.Algorithm;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

//rinominato le variabili in java style
// Replace the synchronized class "Stack" by an unsynchronized one such as "Deque".
//rimpiazzati i println con i logger
public class AkibaJava implements Algorithm {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AkibaJava.class);

    private Map<String, Object> mapResult;

    public static void main(String[] args) {
        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info("Usage: java -jar app.jar GRAPH\n\n");
                return;
            }
            new AkibaJava(args[0], true);
        } else {
            System.out.println("intellij");
            new AkibaJava("", false);
        }
    }

    public AkibaJava(String path, boolean runningFromTerminal) {
        String workingDirectory = System.getProperty("user.dir");
        String datasetsPath;
        List<String> list;

        if (runningFromTerminal) {
            datasetsPath = workingDirectory + "/";

            list = new ArrayList<>();
            list.add(path);
        } else {
            datasetsPath = workingDirectory + DATASETS_PATH;

            list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        }

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            loader.printFilename(filename);

            GraphDiameter gd = new GraphDiameter();
            int diameter = gd.getDiameter(datasetsPath + filename + EXT_TSV);

            mapResult = new HashMap<>();
            mapResult.put(VALUE_DATASET, filename);
            mapResult.put(VALUE_NN, gd.getNumVertices());
            mapResult.put(VALUE_DIAMETER, diameter);
            mapResult.put(VALUE_NUM_OF_BFS, gd.getNumBFS());
            mapResult.put(VALUE_TIME, gd.getTimeElapsed());

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

