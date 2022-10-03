package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.util.FastMath;

public class InterpolatingMicrosphere2D extends InterpolatingMicrosphere
{
    private static final int DIMENSION = 2;
    
    public InterpolatingMicrosphere2D(final int size, final double maxDarkFraction, final double darkThreshold, final double background) {
        super(2, size, maxDarkFraction, darkThreshold, background);
        for (int i = 0; i < size; ++i) {
            final double angle = i * 6.283185307179586 / size;
            this.add(new double[] { FastMath.cos(angle), FastMath.sin(angle) }, false);
        }
    }
    
    protected InterpolatingMicrosphere2D(final InterpolatingMicrosphere2D other) {
        super(other);
    }
    
    @Override
    public InterpolatingMicrosphere2D copy() {
        return new InterpolatingMicrosphere2D(this);
    }
}
