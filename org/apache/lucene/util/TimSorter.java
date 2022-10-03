package org.apache.lucene.util;

import java.util.Arrays;

public abstract class TimSorter extends Sorter
{
    static final int MINRUN = 32;
    static final int THRESHOLD = 64;
    static final int STACKSIZE = 49;
    static final int MIN_GALLOP = 7;
    final int maxTempSlots;
    int minRun;
    int to;
    int stackSize;
    int[] runEnds;
    
    protected TimSorter(final int maxTempSlots) {
        this.runEnds = new int[50];
        this.maxTempSlots = maxTempSlots;
    }
    
    static int minRun(final int length) {
        assert length >= 32;
        int n = length;
        int r = 0;
        while (n >= 64) {
            r |= (n & 0x1);
            n >>>= 1;
        }
        final int minRun = n + r;
        assert minRun >= 32 && minRun <= 64;
        return minRun;
    }
    
    int runLen(final int i) {
        final int off = this.stackSize - i;
        return this.runEnds[off] - this.runEnds[off - 1];
    }
    
    int runBase(final int i) {
        return this.runEnds[this.stackSize - i - 1];
    }
    
    int runEnd(final int i) {
        return this.runEnds[this.stackSize - i];
    }
    
    void setRunEnd(final int i, final int runEnd) {
        this.runEnds[this.stackSize - i] = runEnd;
    }
    
    void pushRunLen(final int len) {
        this.runEnds[this.stackSize + 1] = this.runEnds[this.stackSize] + len;
        ++this.stackSize;
    }
    
    int nextRun() {
        final int runBase = this.runEnd(0);
        assert runBase < this.to;
        if (runBase == this.to - 1) {
            return 1;
        }
        int o = runBase + 2;
        if (this.compare(runBase, runBase + 1) > 0) {
            while (o < this.to && this.compare(o - 1, o) > 0) {
                ++o;
            }
            this.reverse(runBase, o);
        }
        else {
            while (o < this.to && this.compare(o - 1, o) <= 0) {
                ++o;
            }
        }
        final int runHi = Math.max(o, Math.min(this.to, runBase + this.minRun));
        this.binarySort(runBase, runHi, o);
        return runHi - runBase;
    }
    
    void ensureInvariants() {
        while (this.stackSize > 1) {
            final int runLen0 = this.runLen(0);
            final int runLen2 = this.runLen(1);
            if (this.stackSize > 2) {
                final int runLen3 = this.runLen(2);
                if (runLen3 <= runLen2 + runLen0) {
                    if (runLen3 < runLen0) {
                        this.mergeAt(1);
                        continue;
                    }
                    this.mergeAt(0);
                    continue;
                }
            }
            if (runLen2 > runLen0) {
                break;
            }
            this.mergeAt(0);
        }
    }
    
    void exhaustStack() {
        while (this.stackSize > 1) {
            this.mergeAt(0);
        }
    }
    
    void reset(final int from, final int to) {
        this.stackSize = 0;
        Arrays.fill(this.runEnds, 0);
        this.runEnds[0] = from;
        this.to = to;
        final int length = to - from;
        this.minRun = ((length <= 64) ? length : minRun(length));
    }
    
    void mergeAt(final int n) {
        assert this.stackSize >= 2;
        this.merge(this.runBase(n + 1), this.runBase(n), this.runEnd(n));
        for (int j = n + 1; j > 0; --j) {
            this.setRunEnd(j, this.runEnd(j - 1));
        }
        --this.stackSize;
    }
    
    void merge(int lo, final int mid, int hi) {
        if (this.compare(mid - 1, mid) <= 0) {
            return;
        }
        lo = this.upper2(lo, mid, mid);
        hi = this.lower2(mid, hi, mid - 1);
        if (hi - mid <= mid - lo && hi - mid <= this.maxTempSlots) {
            this.mergeHi(lo, mid, hi);
        }
        else if (mid - lo <= this.maxTempSlots) {
            this.mergeLo(lo, mid, hi);
        }
        else {
            this.mergeInPlace(lo, mid, hi);
        }
    }
    
    @Override
    public void sort(final int from, final int to) {
        this.checkRange(from, to);
        if (to - from <= 1) {
            return;
        }
        this.reset(from, to);
        do {
            this.ensureInvariants();
            this.pushRunLen(this.nextRun());
        } while (this.runEnd(0) < to);
        this.exhaustStack();
        assert this.runEnd(0) == to;
    }
    
