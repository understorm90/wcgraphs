package it.unifi.simonesantarsiero.wcgraphs.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static final String DATASETS_PATH = getProperty("datasetsPath");

    public static final String VALUE_GRAPH = "Graph";
    public static final String VALUE_DATASET = "dataset";
    public static final String VALUE_NN = "NN";
    public static final String VALUE_DIAMETER = "Diameter";
    public static final String VALUE_NUM_OF_BFS = "iterD";
    public static final String VALUE_TIME = "Time (s)";

    public static final String EXT_TSV = ".tsv";
    public static final String EXT_GRAPH = ".graph";
    public static final String EXT_JAR = ".jar";

    public static final String SLASH = "/";

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
        } catch (IOException e) {
            LOGGER.error("IOException: ", e);
        }
        return properties.getProperty(propertyName);
    }
}
