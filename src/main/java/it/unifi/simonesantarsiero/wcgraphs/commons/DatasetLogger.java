package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DatasetLogger {

    private Logger LOGGER;

    private String header = "| Graph                        |";
    protected IntArrayList lengths = IntArrayList.wrap(new int[] { header.length() - 4 });
    protected int contLengths;
    private List<String> headers;

    public DatasetLogger(List<String> headersList, Logger logger) {
        LOGGER = logger;
        headers = headersList;

        for (String h : headers) {
            String newHeader = StringUtils.leftPad(h, 10);
            addToHeader(newHeader, newHeader.length());
        }
        printHeader();
    }

    private void addToHeader(String s, int length) {
        lengths.add(length);
        header = header + " " + StringUtils.leftPad(s, length) + " |";
    }

    private void printHeader() {
        LOGGER.info("{}\n", header);
        // rimpiazza:
        // - spazi vuoti
        // - oppure parole
        // - oppure parentesi aperte
        // - oppure parentesi chiuse
        header = header.replaceAll(" |\\w|\\(|\\)", "_");
        LOGGER.info("{}", header);
    }

    private void setContLengths(int value) {
        contLengths = value;
        LOGGER.info("\n| ");
    }

    private void printValue(String value, boolean left) {
        int length;
        if (contLengths >= lengths.size()) {
            length = 10;
        } else {
            length = lengths.getInt(contLengths++);
        }
        if (left) {
            String rightPad = StringUtils.rightPad(value, length);
            LOGGER.info("{} | ", rightPad);
        } else {
            String leftPad = StringUtils.leftPad(value, length);
            LOGGER.info("{} | ", leftPad);
        }
    }
//
//    private void printValue(String value) {
//        printValue(value, false);
//    }

    public void printValues(Map<String, Object> values) {
        for (String h : headers) {
            printValue(String.valueOf(values.get(h)), false);
        }
    }

    public void printFilename(String filename) {
        setContLengths(0);
        printValue(filename, true);
    }

    public void printEmptyRow() {
        printFilename(" ");
        for (int i = 0; i < headers.size(); i++) {
            printValue(" ", false);
        }
    }

    public static List<String> getListOfGraphsAvailableInDirectory(String path, String fileExtension) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> listOfGraphs = new ArrayList<>();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile() && listOfFile.getName().endsWith(fileExtension)) {
                    String fileName = listOfFile.getName();
                    listOfGraphs.add(fileName.substring(0, fileName.length() - fileExtension.length()));
                }
            }
            Collections.sort(listOfGraphs);
            return listOfGraphs;
        }
        return Collections.emptyList();
    }
}
