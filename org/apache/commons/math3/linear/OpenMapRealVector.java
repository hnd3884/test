package org.apache.commons.math3.linear;

import java.util.Iterator;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.OpenIntToDoubleHashMap;
import java.io.Serializable;

public class OpenMapRealVector extends SparseRealVector implements Serializable
{
    public static final double DEFAULT_ZERO_TOLERANCE = 1.0E-12;
    private static final long serialVersionUID = 8772222695580707260L;
    private final OpenIntToDoubleHashMap entries;
    private final int virtualSize;
    private final double epsilon;
    
    public OpenMapRealVector() {
        this(0, 1.0E-12);
    }
    
    public OpenMapRealVector(final int dimension) {
        this(dimension, 1.0E-12);
    }
    
    public OpenMapRealVector(final int dimension, final double epsilon) {
        this.virtualSize = dimension;
        this.entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
    }
    
    protected OpenMapRealVector(final OpenMapRealVector v, final int resize) {
        this.virtualSize = v.getDimension() + resize;
        this.entries = new OpenIntToDoubleHashMap(v.entries);
        this.epsilon = v.epsilon;
    }
    
    public OpenMapRealVector(final int dimension, final int expectedSize) {
        this(dimension, expectedSize, 1.0E-12);
    }
    
    public OpenMapRealVector(final int dimension, final int expectedSize, final double epsilon) {
        this.virtualSize = dimension;
        this.entries = new OpenIntToDoubleHashMap(expectedSize, 0.0);
        this.epsilon = epsilon;
    }
    
    public OpenMapRealVector(final double[] values) {
        this(values, 1.0E-12);
    }
    
    public OpenMapRealVector(final double[] values, final double epsilon) {
        this.virtualSize = values.length;
        this.entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
        for (int key = 0; key < values.length; ++key) {
            final double value = values[key];
            if (!this.isDefaultValue(value)) {
                this.entries.put(key, value);
            }
        }
    }
    
    public OpenMapRealVector(final Double[] values) {
        this(values, 1.0E-12);
    }
    
    public OpenMapRealVector(final Double[] values, final double epsilon) {
        this.virtualSize = values.length;
        this.entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = epsilon;
        for (int key = 0; key < values.length; ++key) {
            final double value = values[key];
            if (!this.isDefaultValue(value)) {
                this.entries.put(key, value);
            }
        }
    }
    
    public OpenMapRealVector(final OpenMapRealVector v) {
        this.virtualSize = v.getDimension();
        this.entries = new OpenIntToDoubleHashMap(v.getEntries());
        this.epsilon = v.epsilon;
    }
    
    public OpenMapRealVector(final RealVector v) {
        this.virtualSize = v.getDimension();
        this.entries = new OpenIntToDoubleHashMap(0.0);
        this.epsilon = 1.0E-12;
        for (int key = 0; key < this.virtualSize; ++key) {
            final double value = v.getEntry(key);
            if (!this.isDefaultValue(value)) {
                this.entries.put(key, value);
            }
        }
    }
    
    private OpenIntToDoubleHashMap getEntries() {
        return this.entries;
    }
    
    protected boolean isDefaultValue(final double value) {
        return FastMath.abs(value) < this.epsilon;
    }
    
