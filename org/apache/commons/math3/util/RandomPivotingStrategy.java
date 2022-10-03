package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.random.RandomGenerator;
import java.io.Serializable;

public class RandomPivotingStrategy implements PivotingStrategyInterface, Serializable
{
    private static final long serialVersionUID = 20140713L;
    private final RandomGenerator random;
    
    public RandomPivotingStrategy(final RandomGenerator random) {
        this.random = random;
    }
    
    public int pivotIndex(final double[] work, final int begin, final int end) throws MathIllegalArgumentException {
        MathArrays.verifyValues(work, begin, end - begin);
        return begin + this.random.nextInt(end - begin - 1);
    }
}
