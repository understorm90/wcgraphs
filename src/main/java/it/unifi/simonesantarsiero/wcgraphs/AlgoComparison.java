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
        AkibaJava.disableLogger();
        AkibaCpp.disableLogger();
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

    public AlgoComparison(String datasetPath, boolean runningFromTerminal) {
        String workingDirectory = System.getProperty("user.dir");
        String datasetsPath;
        List<String> list;

        if (runningFromTerminal) {
            datasetsPath = "";

            list = new ArrayList<>();
            list.add(datasetPath);
        } else {
            datasetsPath = workingDirectory + DATASETS_PATH;

            list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        }

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);

        AlgorithmResults mStatsAkibaCPP = new AlgorithmResults("akiba-cpp");
        AlgorithmResults mStatsAkiba = new AlgorithmResults("akiba-java");
        AlgorithmResults mStatsSumSweep = new AlgorithmResults("sumsweep"); //  sumsweep = borassi
        AlgorithmResults mStatsNewSumSweep = new AlgorithmResults("newsumsweep");

        for (String filename : list) {
            loader.printFilename(filename+ " [akiba-cpp]");
            AkibaCpp akibaCpp = new AkibaCpp(datasetsPath + filename, runningFromTerminal);
            mStatsAkibaCPP.add(akibaCpp.getResults());
            loader.printValues(akibaCpp.getResults());

            loader.printFilename(filename + " [akiba-java]");
            AkibaJava akibaJava = new AkibaJava(datasetsPath + filename, runningFromTerminal);
            mStatsAkiba.add(akibaJava.getResults());
            loader.printValues(akibaJava.getResults());

//            loader.printFilename(filename+ " [sumsweep]");
//            SumSweep sumSweep = new SumSweep(DATASETS_PATH + filename, true);
//            loader.printValues(sumSweep.getResults());
//
//            loader.printFilename(filename+ " [webgraph]");
//            WebGraph webGraph = new WebGraph(DATASETS_PATH + filename, true);
//            loader.printValues(webGraph.getResults());
//
            loader.printFilename(filename+ " [newsumsweep]");
            NewSumSweep newSumSweep = new NewSumSweep(datasetsPath + filename, runningFromTerminal);
            mStatsNewSumSweep.add(newSumSweep.getResults());
            loader.printValues(newSumSweep.getResults());

            loader.printEmptyRow();
        }
        LOGGER.info("\n\n");

        List<AlgorithmResults> algorithmsResults = Arrays.asList(mStatsAkibaCPP, mStatsAkiba, mStatsNewSumSweep);
        new Chart(algorithmsResults);
    }
}
