package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class DatasetLogger {

    private final Logger logger;
    private final List<String> headers;
    private static final String VERTICAL_BAR = "|";
    private static final String SPACE = " ";
    public static final String BOTTOM_BAR = "_";
    private String header;
    protected IntArrayList lengths;
    protected int contLengths;

    public DatasetLogger(List<String> headersList, Logger logger) {
        this.logger = logger;
        headers = headersList;
        initialize();
        printHeader();
    }

    public DatasetLogger(Logger logger) {
        this.logger = logger;
        headers = Arrays.asList(VALUE_VERTICES, VALUE_EDGES, VALUE_DENSITY, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        initialize();
        printHeader();
    }

    private void initialize() {
        header = VERTICAL_BAR + SPACE + Utils.VALUE_GRAPH + StringUtils.leftPad("", 45) + VERTICAL_BAR;
        lengths = IntArrayList.wrap(new int[]{header.length() - 4});

        for (String h : headers) {
            String newHeader = StringUtils.leftPad(h, 10);
            addToHeader(newHeader, newHeader.length());
        }
    }

    private void addToHeader(String s, int length) {
        lengths.add(length);
        header = header + SPACE + StringUtils.leftPad(s, length) + SPACE + VERTICAL_BAR;
    }

    private void printHeader() {
        logger.info("\n{}\n", header);
        // rimpiazza:
        // - spazi vuoti
        // - oppure parole
        // - oppure parentesi aperte
        // - oppure parentesi chiuse
        // - oppure slash
        header = header.replaceAll("[ \\w()/#]", BOTTOM_BAR);
        logger.info("{}", header);
    }

    private void setContLengths(int value) {
        contLengths = value;
        logger.info("\n" + VERTICAL_BAR + SPACE);
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
            logger.info("{} " + VERTICAL_BAR + SPACE, rightPad);
        } else {
            String leftPad = StringUtils.leftPad(value, length);
            logger.info("{} " + VERTICAL_BAR + SPACE, leftPad);
        }
    }

    public void printValues(Map<String, Object> values) {
        for (String h : headers) {
            String valueToPrint = String.valueOf(values.get(h));
            printValue("null".equals(valueToPrint) ? "-" : valueToPrint, false);
        }
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void printFilename(String filename) {
        setContLengths(0);
        printValue(filename, true);
    }

    public void printEmptyRow() {
        printFilename(SPACE);
        for (int i = 0; i < headers.size(); i++) {
            printValue(SPACE, false);
        }
    }
}
