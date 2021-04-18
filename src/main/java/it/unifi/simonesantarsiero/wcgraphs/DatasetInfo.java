package it.unifi.simonesantarsiero.wcgraphs;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unifi.simonesantarsiero.wcgraphs.newsumsweep.NewSumSweep;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

/**
 * Utility class to list all the info about a specific dataset, using the NewSumSweep algorithm.
 * This is used to describe all the graphs in my thesis.
 */
public class DatasetInfo {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DatasetInfo.class);

    public static void main(String[] args) {

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, DatasetInfo.class.getCanonicalName());
                return;
            }
            new DatasetInfo(args[0]);
        } else {
            new DatasetInfo();
        }
    }

    public String getGraphName(String filename) {
        String[] split = filename.split(FILE_SEPARATOR);
        return split[split.length - 1];
    }

    public DatasetInfo() {
        List<String> list = new ArrayList<>(
                getPathsOfGraphsAvailableInDirectory(
                        System.getProperty("user.dir") + FILE_SEPARATOR + DATASETS_PATH, EXT_TSV));
        compareAlgorithms(list);
    }

    public DatasetInfo(String datasetFile) {
        compareAlgorithms(Collections.singletonList(datasetFile));
    }

    private void compareAlgorithms(List<String> list) {
        List<String> headers = Arrays.asList(VALUE_DATASET_SOURCE, VALUE_VERTICES, VALUE_EDGES, VALUE_DENSITY, VALUE_MAX_DEGREE, VALUE_DIAMETER);
        DatasetLogger loader = new DatasetLogger(headers, LOGGER);

        Comparator comparator = new Comparator();
        for (String filename : list) {
            loader.printFilename(getGraphName(filename));
            comparator.setAlgorithm(new NewSumSweep());
            comparator.disableLogger();
            comparator.setDatasetFile(filename);
            comparator.compute();
            loader.printValues(comparator.getResults());
            loader.printEmptyRow();
        }
        LOGGER.info("\n\n");
    }
}
