package it.unifi.simonesantarsiero.wcgraphs.akibacpp;

import it.unifi.simonesantarsiero.wcgraphs.commons.Algorithm;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AkibaCpp implements Algorithm {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AkibaCpp.class);

    private static final String TEST_CPP = "test.cpp";
    private static final String GRAPH_DIAMETER_H = "graph_diameter.h";
    private static final String TEST_EXE = "test";

    private Map<String, Object> mapResult;

    public static void main(String[] args) {
        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info("Usage: java -jar app.jar GRAPH\n\n");
                return;
            }
            new AkibaCpp(args[0], true);
        } else {
            new AkibaCpp("", false);
        }
    }

    public AkibaCpp(String path, boolean runningFromTerminal) {
        String workingDirectory = System.getProperty("user.dir");
        String currentPath;
        String datasetsPath;
        List<String> list;

        if (runningFromTerminal) {
            currentPath = workingDirectory + "/";
            datasetsPath = currentPath;

            // copy .CPP and .H files
            copyFileFromResources(workingDirectory, TEST_CPP);
            copyFileFromResources(workingDirectory, GRAPH_DIAMETER_H);

            list = new ArrayList<>();
            list.add(path);
        } else {
            currentPath = getClass().getResource("/").getPath();
            datasetsPath = workingDirectory + DATASETS_PATH;

            list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        }

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            loader.printFilename(filename);

            // COMPILE
            TerminalUtils.exeCommand("gcc -lstdc++ " + currentPath + TEST_CPP + " -o " + currentPath + TEST_EXE);

            // RUN
            String output = TerminalUtils.exeCommand(currentPath + TEST_EXE + " " + datasetsPath + filename + EXT_TSV);
            OutputParser parser = new OutputParser(output);

            mapResult = new HashMap<>();
            mapResult.put(VALUE_DATASET, filename);
            mapResult.put(VALUE_NN, parser.getVertices());
            mapResult.put(VALUE_DIAMETER, parser.getDiameter());
            mapResult.put(VALUE_NUM_OF_BFS, parser.getBFS());
            mapResult.put(VALUE_TIME, parser.getTime());

            loader.printValues(mapResult);
        }
        LOGGER.info("\n\n");
    }

    private void copyFileFromResources(String workingDirectory, String fileName) {
        try (InputStream is = getClass().getResourceAsStream("/" + fileName)) {
            Files.copy(is, Paths.get(workingDirectory + "/" + fileName));
        } catch (IOException e) {
            LOGGER.error("IOException: ", e);
        }
    }

    @Override
    public Map<String, Object> getResults() {
        return mapResult;
    }

    public static void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}
