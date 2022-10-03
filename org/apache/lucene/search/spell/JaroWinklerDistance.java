package org.apache.lucene.search.spell;

import java.util.Arrays;

public class JaroWinklerDistance implements StringDistance
{
    private float threshold;
    
    public JaroWinklerDistance() {
        this.threshold = 0.7f;
    }
    
    private int[] matches(final String s1, final String s2) {
        String max;
        String min;
        if (s1.length() > s2.length()) {
            max = s1;
            min = s2;
        }
        else {
            max = s2;
            min = s1;
        }
        final int range = Math.max(max.length() / 2 - 1, 0);
        final int[] matchIndexes = new int[min.length()];
        Arrays.fill(matchIndexes, -1);
        final boolean[] matchFlags = new boolean[max.length()];
        int matches = 0;
        for (int mi = 0; mi < min.length(); ++mi) {
            final char c1 = min.charAt(mi);
            for (int xi = Math.max(mi - range, 0), xn = Math.min(mi + range + 1, max.length()); xi < xn; ++xi) {
                if (!matchFlags[xi] && c1 == max.charAt(xi)) {
                    matchFlags[matchIndexes[mi] = xi] = true;
                    ++matches;
                    break;
                }
            }
        }
        final char[] ms1 = new char[matches];
        final char[] ms2 = new char[matches];
        int i = 0;
        int si = 0;
        while (i < min.length()) {
            if (matchIndexes[i] != -1) {
                ms1[si] = min.charAt(i);
                ++si;
            }
            ++i;
        }
        i = 0;
        si = 0;
        while (i < max.length()) {
            if (matchFlags[i]) {
                ms2[si] = max.charAt(i);
                ++si;
            }
            ++i;
        }
        int transpositions = 0;
        for (int mi2 = 0; mi2 < ms1.length; ++mi2) {
            if (ms1[mi2] != ms2[mi2]) {
                ++transpositions;
            }
        }
        int prefix = 0;
        for (int mi3 = 0; mi3 < min.length() && s1.charAt(mi3) == s2.charAt(mi3); ++mi3) {
            ++prefix;
        }
        return new int[] { matches, transpositions / 2, prefix, max.length() };
    }
    
    @Override
    public float getDistance(final String s1, final String s2) {
        final int[] mtp = this.matches(s1, s2);
        final float m = (float)mtp[0];
        if (m == 0.0f) {
            return 0.0f;
        }
        final float j = (m / s1.length() + m / s2.length() + (m - mtp[1]) / m) / 3.0f;
        final float jw = (j < this.getThreshold()) ? j : (j + Math.min(0.1f, 1.0f / mtp[3]) * mtp[2] * (1.0f - j));
        return jw;
    }
    
    public void setThreshold(final float threshold) {
        this.threshold = threshold;
    }
    
    public float getThreshold() {
        return this.threshold;
    }
    
    @Override
    public int hashCode() {
        return 113 * Float.floatToIntBits(this.threshold) * this.getClass().hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj || this.getClass() != obj.getClass()) {
            return false;
        }
        final JaroWinklerDistance o = (JaroWinklerDistance)obj;
        return Float.floatToIntBits(o.threshold) == Float.floatToIntBits(this.threshold);
    }
    
    @Override
    public String toString() {
        return "jarowinkler(" + this.threshold + ")";
    }
}
