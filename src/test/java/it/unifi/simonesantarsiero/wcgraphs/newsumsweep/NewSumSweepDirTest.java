package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import it.unimi.dsi.webgraph.ArrayListMutableGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NewSumSweepDirTest {

    private NewSumSweepDir ss;

    @Test
    public void testDiameter_example1() throws Exception {
        final ImmutableGraph graph = new ArrayListMutableGraph(3,
                new int[][]{{0, 1}, {1, 2}, {2, 1}, {1, 0}}).immutableView();
        ss = new NewSumSweepDir(graph);

        ss.runAuto();

        assertEquals(2, ss.getD());
        assertEquals(4, ss.getIterD());
    }

    @Test
    public void testDiameter_example2() throws Exception {
        final ImmutableGraph graph = new ArrayListMutableGraph(12,
                new int[][]{
                        {0, 1},
                        {1, 2}, {1, 4}, {1, 5},
                        {2, 0}, {2, 3},
                        {3, 4}, {3, 11},
                        {4, 3}, {4, 9},
                        {5, 6}, {5, 9},
                        {6, 7},
                        {7, 8},
                        {8, 5}, {8, 10},
                        {9, 10},
                        {10, 9}}).immutableView();
        ss = new NewSumSweepDir(graph);

        ss.runAuto();

        assertEquals(6, ss.getD());
        assertEquals(10, ss.getIterD());
    }

    @Test
    public void testEccentricities_example1() {
        final ImmutableGraph graph = new ArrayListMutableGraph(3,
                new int[][]{{0, 1}, {1, 2}, {2, 1}, {1, 0}}).immutableView();
        int[] expectedEcc = new int[]{2, 1, 2};

        int[] actualEcc = NewSumSweepDir.computeAllEccentricities(graph);

        assertArrayEquals(expectedEcc, actualEcc);
    }

    @Test
    public void testEccentricities_example2() {
        final ImmutableGraph graph = new ArrayListMutableGraph(12,
                new int[][]{
                        {0, 1},
                        {1, 2}, {1, 4}, {1, 5},
                        {2, 0}, {2, 3},
                        {3, 4}, {3, 11},
                        {4, 3}, {4, 9},
                        {5, 6}, {5, 9},
                        {6, 7},
                        {7, 8},
                        {8, 5}, {8, 10},
                        {9, 10},
                        {10, 9}}).immutableView();
        int[] expectedEcc = new int[]{5, 4, 6, 3, 2, 3, 4, 3, 3, 1, 1, 0};

        int[] actualEcc = NewSumSweepDir.computeAllEccentricities(graph);

        assertArrayEquals(expectedEcc, actualEcc);
    }
}