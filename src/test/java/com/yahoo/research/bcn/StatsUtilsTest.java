package com.yahoo.research.bcn;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class StatsUtilsTest {

    @Test
    public void testJaccardOverlap() {
        // fail("Not yet implemented");
    }

    @Test
    public void testJSdivergence() {
        // fail("Not yet implemented");
    }

    @Test
    public void testToMap() {
        // fail("Not yet implemented");
    }

    @Test
    public void testQuantizeSimilarity() {
        assertEquals(0, StatsUtils.quantizeSimilarity(0.0, 5));
        assertEquals(4, StatsUtils.quantizeSimilarity(1.0, 5));
        assertEquals(5, StatsUtils.quantizeSimilarity(0.9, 6));
        assertEquals(1, StatsUtils.quantizeSimilarity(0.51, 2));
        assertEquals(1, StatsUtils.quantizeSimilarity(0.5, 3));
    }

    @Test
    public void testTopK() {
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("one", 1.0);
        map.put("two", 2.0);
        map.put("three", 3.0);

        map = StatsUtils.topK(map, 2);
        assertEquals(2, map.size());
        assertTrue(map.containsKey("two"));
        assertTrue(map.containsKey("three"));
    }

    @Test
    public void testTopKComparable() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("one", "a");
        map.put("two", "aa");
        map.put("three", "aaa");

        map = StatsUtils.topKComparable(map, 1);
        assertEquals(1, map.size());
        assertTrue(map.containsKey("three"));
        assertTrue(map.containsValue("aaa"));
    }
}
