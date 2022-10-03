package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.FieldElement;

public class FieldHermiteInterpolator<T extends FieldElement<T>>
{
    private final List<T> abscissae;
    private final List<T[]> topDiagonal;
    private final List<T[]> bottomDiagonal;
    
    public FieldHermiteInterpolator() {
        this.abscissae = new ArrayList<T>();
        this.topDiagonal = new ArrayList<T[]>();
        this.bottomDiagonal = new ArrayList<T[]>();
    }
    
    public void addSamplePoint(final T x, final T[]... value) throws ZeroException, MathArithmeticException, DimensionMismatchException, NullArgumentException {
        MathUtils.checkNotNull(x);
        T factorial = x.getField().getOne();
        for (int i = 0; i < value.length; ++i) {
            final T[] y = value[i].clone();
            if (i > 1) {
                factorial = factorial.multiply(i);
                final T inv = factorial.reciprocal();
                for (int j = 0; j < y.length; ++j) {
                    y[j] = y[j].multiply(inv);
                }
            }
            final int n = this.abscissae.size();
            this.bottomDiagonal.add(n - i, y);
            T[] bottom0 = y;
            for (int k = i; k < n; ++k) {
                final T[] bottom2 = this.bottomDiagonal.get(n - (k + 1));
                if (x.equals(this.abscissae.get(n - (k + 1)))) {
                    throw new ZeroException(LocalizedFormats.DUPLICATED_ABSCISSA_DIVISION_BY_ZERO, new Object[] { x });
                }
                final T inv2 = x.subtract(this.abscissae.get(n - (k + 1))).reciprocal();
                for (int l = 0; l < y.length; ++l) {
                    bottom2[l] = inv2.multiply(bottom0[l].subtract(bottom2[l]));
                }
                bottom0 = bottom2;
            }
            this.topDiagonal.add(bottom0.clone());
            this.abscissae.add(x);
        }
    }
    
    public T[] value(final T x) throws NoDataException, NullArgumentException {
        MathUtils.checkNotNull(x);
        if (this.abscissae.isEmpty()) {
            throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
        }
        final T[] value = MathArrays.buildArray(x.getField(), this.topDiagonal.get(0).length);
        T valueCoeff = x.getField().getOne();
        for (int i = 0; i < this.topDiagonal.size(); ++i) {
            final T[] dividedDifference = this.topDiagonal.get(i);
            for (int k = 0; k < value.length; ++k) {
                value[k] = value[k].add(dividedDifference[k].multiply(valueCoeff));
            }
            final T deltaX = x.subtract(this.abscissae.get(i));
            valueCoeff = valueCoeff.multiply(deltaX);
        }
        return value;
    }
    
    public T[][] derivatives(final T x, final int order) throws NoDataException, NullArgumentException {
        MathUtils.checkNotNull(x);
        if (this.abscissae.isEmpty()) {
            throw new NoDataException(LocalizedFormats.EMPTY_INTERPOLATION_SAMPLE);
        }
        final T zero = x.getField().getZero();
        final T one = x.getField().getOne();
        final T[] tj = MathArrays.buildArray(x.getField(), order + 1);
        tj[0] = zero;
        for (int i = 0; i < order; ++i) {
            tj[i + 1] = tj[i].add(one);
        }
        final T[][] derivatives = MathArrays.buildArray(x.getField(), order + 1, this.topDiagonal.get(0).length);
        final T[] valueCoeff = MathArrays.buildArray(x.getField(), order + 1);
        valueCoeff[0] = x.getField().getOne();
        for (int j = 0; j < this.topDiagonal.size(); ++j) {
            final T[] dividedDifference = this.topDiagonal.get(j);
            final T deltaX = x.subtract(this.abscissae.get(j));
            for (int k = order; k >= 0; --k) {
                for (int l = 0; l < derivatives[k].length; ++l) {
                    derivatives[k][l] = derivatives[k][l].add(dividedDifference[l].multiply(valueCoeff[k]));
                }
                valueCoeff[k] = valueCoeff[k].multiply(deltaX);
                if (k > 0) {
                    valueCoeff[k] = valueCoeff[k].add(tj[k].multiply(valueCoeff[k - 1]));
                }
            }
        }
        return derivatives;
    }
}
