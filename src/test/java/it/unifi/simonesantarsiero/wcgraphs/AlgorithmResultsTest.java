package it.unifi.simonesantarsiero.wcgraphs;

import org.junit.Assert;
import org.junit.Test;

import static it.unifi.simonesantarsiero.wcgraphs.commons.Utils.*;

public class AlgorithmResultsTest extends ResultFromDataset {

    @Test
    public void testAlgorithmResults() {
        AlgorithmResults sut = new AlgorithmResults("algoritmo-prova");

        sut.add(getSampleResultsFromDataset("dataset1", 1,22,33,44.5));
        sut.add(getSampleResultsFromDataset("dataset2", 5,55,555,5555.5));
        sut.add(getSampleResultsFromDataset("dataset3", 2,2,2,2.2));

        Assert.assertEquals("algoritmo-prova", sut.getAlgorithmName());
        Assert.assertEquals(3, sut.size());
        int datasetIndex = 0;
        Assert.assertEquals("dataset1", sut.get(datasetIndex).get(VALUE_DATASET));
        Assert.assertEquals(1, sut.get(datasetIndex).get(VALUE_NN));
        Assert.assertEquals(22, sut.get(datasetIndex).get(VALUE_DIAMETER));
        Assert.assertEquals(33, sut.get(datasetIndex).get(VALUE_NUM_OF_BFS));
        Assert.assertEquals(44.5, sut.get(datasetIndex).get(VALUE_TIME));
        Assert.assertEquals("dataset2", sut.get(1).get(VALUE_DATASET));

    }
}
