package it.unifi.simonesantarsiero.wcgraphs;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.akibacpp.AkibaCpp;
import it.unifi.simonesantarsiero.wcgraphs.akibajava.AkibaJava;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unifi.simonesantarsiero.wcgraphs.newsumsweep.NewSumSweep;
import it.unifi.simonesantarsiero.wcgraphs.newsumsweep.NewSumSweepDir;
import it.unifi.simonesantarsiero.wcgraphs.sumsweep.SumSweep;
import it.unifi.simonesantarsiero.wcgraphs.webgraph.WebGraph;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AlgoComparison {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AlgoComparison.class);

    public static void main(String[] args) {
        AkibaCpp.disableLogger();
        AkibaJava.disableLogger();
        SumSweep.disableLogger();
        WebGraph.disableLogger();
        NewSumSweep.disableLogger();
        NewSumSweepDir.disableLogger();

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

    public AlgoComparison(String datasetFile, boolean runningFromTerminal) {

        String workingDirectory = System.getProperty("user.dir");
        String datasetsPath = "";
        List<String> list = new ArrayList<>();
        ;

        if (runningFromTerminal) {
            list.add(datasetFile);
        } else {
            list.addAll(DatasetLogger.getListOfGraphsAvailableInDirectory(workingDirectory + DATASETS_PATH, EXT_TSV));
        }

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);

        AlgorithmResults mStatsAkibaCPP = new AlgorithmResults("akiba-cpp");
        AlgorithmResults mStatsAkiba = new AlgorithmResults("akiba-java");
        AlgorithmResults mStatsWebGraph = new AlgorithmResults("webgraph");
        AlgorithmResults mStatsSumSweep = new AlgorithmResults("sumsweep"); //  sumsweep = borassi
        AlgorithmResults mStatsNewSumSweep = new AlgorithmResults("newsumsweep");

        Comparator comparator = new Comparator();
        for (String filename : list) {
            loader.printFilename(filename + " [akiba-cpp]");
            comparator.setDiameterCalculatorAlgorithm(new AkibaCpp());
            comparator.setDatasetFile(datasetsPath + filename, runningFromTerminal);
            comparator.compute();
            mStatsAkibaCPP.add(comparator.getResults());
            loader.printValues(comparator.getResults());

            loader.printFilename(filename + " [akiba-java]");
            comparator.setDiameterCalculatorAlgorithm(new AkibaJava());
            comparator.setDatasetFile(datasetsPath + filename, runningFromTerminal);
            comparator.compute();
            mStatsAkiba.add(comparator.getResults());
            loader.printValues(comparator.getResults());

            loader.printFilename(filename + " [webgraph]");
            comparator.setDiameterCalculatorAlgorithm(new WebGraph());
            comparator.setDatasetFile(datasetsPath + filename, runningFromTerminal);
            comparator.compute();
            mStatsWebGraph.add(comparator.getResults());
            loader.printValues(comparator.getResults());

            loader.printFilename(filename + " [sumsweep]");
            comparator.setDiameterCalculatorAlgorithm(new SumSweep());
            comparator.setDatasetFile(datasetsPath + filename, runningFromTerminal);
            comparator.compute();
            mStatsSumSweep.add(comparator.getResults());
            loader.printValues(comparator.getResults());

            loader.printFilename(filename + " [newsumsweep]");
            comparator.setDiameterCalculatorAlgorithm(new NewSumSweep());
            comparator.setDatasetFile(datasetsPath + filename, runningFromTerminal);
            comparator.compute();
            mStatsNewSumSweep.add(comparator.getResults());
            loader.printValues(comparator.getResults());

            loader.printEmptyRow();
        }
        LOGGER.info("\n\n");

        List<AlgorithmResults> algorithmsResults = Arrays.asList(mStatsAkibaCPP, mStatsAkiba, mStatsWebGraph, mStatsSumSweep, mStatsNewSumSweep);
        new Chart(algorithmsResults);
    }
}
