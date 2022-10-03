package org.apache.commons.math3.analysis.interpolation;

import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;
import java.util.ArrayList;
import org.apache.commons.math3.linear.ArrayRealVector;
import java.util.HashMap;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.linear.RealVector;
import java.util.Map;
import java.util.List;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class MicrosphereInterpolatingFunction implements MultivariateFunction
{
    private final int dimension;
    private final List<MicrosphereSurfaceElement> microsphere;
    private final double brightnessExponent;
    private final Map<RealVector, Double> samples;
    
    public MicrosphereInterpolatingFunction(final double[][] xval, final double[] yval, final int brightnessExponent, final int microsphereElements, final UnitSphereRandomVectorGenerator rand) throws DimensionMismatchException, NoDataException, NullArgumentException {
        if (xval == null || yval == null) {
            throw new NullArgumentException();
        }
        if (xval.length == 0) {
            throw new NoDataException();
        }
        if (xval.length != yval.length) {
            throw new DimensionMismatchException(xval.length, yval.length);
        }
        if (xval[0] == null) {
            throw new NullArgumentException();
        }
        this.dimension = xval[0].length;
        this.brightnessExponent = brightnessExponent;
        this.samples = new HashMap<RealVector, Double>(yval.length);
        for (int i = 0; i < xval.length; ++i) {
            final double[] xvalI = xval[i];
            if (xvalI == null) {
                throw new NullArgumentException();
            }
            if (xvalI.length != this.dimension) {
                throw new DimensionMismatchException(xvalI.length, this.dimension);
            }
            this.samples.put(new ArrayRealVector(xvalI), yval[i]);
        }
        this.microsphere = new ArrayList<MicrosphereSurfaceElement>(microsphereElements);
        for (int i = 0; i < microsphereElements; ++i) {
            this.microsphere.add(new MicrosphereSurfaceElement(rand.nextVector()));
        }
    }
    
    public double value(final double[] point) throws DimensionMismatchException {
        final RealVector p = new ArrayRealVector(point);
        for (final MicrosphereSurfaceElement md : this.microsphere) {
            md.reset();
        }
        for (final Map.Entry<RealVector, Double> sd : this.samples.entrySet()) {
            final RealVector diff = sd.getKey().subtract(p);
            final double diffNorm = diff.getNorm();
            if (FastMath.abs(diffNorm) < FastMath.ulp(1.0)) {
                return sd.getValue();
            }
            for (final MicrosphereSurfaceElement md2 : this.microsphere) {
                final double w = FastMath.pow(diffNorm, -this.brightnessExponent);
                md2.store(this.cosAngle(diff, md2.normal()) * w, sd);
            }
        }
        double value = 0.0;
        double totalWeight = 0.0;
        for (final MicrosphereSurfaceElement md3 : this.microsphere) {
            final double iV = md3.illumination();
            final Map.Entry<RealVector, Double> sd2 = md3.sample();
            if (sd2 != null) {
                value += iV * sd2.getValue();
                totalWeight += iV;
            }
        }
        return value / totalWeight;
    }
    
    private double cosAngle(final RealVector v, final RealVector w) {
        return v.dotProduct(w) / (v.getNorm() * w.getNorm());
    }
    
    private static class MicrosphereSurfaceElement
    {
        private final RealVector normal;
        private double brightestIllumination;
        private Map.Entry<RealVector, Double> brightestSample;
        
        MicrosphereSurfaceElement(final double[] n) {
            this.normal = new ArrayRealVector(n);
        }
        
        RealVector normal() {
            return this.normal;
        }
        
        void reset() {
            this.brightestIllumination = 0.0;
            this.brightestSample = null;
        }
        
        void store(final double illuminationFromSample, final Map.Entry<RealVector, Double> sample) {
            if (illuminationFromSample > this.brightestIllumination) {
                this.brightestIllumination = illuminationFromSample;
                this.brightestSample = sample;
            }
        }
        
        double illumination() {
            return this.brightestIllumination;
        }
        
        Map.Entry<RealVector, Double> sample() {
            return this.brightestSample;
        }
    }
}
