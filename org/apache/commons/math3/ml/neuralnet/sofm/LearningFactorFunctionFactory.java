package org.apache.commons.math3.ml.neuralnet.sofm;

import org.apache.commons.math3.ml.neuralnet.sofm.util.QuasiSigmoidDecayFunction;
import org.apache.commons.math3.ml.neuralnet.sofm.util.ExponentialDecayFunction;
import org.apache.commons.math3.exception.OutOfRangeException;

public class LearningFactorFunctionFactory
{
    private LearningFactorFunctionFactory() {
    }
    
    public static LearningFactorFunction exponentialDecay(final double initValue, final double valueAtNumCall, final long numCall) {
        if (initValue <= 0.0 || initValue > 1.0) {
            throw new OutOfRangeException(initValue, 0, 1);
        }
        return new LearningFactorFunction() {
            private final ExponentialDecayFunction decay = new ExponentialDecayFunction(initValue, valueAtNumCall, numCall);
            
            public double value(final long n) {
                return this.decay.value(n);
            }
        };
    }
    
    public static LearningFactorFunction quasiSigmoidDecay(final double initValue, final double slope, final long numCall) {
        if (initValue <= 0.0 || initValue > 1.0) {
            throw new OutOfRangeException(initValue, 0, 1);
        }
        return new LearningFactorFunction() {
            private final QuasiSigmoidDecayFunction decay = new QuasiSigmoidDecayFunction(initValue, slope, numCall);
            
            public double value(final long n) {
                return this.decay.value(n);
            }
        };
    }
}
