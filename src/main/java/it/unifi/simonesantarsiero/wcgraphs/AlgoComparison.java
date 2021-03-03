package it.unifi.simonesantarsiero.wcgraphs;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.akibacpp.AkibaCpp;
import it.unifi.simonesantarsiero.wcgraphs.akibajava.AkibaJava;
import it.unifi.simonesantarsiero.wcgraphs.commons.Algorithm;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmEnum;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unifi.simonesantarsiero.wcgraphs.newsumsweep.NewSumSweep;
import it.unifi.simonesantarsiero.wcgraphs.newsumsweep.NewSumSweepDir;
import it.unifi.simonesantarsiero.wcgraphs.sumsweep.SumSweep;
import it.unifi.simonesantarsiero.wcgraphs.webgraph.WebGraph;
import org.slf4j.LoggerFactory;

import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AlgoComparison {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AlgoComparison.class);

    public static void main(String[] args) {
        disableLogger();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, AlgoComparison.class.getCanonicalName());
                return;
            }
            new AlgoComparison(args[0], true);
        } else {
            new AlgoComparison("", false);
        }
    }

    private static void disableLogger() {
        AkibaCpp.disableLogger();
        AkibaJava.disableLogger();
        SumSweep.disableLogger();
        WebGraph.disableLogger();
        NewSumSweep.disableLogger();
        NewSumSweepDir.disableLogger();
    }

    public AlgoComparison(String datasetFile, boolean runningFromTerminal) {

        String workingDirectory = System.getProperty("user.dir");
        String datasetsPath = "";
        List<String> list = new ArrayList<>();

        if (runningFromTerminal) {
            list.add(datasetFile);
        } else {
            list.addAll(DatasetLogger.getListOfGraphsAvailableInDirectory(workingDirectory + DATASETS_PATH, EXT_TSV));
        }

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);

        Map<String, AlgorithmResults> algorithmResultsMap = new HashMap<>();
        List<AlgorithmEnum> algorithmEnumsList = Arrays.asList(AlgorithmEnum.values());
        algorithmEnumsList.forEach(algorithm -> {
            algorithmResultsMap.put(algorithm.getValue(), new AlgorithmResults(algorithm.getValue()));
        });

        Comparator comparator = new Comparator();
        for (String filename : list) {
            for (AlgorithmEnum algorithmEnum : algorithmEnumsList) {
                loader.printFilename(filename + " [" + algorithmEnum.getValue() + "]");
                Algorithm algo = getAlgorithm(algorithmEnum);
                comparator.setDiameterCalculatorAlgorithm(algo);
                comparator.setDatasetFile(datasetsPath + filename, runningFromTerminal);
                comparator.compute();
                algorithmResultsMap.get(algorithmEnum.getValue()).add(comparator.getResults());
                loader.printValues(comparator.getResults());
            }
            loader.printEmptyRow();
        }
        LOGGER.info("\n\n");

        new Chart(new ArrayList<>(algorithmResultsMap.values()));
    }

    private Algorithm getAlgorithm(AlgorithmEnum algorithmEnum) {
        Algorithm algorithm;
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
                algorithm = new NewSumSweep();
                break;
            default:
                algorithm = null;
        }
        return algorithm;
    }
}
