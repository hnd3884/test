package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.io.Serializable;

public class SimpleRegression implements Serializable, UpdatingMultipleLinearRegression
{
    private static final long serialVersionUID = -3004689053607543335L;
    private double sumX;
    private double sumXX;
    private double sumY;
    private double sumYY;
    private double sumXY;
    private long n;
    private double xbar;
    private double ybar;
    private final boolean hasIntercept;
    
    public SimpleRegression() {
        this(true);
    }
    
    public SimpleRegression(final boolean includeIntercept) {
        this.sumX = 0.0;
        this.sumXX = 0.0;
        this.sumY = 0.0;
        this.sumYY = 0.0;
        this.sumXY = 0.0;
        this.n = 0L;
        this.xbar = 0.0;
        this.ybar = 0.0;
        this.hasIntercept = includeIntercept;
    }
    
    public void addData(final double x, final double y) {
        if (this.n == 0L) {
            this.xbar = x;
            this.ybar = y;
        }
        else if (this.hasIntercept) {
            final double fact1 = 1.0 + this.n;
            final double fact2 = this.n / (1.0 + this.n);
            final double dx = x - this.xbar;
            final double dy = y - this.ybar;
            this.sumXX += dx * dx * fact2;
            this.sumYY += dy * dy * fact2;
            this.sumXY += dx * dy * fact2;
            this.xbar += dx / fact1;
            this.ybar += dy / fact1;
        }
        if (!this.hasIntercept) {
            this.sumXX += x * x;
            this.sumYY += y * y;
            this.sumXY += x * y;
        }
        this.sumX += x;
        this.sumY += y;
        ++this.n;
    }
    
    public void append(final SimpleRegression reg) {
        if (this.n == 0L) {
            this.xbar = reg.xbar;
            this.ybar = reg.ybar;
            this.sumXX = reg.sumXX;
            this.sumYY = reg.sumYY;
            this.sumXY = reg.sumXY;
        }
        else if (this.hasIntercept) {
            final double fact1 = reg.n / (double)(reg.n + this.n);
            final double fact2 = this.n * reg.n / (double)(reg.n + this.n);
            final double dx = reg.xbar - this.xbar;
            final double dy = reg.ybar - this.ybar;
            this.sumXX += reg.sumXX + dx * dx * fact2;
            this.sumYY += reg.sumYY + dy * dy * fact2;
            this.sumXY += reg.sumXY + dx * dy * fact2;
            this.xbar += dx * fact1;
            this.ybar += dy * fact1;
        }
        else {
            this.sumXX += reg.sumXX;
            this.sumYY += reg.sumYY;
            this.sumXY += reg.sumXY;
        }
        this.sumX += reg.sumX;
        this.sumY += reg.sumY;
        this.n += reg.n;
    }
    
    public void removeData(final double x, final double y) {
        if (this.n > 0L) {
            if (this.hasIntercept) {
                final double fact1 = this.n - 1.0;
                final double fact2 = this.n / (this.n - 1.0);
                final double dx = x - this.xbar;
                final double dy = y - this.ybar;
                this.sumXX -= dx * dx * fact2;
                this.sumYY -= dy * dy * fact2;
                this.sumXY -= dx * dy * fact2;
                this.xbar -= dx / fact1;
                this.ybar -= dy / fact1;
            }
            else {
                final double fact1 = this.n - 1.0;
                this.sumXX -= x * x;
                this.sumYY -= y * y;
                this.sumXY -= x * y;
                this.xbar -= x / fact1;
                this.ybar -= y / fact1;
            }
            this.sumX -= x;
            this.sumY -= y;
            --this.n;
        }
    }
    
    public void addData(final double[][] data) throws ModelSpecificationException {
        for (int i = 0; i < data.length; ++i) {
            if (data[i].length < 2) {
                throw new ModelSpecificationException(LocalizedFormats.INVALID_REGRESSION_OBSERVATION, new Object[] { data[i].length, 2 });
            }
            this.addData(data[i][0], data[i][1]);
        }
    }
    
    public void addObservation(final double[] x, final double y) throws ModelSpecificationException {
        if (x == null || x.length == 0) {
            throw new ModelSpecificationException(LocalizedFormats.INVALID_REGRESSION_OBSERVATION, new Object[] { (x != null) ? x.length : 0, 1 });
        }
        this.addData(x[0], y);
    }
    
