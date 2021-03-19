package it.unifi.simonesantarsiero.wcgraphs.akibacpp;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AkibaCpp extends AlgorithmStrategy {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(AkibaCpp.class);

    private static final String TEST_CPP = "test.cpp";
    private static final String GRAPH_DIAMETER_H = "graph_diameter.h";
    private static final String TEST_EXE = "test";

    public static void main(String[] args) {
        AlgorithmStrategy algorithm = new AkibaCpp();

        if (System.console() != null) {
            if (args.length != 1) {
                LOGGER.info(USAGE_ERROR_MESSAGE, AkibaCpp.class.getCanonicalName());
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
        String currentPath = prepareFilesToExecute();

        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);
        for (String filename : list) {
            String graphName = getGraphName(filename);
            loader.printFilename(graphName);

            // COMPILE
            TerminalUtils.exeCommand("gcc -lstdc++ " + currentPath + TEST_CPP + " -o " + currentPath + TEST_EXE);

            // RUN
            String output = TerminalUtils.exeCommand(currentPath + TEST_EXE + " " + filename + EXT_TSV);
            OutputParser parser = new OutputParser(output);

            setResults(graphName, parser.getVertices(), parser.getDiameter(), parser.getBFS(), parser.getTime());

            loader.printValues(mapResult);
        }
        LOGGER.info("\n\n");
    }

    private String prepareFilesToExecute() {
        String currentPath;

        if (System.console() != null) {
            String workingDirectory = System.getProperty("user.dir");
            currentPath = workingDirectory + FILE_SEPARATOR;

            // copy .CPP and .H files
            copyFileFromResources(workingDirectory, TEST_CPP);
            copyFileFromResources(workingDirectory, GRAPH_DIAMETER_H);
        } else {
            currentPath = getClass().getResource(FILE_SEPARATOR).getPath();
        }
        return currentPath;
    }

    private void copyFileFromResources(String workingDirectory, String fileName) {
        if (new File(workingDirectory + FILE_SEPARATOR + fileName).isFile()) {
            return;
        }
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
