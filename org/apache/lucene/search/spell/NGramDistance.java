package org.apache.lucene.search.spell;

public class NGramDistance implements StringDistance
{
    private int n;
    
    public NGramDistance(final int size) {
        this.n = size;
    }
    
    public NGramDistance() {
        this(2);
    }
    
    @Override
    public float getDistance(final String source, final String target) {
        final int sl = source.length();
        final int tl = target.length();
        if (sl == 0 || tl == 0) {
            if (sl == tl) {
                return 1.0f;
            }
            return 0.0f;
        }
        else {
            int cost = 0;
            if (sl < this.n || tl < this.n) {
                for (int i = 0, ni = Math.min(sl, tl); i < ni; ++i) {
                    if (source.charAt(i) == target.charAt(i)) {
                        ++cost;
                    }
                }
                return cost / (float)Math.max(sl, tl);
            }
            final char[] sa = new char[sl + this.n - 1];
            for (int j = 0; j < sa.length; ++j) {
                if (j < this.n - 1) {
                    sa[j] = '\0';
                }
                else {
                    sa[j] = source.charAt(j - this.n + 1);
                }
            }
            float[] p = new float[sl + 1];
            float[] d = new float[sl + 1];
            char[] t_j = new char[this.n];
            for (int j = 0; j <= sl; ++j) {
                p[j] = (float)j;
            }
            for (int k = 1; k <= tl; ++k) {
                if (k < this.n) {
                    for (int ti = 0; ti < this.n - k; ++ti) {
                        t_j[ti] = '\0';
                    }
                    for (int ti = this.n - k; ti < this.n; ++ti) {
                        t_j[ti] = target.charAt(ti - (this.n - k));
                    }
                }
                else {
                    t_j = target.substring(k - this.n, k).toCharArray();
                }
                d[0] = (float)k;
                for (int j = 1; j <= sl; ++j) {
                    cost = 0;
                    int tn = this.n;
                    for (int ni2 = 0; ni2 < this.n; ++ni2) {
                        if (sa[j - 1 + ni2] != t_j[ni2]) {
                            ++cost;
                        }
                        else if (sa[j - 1 + ni2] == '\0') {
                            --tn;
                        }
                    }
                    final float ec = cost / (float)tn;
                    d[j] = Math.min(Math.min(d[j - 1] + 1.0f, p[j] + 1.0f), p[j - 1] + ec);
                }
                final float[] _d = p;
                p = d;
                d = _d;
            }
            return 1.0f - p[sl] / Math.max(tl, sl);
        }
    }
    
    @Override
    public int hashCode() {
        return 1427 * this.n * this.getClass().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj || this.getClass() != obj.getClass()) {
            return false;
        }
        final NGramDistance o = (NGramDistance)obj;
        return o.n == this.n;
    }
    
    @Override
    public String toString() {
        return "ngram(" + this.n + ")";
    }
}
