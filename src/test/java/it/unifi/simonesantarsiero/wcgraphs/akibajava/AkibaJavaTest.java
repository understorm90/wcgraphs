package it.unifi.simonesantarsiero.wcgraphs.akibajava;

import it.unifi.simonesantarsiero.wcgraphs.commons.DatasetLogger;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;
import static org.junit.Assert.assertEquals;

public class AkibaJavaTest {

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
    public void testAkibaJava() {
        AkibaJava.disableLogger();
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(0);
        AkibaJava sut = new AkibaJava();
        sut.setDatasetFile(filename, false);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("p2p-Gnutella04", results.get(VALUE_DATASET));
        assertEquals(10879, results.get(VALUE_NN));
        assertEquals(26, results.get(VALUE_DIAMETER));
        assertEquals(118, results.get(VALUE_NUM_OF_BFS));
    }

    @Test
    public void testAkibaJava_p2p_Gnutella05() {
        AkibaJava.disableLogger();
        String datasetsPath = getDatasetsPathFromResource();
        List<String> list = DatasetLogger.getListOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(1);
        AkibaJava sut = new AkibaJava();
        sut.setDatasetFile(filename, false);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("p2p-Gnutella05", results.get(VALUE_DATASET));
        assertEquals(8846, results.get(VALUE_NN));
        assertEquals(22, results.get(VALUE_DIAMETER));
        assertEquals(26, results.get(VALUE_NUM_OF_BFS));
    }
}