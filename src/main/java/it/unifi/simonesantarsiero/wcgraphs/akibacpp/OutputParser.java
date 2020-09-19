package it.unifi.simonesantarsiero.wcgraphs.akibacpp;

public class OutputParser {
    private int diameter;
    private int nVertices;
    private int nBFS;
    private double timeElapsed;

    public OutputParser(String data) {
        String[] arrayData = data.split("\n");
        String sDiameter = arrayData[0].split(":")[1].trim();
        String sVertices = arrayData[1].split(":")[1].split("->")[0].trim();
        String sNumBFS = arrayData[1].split(":")[1].split("->")[1].trim();
        String sTime = arrayData[2].split(":")[1].trim();
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
