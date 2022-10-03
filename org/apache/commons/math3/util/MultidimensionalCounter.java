package org.apache.commons.math3.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class MultidimensionalCounter implements Iterable<Integer>
{
    private final int dimension;
    private final int[] uniCounterOffset;
    private final int[] size;
    private final int totalSize;
    private final int last;
    
    public MultidimensionalCounter(final int... size) throws NotStrictlyPositiveException {
        this.dimension = size.length;
        this.size = MathArrays.copyOf(size);
        this.uniCounterOffset = new int[this.dimension];
        this.last = this.dimension - 1;
        int tS = size[this.last];
        for (int i = 0; i < this.last; ++i) {
            int count = 1;
            for (int j = i + 1; j < this.dimension; ++j) {
                count *= size[j];
            }
            this.uniCounterOffset[i] = count;
            tS *= size[i];
        }
        this.uniCounterOffset[this.last] = 0;
        if (tS <= 0) {
            throw new NotStrictlyPositiveException(tS);
        }
        this.totalSize = tS;
    }
    
    public Iterator iterator() {
        return new Iterator();
    }
    
    public int getDimension() {
        return this.dimension;
    }
    
    public int[] getCounts(final int index) throws OutOfRangeException {
        if (index < 0 || index >= this.totalSize) {
            throw new OutOfRangeException(index, 0, this.totalSize);
        }
        final int[] indices = new int[this.dimension];
        int count = 0;
        for (int i = 0; i < this.last; ++i) {
            int idx;
            int offset;
            for (idx = 0, offset = this.uniCounterOffset[i]; count <= index; count += offset, ++idx) {}
            --idx;
            count -= offset;
            indices[i] = idx;
        }
        indices[this.last] = index - count;
        return indices;
    }
    
    public int getCount(final int... c) throws OutOfRangeException, DimensionMismatchException {
        if (c.length != this.dimension) {
            throw new DimensionMismatchException(c.length, this.dimension);
        }
        int count = 0;
        for (int i = 0; i < this.dimension; ++i) {
            final int index = c[i];
            if (index < 0 || index >= this.size[i]) {
                throw new OutOfRangeException(index, 0, this.size[i] - 1);
            }
            count += this.uniCounterOffset[i] * c[i];
        }
        return count + c[this.last];
    }
    
    public int getSize() {
        return this.totalSize;
    }
    
    public int[] getSizes() {
        return MathArrays.copyOf(this.size);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.dimension; ++i) {
            sb.append("[").append(this.getCount(i)).append("]");
        }
        return sb.toString();
    }
    
    public class Iterator implements java.util.Iterator<Integer>
    {
        private final int[] counter;
        private int count;
        private final int maxCount;
        
        Iterator() {
            this.counter = new int[MultidimensionalCounter.this.dimension];
            this.count = -1;
            this.maxCount = MultidimensionalCounter.this.totalSize - 1;
            this.counter[MultidimensionalCounter.this.last] = -1;
        }
        
        public boolean hasNext() {
            return this.count < this.maxCount;
        }
        
        public Integer next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            for (int i = MultidimensionalCounter.this.last; i >= 0; --i) {
                if (this.counter[i] != MultidimensionalCounter.this.size[i] - 1) {
                    final int[] counter = this.counter;
                    final int n = i;
                    ++counter[n];
                    break;
                }
                this.counter[i] = 0;
            }
            return ++this.count;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public int[] getCounts() {
            return MathArrays.copyOf(this.counter);
        }
        
        public int getCount(final int dim) {
            return this.counter[dim];
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
