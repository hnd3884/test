package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.OutOfRangeException;
import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Comparator;
import org.apache.commons.math3.exception.MathInternalError;
import java.util.Iterator;

public class Combinations implements Iterable<int[]>
{
    private final int n;
    private final int k;
    private final IterationOrder iterationOrder;
    
    public Combinations(final int n, final int k) {
        this(n, k, IterationOrder.LEXICOGRAPHIC);
    }
    
    private Combinations(final int n, final int k, final IterationOrder iterationOrder) {
        CombinatoricsUtils.checkBinomial(n, k);
        this.n = n;
        this.k = k;
        this.iterationOrder = iterationOrder;
    }
    
    public int getN() {
        return this.n;
    }
    
    public int getK() {
        return this.k;
    }
    
    public Iterator<int[]> iterator() {
        if (this.k == 0 || this.k == this.n) {
            return new SingletonIterator(MathArrays.natural(this.k));
        }
        switch (this.iterationOrder) {
            case LEXICOGRAPHIC: {
                return new LexicographicIterator(this.n, this.k);
            }
            default: {
                throw new MathInternalError();
            }
        }
    }
    
    public Comparator<int[]> comparator() {
        return new LexicographicComparator(this.n, this.k);
    }
    
    private enum IterationOrder
    {
        LEXICOGRAPHIC;
    }
    
    private static class LexicographicIterator implements Iterator<int[]>
    {
        private final int k;
        private final int[] c;
        private boolean more;
        private int j;
        
        LexicographicIterator(final int n, final int k) {
            this.more = true;
            this.k = k;
            this.c = new int[k + 3];
            if (k == 0 || k >= n) {
                this.more = false;
                return;
            }
            for (int i = 1; i <= k; ++i) {
                this.c[i] = i - 1;
            }
            this.c[k + 1] = n;
            this.c[k + 2] = 0;
            this.j = k;
        }
        
        public boolean hasNext() {
            return this.more;
        }
        
        public int[] next() {
            if (!this.more) {
                throw new NoSuchElementException();
            }
            final int[] ret = new int[this.k];
            System.arraycopy(this.c, 1, ret, 0, this.k);
            int x = 0;
            if (this.j > 0) {
                x = this.j;
                this.c[this.j] = x;
                --this.j;
                return ret;
            }
            if (this.c[1] + 1 < this.c[2]) {
                final int[] c = this.c;
                final int n = 1;
                ++c[n];
                return ret;
            }
            this.j = 2;
            boolean stepDone = false;
            while (!stepDone) {
                this.c[this.j - 1] = this.j - 2;
                x = this.c[this.j] + 1;
                if (x == this.c[this.j + 1]) {
                    ++this.j;
                }
                else {
                    stepDone = true;
                }
            }
            if (this.j > this.k) {
                this.more = false;
                return ret;
            }
            this.c[this.j] = x;
            --this.j;
            return ret;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class SingletonIterator implements Iterator<int[]>
    {
        private final int[] singleton;
        private boolean more;
        
        SingletonIterator(final int[] singleton) {
            this.more = true;
            this.singleton = singleton;
        }
        
        public boolean hasNext() {
            return this.more;
        }
        
        public int[] next() {
            if (this.more) {
                this.more = false;
                return this.singleton;
            }
            throw new NoSuchElementException();
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static class LexicographicComparator implements Comparator<int[]>, Serializable
    {
        private static final long serialVersionUID = 20130906L;
        private final int n;
        private final int k;
        
        LexicographicComparator(final int n, final int k) {
            this.n = n;
            this.k = k;
        }
        
        public int compare(final int[] c1, final int[] c2) {
            if (c1.length != this.k) {
                throw new DimensionMismatchException(c1.length, this.k);
            }
            if (c2.length != this.k) {
                throw new DimensionMismatchException(c2.length, this.k);
            }
            final int[] c1s = MathArrays.copyOf(c1);
            Arrays.sort(c1s);
            final int[] c2s = MathArrays.copyOf(c2);
            Arrays.sort(c2s);
            final long v1 = this.lexNorm(c1s);
            final long v2 = this.lexNorm(c2s);
            if (v1 < v2) {
                return -1;
            }
            if (v1 > v2) {
                return 1;
            }
            return 0;
        }
        
        private long lexNorm(final int[] c) {
            long ret = 0L;
            for (int i = 0; i < c.length; ++i) {
                final int digit = c[i];
                if (digit < 0 || digit >= this.n) {
                    throw new OutOfRangeException(digit, 0, this.n - 1);
                }
                ret += c[i] * ArithmeticUtils.pow(this.n, i);
            }
            return ret;
        }
    }
}
