package it.unifi.simonesantarsiero.wcgraphs;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.akibacpp.AkibaCpp;
import it.unifi.simonesantarsiero.wcgraphs.akibajava.AkibaJava;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmEnum;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unifi.simonesantarsiero.wcgraphs.commons.RandomGraphGenerator;
import it.unifi.simonesantarsiero.wcgraphs.newsumsweep.NewSumSweep;
import it.unifi.simonesantarsiero.wcgraphs.sumsweep.SumSweep;
import it.unifi.simonesantarsiero.wcgraphs.webgraph.WebGraph;
import org.slf4j.LoggerFactory;

import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AlgoComparison {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AlgoComparison.class);
    private static final int MAX_DENSITY = 100;
    private static final int MAX_VERTICES = 10000;
    private static final int N_EXPERIMENTS = 1000;
    private static final String VALUE_TIME_MEAN = "Time (s) (mean of " + N_EXPERIMENTS + " experiments)";

    public static void main(String[] args) {

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, AlgoComparison.class.getCanonicalName());
                return;
            }
            new AlgoComparison(args[0]);
        } else {
            new AlgoComparison(false);
        }
    }

    public String getGraphName(String filename) {
        String[] split = filename.split(FILE_SEPARATOR);
        return split[split.length - 1];
    }

    public AlgoComparison(boolean randomGraphs) {
        if (randomGraphs) {
            compareAlgorithmsForRandomGeneratedGraphs();
        } else {
            List<String> list = new ArrayList<>(
                    getPathsOfGraphsAvailableInDirectory(
                            System.getProperty("user.dir") + FILE_SEPARATOR + DATASETS_PATH, EXT_TSV));
            compareAlgorithms(list);
        }
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

    private void compareAlgorithmsForRandomGeneratedGraphs() {

        List<String> headersList = Arrays.asList(VALUE_VERTICES, VALUE_EDGES, VALUE_DENSITY, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME, VALUE_TIME_MEAN);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);

        Map<String, AlgorithmResults> algorithmResultsMap = new HashMap<>();
        List<AlgorithmEnum> algorithmEnumsList = Arrays.asList(AlgorithmEnum.values());
        algorithmEnumsList.forEach(algorithm -> algorithmResultsMap.put(algorithm.getValue(), new AlgorithmResults(algorithm.getValue())));

        Comparator comparator = new Comparator();

        for (int density = 1; density < MAX_DENSITY; density += 10) {
            for (int nVertices = 1000; nVertices < MAX_VERTICES; nVertices += 1000) {
                int mEdges = density * nVertices;

                RandomGraphGenerator randomGraphGenerator = new RandomGraphGenerator(nVertices, mEdges);
                String basename = randomGraphGenerator.writeToFileTSV();
                randomGraphGenerator.generateFilesForWebgraph(basename);

                for (int exp = 0; exp < N_EXPERIMENTS; exp++) {
                    for (AlgorithmEnum algorithmEnum : algorithmEnumsList) {
                        comparator.setAlgorithm(getAlgorithm(algorithmEnum));
                        comparator.disableLogger();
                        comparator.setDatasetFile(basename);
                        comparator.compute();
                        algorithmResultsMap.get(algorithmEnum.getValue()).add(comparator.getResults());
                        Map<String, Object> results = comparator.getResults();
                        if (exp == N_EXPERIMENTS - 1) {
                            loader.printFilename(getGraphName(basename) + " [" + algorithmEnum.getValue() + "]");
                            double mean = algorithmResultsMap.get(algorithmEnum.getValue()).getMean();
                            results.put(VALUE_TIME_MEAN, mean);
                            loader.printValues(results);
                        }
                    }
                }
                loader.printEmptyRow();
            }
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
