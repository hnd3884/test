package org.apache.commons.math3.stat.interval;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class ConfidenceInterval
{
    private double lowerBound;
    private double upperBound;
    private double confidenceLevel;
    
    public ConfidenceInterval(final double lowerBound, final double upperBound, final double confidenceLevel) {
        this.checkParameters(lowerBound, upperBound, confidenceLevel);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.confidenceLevel = confidenceLevel;
    }
    
    public double getLowerBound() {
        return this.lowerBound;
    }
    
    public double getUpperBound() {
        return this.upperBound;
    }
    
    public double getConfidenceLevel() {
        return this.confidenceLevel;
    }
    
    @Override
    public String toString() {
        return "[" + this.lowerBound + ";" + this.upperBound + "] (confidence level:" + this.confidenceLevel + ")";
    }
    
    private void checkParameters(final double lower, final double upper, final double confidence) {
        if (lower >= upper) {
            throw new MathIllegalArgumentException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, new Object[] { lower, upper });
        }
        if (confidence <= 0.0 || confidence >= 1.0) {
            throw new MathIllegalArgumentException(LocalizedFormats.OUT_OF_BOUNDS_CONFIDENCE_LEVEL, new Object[] { confidence, 0, 1 });
        }
    }
}
