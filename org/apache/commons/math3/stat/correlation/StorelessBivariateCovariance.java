package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

class StorelessBivariateCovariance
{
    private double meanX;
    private double meanY;
    private double n;
    private double covarianceNumerator;
    private boolean biasCorrected;
    
    StorelessBivariateCovariance() {
        this(true);
    }
    
    StorelessBivariateCovariance(final boolean biasCorrection) {
        final double n = 0.0;
        this.meanY = n;
        this.meanX = n;
        this.n = 0.0;
        this.covarianceNumerator = 0.0;
        this.biasCorrected = biasCorrection;
    }
    
    public void increment(final double x, final double y) {
        ++this.n;
        final double deltaX = x - this.meanX;
        final double deltaY = y - this.meanY;
        this.meanX += deltaX / this.n;
        this.meanY += deltaY / this.n;
        this.covarianceNumerator += (this.n - 1.0) / this.n * deltaX * deltaY;
    }
    
    public void append(final StorelessBivariateCovariance cov) {
        final double oldN = this.n;
        this.n += cov.n;
        final double deltaX = cov.meanX - this.meanX;
        final double deltaY = cov.meanY - this.meanY;
        this.meanX += deltaX * cov.n / this.n;
        this.meanY += deltaY * cov.n / this.n;
        this.covarianceNumerator += cov.covarianceNumerator + oldN * cov.n / this.n * deltaX * deltaY;
    }
    
    public double getN() {
        return this.n;
    }
    
    public double getResult() throws NumberIsTooSmallException {
        if (this.n < 2.0) {
            throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_DIMENSION, this.n, 2, true);
        }
        if (this.biasCorrected) {
            return this.covarianceNumerator / (this.n - 1.0);
        }
        return this.covarianceNumerator / this.n;
    }
}
