package it.unifi.simonesantarsiero.wcgraphs.commons;

import ch.qos.logback.classic.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class PairTest {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(PairTest.class);

    @Test
    public void testAddPair() {
        ArrayList<Pair<Integer, Integer>> values = new ArrayList<>();
        Pair<Integer, Integer>p1 = new Pair<>(3, 1);
        Pair<Integer, Integer>p2 = new Pair<>(1, 0);
        Pair<Integer, Integer>p3 = new Pair<>(0, 7);
        Pair<Integer, Integer>p4 = new Pair<>(2, 2);

        values.add(p1);
        values.add(p2);
        values.add(p3);
        values.add(p4);

        assertEquals(4, values.size());
        assertEquals(new Pair<>(3, 1), values.get(0));

        for (Pair<Integer, Integer> value : values) {
            LOGGER.info("{}", value);
        }

        Collections.sort(values);

        assertEquals(new Pair<>(0, 7), values.get(0));

        LOGGER.info("");
        for (Pair<Integer, Integer> value : values) {
            LOGGER.info("{}", value);
        }
    }
}