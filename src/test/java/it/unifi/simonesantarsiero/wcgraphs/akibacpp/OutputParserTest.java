package it.unifi.simonesantarsiero.wcgraphs.akibacpp;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class OutputParserTest {

    @Test
    public void testParser() {
        String expectedDiameter = "Diameter : 13\n";
        String expectedNumberOfBfsAndVertices = "#BFS : 89208 -> 494\n";
        String expectedTime = "Time : 1.834764 sec";
        String expectedOutput = expectedDiameter + expectedNumberOfBfsAndVertices + expectedTime;

        OutputParser sut = new OutputParser(expectedOutput);

        assertEquals(13, sut.getDiameter());
        assertEquals(89208, sut.getVertices());
        assertEquals(494, sut.getBFS());
        assertEquals(1.834764, sut.getTime());
    }
}