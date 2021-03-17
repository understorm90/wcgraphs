package it.unifi.simonesantarsiero.wcgraphs.newsumsweep;

import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;
import static org.junit.Assert.assertEquals;

public class NewSumSweepTest {

    private String getDatasetsPathFromResource() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("DATASETS/test/").getFile());
        return file.getAbsolutePath() + SLASH;
    }

    @Test
    public void testDatasetFiles() {
        String datasetsPath = getDatasetsPathFromResource();

        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);

        Assert.assertEquals(3, list.size());
        Assert.assertTrue(list.contains("p2p-Gnutella04"));
        Assert.assertTrue(list.contains("p2p-Gnutella05"));
        Assert.assertTrue(list.contains("wiki-Vote"));
    }

    @Test
    public void testNewSumSweep() {
        NewSumSweep.disableLogger();
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(0);
        NewSumSweep sut = new NewSumSweep();
        sut.setDatasetFile(filename, false);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("p2p-Gnutella04", results.get(VALUE_DATASET));
        assertEquals(10879, results.get(VALUE_NN));
        assertEquals(26, results.get(VALUE_DIAMETER));
        assertEquals(17, results.get(VALUE_NUM_OF_BFS));
    }

    @Test
    public void testNewSumSweep_p2p_Gnutella04() {
        NewSumSweep.disableLogger();
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(0);
        NewSumSweep sut = new NewSumSweep();
        sut.setDatasetFile(filename, false);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("p2p-Gnutella04", results.get(VALUE_DATASET));
        assertEquals(10879, results.get(VALUE_NN));
        assertEquals(26, results.get(VALUE_DIAMETER));
        assertEquals(17, results.get(VALUE_NUM_OF_BFS));
    }

    @Test
    public void testNewSumSweep_p2p_Gnutella05() {
        NewSumSweep.disableLogger();
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(1);
        NewSumSweep sut = new NewSumSweep();
        sut.setDatasetFile(filename, false);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("p2p-Gnutella05", results.get(VALUE_DATASET));
        assertEquals(8846, results.get(VALUE_NN));
        assertEquals(22, results.get(VALUE_DIAMETER));
        assertEquals(27, results.get(VALUE_NUM_OF_BFS));
    }

    @Test
    public void testNewSumSweep_wiki_Vote() {
        NewSumSweep.disableLogger();
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(2);
        NewSumSweep sut = new NewSumSweep();
        sut.setDatasetFile(filename, false);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("wiki-Vote", results.get(VALUE_DATASET));
        assertEquals(8295, results.get(VALUE_NN));
        assertEquals(10, results.get(VALUE_DIAMETER));
        assertEquals(10, results.get(VALUE_NUM_OF_BFS));
    }
}