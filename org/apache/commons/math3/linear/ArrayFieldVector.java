package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;
import java.util.Arrays;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.Field;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class ArrayFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable
{
    private static final long serialVersionUID = 7648186910365927050L;
    private T[] data;
    private final Field<T> field;
    
    public ArrayFieldVector(final Field<T> field) {
        this(field, 0);
    }
    
    public ArrayFieldVector(final Field<T> field, final int size) {
        this.field = field;
        this.data = MathArrays.buildArray(field, size);
    }
    
    public ArrayFieldVector(final int size, final T preset) {
        this(preset.getField(), size);
        Arrays.fill(this.data, preset);
    }
    
    public ArrayFieldVector(final T[] d) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(d);
        try {
            this.field = d[0].getField();
            this.data = d.clone();
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] d) throws NullArgumentException {
        MathUtils.checkNotNull(d);
        this.field = field;
        this.data = d.clone();
    }
    
    public ArrayFieldVector(final T[] d, final boolean copyArray) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(d);
        if (d.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        this.field = d[0].getField();
        this.data = (copyArray ? d.clone() : d);
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] d, final boolean copyArray) throws NullArgumentException {
        MathUtils.checkNotNull(d);
        this.field = field;
        this.data = (copyArray ? d.clone() : d);
    }
    
    public ArrayFieldVector(final T[] d, final int pos, final int size) throws NullArgumentException, NumberIsTooLargeException {
        MathUtils.checkNotNull(d);
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        this.field = d[0].getField();
        System.arraycopy(d, pos, this.data = MathArrays.buildArray(this.field, size), 0, size);
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] d, final int pos, final int size) throws NullArgumentException, NumberIsTooLargeException {
        MathUtils.checkNotNull(d);
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        this.field = field;
        System.arraycopy(d, pos, this.data = MathArrays.buildArray(field, size), 0, size);
    }
    
    public ArrayFieldVector(final FieldVector<T> v) throws NullArgumentException {
        MathUtils.checkNotNull(v);
        this.field = v.getField();
        this.data = MathArrays.buildArray(this.field, v.getDimension());
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = v.getEntry(i);
        }
    }
    
    public ArrayFieldVector(final ArrayFieldVector<T> v) throws NullArgumentException {
        MathUtils.checkNotNull(v);
        this.field = v.getField();
        this.data = v.data.clone();
    }
    
    public ArrayFieldVector(final ArrayFieldVector<T> v, final boolean deep) throws NullArgumentException {
        MathUtils.checkNotNull(v);
        this.field = v.getField();
        this.data = (deep ? v.data.clone() : v.data);
    }
    
    @Deprecated
    public ArrayFieldVector(final ArrayFieldVector<T> v1, final ArrayFieldVector<T> v2) throws NullArgumentException {
        this(v1, (FieldVector)v2);
    }
    
    public ArrayFieldVector(final FieldVector<T> v1, final FieldVector<T> v2) throws NullArgumentException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        this.field = v1.getField();
        final T[] v1Data = (v1 instanceof ArrayFieldVector) ? ((ArrayFieldVector)v1).data : v1.toArray();
        final T[] v2Data = (v2 instanceof ArrayFieldVector) ? ((ArrayFieldVector)v2).data : v2.toArray();
        System.arraycopy(v1Data, 0, this.data = MathArrays.buildArray(this.field, v1Data.length + v2Data.length), 0, v1Data.length);
        System.arraycopy(v2Data, 0, this.data, v1Data.length, v2Data.length);
    }
    
    @Deprecated
    public ArrayFieldVector(final ArrayFieldVector<T> v1, final T[] v2) throws NullArgumentException {
        this((FieldVector<FieldElement>)v1, v2);
    }
    
    public ArrayFieldVector(final FieldVector<T> v1, final T[] v2) throws NullArgumentException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        this.field = v1.getField();
        final T[] v1Data = (v1 instanceof ArrayFieldVector) ? ((ArrayFieldVector)v1).data : v1.toArray();
        System.arraycopy(v1Data, 0, this.data = MathArrays.buildArray(this.field, v1Data.length + v2.length), 0, v1Data.length);
        System.arraycopy(v2, 0, this.data, v1Data.length, v2.length);
    }
    
    @Deprecated
    public ArrayFieldVector(final T[] v1, final ArrayFieldVector<T> v2) throws NullArgumentException {
        this(v1, (FieldVector<FieldElement>)v2);
    }
    
    public ArrayFieldVector(final T[] v1, final FieldVector<T> v2) throws NullArgumentException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        this.field = v2.getField();
        final T[] v2Data = (v2 instanceof ArrayFieldVector) ? ((ArrayFieldVector)v2).data : v2.toArray();
        System.arraycopy(v1, 0, this.data = MathArrays.buildArray(this.field, v1.length + v2Data.length), 0, v1.length);
        System.arraycopy(v2Data, 0, this.data, v1.length, v2Data.length);
    }
    
    public ArrayFieldVector(final T[] v1, final T[] v2) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        System.arraycopy(v1, 0, this.data = MathArrays.buildArray(v1[0].getField(), v1.length + v2.length), 0, v1.length);
        System.arraycopy(v2, 0, this.data, v1.length, v2.length);
        this.field = this.data[0].getField();
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] v1, final T[] v2) throws NullArgumentException, ZeroException {
        MathUtils.checkNotNull(v1);
        MathUtils.checkNotNull(v2);
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        System.arraycopy(v1, 0, this.data = MathArrays.buildArray(field, v1.length + v2.length), 0, v1.length);
        System.arraycopy(v2, 0, this.data, v1.length, v2.length);
        this.field = field;
    }
    
    public Field<T> getField() {
        return this.field;
    }
    
    public FieldVector<T> copy() {
        return new ArrayFieldVector((ArrayFieldVector<FieldElement>)this, true);
    }
    
    public FieldVector<T> add(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.add((ArrayFieldVector)v);
        }
        catch (final ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                out[i] = this.data[i].add(v.getEntry(i));
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> add(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].add(v.data[i]);
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public FieldVector<T> subtract(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.subtract((ArrayFieldVector)v);
        }
        catch (final ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                out[i] = this.data[i].subtract(v.getEntry(i));
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> subtract(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].subtract(v.data[i]);
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public FieldVector<T> mapAdd(final T d) throws NullArgumentException {
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].add(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapAddToSelf(final T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].add(d);
        }
        return this;
    }
    
    public FieldVector<T> mapSubtract(final T d) throws NullArgumentException {
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].subtract(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapSubtractToSelf(final T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].subtract(d);
        }
        return this;
    }
    
    public FieldVector<T> mapMultiply(final T d) throws NullArgumentException {
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].multiply(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapMultiplyToSelf(final T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].multiply(d);
        }
        return this;
    }
    
    public FieldVector<T> mapDivide(final T d) throws NullArgumentException, MathArithmeticException {
        MathUtils.checkNotNull(d);
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].divide(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapDivideToSelf(final T d) throws NullArgumentException, MathArithmeticException {
        MathUtils.checkNotNull(d);
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].divide(d);
        }
        return this;
    }
    
    public FieldVector<T> mapInv() throws MathArithmeticException {
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        final T one = this.field.getOne();
        for (int i = 0; i < this.data.length; ++i) {
            try {
                out[i] = one.divide(this.data[i]);
            }
            catch (final MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
            }
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapInvToSelf() throws MathArithmeticException {
        final T one = this.field.getOne();
        for (int i = 0; i < this.data.length; ++i) {
            try {
                this.data[i] = one.divide(this.data[i]);
            }
            catch (final MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
            }
        }
        return this;
    }
    
    public FieldVector<T> ebeMultiply(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.ebeMultiply((ArrayFieldVector)v);
        }
        catch (final ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                out[i] = this.data[i].multiply(v.getEntry(i));
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> ebeMultiply(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].multiply(v.data[i]);
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public FieldVector<T> ebeDivide(final FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        try {
            return this.ebeDivide((ArrayFieldVector)v);
        }
        catch (final ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = MathArrays.buildArray(this.field, this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                try {
                    out[i] = this.data[i].divide(v.getEntry(i));
                }
                catch (final MathArithmeticException e) {
                    throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
                }
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> ebeDivide(final ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = MathArrays.buildArray(this.field, this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            try {
                out[i] = this.data[i].divide(v.data[i]);
            }
            catch (final MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
            }
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public T[] getData() {
        return this.data.clone();
    }
    
    public T[] getDataRef() {
        return this.data;
    }
    
    public T dotProduct(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.dotProduct((ArrayFieldVector)v);
        }
        catch (final ClassCastException cce) {
            this.checkVectorDimensions(v);
            T dot = this.field.getZero();
            for (int i = 0; i < this.data.length; ++i) {
                dot = dot.add(this.data[i].multiply(v.getEntry(i)));
            }
            return dot;
        }
    }
    
    public T dotProduct(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        T dot = this.field.getZero();
        for (int i = 0; i < this.data.length; ++i) {
            dot = dot.add(this.data[i].multiply(v.data[i]));
        }
        return dot;
    }
    
    public FieldVector<T> projection(final FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        return v.mapMultiply((T)this.dotProduct((FieldVector<FieldElement<FieldElement<FieldElement<FieldElement>>>>)v).divide((FieldElement<FieldElement<FieldElement<T>>>)v.dotProduct(v)));
    }
    
    public ArrayFieldVector<T> projection(final ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        return (ArrayFieldVector)v.mapMultiply((T)this.dotProduct((ArrayFieldVector<FieldElement<FieldElement<FieldElement<FieldElement>>>>)v).divide((FieldElement<FieldElement<FieldElement<T>>>)v.dotProduct(v)));
    }
    
    public FieldMatrix<T> outerProduct(final FieldVector<T> v) {
        try {
            return this.outerProduct((ArrayFieldVector)v);
        }
        catch (final ClassCastException cce) {
            final int m = this.data.length;
            final int n = v.getDimension();
            final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(this.field, m, n);
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < n; ++j) {
                    out.setEntry(i, j, this.data[i].multiply(v.getEntry(j)));
                }
            }
            return out;
        }
    }
    
    public FieldMatrix<T> outerProduct(final ArrayFieldVector<T> v) {
        final int m = this.data.length;
        final int n = v.data.length;
        final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(this.field, m, n);
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                out.setEntry(i, j, this.data[i].multiply(v.data[j]));
            }
        }
        return out;
    }
    
    public T getEntry(final int index) {
        return this.data[index];
    }
    
    public int getDimension() {
        return this.data.length;
    }
    
    public FieldVector<T> append(final FieldVector<T> v) {
        try {
            return this.append((ArrayFieldVector)v);
        }
        catch (final ClassCastException cce) {
            return new ArrayFieldVector((ArrayFieldVector<FieldElement>)this, new ArrayFieldVector<FieldElement>((FieldVector<FieldElement>)v));
        }
    }
    
    public ArrayFieldVector<T> append(final ArrayFieldVector<T> v) {
        return new ArrayFieldVector<T>(this, v);
    }
    
    public FieldVector<T> append(final T in) {
        final T[] out = MathArrays.buildArray(this.field, this.data.length + 1);
        System.arraycopy(this.data, 0, out, 0, this.data.length);
        out[this.data.length] = in;
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> getSubVector(final int index, final int n) throws OutOfRangeException, NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
        }
        final ArrayFieldVector<T> out = new ArrayFieldVector<T>(this.field, n);
        try {
            System.arraycopy(this.data, index, out.data, 0, n);
        }
        catch (final IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + n - 1);
        }
        return out;
    }
    
    public void setEntry(final int index, final T value) {
        try {
            this.data[index] = value;
        }
        catch (final IndexOutOfBoundsException e) {
            this.checkIndex(index);
        }
    }
    
    public void setSubVector(final int index, final FieldVector<T> v) throws OutOfRangeException {
        try {
            try {
                this.set(index, (ArrayFieldVector)v);
            }
            catch (final ClassCastException cce) {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    this.data[i] = v.getEntry(i - index);
                }
            }
        }
        catch (final IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + v.getDimension() - 1);
        }
    }
    
    public void set(final int index, final ArrayFieldVector<T> v) throws OutOfRangeException {
        try {
            System.arraycopy(v.data, 0, this.data, index, v.data.length);
        }
        catch (final IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + v.data.length - 1);
        }
    }
    
    public void set(final T value) {
        Arrays.fill(this.data, value);
    }
    
    public T[] toArray() {
        return this.data.clone();
    }
    
    protected void checkVectorDimensions(final FieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
    }
    
    protected void checkVectorDimensions(final int n) throws DimensionMismatchException {
        if (this.data.length != n) {
            throw new DimensionMismatchException(this.data.length, n);
        }
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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        try {
            final FieldVector<T> rhs = (FieldVector<T>)other;
            if (this.data.length != rhs.getDimension()) {
                return false;
            }
            for (int i = 0; i < this.data.length; ++i) {
                if (!this.data[i].equals(rhs.getEntry(i))) {
                    return false;
                }
            }
            return true;
        }
        catch (final ClassCastException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int h = 3542;
        for (final T a : this.data) {
            h ^= a.hashCode();
        }
        return h;
    }
    
    private void checkIndex(final int index) throws OutOfRangeException {
        if (index < 0 || index >= this.getDimension()) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0, this.getDimension() - 1);
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
}
