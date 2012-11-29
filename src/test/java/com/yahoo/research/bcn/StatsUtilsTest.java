package com.yahoo.research.bcn;

import static org.junit.Assert.assertEquals;

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
    public void testGetBufferedReader() {
        // fail("Not yet implemented");
    }

    @Test
    public void testReadStopwords() {
        // fail("Not yet implemented");
    }

    @Test
    public void testReadIdfs() {
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
}
