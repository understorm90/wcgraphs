package it.unifi.simonesantarsiero.wcgraphs.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public abstract class AlgorithmStrategy {
    protected Map<String, Object> mapResult;
    protected final List<String> list = new ArrayList<>();
    protected final String workingDirectory = System.getProperty("user.dir");

    public void setDatasetFile(String datasetFile) {
        list.add(datasetFile);
    }

    public void setDatasetsFromSNAP() {
        list.addAll(getPathsOfGraphsAvailableInDirectory(workingDirectory + FILE_SEPARATOR + DATASETS_PATH, getDatasetFileExtension()));
    }

    public String getGraphName(String filename) {
        String[] split = filename.split(FILE_SEPARATOR);
        return split[split.length - 1];
    }

    public final Map<String, Object> getResults() {
        return Collections.unmodifiableMap(mapResult);
    }

    public abstract String getDatasetFileExtension();

    public abstract void compute();

    public abstract void disableLogger();
}
