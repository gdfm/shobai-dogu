package com.gihtub.gdfm.shobaidogu;

/*
 * #%L
 * shobai-dogu
 * %%
 * Copyright (C) 2012 - 2013 gdfm
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.gdfm.shobaidogu.StatsUtils;

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
