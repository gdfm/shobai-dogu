package com.yahoo.research.bcn;

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
    public static <T> double JSdivergence(Map<T, Double> p, Map<T, Double> q) {
        checkNotNull(p);
        checkNotNull(q);
        Map<T, Double> m = Maps.newHashMap();
        // compute m = (p + q) / 2
        for (Entry<T, Double> pi : p.entrySet()) {
            m.put(pi.getKey(), pi.getValue() / 2);
        }
        for (Entry<T, Double> qi : q.entrySet()) {
            Double mi = m.get(qi.getKey());
            if (mi == null)
                mi = 0.0;
            m.put(qi.getKey(), qi.getValue() / 2 + mi.doubleValue());
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
    private static <T> double KLdivergence(Map<T, Double> pq, Map<T, Double> m) {
        checkNotNull(pq);
        checkNotNull(m);
        double sum = 0;
        for (Entry<T, Double> pEntry : pq.entrySet()) {
            double pi = pEntry.getValue();
            double mi = m.get(pEntry.getKey());
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
    public static <T extends Number> Map<String, T> topK(Map<String, T> counts, int k) {
        MinMaxPriorityQueue<Entry<String, T>> maxHeap = MinMaxPriorityQueue.<Entry<String, T>> orderedBy(new Comparator<Entry<String, T>>() {
            @Override
            public int compare(Entry<String, T> o1, Entry<String, T> o2) {
                return -1 * Double.compare(o1.getValue().doubleValue(), o2.getValue().doubleValue()); // reverse comparator
            }
        }).maximumSize(k).create();
        // keep top K
        for (Entry<String, T> e : counts.entrySet())
            maxHeap.add(e);
        Map<String, T> result = Maps.newHashMapWithExpectedSize(k);
        for (Entry<String, T> e : maxHeap)
            result.put(e.getKey(), e.getValue());
        return result;
    }

}