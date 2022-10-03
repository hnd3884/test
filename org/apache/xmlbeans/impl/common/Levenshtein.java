package org.apache.xmlbeans.impl.common;

public class Levenshtein
{
    private static int minimum(final int a, final int b, final int c) {
        int mi = a;
        if (b < mi) {
            mi = b;
        }
        if (c < mi) {
            mi = c;
        }
        return mi;
    }
    
    public static int distance(final String s, final String t) {
        final int n = s.length();
        final int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        final int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; ++i) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; ++j) {
            d[0][j] = j;
        }
        for (int i = 1; i <= n; ++i) {
            final char s_i = s.charAt(i - 1);
            for (int j = 1; j <= m; ++j) {
                final char t_j = t.charAt(j - 1);
                int cost;
                if (s_i == t_j) {
                    cost = 0;
                }
                else {
                    cost = 1;
                }
                d[i][j] = minimum(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
            }
        }
        return d[n][m];
    }
}
