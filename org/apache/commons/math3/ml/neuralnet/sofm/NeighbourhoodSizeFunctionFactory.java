package org.apache.commons.math3.ml.neuralnet.sofm;

import org.apache.commons.math3.ml.neuralnet.sofm.util.QuasiSigmoidDecayFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ml.neuralnet.sofm.util.ExponentialDecayFunction;

public class NeighbourhoodSizeFunctionFactory
{
    private NeighbourhoodSizeFunctionFactory() {
    }
    
    public static NeighbourhoodSizeFunction exponentialDecay(final double initValue, final double valueAtNumCall, final long numCall) {
        return new NeighbourhoodSizeFunction() {
            private final ExponentialDecayFunction decay = new ExponentialDecayFunction(initValue, valueAtNumCall, numCall);
            
            public int value(final long n) {
                return (int)FastMath.rint(this.decay.value(n));
            }
        };
    }
    
    public static NeighbourhoodSizeFunction quasiSigmoidDecay(final double initValue, final double slope, final long numCall) {
        return new NeighbourhoodSizeFunction() {
            private final QuasiSigmoidDecayFunction decay = new QuasiSigmoidDecayFunction(initValue, slope, numCall);
            
            public int value(final long n) {
                return (int)FastMath.rint(this.decay.value(n));
            }
        };
    }
}
