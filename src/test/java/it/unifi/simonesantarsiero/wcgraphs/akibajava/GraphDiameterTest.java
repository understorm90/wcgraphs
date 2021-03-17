package it.unifi.simonesantarsiero.wcgraphs.akibajava;

import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.EXT_TSV;
import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.SLASH;

public class GraphDiameterTest {

    private String getDatasetsPathFromResource() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("DATASETS/test/").getFile());
        return file.getAbsolutePath() + SLASH;
    }

    @Test
    public void testGraphDiameter_p2pGnutella04() {
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(0);
        GraphDiameter sut = new GraphDiameter();
        System.out.println(filename);

        int diameter = sut.getDiameter(datasetsPath + filename + EXT_TSV);

        Assert.assertEquals("p2p-Gnutella04", filename);
        Assert.assertEquals(10879, sut.getNumVertices());
        Assert.assertEquals(26, diameter);
        Assert.assertEquals(118, sut.getNumBFS());
    }

    @Test
    public void testGraphDiameter_p2pGnutella05() {
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(1);
        GraphDiameter sut = new GraphDiameter();
        System.out.println(filename);

        int diameter = sut.getDiameter(datasetsPath + filename + EXT_TSV);

        Assert.assertEquals("p2p-Gnutella05", filename);
        Assert.assertEquals(8846, sut.getNumVertices());
        Assert.assertEquals(22, diameter);
        Assert.assertEquals(26, sut.getNumBFS());
    }

    @Test
    public void testGraphDiameter_wikiVote() {
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(2);
        GraphDiameter sut = new GraphDiameter();
        System.out.println(filename);

        int diameter = sut.getDiameter(datasetsPath + filename + EXT_TSV);

        Assert.assertEquals("wiki-Vote", filename);
        Assert.assertEquals(8295, sut.getNumVertices());
        Assert.assertEquals(10, diameter);
        Assert.assertEquals(3, sut.getNumBFS());
    }
}