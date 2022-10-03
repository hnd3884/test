package org.apache.lucene.analysis.charfilter;

import java.util.Arrays;
import org.apache.lucene.util.ArrayUtil;
import java.io.Reader;
import org.apache.lucene.analysis.CharFilter;

public abstract class BaseCharFilter extends CharFilter
{
    private int[] offsets;
    private int[] diffs;
    private int size;
    
    public BaseCharFilter(final Reader in) {
        super(in);
        this.size = 0;
    }
    
    protected int correct(final int currentOff) {
        if (this.offsets == null || currentOff < this.offsets[0]) {
            return currentOff;
        }
        int hi = this.size - 1;
        if (currentOff >= this.offsets[hi]) {
            return currentOff + this.diffs[hi];
        }
        int lo = 0;
        int mid = -1;
        while (hi >= lo) {
            mid = lo + hi >>> 1;
            if (currentOff < this.offsets[mid]) {
                hi = mid - 1;
            }
            else {
                if (currentOff <= this.offsets[mid]) {
                    return currentOff + this.diffs[mid];
                }
                lo = mid + 1;
            }
        }
        if (currentOff < this.offsets[mid]) {
            return (mid == 0) ? currentOff : (currentOff + this.diffs[mid - 1]);
        }
        return currentOff + this.diffs[mid];
    }
    
    protected int getLastCumulativeDiff() {
        return (this.offsets == null) ? 0 : this.diffs[this.size - 1];
    }
    
    protected void addOffCorrectMap(final int off, final int cumulativeDiff) {
        if (this.offsets == null) {
            this.offsets = new int[64];
            this.diffs = new int[64];
        }
        else if (this.size == this.offsets.length) {
            this.offsets = ArrayUtil.grow(this.offsets);
            this.diffs = ArrayUtil.grow(this.diffs);
        }
        assert off >= this.offsets[this.size - 1] : "Offset #" + this.size + "(" + off + ") is less than the last recorded offset " + this.offsets[this.size - 1] + "\n" + Arrays.toString(this.offsets) + "\n" + Arrays.toString(this.diffs);
        if (this.size == 0 || off != this.offsets[this.size - 1]) {
            this.offsets[this.size] = off;
            this.diffs[this.size++] = cumulativeDiff;
        }
        else {
            this.diffs[this.size - 1] = cumulativeDiff;
        }
    }
}
