package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.OpenIntToFieldHashMap;
import org.apache.commons.math3.Field;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class SparseFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable
{
    private static final long serialVersionUID = 7841233292190413362L;
    private final Field<T> field;
    private final OpenIntToFieldHashMap<T> entries;
    private final int virtualSize;
    
    public SparseFieldVector(final Field<T> field) {
        this(field, 0);
    }
    
    public SparseFieldVector(final Field<T> field, final int dimension) {
        this.field = field;
        this.virtualSize = dimension;
        this.entries = new OpenIntToFieldHashMap<T>(field);
    }
    
    protected SparseFieldVector(final SparseFieldVector<T> v, final int resize) {
        this.field = v.field;
        this.virtualSize = v.getDimension() + resize;
        this.entries = new OpenIntToFieldHashMap<T>(v.entries);
    }
    
    public SparseFieldVector(final Field<T> field, final int dimension, final int expectedSize) {
        this.field = field;
        this.virtualSize = dimension;
        this.entries = new OpenIntToFieldHashMap<T>(field, expectedSize);
    }
    
    public SparseFieldVector(final Field<T> field, final T[] values) throws NullArgumentException {
        MathUtils.checkNotNull(values);
        this.field = field;
        this.virtualSize = values.length;
        this.entries = new OpenIntToFieldHashMap<T>(field);
        for (int key = 0; key < values.length; ++key) {
            final T value = values[key];
            this.entries.put(key, value);
        }
    }
    
    public SparseFieldVector(final SparseFieldVector<T> v) {
        this.field = v.field;
        this.virtualSize = v.getDimension();
        this.entries = new OpenIntToFieldHashMap<T>(v.getEntries());
    }
    
    private OpenIntToFieldHashMap<T> getEntries() {
        return this.entries;
    }
    
    public FieldVector<T> add(final SparseFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        final SparseFieldVector<T> res = (SparseFieldVector)this.copy();
        final OpenIntToFieldHashMap.Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            final T value = iter.value();
            if (this.entries.containsKey(key)) {
                res.setEntry(key, this.entries.get(key).add(value));
            }
            else {
                res.setEntry(key, value);
            }
        }
        return res;
    }
    
    public FieldVector<T> append(final SparseFieldVector<T> v) {
        final SparseFieldVector<T> res = new SparseFieldVector<T>(this, v.getDimension());
        final OpenIntToFieldHashMap.Iterator iter = v.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key() + this.virtualSize, iter.value());
        }
        return res;
    }
    
    public FieldVector<T> append(final FieldVector<T> v) {
        if (v instanceof SparseFieldVector) {
            return this.append((SparseFieldVector)v);
        }
        final int n = v.getDimension();
        final FieldVector<T> res = new SparseFieldVector((SparseFieldVector<FieldElement>)this, n);
        for (int i = 0; i < n; ++i) {
            res.setEntry(i + this.virtualSize, v.getEntry(i));
        }
        return res;
    }
    
    public FieldVector<T> append(final T d) throws NullArgumentException {
        MathUtils.checkNotNull(d);
        final FieldVector<T> res = new SparseFieldVector((SparseFieldVector<FieldElement>)this, 1);
        res.setEntry(this.virtualSize, d);
        return res;
    }
    
    public FieldVector<T> copy() {
        return new SparseFieldVector((SparseFieldVector<FieldElement>)this);
    }
    
    public T dotProduct(final FieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        T res = this.field.getZero();
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res = res.add(v.getEntry(iter.key()).multiply(iter.value()));
        }
        return res;
    }
    
    public FieldVector<T> ebeDivide(final FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        this.checkVectorDimensions(v.getDimension());
        final SparseFieldVector<T> res = new SparseFieldVector<T>(this);
        final OpenIntToFieldHashMap.Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value().divide(v.getEntry(iter.key())));
        }
        return res;
    }
    
    public FieldVector<T> ebeMultiply(final FieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        final SparseFieldVector<T> res = new SparseFieldVector<T>(this);
        final OpenIntToFieldHashMap.Iterator iter = res.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res.setEntry(iter.key(), iter.value().multiply(v.getEntry(iter.key())));
        }
        return res;
    }
    
    @Deprecated
    public T[] getData() {
        return this.toArray();
    }
    
    public int getDimension() {
        return this.virtualSize;
    }
    
    public T getEntry(final int index) throws OutOfRangeException {
        this.checkIndex(index);
        return this.entries.get(index);
    }
    
    public Field<T> getField() {
        return this.field;
    }
    
    public FieldVector<T> getSubVector(final int index, final int n) throws OutOfRangeException, NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
        }
        this.checkIndex(index);
        this.checkIndex(index + n - 1);
        final SparseFieldVector<T> res = new SparseFieldVector<T>(this.field, n);
        final int end = index + n;
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (key >= index && key < end) {
                res.setEntry(key - index, iter.value());
            }
        }
        return res;
    }
    
    public FieldVector<T> mapAdd(final T d) throws NullArgumentException {
        return this.copy().mapAddToSelf(d);
    }
    
    public FieldVector<T> mapAddToSelf(final T d) throws NullArgumentException {
        for (int i = 0; i < this.virtualSize; ++i) {
            this.setEntry(i, (T)this.getEntry(i).add((FieldElement<FieldElement<FieldElement<T>>>)d));
        }
        return this;
    }
    
    public FieldVector<T> mapDivide(final T d) throws NullArgumentException, MathArithmeticException {
        return this.copy().mapDivideToSelf(d);
    }
    
    public FieldVector<T> mapDivideToSelf(final T d) throws NullArgumentException, MathArithmeticException {
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.entries.put(iter.key(), iter.value().divide(d));
        }
        return this;
    }
    
    public FieldVector<T> mapInv() throws MathArithmeticException {
        return this.copy().mapInvToSelf();
    }
    
    public FieldVector<T> mapInvToSelf() throws MathArithmeticException {
        for (int i = 0; i < this.virtualSize; ++i) {
            this.setEntry(i, this.field.getOne().divide(this.getEntry(i)));
        }
        return this;
    }
    
    public FieldVector<T> mapMultiply(final T d) throws NullArgumentException {
        return this.copy().mapMultiplyToSelf(d);
    }
    
    public FieldVector<T> mapMultiplyToSelf(final T d) throws NullArgumentException {
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            this.entries.put(iter.key(), iter.value().multiply(d));
        }
        return this;
    }
    
    public FieldVector<T> mapSubtract(final T d) throws NullArgumentException {
        return this.copy().mapSubtractToSelf(d);
    }
    
    public FieldVector<T> mapSubtractToSelf(final T d) throws NullArgumentException {
        return this.mapAddToSelf(this.field.getZero().subtract(d));
    }
    
    public FieldMatrix<T> outerProduct(final SparseFieldVector<T> v) {
        final int n = v.getDimension();
        final SparseFieldMatrix<T> res = new SparseFieldMatrix<T>(this.field, this.virtualSize, n);
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final OpenIntToFieldHashMap.Iterator iter2 = v.entries.iterator();
            while (iter2.hasNext()) {
                iter2.advance();
                res.setEntry(iter.key(), iter2.key(), iter.value().multiply(iter2.value()));
            }
        }
        return res;
    }
    
    public FieldMatrix<T> outerProduct(final FieldVector<T> v) {
        if (v instanceof SparseFieldVector) {
            return this.outerProduct((SparseFieldVector)v);
        }
        final int n = v.getDimension();
        final FieldMatrix<T> res = new SparseFieldMatrix<T>(this.field, this.virtualSize, n);
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int row = iter.key();
            final FieldElement<T> value = iter.value();
            for (int col = 0; col < n; ++col) {
                res.setEntry(row, col, value.multiply(v.getEntry(col)));
            }
        }
        return res;
    }
    
    public FieldVector<T> projection(final FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        this.checkVectorDimensions(v.getDimension());
        return v.mapMultiply((T)this.dotProduct((FieldVector<FieldElement<FieldElement<FieldElement<FieldElement>>>>)v).divide((FieldElement<FieldElement<FieldElement<T>>>)v.dotProduct(v)));
    }
    
    public void set(final T value) {
        MathUtils.checkNotNull(value);
        for (int i = 0; i < this.virtualSize; ++i) {
            this.setEntry(i, value);
        }
    }
    
    public void setEntry(final int index, final T value) throws NullArgumentException, OutOfRangeException {
        MathUtils.checkNotNull(value);
        this.checkIndex(index);
        this.entries.put(index, value);
    }
    
    public void setSubVector(final int index, final FieldVector<T> v) throws OutOfRangeException {
        this.checkIndex(index);
        this.checkIndex(index + v.getDimension() - 1);
        for (int n = v.getDimension(), i = 0; i < n; ++i) {
            this.setEntry(i + index, v.getEntry(i));
        }
    }
    
    public SparseFieldVector<T> subtract(final SparseFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
        final SparseFieldVector<T> res = (SparseFieldVector)this.copy();
        final OpenIntToFieldHashMap.Iterator iter = v.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int key = iter.key();
            if (this.entries.containsKey(key)) {
                res.setEntry(key, this.entries.get(key).subtract(iter.value()));
            }
            else {
                res.setEntry(key, this.field.getZero().subtract(iter.value()));
            }
        }
        return res;
    }
    
    public FieldVector<T> subtract(final FieldVector<T> v) throws DimensionMismatchException {
        if (v instanceof SparseFieldVector) {
            return this.subtract((SparseFieldVector)v);
        }
        final int n = v.getDimension();
        this.checkVectorDimensions(n);
        final SparseFieldVector<T> res = new SparseFieldVector<T>(this);
        for (int i = 0; i < n; ++i) {
            if (this.entries.containsKey(i)) {
                res.setEntry(i, this.entries.get(i).subtract(v.getEntry(i)));
            }
            else {
                res.setEntry(i, this.field.getZero().subtract(v.getEntry(i)));
            }
        }
        return res;
    }
    
    public T[] toArray() {
        final T[] res = MathArrays.buildArray(this.field, this.virtualSize);
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            res[iter.key()] = iter.value();
        }
        return res;
    }
    
    private void checkIndex(final int index) throws OutOfRangeException {
        if (index < 0 || index >= this.getDimension()) {
            throw new OutOfRangeException(index, 0, this.getDimension() - 1);
        }
    }
    
    private void checkIndices(final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
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
    
    protected void checkVectorDimensions(final int n) throws DimensionMismatchException {
        if (this.getDimension() != n) {
            throw new DimensionMismatchException(this.getDimension(), n);
        }
    }
    
    public FieldVector<T> add(final FieldVector<T> v) throws DimensionMismatchException {
        if (v instanceof SparseFieldVector) {
            return this.add((SparseFieldVector)v);
        }
        final int n = v.getDimension();
        this.checkVectorDimensions(n);
        final SparseFieldVector<T> res = new SparseFieldVector<T>(this.field, this.getDimension());
        for (int i = 0; i < n; ++i) {
            res.setEntry(i, v.getEntry(i).add(this.getEntry(i)));
        }
        return res;
    }
    
    public T walkInDefaultOrder(final FieldVectorPreservingVisitor<T> visitor) {
        final int dim = this.getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; ++i) {
            visitor.visit(i, this.getEntry(i));
        }
        return visitor.end();
    }
    
    public T walkInDefaultOrder(final FieldVectorPreservingVisitor<T> visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkIndices(start, end);
        visitor.start(this.getDimension(), start, end);
        for (int i = start; i <= end; ++i) {
            visitor.visit(i, this.getEntry(i));
        }
        return visitor.end();
    }
    
    public T walkInOptimizedOrder(final FieldVectorPreservingVisitor<T> visitor) {
        return this.walkInDefaultOrder(visitor);
    }
    
    public T walkInOptimizedOrder(final FieldVectorPreservingVisitor<T> visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInDefaultOrder(visitor, start, end);
    }
    
    public T walkInDefaultOrder(final FieldVectorChangingVisitor<T> visitor) {
        final int dim = this.getDimension();
        visitor.start(dim, 0, dim - 1);
        for (int i = 0; i < dim; ++i) {
            this.setEntry(i, visitor.visit(i, this.getEntry(i)));
        }
        return visitor.end();
    }
    
    public T walkInDefaultOrder(final FieldVectorChangingVisitor<T> visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkIndices(start, end);
        visitor.start(this.getDimension(), start, end);
        for (int i = start; i <= end; ++i) {
            this.setEntry(i, visitor.visit(i, this.getEntry(i)));
        }
        return visitor.end();
    }
    
    public T walkInOptimizedOrder(final FieldVectorChangingVisitor<T> visitor) {
        return this.walkInDefaultOrder(visitor);
    }
    
    public T walkInOptimizedOrder(final FieldVectorChangingVisitor<T> visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInDefaultOrder(visitor, start, end);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.field == null) ? 0 : this.field.hashCode());
        result = 31 * result + this.virtualSize;
        final OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final int temp = iter.value().hashCode();
            result = 31 * result + temp;
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SparseFieldVector)) {
            return false;
        }
        final SparseFieldVector<T> other = (SparseFieldVector<T>)obj;
        if (this.field == null) {
            if (other.field != null) {
                return false;
            }
        }
        else if (!this.field.equals(other.field)) {
            return false;
        }
        if (this.virtualSize != other.virtualSize) {
            return false;
        }
        OpenIntToFieldHashMap.Iterator iter = this.entries.iterator();
        while (iter.hasNext()) {
            iter.advance();
            final T test = other.getEntry(iter.key());
            if (!test.equals(iter.value())) {
                return false;
            }
        }
        iter = other.getEntries().iterator();
        while (iter.hasNext()) {
            iter.advance();
            final T test = iter.value();
            if (!test.equals(this.getEntry(iter.key()))) {
                return false;
            }
        }
        return true;
    }
}
