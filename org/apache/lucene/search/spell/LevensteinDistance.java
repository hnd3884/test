package org.apache.lucene.search.spell;

public final class LevensteinDistance implements StringDistance
{
    @Override
    public float getDistance(final String target, final String other) {
        final char[] sa = target.toCharArray();
        final int n = sa.length;
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        final int m = other.length();
        if (n != 0 && m != 0) {
            for (int i = 0; i <= n; ++i) {
                p[i] = i;
            }
            for (int j = 1; j <= m; ++j) {
                final char t_j = other.charAt(j - 1);
                d[0] = j;
                for (int i = 1; i <= n; ++i) {
                    final int cost = (sa[i - 1] != t_j) ? 1 : 0;
                    d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
                }
                final int[] _d = p;
                p = d;
                d = _d;
            }
            return 1.0f - p[n] / (float)Math.max(other.length(), sa.length);
        }
        if (n == m) {
            return 1.0f;
        }
        return 0.0f;
    }
    
    @Override
    public int hashCode() {
        return 163 * this.getClass().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (null != obj && this.getClass() == obj.getClass());
    }
    
    @Override
    public String toString() {
        return "levenstein";
    }
}
