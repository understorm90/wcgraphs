package it.unifi.simonesantarsiero.wcgraphs.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    public static final String VALUE_GRAPH = "Graph";
    public static final String VALUE_DATASET = "dataset";
    public static final String VALUE_NN = "NN";
    public static final String VALUE_DIAMETER = "Diameter";
    public static final String VALUE_NUM_OF_BFS = "iterD";
    public static final String VALUE_TIME = "Time (s)";

    public static final String EXT_TSV = ".tsv";
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

    private static String getProperty(String propertyName) {
        Properties properties = new Properties();
        try {
            properties.load(Utils.class.getClassLoader().getResourceAsStream("wcgraphs.properties"));
        } catch (Exception e) {
            LOGGER.error("Exception: ", e);
        }
        return properties.getProperty(propertyName);
    }

    public static List<String> getListOfGraphsAvailableInDirectory(String path, String fileExtension) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfGraphs = new ArrayList<>();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile() && listOfFile.getName().endsWith(fileExtension)) {
                    String fileName = listOfFile.getName();
                    listOfGraphs.add(fileName.substring(0, fileName.length() - fileExtension.length()));
                }
            }
            Collections.sort(listOfGraphs);
            return listOfGraphs;
        }
        return Collections.emptyList();
    }
}
