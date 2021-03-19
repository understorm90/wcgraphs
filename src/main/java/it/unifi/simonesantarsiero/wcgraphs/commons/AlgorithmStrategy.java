package it.unifi.simonesantarsiero.wcgraphs.commons;

import java.util.*;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public abstract class AlgorithmStrategy {
    protected Map<String, Object> mapResult;
    protected final List<String> list = new ArrayList<>();

    public void setDatasetFile(String datasetFile) {
        list.add(datasetFile);
    }

    public void setDatasetsFromSNAP() {
        list.addAll(getPathsOfGraphsAvailableInDirectory(System.getProperty("user.dir") + FILE_SEPARATOR + DATASETS_PATH, getDatasetFileExtension()));
    }

    public String getGraphName(String filename) {
        String[] split = filename.split(FILE_SEPARATOR);
        return split[split.length - 1];
    }

    public void setResults(String datasetName, int nVertices, int diameter, int nBFSs, double elapsedTime) {
        mapResult = new HashMap<>();
        mapResult.put(VALUE_DATASET, datasetName);
        mapResult.put(VALUE_NN, nVertices);
        mapResult.put(VALUE_DIAMETER, diameter);
        mapResult.put(VALUE_NUM_OF_BFS, nBFSs);
        mapResult.put(VALUE_TIME, elapsedTime);
    }

    public final Map<String, Object> getResults() {
        return Collections.unmodifiableMap(mapResult);
    }

    public abstract String getDatasetFileExtension();

    public abstract void compute();

    public abstract void disableLogger();
}
