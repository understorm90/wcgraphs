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
    private static final int N_EXPERIMENTS = 1; //1000 default
    private static final String VALUE_TIME_MEAN = "Time (s) (mean of " + N_EXPERIMENTS + " experiments)";
    private static final boolean WITH_SYNTETIC_GRAPH = false;

    public static void main(String[] args) {

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, AlgoComparison.class.getCanonicalName());
                return;
            }
            new AlgoComparison(args[0]);
        } else {
            new AlgoComparison(WITH_SYNTETIC_GRAPH);
        }
    }

    public String getGraphName(String filename) {
        String[] split = filename.split(FILE_SEPARATOR);
        return split[split.length - 1];
    }

    public AlgoComparison(boolean randomGraphs) {
        String graphDirectory;
        if (randomGraphs) {
            graphDirectory = System.getProperty("user.dir") + FILE_SEPARATOR + RANDOM_GENERATED_DATASETS_PATH;
        } else {
            graphDirectory = System.getProperty("user.dir") + FILE_SEPARATOR + DATASETS_PATH;
        }

        List<String> list = new ArrayList<>(
                getPathsOfGraphsAvailableInDirectory(graphDirectory, EXT_TSV));
        compareAlgorithms(list);
    }

    public AlgoComparison(String datasetFile) {
        compareAlgorithms(Collections.singletonList(datasetFile));
    }

    private void compareAlgorithms(List<String> list) {
        List<String> headersList = Arrays.asList(VALUE_VERTICES, VALUE_EDGES, VALUE_DENSITY, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME, VALUE_TIME_MEAN);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);

        Map<String, AlgorithmResults> algorithmResultsMap = new HashMap<>();
        List<AlgorithmEnum> algorithmEnumsList = Arrays.asList(AlgorithmEnum.values());
        algorithmEnumsList.forEach(algorithm -> algorithmResultsMap.put(algorithm.getValue(), new AlgorithmResults(algorithm.getValue())));

        Comparator comparator = new Comparator();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("graphName,algorithmName,");
        stringBuilder.append(printHeaders(loader.getHeaders(), ",") + "\n");

        for (String filename : list) {
            for (int exp = 0; exp < N_EXPERIMENTS; exp++) {
                for (AlgorithmEnum algorithmEnum : algorithmEnumsList) {
                    if (exp == N_EXPERIMENTS - 1) {
                        loader.printFilename(getGraphName(filename) + " [" + algorithmEnum.getValue() + "]");
                    }
                    comparator.setAlgorithm(getAlgorithm(algorithmEnum));
                    comparator.disableLogger();
                    comparator.setDatasetFile(filename);
                    comparator.compute();
                    algorithmResultsMap.get(algorithmEnum.getValue()).add(comparator.getResults());
                    Map<String, Object> results = comparator.getResults();
                    if (exp == N_EXPERIMENTS - 1) {
                        double mean = algorithmResultsMap.get(algorithmEnum.getValue()).getMean();
                        results.put(VALUE_TIME_MEAN, mean);
                        loader.printValues(results);
                        stringBuilder.append(getGraphName(filename) + "," + algorithmEnum.getValue() + ",");
                        stringBuilder.append(addResultToStringBuilder(loader.getHeaders(), comparator.getResults(), ",") + "\n");
                    }
                }
                loader.printEmptyRow();
            }
        }
        LOGGER.info("\n\n");
        writeFile("wcgraphs-export.txt", stringBuilder.toString());

        new Chart(new ArrayList<>(algorithmResultsMap.values()), true);
        new Chart(new ArrayList<>(algorithmResultsMap.values()), false);
    }

    private String printHeaders(List<String> headers, String delim) {
        StringBuilder stringBuilder = new StringBuilder();
        String loopDelim = "";
        for (String h : headers) {
            stringBuilder.append(loopDelim);
            stringBuilder.append(h);
            loopDelim = delim;
        }
        return stringBuilder.toString();
    }

    private String addResultToStringBuilder(List<String> headers, Map<String, Object> results, String delim) {
        StringBuilder stringBuilder = new StringBuilder();
        String loopDelim = "";
        for (String h : headers) {
            String valueToPrint = String.valueOf(results.get(h));
            stringBuilder.append(loopDelim);
            stringBuilder.append(valueToPrint);
            loopDelim = delim;
        }
        return stringBuilder.toString();
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
