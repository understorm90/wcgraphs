package it.unifi.simonesantarsiero.wcgraphs.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.DATASETS_PATH;
import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.SLASH;

public abstract class AlgorithmStrategy {
    protected Map<String, Object> mapResult;
    protected final List<String> list = new ArrayList<>();
    protected String datasetsPath;
    protected final String workingDirectory = System.getProperty("user.dir");

    public void setDatasetFile(String datasetFile, boolean runningFromTerminal) {
        datasetsPath = runningFromTerminal ? workingDirectory + SLASH : workingDirectory + DATASETS_PATH;

        if ("".equals(datasetFile)) {
            list.addAll(DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, getDatasetFileExtension()));
        } else {
            list.add(datasetFile);
        }
    }

    public final Map<String, Object> getResults() {
        return Collections.unmodifiableMap(mapResult);
    }

    public abstract String getDatasetFileExtension();

    public abstract void compute();

    public abstract void disableLogger();
}
