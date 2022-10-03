package org.apache.commons.math3.geometry.spherical.oned;

import org.apache.commons.math3.geometry.partitioning.Region;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.Precision;

public class Arc
{
    private final double lower;
    private final double upper;
    private final double middle;
    private final double tolerance;
    
    public Arc(final double lower, final double upper, final double tolerance) throws NumberIsTooLargeException {
        this.tolerance = tolerance;
        if (Precision.equals(lower, upper, 0) || upper - lower >= 6.283185307179586) {
            this.lower = 0.0;
            this.upper = 6.283185307179586;
            this.middle = 3.141592653589793;
        }
        else {
            if (lower > upper) {
                throw new NumberIsTooLargeException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, lower, upper, true);
            }
            this.lower = MathUtils.normalizeAngle(lower, 3.141592653589793);
            this.upper = this.lower + (upper - lower);
            this.middle = 0.5 * (this.lower + this.upper);
        }
    }
    
    public double getInf() {
        return this.lower;
    }
    
    public double getSup() {
        return this.upper;
    }
    
    public double getSize() {
        return this.upper - this.lower;
    }
    
    public double getBarycenter() {
        return this.middle;
    }
    
    public double getTolerance() {
        return this.tolerance;
    }
    
    public Region.Location checkPoint(final double point) {
        final double normalizedPoint = MathUtils.normalizeAngle(point, this.middle);
        if (normalizedPoint < this.lower - this.tolerance || normalizedPoint > this.upper + this.tolerance) {
            return Region.Location.OUTSIDE;
        }
        if (normalizedPoint > this.lower + this.tolerance && normalizedPoint < this.upper - this.tolerance) {
            return Region.Location.INSIDE;
        }
        return (this.getSize() >= 6.283185307179586 - this.tolerance) ? Region.Location.INSIDE : Region.Location.BOUNDARY;
    }
}
