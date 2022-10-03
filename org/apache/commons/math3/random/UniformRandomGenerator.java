package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;

public class UniformRandomGenerator implements NormalizedRandomGenerator
{
    private static final double SQRT3;
    private final RandomGenerator generator;
    
    public UniformRandomGenerator(final RandomGenerator generator) {
        this.generator = generator;
    }
    
    public double nextNormalizedDouble() {
        return UniformRandomGenerator.SQRT3 * (2.0 * this.generator.nextDouble() - 1.0);
    }
    
    static {
        SQRT3 = FastMath.sqrt(3.0);
    }
}
