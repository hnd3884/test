package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import java.util.Iterator;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import java.util.List;

public class InterpolatingMicrosphere
{
    private final List<Facet> microsphere;
    private final List<FacetData> microsphereData;
    private final int dimension;
    private final int size;
    private final double maxDarkFraction;
    private final double darkThreshold;
    private final double background;
    
    protected InterpolatingMicrosphere(final int dimension, final int size, final double maxDarkFraction, final double darkThreshold, final double background) {
        if (dimension <= 0) {
            throw new NotStrictlyPositiveException(dimension);
        }
        if (size <= 0) {
            throw new NotStrictlyPositiveException(size);
        }
        if (maxDarkFraction < 0.0 || maxDarkFraction > 1.0) {
            throw new OutOfRangeException(maxDarkFraction, 0, 1);
        }
        if (darkThreshold < 0.0) {
            throw new NotPositiveException(darkThreshold);
        }
        this.dimension = dimension;
        this.size = size;
        this.maxDarkFraction = maxDarkFraction;
        this.darkThreshold = darkThreshold;
        this.background = background;
        this.microsphere = new ArrayList<Facet>(size);
        this.microsphereData = new ArrayList<FacetData>(size);
    }
    
    public InterpolatingMicrosphere(final int dimension, final int size, final double maxDarkFraction, final double darkThreshold, final double background, final UnitSphereRandomVectorGenerator rand) {
        this(dimension, size, maxDarkFraction, darkThreshold, background);
        for (int i = 0; i < size; ++i) {
            this.add(rand.nextVector(), false);
        }
    }
    
    protected InterpolatingMicrosphere(final InterpolatingMicrosphere other) {
        this.dimension = other.dimension;
        this.size = other.size;
        this.maxDarkFraction = other.maxDarkFraction;
        this.darkThreshold = other.darkThreshold;
        this.background = other.background;
        this.microsphere = other.microsphere;
        this.microsphereData = new ArrayList<FacetData>(this.size);
        for (final FacetData fd : other.microsphereData) {
            this.microsphereData.add(new FacetData(fd.illumination(), fd.sample()));
        }
    }
    
    public InterpolatingMicrosphere copy() {
        return new InterpolatingMicrosphere(this);
    }
    
    public int getDimension() {
        return this.dimension;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public double value(final double[] point, final double[][] samplePoints, final double[] sampleValues, final double exponent, final double noInterpolationTolerance) {
        if (exponent < 0.0) {
            throw new NotPositiveException(exponent);
        }
        this.clear();
        for (int numSamples = samplePoints.length, i = 0; i < numSamples; ++i) {
            final double[] diff = MathArrays.ebeSubtract(samplePoints[i], point);
            final double diffNorm = MathArrays.safeNorm(diff);
            if (FastMath.abs(diffNorm) < noInterpolationTolerance) {
                return sampleValues[i];
            }
            final double weight = FastMath.pow(diffNorm, -exponent);
            this.illuminate(diff, sampleValues[i], weight);
        }
        return this.interpolate();
    }
    
    protected void add(final double[] normal, final boolean copy) {
        if (this.microsphere.size() >= this.size) {
            throw new MaxCountExceededException(this.size);
        }
        if (normal.length > this.dimension) {
            throw new DimensionMismatchException(normal.length, this.dimension);
        }
        this.microsphere.add(new Facet(copy ? normal.clone() : normal));
        this.microsphereData.add(new FacetData(0.0, 0.0));
    }
    
    private double interpolate() {
        int darkCount = 0;
        double value = 0.0;
        double totalWeight = 0.0;
        for (final FacetData fd : this.microsphereData) {
            final double iV = fd.illumination();
            if (iV != 0.0) {
                value += iV * fd.sample();
                totalWeight += iV;
            }
            else {
                ++darkCount;
            }
        }
        final double darkFraction = darkCount / (double)this.size;
        return (darkFraction <= this.maxDarkFraction) ? (value / totalWeight) : this.background;
    }
    
    private void illuminate(final double[] sampleDirection, final double sampleValue, final double weight) {
        for (int i = 0; i < this.size; ++i) {
            final double[] n = this.microsphere.get(i).getNormal();
            final double cos = MathArrays.cosAngle(n, sampleDirection);
            if (cos > 0.0) {
                final double illumination = cos * weight;
                if (illumination > this.darkThreshold && illumination > this.microsphereData.get(i).illumination()) {
                    this.microsphereData.set(i, new FacetData(illumination, sampleValue));
                }
            }
        }
    }
    
    private void clear() {
        for (int i = 0; i < this.size; ++i) {
            this.microsphereData.set(i, new FacetData(0.0, 0.0));
        }
    }
    
    private static class Facet
    {
        private final double[] normal;
        
        Facet(final double[] n) {
            this.normal = n;
        }
        
        public double[] getNormal() {
            return this.normal;
        }
    }
    
    private static class FacetData
    {
        private final double illumination;
        private final double sample;
        
        FacetData(final double illumination, final double sample) {
            this.illumination = illumination;
            this.sample = sample;
        }
        
        public double illumination() {
            return this.illumination;
        }
        
        public double sample() {
            return this.sample;
        }
    }
}
