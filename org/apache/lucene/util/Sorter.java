package org.apache.lucene.util;

public abstract class Sorter
{
    static final int THRESHOLD = 20;
    
    protected Sorter() {
    }
    
    protected abstract int compare(final int p0, final int p1);
    
    protected abstract void swap(final int p0, final int p1);
    
    public abstract void sort(final int p0, final int p1);
    
    void checkRange(final int from, final int to) {
        if (to < from) {
            throw new IllegalArgumentException("'to' must be >= 'from', got from=" + from + " and to=" + to);
        }
    }
    
    void mergeInPlace(int from, final int mid, int to) {
        if (from == mid || mid == to || this.compare(mid - 1, mid) <= 0) {
            return;
        }
        if (to - from == 2) {
            this.swap(mid - 1, mid);
            return;
        }
        while (this.compare(from, mid) <= 0) {
            ++from;
        }
        while (this.compare(mid - 1, to - 1) <= 0) {
            --to;
        }
        int first_cut;
        int second_cut;
        int len12;
        if (mid - from > to - mid) {
            final int len11 = mid - from >>> 1;
            first_cut = from + len11;
            second_cut = this.lower(mid, to, first_cut);
            len12 = second_cut - mid;
        }
        else {
            len12 = to - mid >>> 1;
            second_cut = mid + len12;
            first_cut = this.upper(from, mid, second_cut);
            final int len11 = first_cut - from;
        }
        this.rotate(first_cut, mid, second_cut);
        final int new_mid = first_cut + len12;
        this.mergeInPlace(from, first_cut, new_mid);
        this.mergeInPlace(new_mid, second_cut, to);
    }
    
    int lower(int from, final int to, final int val) {
        int len = to - from;
        while (len > 0) {
            final int half = len >>> 1;
            final int mid = from + half;
            if (this.compare(mid, val) < 0) {
                from = mid + 1;
                len = len - half - 1;
            }
            else {
                len = half;
            }
        }
        return from;
    }
    
    int upper(int from, final int to, final int val) {
        int len = to - from;
        while (len > 0) {
            final int half = len >>> 1;
            final int mid = from + half;
            if (this.compare(val, mid) < 0) {
                len = half;
            }
            else {
                from = mid + 1;
                len = len - half - 1;
            }
        }
        return from;
    }
    
    int lower2(final int from, final int to, final int val) {
        int f = to - 1;
        int t = to;
        while (f > from) {
            if (this.compare(f, val) < 0) {
                return this.lower(f, t, val);
            }
            final int delta = t - f;
            t = f;
            f -= delta << 1;
        }
        return this.lower(from, t, val);
    }
    
    int upper2(final int from, final int to, final int val) {
        int f = from;
        int delta;
        for (int t = f + 1; t < to; t += delta << 1) {
            if (this.compare(t, val) > 0) {
                return this.upper(f, t, val);
            }
            delta = t - f;
            f = t;
        }
        return this.upper(f, to, val);
    }
    
    final void reverse(int from, int to) {
        --to;
        while (from < to) {
            this.swap(from, to);
            ++from;
            --to;
        }
    }
    
    final void rotate(final int lo, final int mid, final int hi) {
        assert lo <= mid && mid <= hi;
        if (lo == mid || mid == hi) {
            return;
        }
        this.doRotate(lo, mid, hi);
    }
    
    void doRotate(int lo, int mid, final int hi) {
        if (mid - lo == hi - mid) {
            while (mid < hi) {
                this.swap(lo++, mid++);
            }
        }
        else {
            this.reverse(lo, mid);
            this.reverse(mid, hi);
            this.reverse(lo, hi);
        }
    }
    
    void insertionSort(final int from, final int to) {
        for (int i = from + 1; i < to; ++i) {
            for (int j = i; j > from && this.compare(j - 1, j) > 0; --j) {
                this.swap(j - 1, j);
            }
        }
    }
    
    void binarySort(final int from, final int to) {
        this.binarySort(from, to, from + 1);
    }
    
    void binarySort(final int from, final int to, int i) {
        while (i < to) {
            int l = from;
            int h = i - 1;
            while (l <= h) {
                final int mid = l + h >>> 1;
                final int cmp = this.compare(i, mid);
                if (cmp < 0) {
                    h = mid - 1;
                }
                else {
                    l = mid + 1;
                }
            }
            switch (i - l) {
                case 2: {
                    this.swap(l + 1, l + 2);
                    this.swap(l, l + 1);
                    break;
                }
                case 1: {
                    this.swap(l, l + 1);
                    break;
                }
                case 0: {
                    break;
                }
                default: {
                    for (int j = i; j > l; --j) {
                        this.swap(j - 1, j);
                    }
                    break;
                }
            }
            ++i;
        }
    }
    
    void heapSort(final int from, final int to) {
        if (to - from <= 1) {
            return;
        }
        this.heapify(from, to);
        for (int end = to - 1; end > from; --end) {
            this.swap(from, end);
            this.siftDown(from, from, end);
        }
    }
    
    void heapify(final int from, final int to) {
        for (int i = heapParent(from, to - 1); i >= from; --i) {
            this.siftDown(i, from, to);
        }
    }
    
    void siftDown(int i, final int from, final int to) {
        for (int leftChild = heapChild(from, i); leftChild < to; leftChild = heapChild(from, i)) {
            final int rightChild = leftChild + 1;
            if (this.compare(i, leftChild) < 0) {
                if (rightChild < to && this.compare(leftChild, rightChild) < 0) {
                    this.swap(i, rightChild);
                    i = rightChild;
                }
                else {
                    this.swap(i, leftChild);
                    i = leftChild;
                }
            }
            else {
                if (rightChild >= to || this.compare(i, rightChild) >= 0) {
                    break;
                }
                this.swap(i, rightChild);
                i = rightChild;
            }
        }
    }
    
    static int heapParent(final int from, final int i) {
        return (i - 1 - from >>> 1) + from;
    }
    
    static int heapChild(final int from, final int i) {
        return (i - from << 1) + 1 + from;
    }
}
