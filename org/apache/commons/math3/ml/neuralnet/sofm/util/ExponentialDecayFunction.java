package org.apache.commons.math3.ml.neuralnet.sofm.util;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class ExponentialDecayFunction
{
    private final double a;
    private final double oneOverB;
    
    public ExponentialDecayFunction(final double initValue, final double valueAtNumCall, final long numCall) {
        if (initValue <= 0.0) {
            throw new NotStrictlyPositiveException(initValue);
        }
        if (valueAtNumCall <= 0.0) {
            throw new NotStrictlyPositiveException(valueAtNumCall);
        }
        if (valueAtNumCall >= initValue) {
            throw new NumberIsTooLargeException(valueAtNumCall, initValue, false);
        }
        if (numCall <= 0L) {
            throw new NotStrictlyPositiveException(numCall);
        }
        this.a = initValue;
        this.oneOverB = -FastMath.log(valueAtNumCall / initValue) / numCall;
    }
    
    public double value(final long numCall) {
        return this.a * FastMath.exp(-numCall * this.oneOverB);
    }
}
