package it.unifi.simonesantarsiero.wcgraphs.commons;

import org.junit.Assert;
import org.junit.Test;

public class AlgorithmEnumTest {

    @Test
    public void testAlgorithmEnum() {
        Assert.assertEquals("AKIBA_CPP", AlgorithmEnum.AKIBA_CPP.getValue());
    }
}