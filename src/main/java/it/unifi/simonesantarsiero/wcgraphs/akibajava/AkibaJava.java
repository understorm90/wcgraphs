package it.unifi.simonesantarsiero.wcgraphs.akibajava;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

//rinominato le variabili in java style
// Replace the synchronized class "Stack" by an unsynchronized one such as "Deque".
//rimpiazzati i println con i logger
public class AkibaJava extends AlgorithmStrategy {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AkibaJava.class);

    public static void main(String[] args) {
        AlgorithmStrategy algorithm = new AkibaJava();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, AkibaJava.class.getCanonicalName());
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
        return EXT_TSV;
    }

    @Override
    public void compute() {
        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            String graphName = getGraphName(filename);
            loader.printFilename(graphName);

            GraphDiameter gd = new GraphDiameter();
            int diameter = gd.getDiameter(filename + EXT_TSV);

            mapResult = new HashMap<>();
            mapResult.put(VALUE_DATASET, graphName);
            mapResult.put(VALUE_NN, gd.getNumVertices());
            mapResult.put(VALUE_DIAMETER, diameter);
            mapResult.put(VALUE_NUM_OF_BFS, gd.getNumBFS());
            mapResult.put(VALUE_TIME, gd.getTimeElapsed());

            loader.printValues(mapResult);
        }
        LOGGER.info("\n\n");
    }

    @Override
    public void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}

