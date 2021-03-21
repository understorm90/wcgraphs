package it.unifi.simonesantarsiero.wcgraphs;

import java.util.HashMap;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class ResultFromDataset {

    protected Map<String, Object> getSampleResultsFromDataset(String datasetName, int nn, int diameter, int bfs, double time) {
        Map<String, Object> mapResult = new HashMap<>();
        mapResult.put(VALUE_DATASET, datasetName);
        mapResult.put(VALUE_VERTICES, nn);
        mapResult.put(VALUE_DIAMETER, diameter);
        mapResult.put(VALUE_NUM_OF_BFS, bfs);
        mapResult.put(VALUE_TIME, time);
        return mapResult;
    }
}
