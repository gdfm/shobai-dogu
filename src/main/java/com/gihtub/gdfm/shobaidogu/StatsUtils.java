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

import static com.google.common.base.Preconditions.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.collect.Sets;
import com.google.common.math.DoubleMath;

public final class StatsUtils {

    /**
     * Computes the Jaccard overlap between two sets.
     * 
     * @param s1
     *            first set.
     * @param s2
     *            second set.
     * @return the Jaccard overlap.
     */
    public static <T> double jaccardOverlap(Set<T> s1, Set<T> s2) {
        checkNotNull(s1);
        checkNotNull(s2);
        if (s1.isEmpty() || s2.isEmpty())
            return 0;
        double intersectSize = Sets.intersection(s1, s2).size();
        double unionSize = Sets.union(s1, s2).size();
        return intersectSize / unionSize;
    }

    /**
     * Computes the fraction of source elements that are also in target.
     * 
     * @param source
     *            the source set.
     * @param target
     *            the target set.
     * @return the fraction.
     */
    public static <T> double hitPercent(Set<T> source, Set<T> target) {
        checkNotNull(source);
        checkNotNull(target);
        if (source.isEmpty() || target.isEmpty())
            return 0;
        double intersectSize = Sets.intersection(source, target).size();
        return intersectSize / source.size();
    }

    /**
     * Computes the Jensen-Shannon divergence between two distributions. The distributions are represented by maps with double values.
     * 
     * @param p
     *            the first distribution.
     * @param q
     *            the second distribution.
     * @return the JS divergence.
     */
    public static <K, V extends Number> double JSdivergence(Map<K, V> p, Map<K, V> q) {
        checkNotNull(p);
        checkNotNull(q);
        Map<K, Double> m = Maps.newHashMap();
        // compute m = (p + q) / 2
        for (Entry<K, V> pi : p.entrySet()) {
            m.put(pi.getKey(), pi.getValue().doubleValue() / 2);
        }
        for (Entry<K, V> qi : q.entrySet()) {
            Double mi = m.get(qi.getKey());
            if (mi == null)
                mi = 0.0;
            m.put(qi.getKey(), qi.getValue().doubleValue() / 2 + mi.doubleValue());
        }
        double jsd = (KLdivergence(p, m) + KLdivergence(q, m)) / 2;
        return jsd;
    }

    /**
     * Computes the Kullback–Leibler divergence between two distributions. Assumes that m = (p + q) / 2. Thus p and q are subsets of m and I can ignore corner
     * cases where frequencies are zero.
     * 
     * @param pq
     * @param m
     * @return
     */
    private static <K, V extends Number> double KLdivergence(Map<K, V> pq, Map<K, Double> m) {
        checkNotNull(pq);
        checkNotNull(m);
        double sum = 0;
        for (Entry<K, V> pEntry : pq.entrySet()) {
            double pi = pEntry.getValue().doubleValue();
            double mi = m.get(pEntry.getKey()).doubleValue();
            sum += pi * Math.log(pi / mi);
        }
        return sum;
    }

    /**
     * Computes a similarity level in [0, numLevels - 1].
     * 
     * @param similarity
     *            similarity score in [0,1].
     * @param numLevels
     *            number of discrete levels to use.
     * @return a quantized similarity level.
     */
    public static int quantizeSimilarity(double similarity, int numLevels) {
        checkArgument(similarity >= 0 && similarity <= 1, "Similarity should be in [0,1]: " + similarity);
        checkArgument(numLevels > 1, "Number of levels should be greater than one: " + numLevels);
        return (int) Math.min(Math.floor(similarity * numLevels), numLevels - 1);
    }

    /**
     * Compute Discount Cumulative Gain for a relevance vector.
     * 
     * @param relevance
     *            the vector or relevance values.
     * @return DCG.
     */
    public static double[] computeDCG(double[] relevance) {
        checkNotNull(relevance);
        checkArgument(relevance.length > 0);
        double[] dcg = Arrays.copyOf(relevance, relevance.length);
        for (int i = 1; i < dcg.length; i++)
            dcg[i] = dcg[i - 1] + dcg[i] / DoubleMath.log2(i + 1);
        return dcg;
    }

    /**
     * Compute a proxy to Ideal Discount Cumulative Gain for a relevance vector. This method simply sorts the entries by decreasing relevance before computing a
     * normal DCG.
     * 
     * @param relevance
     *            vector or relevance values.
     * @return IDCG.
     */
    public static double[] computeIDCG(double[] relevance) {
        checkNotNull(relevance);
        checkArgument(relevance.length > 0);
        double[] idcg = Arrays.copyOf(relevance, relevance.length);
        Arrays.sort(idcg);
        ArrayUtils.reverse(idcg);
        idcg = computeDCG(idcg);
        return idcg;
    }

