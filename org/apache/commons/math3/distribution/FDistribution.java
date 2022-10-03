package org.apache.commons.math3.distribution;

import org.apache.commons.math3.special.Beta;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class FDistribution extends AbstractRealDistribution
{
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9;
    private static final long serialVersionUID = -8516354193418641566L;
    private final double numeratorDegreesOfFreedom;
    private final double denominatorDegreesOfFreedom;
    private final double solverAbsoluteAccuracy;
    private double numericalVariance;
    private boolean numericalVarianceIsCalculated;
    
    public FDistribution(final double numeratorDegreesOfFreedom, final double denominatorDegreesOfFreedom) throws NotStrictlyPositiveException {
        this(numeratorDegreesOfFreedom, denominatorDegreesOfFreedom, 1.0E-9);
    }
    
    public FDistribution(final double numeratorDegreesOfFreedom, final double denominatorDegreesOfFreedom, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        this(new Well19937c(), numeratorDegreesOfFreedom, denominatorDegreesOfFreedom, inverseCumAccuracy);
    }
    
    public FDistribution(final RandomGenerator rng, final double numeratorDegreesOfFreedom, final double denominatorDegreesOfFreedom) throws NotStrictlyPositiveException {
        this(rng, numeratorDegreesOfFreedom, denominatorDegreesOfFreedom, 1.0E-9);
    }
    
    public FDistribution(final RandomGenerator rng, final double numeratorDegreesOfFreedom, final double denominatorDegreesOfFreedom, final double inverseCumAccuracy) throws NotStrictlyPositiveException {
        super(rng);
        this.numericalVariance = Double.NaN;
        this.numericalVarianceIsCalculated = false;
        if (numeratorDegreesOfFreedom <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM, numeratorDegreesOfFreedom);
        }
        if (denominatorDegreesOfFreedom <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM, denominatorDegreesOfFreedom);
        }
        this.numeratorDegreesOfFreedom = numeratorDegreesOfFreedom;
        this.denominatorDegreesOfFreedom = denominatorDegreesOfFreedom;
        this.solverAbsoluteAccuracy = inverseCumAccuracy;
    }
    
    public double density(final double x) {
        return FastMath.exp(this.logDensity(x));
    }
    
    @Override
    public double logDensity(final double x) {
        final double nhalf = this.numeratorDegreesOfFreedom / 2.0;
        final double mhalf = this.denominatorDegreesOfFreedom / 2.0;
        final double logx = FastMath.log(x);
        final double logn = FastMath.log(this.numeratorDegreesOfFreedom);
        final double logm = FastMath.log(this.denominatorDegreesOfFreedom);
        final double lognxm = FastMath.log(this.numeratorDegreesOfFreedom * x + this.denominatorDegreesOfFreedom);
        return nhalf * logn + nhalf * logx - logx + mhalf * logm - nhalf * lognxm - mhalf * lognxm - Beta.logBeta(nhalf, mhalf);
    }
    
    public double cumulativeProbability(final double x) {
        double ret;
        if (x <= 0.0) {
            ret = 0.0;
        }
        else {
            final double n = this.numeratorDegreesOfFreedom;
            final double m = this.denominatorDegreesOfFreedom;
            ret = Beta.regularizedBeta(n * x / (m + n * x), 0.5 * n, 0.5 * m);
        }
        return ret;
    }
    
    public double getNumeratorDegreesOfFreedom() {
        return this.numeratorDegreesOfFreedom;
    }
    
    public double getDenominatorDegreesOfFreedom() {
        return this.denominatorDegreesOfFreedom;
    }
    
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return this.solverAbsoluteAccuracy;
    }
    
    public double getNumericalMean() {
        final double denominatorDF = this.getDenominatorDegreesOfFreedom();
        if (denominatorDF > 2.0) {
            return denominatorDF / (denominatorDF - 2.0);
        }
        return Double.NaN;
    }
    
    public double getNumericalVariance() {
        if (!this.numericalVarianceIsCalculated) {
            this.numericalVariance = this.calculateNumericalVariance();
            this.numericalVarianceIsCalculated = true;
        }
        return this.numericalVariance;
    }
    
    protected double calculateNumericalVariance() {
        final double denominatorDF = this.getDenominatorDegreesOfFreedom();
        if (denominatorDF > 4.0) {
            final double numeratorDF = this.getNumeratorDegreesOfFreedom();
            final double denomDFMinusTwo = denominatorDF - 2.0;
            return 2.0 * (denominatorDF * denominatorDF) * (numeratorDF + denominatorDF - 2.0) / (numeratorDF * (denomDFMinusTwo * denomDFMinusTwo) * (denominatorDF - 4.0));
        }
        return Double.NaN;
    }
    
    public double getSupportLowerBound() {
        return 0.0;
    }
    
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return false;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
}
