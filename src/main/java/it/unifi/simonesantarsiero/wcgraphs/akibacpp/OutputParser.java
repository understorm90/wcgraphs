package it.unifi.simonesantarsiero.wcgraphs.akibacpp;

public class OutputParser {
    public static final String LINE_SEPARATOR = "\n";
    public static final String COLON_SEPARATOR = ":";
    public static final String ARROW_SEPARATOR = "->";

    private final int diameter;
    private final int nVertices;
    private final int nBFS;
    private final double timeElapsed;

    public OutputParser(String data) {
        String[] arrayData = data.split(LINE_SEPARATOR);
        String sDiameter = arrayData[0].split(COLON_SEPARATOR)[1].trim();
        String sVertices = arrayData[1].split(COLON_SEPARATOR)[1].split(ARROW_SEPARATOR)[0].trim();
        String sNumBFS = arrayData[1].split(COLON_SEPARATOR)[1].split(ARROW_SEPARATOR)[1].trim();
        String sTime = arrayData[2].split(COLON_SEPARATOR)[1].trim();
        sTime = sTime.substring(0, sTime.length() - 4);

        diameter = Integer.parseInt(sDiameter);
        nVertices = Integer.parseInt(sVertices);
        nBFS = Integer.parseInt(sNumBFS);
        timeElapsed = Double.parseDouble(sTime);
    }

    public int getDiameter() {
        return diameter;
    }

    public int getVertices() {
        return nVertices;
    }

    public int getBFS() {
        return nBFS;
    }

    public double getTime() {
        return timeElapsed;
    }
}