    @Override
    public RealVector add(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return this.add((OpenMapRealVector)v);
        }
        return super.add(v);
    }
    
    public OpenMapRealVector add(final OpenMapRealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        final boolean copyThis = this.entries.size() > v.entries.size();
        final OpenMapRealVector res = copyThis ? this.copy() : v.copy();
        final OpenIntToDoubleHashMap.Iterator iter = copyThis ? v.entries.iterator() : this.entries.iterator();
        final OpenIntToDoubleHashMap randomAccess = copyThis ? this.entries : v.entries;
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (randomAccess.containsKey(key)) {
                res.setEntry(key, randomAccess.get(key) + iter.value());
            }
            else {
                res.setEntry(key, iter.value());
            }
        }
        return res;
    }
    
    public OpenMapRealVector append(final OpenMapRealVector v) {
        final OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
        final OpenIntToDoubleHashMap.Iterator iter = v.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key() + this.virtualSize, iter.value());
        }
        return res;
    }
    
    @Override
    public OpenMapRealVector append(final RealVector v) {
        if (v instanceof OpenMapRealVector) {
            return this.append((OpenMapRealVector)v);
        }
        final OpenMapRealVector res = new OpenMapRealVector(this, v.getDimension());
        for (int i = 0; i < v.getDimension(); ++i) {
            res.setEntry(i + this.virtualSize, v.getEntry(i));
        }
        return res;
    }
    
    @Override
    public OpenMapRealVector append(final double d) {
        final OpenMapRealVector res = new OpenMapRealVector(this, 1);
        res.setEntry(this.virtualSize, d);
        return res;
    }
    
    @Override
    public OpenMapRealVector copy() {
        return new OpenMapRealVector(this);
    }
    
    @Deprecated
    public double dotProduct(final OpenMapRealVector v) throws DimensionMismatchException {
        return this.dotProduct(v);
    }
    
    @Override
    public OpenMapRealVector ebeDivide(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        final OpenMapRealVector res = new OpenMapRealVector(this);
        for (int n = this.getDimension(), i = 0; i < n; ++i) {
            res.setEntry(i, this.getEntry(i) / v.getEntry(i));
        }
        return res;
    }
    
    @Override
    public OpenMapRealVector ebeMultiply(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        final OpenMapRealVector res = new OpenMapRealVector(this);
        final OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value() * v.getEntry(iter.key()));
        }
        return res;
    }
    
    @Override
    public OpenMapRealVector getSubVector(final int index, final int n) throws NotPositiveException, OutOfRangeException {
        this.checkIndex(index);
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
        }
        this.checkIndex(index + n - 1);
        final OpenMapRealVector res = new OpenMapRealVector(n);
        final int end = index + n;
        final OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (key >= index && key < end) {
                res.setEntry(key - index, iter.value());
            }
        }
        return res;
    }
    
    @Override
    public int getDimension() {
        return this.virtualSize;
    }
    
    public double getDistance(final OpenMapRealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        double res = 0.0;
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            final double delta = iter.value() - v.getEntry(key);
            res += delta * delta;
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (!this.entries.containsKey(key)) {
                final double value = iter.value();
                res += value * value;
            }
        }
        return FastMath.sqrt(res);
    }
    
    @Override
    public double getDistance(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return this.getDistance((OpenMapRealVector)v);
        }
        return super.getDistance(v);
    }
    
    @Override
    public double getEntry(final int index) throws OutOfRangeException {
        this.checkIndex(index);
        return this.entries.get(index);
    }
    
    public double getL1Distance(final OpenMapRealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        double max = 0.0;
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
            max += delta;
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (!this.entries.containsKey(key)) {
                final double delta2 = FastMath.abs(iter.value());
                max += FastMath.abs(delta2);
            }
        }
        return max;
    }
    
    @Override
    public double getL1Distance(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return this.getL1Distance((OpenMapRealVector)v);
        }
        return super.getL1Distance(v);
    }
    
    private double getLInfDistance(final OpenMapRealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        double max = 0.0;
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final double delta = FastMath.abs(iter.value() - v.getEntry(iter.key()));
            if (delta > max) {
                max = delta;
            }
        }
        iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (!this.entries.containsKey(key) && iter.value() > max) {
                max = iter.value();
            }
        }
        return max;
    }
    
    @Override
    public double getLInfDistance(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return this.getLInfDistance((OpenMapRealVector)v);
        }
        return super.getLInfDistance(v);
    }
    
    @Override
    public boolean isInfinite() {
        boolean infiniteFound = false;
        final OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final double value = iter.value();
            if (Double.isNaN(value)) {
                return false;
            }
            if (!Double.isInfinite(value)) {
                continue;
            }
            infiniteFound = true;
        }
        return infiniteFound;
    }
    
    @Override
    public boolean isNaN() {
        final OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            if (Double.isNaN(iter.value())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public OpenMapRealVector mapAdd(final double d) {
        return this.copy().mapAddToSelf(d);
    }
    
    @Override
    public OpenMapRealVector mapAddToSelf(final double d) {
        for (int i = 0; i < this.virtualSize; ++i) {
            this.setEntry(i, this.getEntry(i) + d);
        }
        return this;
    }
    
    @Override
    public void setEntry(final int index, final double value) throws OutOfRangeException {
        this.checkIndex(index);
        if (!this.isDefaultValue(value)) {
            this.entries.put(index, value);
        }
        else if (this.entries.containsKey(index)) {
            this.entries.remove(index);
        }
    }
    
    @Override
    public void setSubVector(final int index, final RealVector v) throws OutOfRangeException {
        this.checkIndex(index);
        this.checkIndex(index + v.getDimension() - 1);
        for (int i = 0; i < v.getDimension(); ++i) {
            this.setEntry(i + index, v.getEntry(i));
        }
    }
    
    @Override
    public void set(final double value) {
        for (int i = 0; i < this.virtualSize; ++i) {
            this.setEntry(i, value);
        }
    }
    
    public OpenMapRealVector subtract(final OpenMapRealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        final OpenMapRealVector res = this.copy();
        final OpenIntToDoubleHashMap.Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (this.entries.containsKey(key)) {
                res.setEntry(key, this.entries.get(key) - iter.value());
            }
            else {
                res.setEntry(key, -iter.value());
            }
        }
        return res;
    }
    
    @Override
    public RealVector subtract(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        if (v instanceof OpenMapRealVector) {
            return this.subtract((OpenMapRealVector)v);
        }
        return super.subtract(v);
    }
    
    @Override
    public OpenMapRealVector unitVector() throws MathArithmeticException {
        final OpenMapRealVector res = this.copy();
        res.unitize();
        return res;
    }
    
    @Override
    public void unitize() throws MathArithmeticException {
        final double norm = this.getNorm();
        if (this.isDefaultValue(norm)) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
        }
        final OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.entries.put(iter.key(), iter.value() / norm);
        }
    }
    
    @Override
    public double[] toArray() {
        final double[] res = new double[this.virtualSize];
        final OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res[iter.key()] = iter.value();
        }
        return res;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp = Double.doubleToLongBits(this.epsilon);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        result = 31 * result + this.virtualSize;
        final OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            temp = Double.doubleToLongBits(iter.value());
            result = 31 * result + (int)(temp ^ temp >> 32);
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OpenMapRealVector)) {
            return false;
        }
        final OpenMapRealVector other = (OpenMapRealVector)obj;
        if (this.virtualSize != other.virtualSize) {
            return false;
        }
        if (Double.doubleToLongBits(this.epsilon) != Double.doubleToLongBits(other.epsilon)) {
            return false;
        }
        OpenIntToDoubleHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final double test = other.getEntry(iter.key());
            if (Double.doubleToLongBits(test) != Double.doubleToLongBits(iter.value())) {
                return false;
            }
        }
        iter = other.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final double test = iter.value();
            if (Double.doubleToLongBits(test) != Double.doubleToLongBits(this.getEntry(iter.key()))) {
                return false;
            }
        }
        return true;
    }
    
    public double getSparsity() {
        return this.entries.size() / (double)this.getDimension();
    }
    
    @Override
    public Iterator<Entry> sparseIterator() {
        return new OpenMapSparseIterator();
    }
    
    protected class OpenMapEntry extends Entry
    {
        private final OpenIntToDoubleHashMap.Iterator iter;
        
        protected OpenMapEntry(final OpenIntToDoubleHashMap.Iterator iter) {
            this.iter = iter;
        }
        
        @Override
        public double getValue() {
            return this.iter.value();
        }
        
        @Override
        public void setValue(final double value) {
            OpenMapRealVector.this.entries.put(this.iter.key(), value);
        }
        
        @Override
        public int getIndex() {
            return this.iter.key();
        }
    }
    
    protected class OpenMapSparseIterator implements Iterator<Entry>
    {
        private final OpenIntToDoubleHashMap.Iterator iter;
        private final Entry current;
        
        protected OpenMapSparseIterator() {
            this.iter = OpenMapRealVector.this.entries.iterator();
            this.current = new OpenMapEntry(this.iter);
        }
        
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        public Entry next() {
            this.iter.advance();
            return this.current;
        }
        
        public void remove() {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}
