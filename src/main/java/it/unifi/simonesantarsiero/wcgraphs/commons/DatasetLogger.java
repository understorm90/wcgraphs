package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class DatasetLogger {

    private Logger logger;
    private String header;
    protected IntArrayList lengths;
    protected int contLengths;
    private List<String> headers;

    public DatasetLogger(List<String> headersList, Logger logger) {
        this.logger = logger;
        header = "| " + Utils.VALUE_GRAPH + StringUtils.leftPad("", 45) + "|";
        lengths = IntArrayList.wrap(new int[]{header.length() - 4});
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
        logger.info("\n{}\n", header);
        // rimpiazza:
        // - spazi vuoti
        // - oppure parole
        // - oppure parentesi aperte
        // - oppure parentesi chiuse
        header = header.replaceAll(" |\\w|\\(|\\)", "_");
        logger.info("{}", header);
    }

    private void setContLengths(int value) {
        contLengths = value;
        logger.info("\n| ");
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
            logger.info("{} | ", rightPad);
        } else {
            String leftPad = StringUtils.leftPad(value, length);
            logger.info("{} | ", leftPad);
        }
    }

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
}
