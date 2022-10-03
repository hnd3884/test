package org.apache.commons.math3.ml.neuralnet.sofm.util;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.analysis.function.Logistic;

public class QuasiSigmoidDecayFunction
{
    private final Logistic sigmoid;
    private final double scale;
    
    public QuasiSigmoidDecayFunction(final double initValue, final double slope, final long numCall) {
        if (initValue <= 0.0) {
            throw new NotStrictlyPositiveException(initValue);
        }
        if (slope >= 0.0) {
            throw new NumberIsTooLargeException(slope, 0, false);
        }
        if (numCall <= 1L) {
            throw new NotStrictlyPositiveException(numCall);
        }
        final double k = initValue;
        final double m = (double)numCall;
        final double b = 4.0 * slope / initValue;
        final double q = 1.0;
        final double a = 0.0;
        final double n = 1.0;
        this.sigmoid = new Logistic(k, m, b, 1.0, 0.0, 1.0);
        final double y0 = this.sigmoid.value(0.0);
        this.scale = k / y0;
    }
    
    public double value(final long numCall) {
        return this.scale * this.sigmoid.value((double)numCall);
    }
}
