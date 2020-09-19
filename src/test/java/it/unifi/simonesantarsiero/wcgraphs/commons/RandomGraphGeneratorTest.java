package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class RandomGraphGeneratorTest {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(RandomGraphGeneratorTest.class);

    private RandomGraphGenerator sut;

    @Test
    public void testRandomGraphGenerator() {

        sut = new RandomGraphGenerator(3);

        Assert.assertEquals(3, sut.getVertices());
        int nVerticesSquared = sut.getVertices() * sut.getVertices();
        Assert.assertTrue("Number of edges (" + sut.getEdges() + ") should be less than vertices^2 (" + nVerticesSquared + ")", sut.getEdges() < nVerticesSquared);
        LOGGER.info("nVertices: {}\n", sut.getVertices());
        LOGGER.info("mEdges: {}\n", sut.getEdges());
        LOGGER.info("Density (edges/vertices): {}\n\n", sut.getDensity());
        LOGGER.info("{}", sut);
        sut.writeToFileTSV("random-graph_" + sut.getVertices() + "_" + sut.getEdges());
    }
}