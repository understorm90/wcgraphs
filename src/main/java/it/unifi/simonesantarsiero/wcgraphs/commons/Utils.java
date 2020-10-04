package it.unifi.simonesantarsiero.wcgraphs.commons;

public class Utils {
    private Utils() {
    }

    public static final String DATASETS_PATH = "/DATASETS/example/";

    public static final String VALUE_GRAPH = "Graph";
    public static final String VALUE_DATASET = "dataset";
    public static final String VALUE_NN = "NN";
    public static final String VALUE_DIAMETER = "Diameter";
    public static final String VALUE_NUM_OF_BFS = "iterD";
    public static final String VALUE_TIME = "Time (s)";

    public static final String EXT_TSV = ".tsv";
    public static final String EXT_GRAPH = ".graph";

    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";

    private static final String APP_NAME = "wcgraphs-1.0-SNAPSHOT";
    public static final String USAGE_ERROR_MESSAGE = colorize("\nUsage: java -cp " + APP_NAME + ".jar {} GRAPH_PATH\n\n");

    private static String colorize(String message) {
        return RED + message + RESET;
    }
}
