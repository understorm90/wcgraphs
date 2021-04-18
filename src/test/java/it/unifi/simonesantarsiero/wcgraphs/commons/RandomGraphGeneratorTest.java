package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class RandomGraphGeneratorTest {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(RandomGraphGeneratorTest.class);

    private RandomGraphGenerator sut;

    private void logResults(String filename, int nVertices, int mEdges, double density) {
        String separator = "\t";
        LOGGER.info(filename + separator);
        LOGGER.info("nVertices: {}{}", nVertices, separator);
        LOGGER.info("mEdges: {}{}", mEdges, separator);
        LOGGER.info("Density (edges/vertices): {}{}\n", density, separator);
    }

    @Test
    public void testRandomGraphGenerator_3vertices() {
        sut = new RandomGraphGenerator(3);
        String basename = sut.writeToFileTSV();
        DatasetUtils.generateFilesForWebgraph(basename);
        logResults(basename, sut.getVertices(), sut.getEdges(), sut.getDensity());

        int nVerticesSquared = sut.getVertices() * sut.getVertices();
        Assert.assertEquals(3, sut.getVertices());
        Assert.assertTrue("Number of edges (" + sut.getEdges() + ") should be less than vertices^2 (" + nVerticesSquared + ")", sut.getEdges() < nVerticesSquared);
    }

    @Test
    public void testRandomGraphGenerator_4vertices() {
        sut = new RandomGraphGenerator(4);
        String basename = sut.writeToFileTSV();
        DatasetUtils.generateFilesForWebgraph(basename);
        logResults(basename, sut.getVertices(), sut.getEdges(), sut.getDensity());

        int nVerticesSquared = sut.getVertices() * sut.getVertices();
        Assert.assertEquals(4, sut.getVertices());
        Assert.assertTrue("Number of edges (" + sut.getEdges() + ") should be less than vertices^2 (" + nVerticesSquared + ")", sut.getEdges() < nVerticesSquared);
    }
}