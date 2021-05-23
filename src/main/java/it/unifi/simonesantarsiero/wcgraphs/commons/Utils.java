package it.unifi.simonesantarsiero.wcgraphs.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static final String DATASETS_PATH = getProperty("datasetsPath");
    public static final String RANDOM_GENERATED_DATASETS_PATH = getProperty("generatedDatasetsPath");
    public static final String IMPORT_PATH = getProperty("importPath");

    public static final String VALUE_GRAPH = "Graph";
    public static final String VALUE_DATASET = "dataset";
    public static final String VALUE_DATASET_SOURCE = "source";
    public static final String VALUE_VERTICES = "nVertices";
    public static final String VALUE_EDGES = "mEdges";
    public static final String VALUE_DENSITY = "density";
    public static final String VALUE_MAX_DEGREE = "maxDegree";
    public static final String VALUE_DIAMETER = "Diameter";
    public static final String VALUE_NUM_OF_BFS = "#BFS";
    public static final String VALUE_TIME = "Time (s)";

    public static final String EXT_TSV = ".tsv";
    public static final String EXT_ARCS = ".arcs";
    public static final String EXT_GRAPH = ".graph";
    public static final String EXT_JAR = ".jar";

    public static final String FILE_SEPARATOR = "/";

    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";

    private static final String APP_NAME = getProperty("artifactId");
    private static final String APP_VERSION = getProperty("version");
    private static final String JAR_NAME = APP_NAME + "-" + APP_VERSION + EXT_JAR;
    public static final String USAGE_ERROR_MESSAGE = colorize("\nUsage: java -cp " + JAR_NAME + " {} GRAPH_PATH\n\n");

    private static String colorize(String message) {
        return RED + message + RESET;
    }

    // truncate the value to the 'places' decimal
    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private static String getProperty(String propertyName) {
        Properties properties = new Properties();
        try {
            properties.load(Utils.class.getClassLoader().getResourceAsStream("wcgraphs.properties"));
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return properties.getProperty(propertyName);
    }

    public static List<String> getPathsOfGraphsAvailableInDirectory(String dir, String fileExtension) {
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfGraphs = new ArrayList<>();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile() && listOfFile.getName().endsWith(fileExtension)) {
                    String fileName = listOfFile.getName();
                    listOfGraphs.add(dir + fileName.substring(0, fileName.length() - fileExtension.length()));
                }
            }
            Collections.sort(listOfGraphs);
            return listOfGraphs;
        }
        return Collections.emptyList();
    }

    public static List<String> readFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            return readAllLines(reader);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static List<String> readAllLines(BufferedReader reader) throws IOException {
        List<String> content = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            content.add(line);
        }
        return content;
    }

    public static void writeFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
