package org.apache.commons.math3.distribution;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.fraction.BigFractionField;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.io.Serializable;

public class KolmogorovSmirnovDistribution implements Serializable
{
    private static final long serialVersionUID = -4670676796862967187L;
    private int n;
    
    public KolmogorovSmirnovDistribution(final int n) throws NotStrictlyPositiveException {
        if (n <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NOT_POSITIVE_NUMBER_OF_SAMPLES, n);
        }
        this.n = n;
    }
    
    public double cdf(final double d) throws MathArithmeticException {
        return this.cdf(d, false);
    }
    
    public double cdfExact(final double d) throws MathArithmeticException {
        return this.cdf(d, true);
    }
    
    public double cdf(final double d, final boolean exact) throws MathArithmeticException {
        final double ninv = 1.0 / this.n;
        final double ninvhalf = 0.5 * ninv;
        if (d <= ninvhalf) {
            return 0.0;
        }
        if (ninvhalf < d && d <= ninv) {
            double res = 1.0;
            final double f = 2.0 * d - ninv;
            for (int i = 1; i <= this.n; ++i) {
                res *= i * f;
            }
            return res;
        }
        if (1.0 - ninv <= d && d < 1.0) {
            return 1.0 - 2.0 * FastMath.pow(1.0 - d, this.n);
        }
        if (1.0 <= d) {
            return 1.0;
        }
        return exact ? this.exactK(d) : this.roundedK(d);
    }
    
    private double exactK(final double d) throws MathArithmeticException {
        final int k = (int)FastMath.ceil(this.n * d);
        final FieldMatrix<BigFraction> H = this.createH(d);
        final FieldMatrix<BigFraction> Hpower = H.power(this.n);
        BigFraction pFrac = Hpower.getEntry(k - 1, k - 1);
        for (int i = 1; i <= this.n; ++i) {
            pFrac = pFrac.multiply(i).divide(this.n);
        }
        return pFrac.bigDecimalValue(20, 4).doubleValue();
    }
    
    private double roundedK(final double d) throws MathArithmeticException {
        final int k = (int)FastMath.ceil(this.n * d);
        final FieldMatrix<BigFraction> HBigFraction = this.createH(d);
        final int m = HBigFraction.getRowDimension();
        final RealMatrix H = new Array2DRowRealMatrix(m, m);
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                H.setEntry(i, j, HBigFraction.getEntry(i, j).doubleValue());
            }
        }
        final RealMatrix Hpower = H.power(this.n);
        double pFrac = Hpower.getEntry(k - 1, k - 1);
        for (int l = 1; l <= this.n; ++l) {
            pFrac *= l / (double)this.n;
        }
        return pFrac;
    }
    
    private FieldMatrix<BigFraction> createH(final double d) throws NumberIsTooLargeException, FractionConversionException {
        final int k = (int)FastMath.ceil(this.n * d);
        final int m = 2 * k - 1;
        final double hDouble = k - this.n * d;
        if (hDouble >= 1.0) {
            throw new NumberIsTooLargeException(hDouble, 1.0, false);
        }
        BigFraction h = null;
        try {
            h = new BigFraction(hDouble, 1.0E-20, 10000);
        }
        catch (final FractionConversionException e1) {
            try {
                h = new BigFraction(hDouble, 1.0E-10, 10000);
            }
            catch (final FractionConversionException e2) {
                h = new BigFraction(hDouble, 1.0E-5, 10000);
            }
        }
        final BigFraction[][] Hdata = new BigFraction[m][m];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                if (i - j + 1 < 0) {
                    Hdata[i][j] = BigFraction.ZERO;
                }
                else {
                    Hdata[i][j] = BigFraction.ONE;
                }
            }
        }
        final BigFraction[] hPowers = new BigFraction[m];
        hPowers[0] = h;
        for (int l = 1; l < m; ++l) {
            hPowers[l] = h.multiply(hPowers[l - 1]);
        }
        for (int l = 0; l < m; ++l) {
            Hdata[l][0] = Hdata[l][0].subtract(hPowers[l]);
            Hdata[m - 1][l] = Hdata[m - 1][l].subtract(hPowers[m - l - 1]);
        }
        if (h.compareTo(BigFraction.ONE_HALF) == 1) {
            Hdata[m - 1][0] = Hdata[m - 1][0].add(h.multiply(2).subtract(1).pow(m));
        }
        for (int l = 0; l < m; ++l) {
            for (int j2 = 0; j2 < l + 1; ++j2) {
                if (l - j2 + 1 > 0) {
                    for (int g = 2; g <= l - j2 + 1; ++g) {
                        Hdata[l][j2] = Hdata[l][j2].divide(g);
                    }
                }
            }
        }
        return new Array2DRowFieldMatrix<BigFraction>(BigFractionField.getInstance(), Hdata);
    }
}