    @Override
    void doRotate(int lo, int mid, final int hi) {
        final int len1 = mid - lo;
        final int len2 = hi - mid;
        if (len1 == len2) {
            while (mid < hi) {
                this.swap(lo++, mid++);
            }
        }
        else if (len2 < len1 && len2 <= this.maxTempSlots) {
            this.save(mid, len2);
            for (int i = lo + len1 - 1, j = hi - 1; i >= lo; --i, --j) {
                this.copy(i, j);
            }
            for (int i = 0, j = lo; i < len2; ++i, ++j) {
                this.restore(i, j);
            }
        }
        else if (len1 <= this.maxTempSlots) {
            this.save(lo, len1);
            for (int i = mid, j = lo; i < hi; ++i, ++j) {
                this.copy(i, j);
            }
            int i = 0;
            for (int j = lo + len2; j < hi; ++j) {
                this.restore(i, j);
                ++i;
            }
        }
        else {
            this.reverse(lo, mid);
            this.reverse(mid, hi);
            this.reverse(lo, hi);
        }
    }
    
    void mergeLo(final int lo, final int mid, final int hi) {
        assert this.compare(lo, mid) > 0;
        final int len1 = mid - lo;
        this.save(lo, len1);
        this.copy(mid, lo);
        int i = 0;
        int j = mid + 1;
        int dest = lo + 1;
    Label_0183:
        while (true) {
            int count = 0;
            while (count < 7) {
                if (i >= len1) {
                    break Label_0183;
                }
                if (j >= hi) {
                    break Label_0183;
                }
                if (this.compareSaved(i, j) <= 0) {
                    this.restore(i++, dest++);
                    count = 0;
                }
                else {
                    this.copy(j++, dest++);
                    ++count;
                }
            }
            final int next = this.lowerSaved3(j, hi, i);
            while (j < next) {
                this.copy(j++, dest);
                ++dest;
            }
            this.restore(i++, dest++);
        }
        while (i < len1) {
            this.restore(i++, dest);
            ++dest;
        }
        assert j == dest;
    }
    
    void mergeHi(final int lo, final int mid, final int hi) {
        assert this.compare(mid - 1, hi - 1) > 0;
        final int len2 = hi - mid;
        this.save(mid, len2);
        this.copy(mid - 1, hi - 1);
        int i = mid - 2;
        int j = len2 - 1;
        int dest = hi - 2;
    Label_0194:
        while (true) {
            int count = 0;
            while (count < 7) {
                if (i < lo) {
                    break Label_0194;
                }
                if (j < 0) {
                    break Label_0194;
                }
                if (this.compareSaved(j, i) >= 0) {
                    this.restore(j--, dest--);
                    count = 0;
                }
                else {
                    this.copy(i--, dest--);
                    ++count;
                }
            }
            final int next = this.upperSaved3(lo, i + 1, j);
            while (i >= next) {
                this.copy(i--, dest--);
            }
            this.restore(j--, dest--);
        }
        while (j >= 0) {
            this.restore(j--, dest);
            --dest;
        }
        assert i == dest;
    }
    
    int lowerSaved(int from, final int to, final int val) {
        int len = to - from;
        while (len > 0) {
            final int half = len >>> 1;
            final int mid = from + half;
            if (this.compareSaved(val, mid) > 0) {
                from = mid + 1;
                len = len - half - 1;
            }
            else {
                len = half;
            }
        }
        return from;
    }
    
    int upperSaved(int from, final int to, final int val) {
        int len = to - from;
        while (len > 0) {
            final int half = len >>> 1;
            final int mid = from + half;
            if (this.compareSaved(val, mid) < 0) {
                len = half;
            }
            else {
                from = mid + 1;
                len = len - half - 1;
            }
        }
        return from;
    }
    
    int lowerSaved3(final int from, final int to, final int val) {
        int f = from;
        int delta;
        for (int t = f + 1; t < to; t += delta << 1) {
            if (this.compareSaved(val, t) <= 0) {
                return this.lowerSaved(f, t, val);
            }
            delta = t - f;
            f = t;
        }
        return this.lowerSaved(f, to, val);
    }
    
    int upperSaved3(final int from, final int to, final int val) {
        int f = to - 1;
        int t = to;
        while (f > from) {
            if (this.compareSaved(val, f) >= 0) {
                return this.upperSaved(f, t, val);
            }
            final int delta = t - f;
            t = f;
            f -= delta << 1;
        }
        return this.upperSaved(from, t, val);
    }
    
    protected abstract void copy(final int p0, final int p1);
    
    protected abstract void save(final int p0, final int p1);
    
    protected abstract void restore(final int p0, final int p1);
    
    protected abstract int compareSaved(final int p0, final int p1);
}