    /**
     * Compute top-k elements with largest values in a map from string to numbers (e.g., term frequency counts).
     * 
     * @param counts
     *            the map.
     * @param k
     *            how many elements to keep.
     * @return a map with top-k elements.
     */
    public static <K, V extends Number> Map<K, V> topK(Map<K, V> counts, int k) {
        MinMaxPriorityQueue<Entry<K, V>> maxHeap = MinMaxPriorityQueue.<Entry<K, V>> orderedBy(new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return -1 * Double.compare(o1.getValue().doubleValue(), o2.getValue().doubleValue()); // reverse comparator
            }
        }).maximumSize(k).create();
        // keep top-k
        for (Entry<K, V> e : counts.entrySet())
            maxHeap.add(e);
        Map<K, V> result = Maps.newHashMapWithExpectedSize(k);
        for (Entry<K, V> e : maxHeap)
            result.put(e.getKey(), e.getValue());
        return result;
    }

    /**
     * Compute top-k elements with largest values in a map from string to Comparable objects.
     * 
     * @param counts
     *            the map.
     * @param k
     *            how many elements to keep.
     * @return a map with top-k elements.
     */
    public static <K, V extends Comparable<V>> Map<K, V> topKComparable(Map<K, V> counts, int k) {
        MinMaxPriorityQueue<Entry<K, V>> maxHeap = MinMaxPriorityQueue.<Entry<K, V>> orderedBy(new Comparator<Entry<K, V>>() {
            @Override
            public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                return -1 * o1.getValue().compareTo(o2.getValue()); // reverse comparator
            }
        }).maximumSize(k).create();
        // keep top-k
        for (Entry<K, V> e : counts.entrySet())
            maxHeap.add(e);
        Map<K, V> result = Maps.newHashMapWithExpectedSize(k);
        for (Entry<K, V> e : maxHeap)
            result.put(e.getKey(), e.getValue());
        return result;
    }

    /**
     * Normalize in place with l2 norm.
     * 
     * @param vector
     */
    public static <K> void l2NormalizeInPlace(Map<K, Double> vector) {
        if (vector == null || vector.size() == 0)
            throw new IllegalArgumentException("Cannot normalize an empy vector: " + vector);
        double normalizer = magnitude(vector);
        for (Map.Entry<K, Double> entry : vector.entrySet())
            vector.put(entry.getKey(), entry.getValue() / normalizer);
    }

    /**
     * Normalize with l2 norm.
     * 
     * @param vector
     */
    public static <K, V extends Number> Map<K, Double> l2Normalize(Map<K, V> vector) {
        if (vector == null || vector.size() == 0)
            throw new IllegalArgumentException("Cannot normalize an empy vector: " + vector);
        Map<K, Double> result = Maps.newHashMap();
        double normalizer = magnitude(vector);
        for (Map.Entry<K, V> entry : vector.entrySet())
            result.put(entry.getKey(), entry.getValue().doubleValue() / normalizer);
        return result;
    }

    /**
     * Inner (dot) product between two vectors.
     * 
     * @param smallVector
     * @param largeVector
     * @return
     */
    public static <K, V extends Number> double dotProduct(Map<K, V> smallVector, Map<K, V> largeVector) {
        double similarity = 0.0;
        for (Map.Entry<K, V> entry : smallVector.entrySet())
            if (largeVector.containsKey(entry.getKey()))
                similarity += entry.getValue().doubleValue() * largeVector.get(entry.getKey()).doubleValue();
        return similarity;
    }

    /**
     * Compute the magnitude of a vector.
     * 
     * @param vector
     * @return
     */
    public static <K, V extends Number> double magnitude(Map<K, V> vector) {
        double result = 0.0;
        for (V weight : vector.values())
            result += Math.pow(weight.doubleValue(), 2);
        result = Math.sqrt(result);
        return result;
    }

    /**
     * Cosine similarity between two vectors.
     * 
     * @param smallVector
     * @param largeVector
     * @return
     */
    public static <K, V extends Number> double cosineSimilarity(Map<K, V> smallVector, Map<K, V> largeVector) {
        double dotProd = dotProduct(smallVector, largeVector);
        double m1 = magnitude(smallVector);
        double m2 = magnitude(largeVector);
        return dotProd / (m1 * m2);
    }
}