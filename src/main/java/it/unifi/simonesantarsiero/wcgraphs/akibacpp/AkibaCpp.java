package it.unifi.simonesantarsiero.wcgraphs.akibacpp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AkibaCpp extends AlgorithmStrategy {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AkibaCpp.class);

    private static final String TEST_CPP = "test.cpp";
    private static final String GRAPH_DIAMETER_H = "graph_diameter.h";
    private static final String TEST_EXE = "test";

    private String currentPath;

    public static void main(String[] args) {
        AlgorithmStrategy algorithm = new AkibaCpp();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, AkibaCpp.class.getCanonicalName());
                return;
            }
            algorithm.setDatasetFile(args[0], true);
        } else {
            algorithm.setDatasetFile("", false);
        }
        algorithm.compute();
    }

    @Override
    public void setDatasetFile(String datasetFile, boolean runningFromTerminal) {
        super.setDatasetFile(datasetFile, runningFromTerminal);

        if (runningFromTerminal) {
            currentPath = workingDirectory + FILE_SEPARATOR;

            // copy .CPP and .H files
            copyFileFromResources(workingDirectory, TEST_CPP);
            copyFileFromResources(workingDirectory, GRAPH_DIAMETER_H);
        } else {
            currentPath = getClass().getResource(FILE_SEPARATOR).getPath();
        }
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
        try (InputStream is = getClass().getResourceAsStream(FILE_SEPARATOR + fileName)) {
            Files.copy(is, Paths.get(workingDirectory + FILE_SEPARATOR + fileName));
        } catch (Exception e) {
            LOGGER.error("Exception\n", e);
        }
    }

    @Override
    public void disableLogger() {
        LOGGER.setLevel(Level.toLevel("error"));
    }
}
