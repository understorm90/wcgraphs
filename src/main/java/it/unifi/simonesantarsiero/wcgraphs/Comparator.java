package it.unifi.simonesantarsiero.wcgraphs;

import it.unifi.simonesantarsiero.wcgraphs.commons.Algorithm;

import java.util.Map;

public class Comparator {

    private Algorithm diameterCalculatorAlgorithm;

    public void setDiameterCalculatorAlgorithm(Algorithm diameterCalculatorAlgorithm) {
        this.diameterCalculatorAlgorithm = diameterCalculatorAlgorithm;
    }

    public void setDatasetFile(String datasetFile, boolean runningFromTerminal) {
        diameterCalculatorAlgorithm.setDatasetFile(datasetFile, runningFromTerminal);
    }

    public void compute() {
        diameterCalculatorAlgorithm.compute();
    }

    public Map<String, Object> getResults() {
        return diameterCalculatorAlgorithm.getResults();
    }
}
