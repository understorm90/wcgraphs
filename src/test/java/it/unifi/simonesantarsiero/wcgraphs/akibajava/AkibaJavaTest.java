package it.unifi.simonesantarsiero.wcgraphs.akibajava;

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
        return file.getAbsolutePath() + FILE_SEPARATOR;
    }

    @Test
    public void testDatasetFiles() {
        String datasetsPath = getDatasetsPathFromResource();
        System.out.println(datasetsPath);

        List<String> list = getPathsOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);

        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testAkibaJava_p2p_Gnutella04() {
        String datasetsPath = getDatasetsPathFromResource();
        AkibaJava sut = new AkibaJava();
        List<String> list = getPathsOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(0);
        sut.disableLogger();
        sut.setDatasetFile(filename);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("p2p-Gnutella04", results.get(VALUE_DATASET));
        assertEquals(10879, results.get(VALUE_VERTICES));
        assertEquals(26, results.get(VALUE_DIAMETER));
        assertEquals(118, results.get(VALUE_NUM_OF_BFS));
    }

    @Test
    public void testAkibaJava_p2p_Gnutella05() {
        String datasetsPath = getDatasetsPathFromResource();
        AkibaJava sut = new AkibaJava();
        List<String> list = getPathsOfGraphsAvailableInDirectory(datasetsPath, EXT_TSV);
        String filename = list.get(1);
        sut.disableLogger();
        sut.setDatasetFile(filename);

        sut.compute();
        Map<String, Object> results = sut.getResults();

        assertEquals("p2p-Gnutella05", results.get(VALUE_DATASET));
        assertEquals(8846, results.get(VALUE_VERTICES));
        assertEquals(22, results.get(VALUE_DIAMETER));
        assertEquals(26, results.get(VALUE_NUM_OF_BFS));
    }
}