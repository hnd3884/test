package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.random.UnitSphereRandomVectorGenerator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NotPositiveException;

@Deprecated
public class MicrosphereInterpolator implements MultivariateInterpolator
{
    public static final int DEFAULT_MICROSPHERE_ELEMENTS = 2000;
    public static final int DEFAULT_BRIGHTNESS_EXPONENT = 2;
    private final int microsphereElements;
    private final int brightnessExponent;
    
    public MicrosphereInterpolator() {
        this(2000, 2);
    }
    
    public MicrosphereInterpolator(final int elements, final int exponent) throws NotPositiveException, NotStrictlyPositiveException {
        if (exponent < 0) {
            throw new NotPositiveException(exponent);
        }
        if (elements <= 0) {
            throw new NotStrictlyPositiveException(elements);
        }
        this.microsphereElements = elements;
        this.brightnessExponent = exponent;
    }
    
    public MultivariateFunction interpolate(final double[][] xval, final double[] yval) throws DimensionMismatchException, NoDataException, NullArgumentException {
        final UnitSphereRandomVectorGenerator rand = new UnitSphereRandomVectorGenerator(xval[0].length);
        return new MicrosphereInterpolatingFunction(xval, yval, this.brightnessExponent, this.microsphereElements, rand);
    }
}
