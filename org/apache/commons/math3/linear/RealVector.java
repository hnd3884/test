package org.apache.commons.math3.linear;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import java.util.NoSuchElementException;
import org.apache.commons.math3.analysis.function.Divide;
import org.apache.commons.math3.analysis.function.Multiply;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.function.Add;
import java.util.Iterator;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;

public abstract class RealVector
{
    public abstract int getDimension();
    
    public abstract double getEntry(final int p0) throws OutOfRangeException;
    
    public abstract void setEntry(final int p0, final double p1) throws OutOfRangeException;
    
    public void addToEntry(final int index, final double increment) throws OutOfRangeException {
        this.setEntry(index, this.getEntry(index) + increment);
    }
    
    public abstract RealVector append(final RealVector p0);
    
    public abstract RealVector append(final double p0);
    
    public abstract RealVector getSubVector(final int p0, final int p1) throws NotPositiveException, OutOfRangeException;
    
    public abstract void setSubVector(final int p0, final RealVector p1) throws OutOfRangeException;
    
    public abstract boolean isNaN();
    
    public abstract boolean isInfinite();
    
    protected void checkVectorDimensions(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
    }
    
    protected void checkVectorDimensions(final int n) throws DimensionMismatchException {
        final int d = this.getDimension();
        if (d != n) {
            throw new DimensionMismatchException(d, n);
        }
    }
    
