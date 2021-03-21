package it.unifi.simonesantarsiero.wcgraphs;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.akibacpp.AkibaCpp;
import it.unifi.simonesantarsiero.wcgraphs.akibajava.AkibaJava;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmEnum;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unifi.simonesantarsiero.wcgraphs.newsumsweep.NewSumSweep;
import it.unifi.simonesantarsiero.wcgraphs.sumsweep.SumSweep;
import it.unifi.simonesantarsiero.wcgraphs.webgraph.WebGraph;
import org.slf4j.LoggerFactory;

import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AlgoComparison {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AlgoComparison.class);

    public static void main(String[] args) {

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, AlgoComparison.class.getCanonicalName());
                return;
            }
            new AlgoComparison(args[0]);
        } else {
            new AlgoComparison();
        }
    }

    public String getGraphName(String filename) {
        String[] split = filename.split(FILE_SEPARATOR);
        return split[split.length - 1];
    }

    public AlgoComparison() {
        List<String> list = new ArrayList<>(
                getPathsOfGraphsAvailableInDirectory(
                        System.getProperty("user.dir") + FILE_SEPARATOR + DATASETS_PATH, EXT_TSV));
        compareAlgorithms(list);
    }

    public AlgoComparison(String datasetFile) {
        compareAlgorithms(Collections.singletonList(datasetFile));
    }

    private void compareAlgorithms(List<String> list) {
        DatasetLogger loader = new DatasetLogger(LOGGER);

        Map<String, AlgorithmResults> algorithmResultsMap = new HashMap<>();
        List<AlgorithmEnum> algorithmEnumsList = Arrays.asList(AlgorithmEnum.values());
        algorithmEnumsList.forEach(algorithm -> algorithmResultsMap.put(algorithm.getValue(), new AlgorithmResults(algorithm.getValue())));

        Comparator comparator = new Comparator();
        for (String filename : list) {
            for (AlgorithmEnum algorithmEnum : algorithmEnumsList) {
                loader.printFilename(getGraphName(filename) + " [" + algorithmEnum.getValue() + "]");
                comparator.setAlgorithm(getAlgorithm(algorithmEnum));
                comparator.disableLogger();
                comparator.setDatasetFile(filename);
                comparator.compute();
                algorithmResultsMap.get(algorithmEnum.getValue()).add(comparator.getResults());
                loader.printValues(comparator.getResults());
            }
            loader.printEmptyRow();
        }
        LOGGER.info("\n\n");

        new Chart(new ArrayList<>(algorithmResultsMap.values()));
    }

    private AlgorithmStrategy getAlgorithm(AlgorithmEnum algorithmEnum) {
        AlgorithmStrategy algorithm;
        switch (algorithmEnum) {
            case AKIBA_CPP:
                algorithm = new AkibaCpp();
                break;
            case AKIBA_JAVA:
                algorithm = new AkibaJava();
                break;
            case WEBGRAPH:
                algorithm = new WebGraph();
                break;
            case SUMSWEEP:
                algorithm = new SumSweep();
                break;
            case NEWSUMSWEEP:
            default:
                algorithm = new NewSumSweep();
        }
        return algorithm;
    }
}
