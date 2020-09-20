package it.unifi.simonesantarsiero.wcgraphs.webgraph;

import ch.qos.logback.classic.Logger;
import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import it.unimi.dsi.webgraph.ArrayListMutableGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.algo.SumSweepDirectedDiameterRadius;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class WebGraphTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(WebGraphTest.class);

    private Map<String, Object> mapResult;

    @Test
    public void testMutableGraph() {
        List<String> headersList = Arrays.asList(VALUE_NN, VALUE_DIAMETER, VALUE_NUM_OF_BFS, VALUE_TIME);
        DatasetLogger loader = new DatasetLogger(headersList, LOGGER);

        String filename = "test";
        loader.printFilename(filename);

        int expectedNodes = 3;
        ImmutableGraph graph = new ArrayListMutableGraph(expectedNodes, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 1 }, { 1, 0 } }).immutableView();

        long time = -System.currentTimeMillis();

        final SumSweepDirectedDiameterRadius ss = new SumSweepDirectedDiameterRadius(graph, SumSweepDirectedDiameterRadius.OutputLevel.ALL, null, null);
        ss.compute();

        time += System.currentTimeMillis();

        mapResult = new HashMap<>();
        mapResult.put(VALUE_DATASET, filename);
        mapResult.put(VALUE_NN, graph.numNodes());
        mapResult.put("arcs", graph.numArcs());
        mapResult.put(VALUE_DIAMETER, ss.getDiameter());
        mapResult.put(VALUE_NUM_OF_BFS, ss.getDiameterIterations());
        mapResult.put("radius", ss.getRadius());
        mapResult.put(VALUE_TIME, time / 1000d);

        loader.printValues(mapResult);

        LOGGER.info("\n\n");

        Assert.assertEquals(expectedNodes,graph.numNodes());
    }

}