    protected void checkIndex(final int index) throws OutOfRangeException {
        if (index < 0 || index >= this.getDimension()) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0, this.getDimension() - 1);
        }
    }
    
    protected void checkIndices(final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        final int dim = this.getDimension();
        if (start < 0 || start >= dim) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, start, 0, dim - 1);
        }
        if (end < 0 || end >= dim) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, end, 0, dim - 1);
        }
        if (end < start) {
            throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, end, start, false);
        }
    }
    
    public RealVector add(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v);
        final RealVector result = v.copy();
        for (final Entry e : this) {
            final int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }
    
    public RealVector subtract(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v);
        final RealVector result = v.mapMultiply(-1.0);
        for (final Entry e : this) {
            final int index = e.getIndex();
            result.setEntry(index, e.getValue() + result.getEntry(index));
        }
        return result;
    }
    
    public RealVector mapAdd(final double d) {
        return this.copy().mapAddToSelf(d);
    }
    
    public RealVector mapAddToSelf(final double d) {
        if (d != 0.0) {
            return this.mapToSelf(FunctionUtils.fix2ndArgument(new Add(), d));
        }
        return this;
    }
    
    public abstract RealVector copy();
    
    public double dotProduct(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v);
        double d = 0.0;
        for (int n = this.getDimension(), i = 0; i < n; ++i) {
            d += this.getEntry(i) * v.getEntry(i);
        }
        return d;
    }
    
    public double cosine(final RealVector v) throws DimensionMismatchException, MathArithmeticException {
        final double norm = this.getNorm();
        final double vNorm = v.getNorm();
        if (norm == 0.0 || vNorm == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        return this.dotProduct(v) / (norm * vNorm);
    }
    
    public abstract RealVector ebeDivide(final RealVector p0) throws DimensionMismatchException;
    
    public abstract RealVector ebeMultiply(final RealVector p0) throws DimensionMismatchException;
    
    public double getDistance(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v);
        double d = 0.0;
        for (final Entry e : this) {
            final double diff = e.getValue() - v.getEntry(e.getIndex());
            d += diff * diff;
        }
        return FastMath.sqrt(d);
    }
    
    public double getNorm() {
        double sum = 0.0;
        for (final Entry e : this) {
            final double value = e.getValue();
            sum += value * value;
        }
        return FastMath.sqrt(sum);
    }
    
    public double getL1Norm() {
        double norm = 0.0;
        for (final Entry e : this) {
            norm += FastMath.abs(e.getValue());
        }
        return norm;
    }
    
    public double getLInfNorm() {
        double norm = 0.0;
        for (final Entry e : this) {
            norm = FastMath.max(norm, FastMath.abs(e.getValue()));
        }
        return norm;
    }
    
    public double getL1Distance(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v);
        double d = 0.0;
        for (final Entry e : this) {
            d += FastMath.abs(e.getValue() - v.getEntry(e.getIndex()));
        }
        return d;
    }
    
    public double getLInfDistance(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v);
        double d = 0.0;
        for (final Entry e : this) {
            d = FastMath.max(FastMath.abs(e.getValue() - v.getEntry(e.getIndex())), d);
        }
        return d;
    }
    
    public int getMinIndex() {
        int minIndex = -1;
        double minValue = Double.POSITIVE_INFINITY;
        for (final Entry entry : this) {
            if (entry.getValue() <= minValue) {
                minIndex = entry.getIndex();
                minValue = entry.getValue();
            }
        }
        return minIndex;
    }
    
    public double getMinValue() {
        final int minIndex = this.getMinIndex();
        return (minIndex < 0) ? Double.NaN : this.getEntry(minIndex);
    }
    
    public int getMaxIndex() {
        int maxIndex = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (final Entry entry : this) {
            if (entry.getValue() >= maxValue) {
                maxIndex = entry.getIndex();
                maxValue = entry.getValue();
            }
        }
        return maxIndex;
    }
    
    public double getMaxValue() {
        final int maxIndex = this.getMaxIndex();
        return (maxIndex < 0) ? Double.NaN : this.getEntry(maxIndex);
    }
    
    public RealVector mapMultiply(final double d) {
        return this.copy().mapMultiplyToSelf(d);
    }
    
    public RealVector mapMultiplyToSelf(final double d) {
        return this.mapToSelf(FunctionUtils.fix2ndArgument(new Multiply(), d));
    }
    
    public RealVector mapSubtract(final double d) {
        return this.copy().mapSubtractToSelf(d);
    }
    
    public RealVector mapSubtractToSelf(final double d) {
        return this.mapAddToSelf(-d);
    }
    
    public RealVector mapDivide(final double d) {
        return this.copy().mapDivideToSelf(d);
    }
    
    public RealVector mapDivideToSelf(final double d) {
        return this.mapToSelf(FunctionUtils.fix2ndArgument(new Divide(), d));
    }
    
    public RealMatrix outerProduct(final RealVector v) {
        final int m = this.getDimension();
        final int n = v.getDimension();
        RealMatrix product;
        if (v instanceof SparseRealVector || this instanceof SparseRealVector) {
            product = new OpenMapRealMatrix(m, n);
        }
        else {
            product = new Array2DRowRealMatrix(m, n);
        }
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                product.setEntry(i, j, this.getEntry(i) * v.getEntry(j));
            }
        }
        return product;
    }
    
    public RealVector projection(final RealVector v) throws DimensionMismatchException, MathArithmeticException {
        final double norm2 = v.dotProduct(v);
        if (norm2 == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        return v.mapMultiply(this.dotProduct(v) / v.dotProduct(v));
    }
    
    public void set(final double value) {
        for (final Entry e : this) {
            e.setValue(value);
        }
    }
    
    public double[] toArray() {
        final int dim = this.getDimension();
        final double[] values = new double[dim];
        for (int i = 0; i < dim; ++i) {
            values[i] = this.getEntry(i);
        }
        return values;
    }
    
    public RealVector unitVector() throws MathArithmeticException {
        final double norm = this.getNorm();
        if (norm == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        return this.mapDivide(norm);
    }
    
    public void unitize() throws MathArithmeticException {
        final double norm = this.getNorm();
        if (norm == 0.0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        this.mapDivideToSelf(this.getNorm());
    }
    
    public Iterator<Entry> sparseIterator() {
        return new SparseEntryIterator();
    }
    
    public Iterator<Entry> iterator() {
        final int dim = this.getDimension();
        return new Iterator<Entry>() {
            private int i = 0;
            private Entry e = new Entry();
            
            public boolean hasNext() {
                return this.i < dim;
            }
            
            public Entry next() {
                if (this.i < dim) {
                    this.e.setIndex(this.i++);
                    return this.e;
                }
                throw new NoSuchElementException();
            }
            
            public void remove() throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
        };
    }
    
    public RealVector map(final UnivariateFunction function) {
        return this.copy().mapToSelf(function);
    }
    
    public RealVector mapToSelf(final UnivariateFunction function) {
        for (final Entry e : this) {
            e.setValue(function.value(e.getValue()));
        }
        return this;
    }
    
    public RealVector combine(final double a, final double b, final RealVector y) throws DimensionMismatchException {
        return this.copy().combineToSelf(a, b, y);
    }
    
    public RealVector combineToSelf(final double a, final double b, final RealVector y) throws DimensionMismatchException {
        this.checkVectorDimensions(y);
        for (int i = 0; i < this.getDimension(); ++i) {
            final double xi = this.getEntry(i);
            final double yi = y.getEntry(i);
            this.setEntry(i, a * xi + b * yi);
        }
        return this;
    }
    
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor) {
        final int dim = this.getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; ++i) {
            visitor.visit(i, this.getEntry(i));
        }
        return visitor.end();
    }
    
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkIndices(start, end);
        visitor.start(this.getDimension(), start, end);
        for (int i = start; i <= end; ++i) {
            visitor.visit(i, this.getEntry(i));
        }
        return visitor.end();
    }
    
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor) {
        return this.walkInDefaultOrder(visitor);
    }
    
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInDefaultOrder(visitor, start, end);
    }
    
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor) {
        final int dim = this.getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; ++i) {
            this.setEntry(i, visitor.visit(i, this.getEntry(i)));
        }
        return visitor.end();
    }
    
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkIndices(start, end);
        visitor.start(this.getDimension(), start, end);
        for (int i = start; i <= end; ++i) {
            this.setEntry(i, visitor.visit(i, this.getEntry(i)));
        }
        return visitor.end();
    }
    
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor) {
        return this.walkInDefaultOrder(visitor);
    }
    
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInDefaultOrder(visitor, start, end);
    }
    
    @Override
    public boolean equals(final Object other) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public int hashCode() throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    public static RealVector unmodifiableRealVector(final RealVector v) {
        return new RealVector() {
            @Override
            public RealVector mapToSelf(final UnivariateFunction function) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public RealVector map(final UnivariateFunction function) {
                return v.map(function);
            }
            
            @Override
            public Iterator<Entry> iterator() {
                final Iterator<Entry> i = v.iterator();
                return new Iterator<Entry>() {
                    private final UnmodifiableEntry e = new UnmodifiableEntry();
                    
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    
                    public Entry next() {
                        this.e.setIndex(i.next().getIndex());
                        return this.e;
                    }
                    
                    public void remove() throws MathUnsupportedOperationException {
                        throw new MathUnsupportedOperationException();
                    }
                };
            }
            
            @Override
            public Iterator<Entry> sparseIterator() {
                final Iterator<Entry> i = v.sparseIterator();
                return new Iterator<Entry>() {
                    private final UnmodifiableEntry e = new UnmodifiableEntry();
                    
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    
                    public Entry next() {
                        this.e.setIndex(i.next().getIndex());
                        return this.e;
                    }
                    
                    public void remove() throws MathUnsupportedOperationException {
                        throw new MathUnsupportedOperationException();
                    }
                };
            }
            
            @Override
            public RealVector copy() {
                return v.copy();
            }
            
            @Override
            public RealVector add(final RealVector w) throws DimensionMismatchException {
                return v.add(w);
            }
            
            @Override
            public RealVector subtract(final RealVector w) throws DimensionMismatchException {
                return v.subtract(w);
            }
            
            @Override
            public RealVector mapAdd(final double d) {
                return v.mapAdd(d);
            }
            
            @Override
            public RealVector mapAddToSelf(final double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public RealVector mapSubtract(final double d) {
                return v.mapSubtract(d);
            }
            
            @Override
            public RealVector mapSubtractToSelf(final double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public RealVector mapMultiply(final double d) {
                return v.mapMultiply(d);
            }
            
            @Override
            public RealVector mapMultiplyToSelf(final double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public RealVector mapDivide(final double d) {
                return v.mapDivide(d);
            }
            
            @Override
            public RealVector mapDivideToSelf(final double d) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public RealVector ebeMultiply(final RealVector w) throws DimensionMismatchException {
                return v.ebeMultiply(w);
            }
            
            @Override
            public RealVector ebeDivide(final RealVector w) throws DimensionMismatchException {
                return v.ebeDivide(w);
            }
            
            @Override
            public double dotProduct(final RealVector w) throws DimensionMismatchException {
                return v.dotProduct(w);
            }
            
            @Override
            public double cosine(final RealVector w) throws DimensionMismatchException, MathArithmeticException {
                return v.cosine(w);
            }
            
            @Override
            public double getNorm() {
                return v.getNorm();
            }
            
            @Override
            public double getL1Norm() {
                return v.getL1Norm();
            }
            
            @Override
            public double getLInfNorm() {
                return v.getLInfNorm();
            }
            
            @Override
            public double getDistance(final RealVector w) throws DimensionMismatchException {
                return v.getDistance(w);
            }
            
            @Override
            public double getL1Distance(final RealVector w) throws DimensionMismatchException {
                return v.getL1Distance(w);
            }
            
            @Override
            public double getLInfDistance(final RealVector w) throws DimensionMismatchException {
                return v.getLInfDistance(w);
            }
            
            @Override
            public RealVector unitVector() throws MathArithmeticException {
                return v.unitVector();
            }
            
            @Override
            public void unitize() throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public RealMatrix outerProduct(final RealVector w) {
                return v.outerProduct(w);
            }
            
            @Override
            public double getEntry(final int index) throws OutOfRangeException {
                return v.getEntry(index);
            }
            
            @Override
            public void setEntry(final int index, final double value) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public void addToEntry(final int index, final double value) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public int getDimension() {
                return v.getDimension();
            }
            
            @Override
            public RealVector append(final RealVector w) {
                return v.append(w);
            }
            
            @Override
            public RealVector append(final double d) {
                return v.append(d);
            }
            
            @Override
            public RealVector getSubVector(final int index, final int n) throws OutOfRangeException, NotPositiveException {
                return v.getSubVector(index, n);
            }
            
            @Override
            public void setSubVector(final int index, final RealVector w) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public void set(final double value) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            @Override
            public double[] toArray() {
                return v.toArray();
            }
            
            @Override
            public boolean isNaN() {
                return v.isNaN();
            }
            
            @Override
            public boolean isInfinite() {
                return v.isInfinite();
            }
            
            @Override
            public RealVector combine(final double a, final double b, final RealVector y) throws DimensionMismatchException {
                return v.combine(a, b, y);
            }
            
            @Override
            public RealVector combineToSelf(final double a, final double b, final RealVector y) throws MathUnsupportedOperationException {
                throw new MathUnsupportedOperationException();
            }
            
            class UnmodifiableEntry extends Entry
            {
                UnmodifiableEntry() {
                    this$0.super();
                }
                
                @Override
                public double getValue() {
                    return v.getEntry(this.getIndex());
                }
                
                @Override
                public void setValue(final double value) throws MathUnsupportedOperationException {
                    throw new MathUnsupportedOperationException();
                }
            }
        };
    }
    
    protected class Entry
    {
        private int index;
        
        public Entry() {
            this.setIndex(0);
        }
        
        public double getValue() {
            return RealVector.this.getEntry(this.getIndex());
        }
        
        public void setValue(final double value) {
            RealVector.this.setEntry(this.getIndex(), value);
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public void setIndex(final int index) {
            this.index = index;
        }
    }
    
    protected class SparseEntryIterator implements Iterator<Entry>
    {
        private final int dim;
        private Entry current;
        private Entry next;
        
        protected SparseEntryIterator() {
            this.dim = RealVector.this.getDimension();
            this.current = new Entry();
            this.next = new Entry();
            if (this.next.getValue() == 0.0) {
                this.advance(this.next);
            }
        }
        
        protected void advance(final Entry e) {
            if (e == null) {
                return;
            }
            do {
                e.setIndex(e.getIndex() + 1);
            } while (e.getIndex() < this.dim && e.getValue() == 0.0);
            if (e.getIndex() >= this.dim) {
                e.setIndex(-1);
            }
        }
        
        public boolean hasNext() {
            return this.next.getIndex() >= 0;
        }
        
        public Entry next() {
            final int index = this.next.getIndex();
            if (index < 0) {
                throw new NoSuchElementException();
            }
            this.current.setIndex(index);
            this.advance(this.next);
            return this.current;
        }
        
        public void remove() throws MathUnsupportedOperationException {
            throw new MathUnsupportedOperationException();
        }
    }
}
