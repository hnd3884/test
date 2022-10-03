package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.Iterator;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.Arrays;
import java.io.Serializable;

public class ArrayRealVector extends RealVector implements Serializable
{
    private static final long serialVersionUID = -1097961340710804027L;
    private static final RealVectorFormat DEFAULT_FORMAT;
    private double[] data;
    
    public ArrayRealVector() {
        this.data = new double[0];
    }
    
    public ArrayRealVector(final int size) {
        this.data = new double[size];
    }
    
    public ArrayRealVector(final int size, final double preset) {
        Arrays.fill(this.data = new double[size], preset);
    }
    
    public ArrayRealVector(final double[] d) {
        this.data = d.clone();
    }
    
    public ArrayRealVector(final double[] d, final boolean copyArray) throws NullArgumentException {
        if (d == null) {
            throw new NullArgumentException();
        }
        this.data = (copyArray ? d.clone() : d);
    }
    
    public ArrayRealVector(final double[] d, final int pos, final int size) throws NullArgumentException, NumberIsTooLargeException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        System.arraycopy(d, pos, this.data = new double[size], 0, size);
    }
    
    public ArrayRealVector(final Double[] d) {
        this.data = new double[d.length];
        for (int i = 0; i < d.length; ++i) {
            this.data[i] = d[i];
        }
    }
    
    public ArrayRealVector(final Double[] d, final int pos, final int size) throws NullArgumentException, NumberIsTooLargeException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        this.data = new double[size];
        for (int i = pos; i < pos + size; ++i) {
            this.data[i - pos] = d[i];
        }
    }
    
    public ArrayRealVector(final RealVector v) throws NullArgumentException {
        if (v == null) {
            throw new NullArgumentException();
        }
        this.data = new double[v.getDimension()];
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = v.getEntry(i);
        }
    }
    
    public ArrayRealVector(final ArrayRealVector v) throws NullArgumentException {
        this(v, true);
    }
    
    public ArrayRealVector(final ArrayRealVector v, final boolean deep) {
        this.data = (deep ? v.data.clone() : v.data);
    }
    
    public ArrayRealVector(final ArrayRealVector v1, final ArrayRealVector v2) {
        this.data = new double[v1.data.length + v2.data.length];
        System.arraycopy(v1.data, 0, this.data, 0, v1.data.length);
        System.arraycopy(v2.data, 0, this.data, v1.data.length, v2.data.length);
    }
    
    public ArrayRealVector(final ArrayRealVector v1, final RealVector v2) {
        final int l1 = v1.data.length;
        final int l2 = v2.getDimension();
        this.data = new double[l1 + l2];
        System.arraycopy(v1.data, 0, this.data, 0, l1);
        for (int i = 0; i < l2; ++i) {
            this.data[l1 + i] = v2.getEntry(i);
        }
    }
    
    public ArrayRealVector(final RealVector v1, final ArrayRealVector v2) {
        final int l1 = v1.getDimension();
        final int l2 = v2.data.length;
        this.data = new double[l1 + l2];
        for (int i = 0; i < l1; ++i) {
            this.data[i] = v1.getEntry(i);
        }
        System.arraycopy(v2.data, 0, this.data, l1, l2);
    }
    
    public ArrayRealVector(final ArrayRealVector v1, final double[] v2) {
        final int l1 = v1.getDimension();
        final int l2 = v2.length;
        this.data = new double[l1 + l2];
        System.arraycopy(v1.data, 0, this.data, 0, l1);
        System.arraycopy(v2, 0, this.data, l1, l2);
    }
    
    public ArrayRealVector(final double[] v1, final ArrayRealVector v2) {
        final int l1 = v1.length;
        final int l2 = v2.getDimension();
        System.arraycopy(v1, 0, this.data = new double[l1 + l2], 0, l1);
        System.arraycopy(v2.data, 0, this.data, l1, l2);
    }
    
    public ArrayRealVector(final double[] v1, final double[] v2) {
        final int l1 = v1.length;
        final int l2 = v2.length;
        System.arraycopy(v1, 0, this.data = new double[l1 + l2], 0, l1);
        System.arraycopy(v2, 0, this.data, l1, l2);
    }
    
    @Override
    public ArrayRealVector copy() {
        return new ArrayRealVector(this, true);
    }
    
    @Override
    public ArrayRealVector add(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            final int dim = vData.length;
            this.checkVectorDimensions(dim);
            final ArrayRealVector result = new ArrayRealVector(dim);
            final double[] resultData = result.data;
            for (int i = 0; i < dim; ++i) {
                resultData[i] = this.data[i] + vData[i];
            }
            return result;
        }
        this.checkVectorDimensions(v);
        final double[] out = this.data.clone();
        for (final Entry e : v) {
            final double[] array = out;
            final int index = e.getIndex();
            array[index] += e.getValue();
        }
        return new ArrayRealVector(out, false);
    }
    
    @Override
    public ArrayRealVector subtract(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            final int dim = vData.length;
            this.checkVectorDimensions(dim);
            final ArrayRealVector result = new ArrayRealVector(dim);
            final double[] resultData = result.data;
            for (int i = 0; i < dim; ++i) {
                resultData[i] = this.data[i] - vData[i];
            }
            return result;
        }
        this.checkVectorDimensions(v);
        final double[] out = this.data.clone();
        for (final Entry e : v) {
            final double[] array = out;
            final int index = e.getIndex();
            array[index] -= e.getValue();
        }
        return new ArrayRealVector(out, false);
    }
    
    @Override
    public ArrayRealVector map(final UnivariateFunction function) {
        return this.copy().mapToSelf(function);
    }
    
    @Override
    public ArrayRealVector mapToSelf(final UnivariateFunction function) {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = function.value(this.data[i]);
        }
        return this;
    }
    
    @Override
    public RealVector mapAddToSelf(final double d) {
        for (int i = 0; i < this.data.length; ++i) {
            final double[] data = this.data;
            final int n = i;
            data[n] += d;
        }
        return this;
    }
    
    @Override
    public RealVector mapSubtractToSelf(final double d) {
        for (int i = 0; i < this.data.length; ++i) {
            final double[] data = this.data;
            final int n = i;
            data[n] -= d;
        }
        return this;
    }
    
    @Override
    public RealVector mapMultiplyToSelf(final double d) {
        for (int i = 0; i < this.data.length; ++i) {
            final double[] data = this.data;
            final int n = i;
            data[n] *= d;
        }
        return this;
    }
    
    @Override
    public RealVector mapDivideToSelf(final double d) {
        for (int i = 0; i < this.data.length; ++i) {
            final double[] data = this.data;
            final int n = i;
            data[n] /= d;
        }
        return this;
    }
    
    @Override
    public ArrayRealVector ebeMultiply(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            final int dim = vData.length;
            this.checkVectorDimensions(dim);
            final ArrayRealVector result = new ArrayRealVector(dim);
            final double[] resultData = result.data;
            for (int i = 0; i < dim; ++i) {
                resultData[i] = this.data[i] * vData[i];
            }
            return result;
        }
        this.checkVectorDimensions(v);
        final double[] out = this.data.clone();
        for (int j = 0; j < this.data.length; ++j) {
            final double[] array = out;
            final int n = j;
            array[n] *= v.getEntry(j);
        }
        return new ArrayRealVector(out, false);
    }
    
    @Override
    public ArrayRealVector ebeDivide(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            final int dim = vData.length;
            this.checkVectorDimensions(dim);
            final ArrayRealVector result = new ArrayRealVector(dim);
            final double[] resultData = result.data;
            for (int i = 0; i < dim; ++i) {
                resultData[i] = this.data[i] / vData[i];
            }
            return result;
        }
        this.checkVectorDimensions(v);
        final double[] out = this.data.clone();
        for (int j = 0; j < this.data.length; ++j) {
            final double[] array = out;
            final int n = j;
            array[n] /= v.getEntry(j);
        }
        return new ArrayRealVector(out, false);
    }
    
    public double[] getDataRef() {
        return this.data;
    }
    
    @Override
    public double dotProduct(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            this.checkVectorDimensions(vData.length);
            double dot = 0.0;
            for (int i = 0; i < this.data.length; ++i) {
                dot += this.data[i] * vData[i];
            }
            return dot;
        }
        return super.dotProduct(v);
    }
    
    @Override
    public double getNorm() {
        double sum = 0.0;
        for (final double a : this.data) {
            sum += a * a;
        }
        return FastMath.sqrt(sum);
    }
    
    @Override
    public double getL1Norm() {
        double sum = 0.0;
        for (final double a : this.data) {
            sum += FastMath.abs(a);
        }
        return sum;
    }
    
    @Override
    public double getLInfNorm() {
        double max = 0.0;
        for (final double a : this.data) {
            max = FastMath.max(max, FastMath.abs(a));
        }
        return max;
    }
    
    @Override
    public double getDistance(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            this.checkVectorDimensions(vData.length);
            double sum = 0.0;
            for (int i = 0; i < this.data.length; ++i) {
                final double delta = this.data[i] - vData[i];
                sum += delta * delta;
            }
            return FastMath.sqrt(sum);
        }
        this.checkVectorDimensions(v);
        double sum2 = 0.0;
        for (int j = 0; j < this.data.length; ++j) {
            final double delta2 = this.data[j] - v.getEntry(j);
            sum2 += delta2 * delta2;
        }
        return FastMath.sqrt(sum2);
    }
    
    @Override
    public double getL1Distance(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            this.checkVectorDimensions(vData.length);
            double sum = 0.0;
            for (int i = 0; i < this.data.length; ++i) {
                final double delta = this.data[i] - vData[i];
                sum += FastMath.abs(delta);
            }
            return sum;
        }
        this.checkVectorDimensions(v);
        double sum2 = 0.0;
        for (int j = 0; j < this.data.length; ++j) {
            final double delta2 = this.data[j] - v.getEntry(j);
            sum2 += FastMath.abs(delta2);
        }
        return sum2;
    }
    
    @Override
    public double getLInfDistance(final RealVector v) throws DimensionMismatchException {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            this.checkVectorDimensions(vData.length);
            double max = 0.0;
            for (int i = 0; i < this.data.length; ++i) {
                final double delta = this.data[i] - vData[i];
                max = FastMath.max(max, FastMath.abs(delta));
            }
            return max;
        }
        this.checkVectorDimensions(v);
        double max2 = 0.0;
        for (int j = 0; j < this.data.length; ++j) {
            final double delta2 = this.data[j] - v.getEntry(j);
            max2 = FastMath.max(max2, FastMath.abs(delta2));
        }
        return max2;
    }
    
    @Override
    public RealMatrix outerProduct(final RealVector v) {
        if (v instanceof ArrayRealVector) {
            final double[] vData = ((ArrayRealVector)v).data;
            final int m = this.data.length;
            final int n = vData.length;
            final RealMatrix out = MatrixUtils.createRealMatrix(m, n);
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < n; ++j) {
                    out.setEntry(i, j, this.data[i] * vData[j]);
                }
            }
            return out;
        }
        final int k = this.data.length;
        final int n2 = v.getDimension();
        final RealMatrix out2 = MatrixUtils.createRealMatrix(k, n2);
        for (int l = 0; l < k; ++l) {
            for (int j2 = 0; j2 < n2; ++j2) {
                out2.setEntry(l, j2, this.data[l] * v.getEntry(j2));
            }
        }
        return out2;
    }
    
    @Override
    public double getEntry(final int index) throws OutOfRangeException {
        try {
            return this.data[index];
        }
        catch (final IndexOutOfBoundsException e) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0, this.getDimension() - 1);
        }
    }
    
    @Override
    public int getDimension() {
        return this.data.length;
    }
    
    @Override
    public RealVector append(final RealVector v) {
        try {
            return new ArrayRealVector(this, (ArrayRealVector)v);
        }
        catch (final ClassCastException cce) {
            return new ArrayRealVector(this, v);
        }
    }
    
    public ArrayRealVector append(final ArrayRealVector v) {
        return new ArrayRealVector(this, v);
    }
    
    @Override
    public RealVector append(final double in) {
        final double[] out = new double[this.data.length + 1];
        System.arraycopy(this.data, 0, out, 0, this.data.length);
        out[this.data.length] = in;
        return new ArrayRealVector(out, false);
    }
    
    @Override
    public RealVector getSubVector(final int index, final int n) throws OutOfRangeException, NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
        }
        final ArrayRealVector out = new ArrayRealVector(n);
        try {
            System.arraycopy(this.data, index, out.data, 0, n);
        }
        catch (final IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + n - 1);
        }
        return out;
    }
    
    @Override
    public void setEntry(final int index, final double value) throws OutOfRangeException {
        try {
            this.data[index] = value;
        }
        catch (final IndexOutOfBoundsException e) {
            this.checkIndex(index);
        }
    }
    
    @Override
    public void addToEntry(final int index, final double increment) throws OutOfRangeException {
        try {
            final double[] data = this.data;
            data[index] += increment;
        }
        catch (final IndexOutOfBoundsException e) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0, this.data.length - 1);
        }
    }
    
    @Override
    public void setSubVector(final int index, final RealVector v) throws OutOfRangeException {
        if (v instanceof ArrayRealVector) {
            this.setSubVector(index, ((ArrayRealVector)v).data);
        }
        else {
            try {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    this.data[i] = v.getEntry(i - index);
                }
            }
            catch (final IndexOutOfBoundsException e) {
                this.checkIndex(index);
                this.checkIndex(index + v.getDimension() - 1);
            }
        }
    }
    
    public void setSubVector(final int index, final double[] v) throws OutOfRangeException {
        try {
            System.arraycopy(v, 0, this.data, index, v.length);
        }
        catch (final IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + v.length - 1);
        }
    }
    
    @Override
    public void set(final double value) {
        Arrays.fill(this.data, value);
    }
    
    @Override
    public double[] toArray() {
        return this.data.clone();
    }
    
    @Override
    public String toString() {
        return ArrayRealVector.DEFAULT_FORMAT.format(this);
    }
    
    @Override
    protected void checkVectorDimensions(final RealVector v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
    }
    
    @Override
    protected void checkVectorDimensions(final int n) throws DimensionMismatchException {
        if (this.data.length != n) {
            throw new DimensionMismatchException(this.data.length, n);
        }
    }
    
    @Override
    public boolean isNaN() {
        for (final double v : this.data) {
            if (Double.isNaN(v)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isInfinite() {
        if (this.isNaN()) {
            return false;
        }
        for (final double v : this.data) {
            if (Double.isInfinite(v)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RealVector)) {
            return false;
        }
        final RealVector rhs = (RealVector)other;
        if (this.data.length != rhs.getDimension()) {
            return false;
        }
        if (rhs.isNaN()) {
            return this.isNaN();
        }
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] != rhs.getEntry(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        if (this.isNaN()) {
            return 9;
        }
        return MathUtils.hash(this.data);
    }
    
    @Override
    public ArrayRealVector combine(final double a, final double b, final RealVector y) throws DimensionMismatchException {
        return this.copy().combineToSelf(a, b, y);
    }
    
    @Override
    public ArrayRealVector combineToSelf(final double a, final double b, final RealVector y) throws DimensionMismatchException {
        if (y instanceof ArrayRealVector) {
            final double[] yData = ((ArrayRealVector)y).data;
            this.checkVectorDimensions(yData.length);
            for (int i = 0; i < this.data.length; ++i) {
                this.data[i] = a * this.data[i] + b * yData[i];
            }
        }
        else {
            this.checkVectorDimensions(y);
            for (int j = 0; j < this.data.length; ++j) {
                this.data[j] = a * this.data[j] + b * y.getEntry(j);
            }
        }
        return this;
    }
    
    @Override
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor) {
        visitor.start(this.data.length, 0, this.data.length - 1);
        for (int i = 0; i < this.data.length; ++i) {
            visitor.visit(i, this.data[i]);
        }
        return visitor.end();
    }
    
    @Override
    public double walkInDefaultOrder(final RealVectorPreservingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkIndices(start, end);
        visitor.start(this.data.length, start, end);
        for (int i = start; i <= end; ++i) {
            visitor.visit(i, this.data[i]);
        }
        return visitor.end();
    }
    
    @Override
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor) {
        return this.walkInDefaultOrder(visitor);
    }
    
    @Override
    public double walkInOptimizedOrder(final RealVectorPreservingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInDefaultOrder(visitor, start, end);
    }
    
    @Override
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor) {
        visitor.start(this.data.length, 0, this.data.length - 1);
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = visitor.visit(i, this.data[i]);
        }
        return visitor.end();
    }
    
    @Override
    public double walkInDefaultOrder(final RealVectorChangingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        this.checkIndices(start, end);
        visitor.start(this.data.length, start, end);
        for (int i = start; i <= end; ++i) {
            this.data[i] = visitor.visit(i, this.data[i]);
        }
        return visitor.end();
    }
    
    @Override
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor) {
        return this.walkInDefaultOrder(visitor);
    }
    
    @Override
    public double walkInOptimizedOrder(final RealVectorChangingVisitor visitor, final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
        return this.walkInDefaultOrder(visitor, start, end);
    }
    
    static {
        DEFAULT_FORMAT = RealVectorFormat.getInstance();
    }
}
