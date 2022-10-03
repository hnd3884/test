package org.apache.commons.text.similarity;

import java.util.Arrays;

public class LevenshteinDetailedDistance implements EditDistance<LevenshteinResults>
{
    private static final LevenshteinDetailedDistance DEFAULT_INSTANCE;
    private final Integer threshold;
    
    public LevenshteinDetailedDistance() {
        this(null);
    }
    
    public LevenshteinDetailedDistance(final Integer threshold) {
        if (threshold != null && threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        this.threshold = threshold;
    }
    
    @Override
    public LevenshteinResults apply(final CharSequence left, final CharSequence right) {
        if (this.threshold != null) {
            return limitedCompare(left, right, this.threshold);
        }
        return unlimitedCompare(left, right);
    }
    
    public static LevenshteinDetailedDistance getDefaultInstance() {
        return LevenshteinDetailedDistance.DEFAULT_INSTANCE;
    }
    
    public Integer getThreshold() {
        return this.threshold;
    }
    
    private static LevenshteinResults limitedCompare(CharSequence left, CharSequence right, final int threshold) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }
        int n = left.length();
        int m = right.length();
        if (n == 0) {
            return (m <= threshold) ? new LevenshteinResults(m, m, 0, 0) : new LevenshteinResults(-1, 0, 0, 0);
        }
        if (m == 0) {
            return (n <= threshold) ? new LevenshteinResults(n, 0, n, 0) : new LevenshteinResults(-1, 0, 0, 0);
        }
        boolean swapped = false;
        if (n > m) {
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
            swapped = true;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        final int[][] matrix = new int[m + 1][n + 1];
        for (int index = 0; index <= n; ++index) {
            matrix[0][index] = index;
        }
        for (int index = 0; index <= m; ++index) {
            matrix[index][0] = index;
        }
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
            if (min > max) {
                return new LevenshteinResults(-1, 0, 0, 0);
            }
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
                matrix[j][k] = d[k];
            }
            final int[] tempD = p;
            p = d;
            d = tempD;
        }
        if (p[n] <= threshold) {
            return findDetailedResults(left, right, matrix, swapped);
        }
        return new LevenshteinResults(-1, 0, 0, 0);
    }
    
    private static LevenshteinResults unlimitedCompare(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("CharSequences must not be null");
        }
        int n = left.length();
        int m = right.length();
        if (n == 0) {
            return new LevenshteinResults(m, m, 0, 0);
        }
        if (m == 0) {
            return new LevenshteinResults(n, 0, n, 0);
        }
        boolean swapped = false;
        if (n > m) {
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
            swapped = true;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        final int[][] matrix = new int[m + 1][n + 1];
        for (int index = 0; index <= n; ++index) {
            matrix[0][index] = index;
        }
        for (int index = 0; index <= m; ++index) {
            matrix[index][0] = index;
        }
        for (int i = 0; i <= n; ++i) {
            p[i] = i;
        }
        for (int j = 1; j <= m; ++j) {
            final char rightJ = right.charAt(j - 1);
            d[0] = j;
            for (int i = 1; i <= n; ++i) {
                final int cost = (left.charAt(i - 1) != rightJ) ? 1 : 0;
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
                matrix[j][i] = d[i];
            }
            final int[] tempD = p;
            p = d;
            d = tempD;
        }
        return findDetailedResults(left, right, matrix, swapped);
    }
    
    private static LevenshteinResults findDetailedResults(final CharSequence left, final CharSequence right, final int[][] matrix, final boolean swapped) {
        int delCount = 0;
        int addCount = 0;
        int subCount = 0;
        int rowIndex = right.length();
        int columnIndex = left.length();
        int dataAtLeft = 0;
        int dataAtTop = 0;
        int dataAtDiagonal = 0;
        int data = 0;
        boolean deleted = false;
        boolean added = false;
        while (rowIndex >= 0 && columnIndex >= 0) {
            if (columnIndex == 0) {
                dataAtLeft = -1;
            }
            else {
                dataAtLeft = matrix[rowIndex][columnIndex - 1];
            }
            if (rowIndex == 0) {
                dataAtTop = -1;
            }
            else {
                dataAtTop = matrix[rowIndex - 1][columnIndex];
            }
            if (rowIndex > 0 && columnIndex > 0) {
                dataAtDiagonal = matrix[rowIndex - 1][columnIndex - 1];
            }
            else {
                dataAtDiagonal = -1;
            }
            if (dataAtLeft == -1 && dataAtTop == -1 && dataAtDiagonal == -1) {
                break;
            }
            data = matrix[rowIndex][columnIndex];
            if (columnIndex > 0 && rowIndex > 0 && left.charAt(columnIndex - 1) == right.charAt(rowIndex - 1)) {
                --columnIndex;
                --rowIndex;
            }
            else {
                deleted = false;
                added = false;
                if ((data - 1 == dataAtLeft && data <= dataAtDiagonal && data <= dataAtTop) || (dataAtDiagonal == -1 && dataAtTop == -1)) {
                    --columnIndex;
                    if (swapped) {
                        ++addCount;
                        added = true;
                    }
                    else {
                        ++delCount;
                        deleted = true;
                    }
                }
                else if ((data - 1 == dataAtTop && data <= dataAtDiagonal && data <= dataAtLeft) || (dataAtDiagonal == -1 && dataAtLeft == -1)) {
                    --rowIndex;
                    if (swapped) {
                        ++delCount;
                        deleted = true;
                    }
                    else {
                        ++addCount;
                        added = true;
                    }
                }
                if (added || deleted) {
                    continue;
                }
                ++subCount;
                --columnIndex;
                --rowIndex;
            }
        }
        return new LevenshteinResults(addCount + delCount + subCount, addCount, delCount, subCount);
    }
    
    static {
        DEFAULT_INSTANCE = new LevenshteinDetailedDistance();
    }
}
