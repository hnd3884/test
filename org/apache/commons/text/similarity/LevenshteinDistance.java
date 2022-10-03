package org.apache.commons.text.similarity;

import java.util.Arrays;

public class LevenshteinDistance implements EditDistance<Integer>
{
    private static final LevenshteinDistance DEFAULT_INSTANCE;
    private final Integer threshold;
    
    public LevenshteinDistance() {
        this(null);
    }
    
    public LevenshteinDistance(final Integer threshold) {
        if (threshold != null && threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        this.threshold = threshold;
    }
    
    @Override
    public Integer apply(final CharSequence left, final CharSequence right) {
        if (this.threshold != null) {
            return limitedCompare(left, right, this.threshold);
        }
        return unlimitedCompare(left, right);
    }
    
    public static LevenshteinDistance getDefaultInstance() {
        return LevenshteinDistance.DEFAULT_INSTANCE;
    }
    
    public Integer getThreshold() {
        return this.threshold;
    }
    
    private static int limitedCompare(CharSequence left, CharSequence right, final int threshold) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        int n = left.length();
        int m = right.length();
        if (n == 0) {
            return (m <= threshold) ? m : -1;
        }
        if (m == 0) {
            return (n <= threshold) ? n : -1;
        }
        if (n > m) {
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }
        if (m - n > threshold) {
            return -1;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        final int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; ++i) {
            p[i] = i;
        }
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);
        for (int j = 1; j <= m; ++j) {
            final char rightJ = right.charAt(j - 1);
            d[0] = j;
            final int min = Math.max(1, j - threshold);
            final int max = (j > Integer.MAX_VALUE - threshold) ? n : Math.min(n, j + threshold);
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }
            for (int k = min; k <= max; ++k) {
                if (left.charAt(k - 1) == rightJ) {
                    d[k] = p[k - 1];
                }
                else {
                    d[k] = 1 + Math.min(Math.min(d[k - 1], p[k]), p[k - 1]);
                }
            }
            final int[] tempD = p;
            p = d;
            d = tempD;
        }
        if (p[n] <= threshold) {
            return p[n];
        }
        return -1;
    }
    
    private static int unlimitedCompare(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        int n = left.length();
        int m = right.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        if (n > m) {
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }
        final int[] p = new int[n + 1];
        for (int i = 0; i <= n; ++i) {
            p[i] = i;
        }
        for (int j = 1; j <= m; ++j) {
            int upperLeft = p[0];
            final char rightJ = right.charAt(j - 1);
            p[0] = j;
            for (int i = 1; i <= n; ++i) {
                final int upper = p[i];
                final int cost = (left.charAt(i - 1) != rightJ) ? 1 : 0;
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }
        return p[n];
    }
    
    static {
        DEFAULT_INSTANCE = new LevenshteinDistance();
    }
}
