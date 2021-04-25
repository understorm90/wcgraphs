package it.unifi.simonesantarsiero.wcgraphs.akibajava;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.slf4j.LoggerFactory;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.EXT_TSV;
import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.USAGE_ERROR_MESSAGE;

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
        DatasetLogger loader = new DatasetLogger(LOGGER);
        for (String filename : list) {
            String graphName = getGraphName(filename);
            loader.printFilename(graphName);

            GraphDiameter gd = new GraphDiameter();
            int diameter = gd.getDiameter(filename + EXT_TSV);

            setResults(graphName, gd.getNumVertices(), gd.getNumEdges(), diameter, gd.getNumBFS(), gd.getTimeElapsed());

            loader.printValues(mapResult);
        }
        LOGGER.info("\n\n");
    }

    @Override
    public void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}