    public void addObservations(final double[][] x, final double[] y) throws ModelSpecificationException {
        if (x == null || y == null || x.length != y.length) {
            throw new ModelSpecificationException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { (x == null) ? 0 : x.length, (y == null) ? 0 : y.length });
        }
        boolean obsOk = true;
        for (int i = 0; i < x.length; ++i) {
            if (x[i] == null || x[i].length == 0) {
                obsOk = false;
            }
        }
        if (!obsOk) {
            throw new ModelSpecificationException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, new Object[] { 0, 1 });
        }
        for (int i = 0; i < x.length; ++i) {
            this.addData(x[i][0], y[i]);
        }
    }
    
    public void removeData(final double[][] data) {
        for (int i = 0; i < data.length && this.n > 0L; ++i) {
            this.removeData(data[i][0], data[i][1]);
        }
    }
    
    public void clear() {
        this.sumX = 0.0;
        this.sumXX = 0.0;
        this.sumY = 0.0;
        this.sumYY = 0.0;
        this.sumXY = 0.0;
        this.n = 0L;
    }
    
    public long getN() {
        return this.n;
    }
    
    public double predict(final double x) {
        final double b1 = this.getSlope();
        if (this.hasIntercept) {
            return this.getIntercept(b1) + b1 * x;
        }
        return b1 * x;
    }
    
    public double getIntercept() {
        return this.hasIntercept ? this.getIntercept(this.getSlope()) : 0.0;
    }
    
    public boolean hasIntercept() {
        return this.hasIntercept;
    }
    
    public double getSlope() {
        if (this.n < 2L) {
            return Double.NaN;
        }
        if (FastMath.abs(this.sumXX) < 4.9E-323) {
            return Double.NaN;
        }
        return this.sumXY / this.sumXX;
    }
    
    public double getSumSquaredErrors() {
        return FastMath.max(0.0, this.sumYY - this.sumXY * this.sumXY / this.sumXX);
    }
    
    public double getTotalSumSquares() {
        if (this.n < 2L) {
            return Double.NaN;
        }
        return this.sumYY;
    }
    
    public double getXSumSquares() {
        if (this.n < 2L) {
            return Double.NaN;
        }
        return this.sumXX;
    }
    
    public double getSumOfCrossProducts() {
        return this.sumXY;
    }
    
    public double getRegressionSumSquares() {
        return this.getRegressionSumSquares(this.getSlope());
    }
    
    public double getMeanSquareError() {
        if (this.n < 3L) {
            return Double.NaN;
        }
        return this.hasIntercept ? (this.getSumSquaredErrors() / (this.n - 2L)) : (this.getSumSquaredErrors() / (this.n - 1L));
    }
    
    public double getR() {
        final double b1 = this.getSlope();
        double result = FastMath.sqrt(this.getRSquare());
        if (b1 < 0.0) {
            result = -result;
        }
        return result;
    }
    
    public double getRSquare() {
        final double ssto = this.getTotalSumSquares();
        return (ssto - this.getSumSquaredErrors()) / ssto;
    }
    
    public double getInterceptStdErr() {
        if (!this.hasIntercept) {
            return Double.NaN;
        }
        return FastMath.sqrt(this.getMeanSquareError() * (1.0 / this.n + this.xbar * this.xbar / this.sumXX));
    }
    
    public double getSlopeStdErr() {
        return FastMath.sqrt(this.getMeanSquareError() / this.sumXX);
    }
    
    public double getSlopeConfidenceInterval() throws OutOfRangeException {
        return this.getSlopeConfidenceInterval(0.05);
    }
    
    public double getSlopeConfidenceInterval(final double alpha) throws OutOfRangeException {
        if (this.n < 3L) {
            return Double.NaN;
        }
        if (alpha >= 1.0 || alpha <= 0.0) {
            throw new OutOfRangeException(LocalizedFormats.SIGNIFICANCE_LEVEL, alpha, 0, 1);
        }
        final TDistribution distribution = new TDistribution((double)(this.n - 2L));
        return this.getSlopeStdErr() * distribution.inverseCumulativeProbability(1.0 - alpha / 2.0);
    }
    
    public double getSignificance() {
        if (this.n < 3L) {
            return Double.NaN;
        }
        final TDistribution distribution = new TDistribution((double)(this.n - 2L));
        return 2.0 * (1.0 - distribution.cumulativeProbability(FastMath.abs(this.getSlope()) / this.getSlopeStdErr()));
    }
    
    private double getIntercept(final double slope) {
        if (this.hasIntercept) {
            return (this.sumY - slope * this.sumX) / this.n;
        }
        return 0.0;
    }
    
    private double getRegressionSumSquares(final double slope) {
        return slope * slope * this.sumXX;
    }
    
    public RegressionResults regress() throws ModelSpecificationException, NoDataException {
        if (this.hasIntercept) {
            if (this.n < 3L) {
                throw new NoDataException(LocalizedFormats.NOT_ENOUGH_DATA_REGRESSION);
            }
            if (FastMath.abs(this.sumXX) > Precision.SAFE_MIN) {
                final double[] params = { this.getIntercept(), this.getSlope() };
                final double mse = this.getMeanSquareError();
                final double _syy = this.sumYY + this.sumY * this.sumY / this.n;
                final double[] vcv = { mse * (this.xbar * this.xbar / this.sumXX + 1.0 / this.n), -this.xbar * mse / this.sumXX, mse / this.sumXX };
                return new RegressionResults(params, new double[][] { vcv }, true, this.n, 2, this.sumY, _syy, this.getSumSquaredErrors(), true, false);
            }
            final double[] params = { this.sumY / this.n, Double.NaN };
            final double[] vcv2 = { this.ybar / (this.n - 1.0), Double.NaN, Double.NaN };
            return new RegressionResults(params, new double[][] { vcv2 }, true, this.n, 1, this.sumY, this.sumYY, this.getSumSquaredErrors(), true, false);
        }
        else {
            if (this.n < 2L) {
                throw new NoDataException(LocalizedFormats.NOT_ENOUGH_DATA_REGRESSION);
            }
            if (!Double.isNaN(this.sumXX)) {
                final double[] vcv3 = { this.getMeanSquareError() / this.sumXX };
                final double[] params2 = { this.sumXY / this.sumXX };
                return new RegressionResults(params2, new double[][] { vcv3 }, true, this.n, 1, this.sumY, this.sumYY, this.getSumSquaredErrors(), false, false);
            }
            final double[] vcv3 = { Double.NaN };
            final double[] params2 = { Double.NaN };
            return new RegressionResults(params2, new double[][] { vcv3 }, true, this.n, 1, Double.NaN, Double.NaN, Double.NaN, false, false);
        }
    }
    
    public RegressionResults regress(final int[] variablesToInclude) throws MathIllegalArgumentException {
        if (variablesToInclude == null || variablesToInclude.length == 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.ARRAY_ZERO_LENGTH_OR_NULL_NOT_ALLOWED, new Object[0]);
        }
        if (variablesToInclude.length > 2 || (variablesToInclude.length > 1 && !this.hasIntercept)) {
            throw new ModelSpecificationException(LocalizedFormats.ARRAY_SIZE_EXCEEDS_MAX_VARIABLES, new Object[] { (variablesToInclude.length > 1 && !this.hasIntercept) ? 1 : 2 });
        }
        if (this.hasIntercept) {
            if (variablesToInclude.length == 2) {
                if (variablesToInclude[0] == 1) {
                    throw new ModelSpecificationException(LocalizedFormats.NOT_INCREASING_SEQUENCE, new Object[0]);
                }
                if (variablesToInclude[0] != 0) {
                    throw new OutOfRangeException(variablesToInclude[0], 0, 1);
                }
                if (variablesToInclude[1] != 1) {
                    throw new OutOfRangeException(variablesToInclude[0], 0, 1);
                }
                return this.regress();
            }
            else {
                if (variablesToInclude[0] != 1 && variablesToInclude[0] != 0) {
                    throw new OutOfRangeException(variablesToInclude[0], 0, 1);
                }
                final double _mean = this.sumY * this.sumY / this.n;
                final double _syy = this.sumYY + _mean;
                if (variablesToInclude[0] == 0) {
                    final double[] vcv = { this.sumYY / ((this.n - 1L) * this.n) };
                    final double[] params = { this.ybar };
                    return new RegressionResults(params, new double[][] { vcv }, true, this.n, 1, this.sumY, _syy + _mean, this.sumYY, true, false);
                }
                if (variablesToInclude[0] != 1) {
                    return null;
                }
                final double _sxx = this.sumXX + this.sumX * this.sumX / this.n;
                final double _sxy = this.sumXY + this.sumX * this.sumY / this.n;
                final double _sse = FastMath.max(0.0, _syy - _sxy * _sxy / _sxx);
                final double _mse = _sse / (this.n - 1L);
                if (!Double.isNaN(_sxx)) {
                    final double[] vcv2 = { _mse / _sxx };
                    final double[] params2 = { _sxy / _sxx };
                    return new RegressionResults(params2, new double[][] { vcv2 }, true, this.n, 1, this.sumY, _syy, _sse, false, false);
                }
                final double[] vcv2 = { Double.NaN };
                final double[] params2 = { Double.NaN };
                return new RegressionResults(params2, new double[][] { vcv2 }, true, this.n, 1, Double.NaN, Double.NaN, Double.NaN, false, false);
            }
        }
        else {
            if (variablesToInclude[0] != 0) {
                throw new OutOfRangeException(variablesToInclude[0], 0, 0);
            }
            return this.regress();
        }
    }
}
