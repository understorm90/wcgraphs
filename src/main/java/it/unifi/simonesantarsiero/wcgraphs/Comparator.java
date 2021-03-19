package it.unifi.simonesantarsiero.wcgraphs;

import it.unifi.simonesantarsiero.wcgraphs.commons.AlgorithmStrategy;

import java.util.Map;

public class Comparator {

    private AlgorithmStrategy diameterCalculatorAlgorithm;

    public void setAlgorithm(AlgorithmStrategy diameterCalculatorAlgorithm) {
        this.diameterCalculatorAlgorithm = diameterCalculatorAlgorithm;
    }

    public void setDatasetFile(String datasetFile) {
        diameterCalculatorAlgorithm.setDatasetFile(datasetFile);
    }

    public void compute() {
        diameterCalculatorAlgorithm.compute();
    }

    public Map<String, Object> getResults() {
        return diameterCalculatorAlgorithm.getResults();
    }

    public void disableLogger() {
        diameterCalculatorAlgorithm.disableLogger();
    }
}
