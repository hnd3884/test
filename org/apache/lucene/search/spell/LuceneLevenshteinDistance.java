package org.apache.lucene.search.spell;

import org.apache.lucene.util.IntsRef;

public final class LuceneLevenshteinDistance implements StringDistance
{
    @Override
    public float getDistance(final String target, final String other) {
        final IntsRef targetPoints = toIntsRef(target);
        final IntsRef otherPoints = toIntsRef(other);
        final int n = targetPoints.length;
        final int m = otherPoints.length;
        final int[][] d = new int[n + 1][m + 1];
        if (n != 0 && m != 0) {
            for (int i = 0; i <= n; ++i) {
                d[i][0] = i;
            }
            for (int j = 0; j <= m; ++j) {
                d[0][j] = j;
            }
            for (int j = 1; j <= m; ++j) {
                final int t_j = otherPoints.ints[j - 1];
                for (int i = 1; i <= n; ++i) {
                    final int cost = (targetPoints.ints[i - 1] != t_j) ? 1 : 0;
                    d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
                    if (i > 1 && j > 1 && targetPoints.ints[i - 1] == otherPoints.ints[j - 2] && targetPoints.ints[i - 2] == otherPoints.ints[j - 1]) {
                        d[i][j] = Math.min(d[i][j], d[i - 2][j - 2] + cost);
                    }
                }
            }
            return 1.0f - d[n][m] / (float)Math.min(m, n);
        }
        if (n == m) {
            return 0.0f;
        }
        return (float)Math.max(n, m);
    }
    
    private static IntsRef toIntsRef(final String s) {
        final IntsRef ref = new IntsRef(s.length());
        for (int utf16Len = s.length(), i = 0, cp = 0; i < utf16Len; i += Character.charCount(cp)) {
            final int[] ints = ref.ints;
            final int n = ref.length++;
            final int codePoint = Character.codePointAt(s, i);
            ints[n] = codePoint;
            cp = codePoint;
        }
        return ref;
    }
}
