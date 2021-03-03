package it.unifi.simonesantarsiero.wcgraphs.commons;

public enum AlgorithmEnum {
    AKIBA_CPP("AKIBA_CPP"),
    AKIBA_JAVA("AKIBA_JAVA"),
    WEBGRAPH("WEBGRAPH"),
    SUMSWEEP("SUMSWEEP"), // sumsweep == borassi
    NEWSUMSWEEP("NEWSUMSWEEP");

    private final String value;

    AlgorithmEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}