package org.apache.commons.math3.random;

public class GaussianRandomGenerator implements NormalizedRandomGenerator
{
    private final RandomGenerator generator;
    
    public GaussianRandomGenerator(final RandomGenerator generator) {
        this.generator = generator;
    }
    
    public double nextNormalizedDouble() {
        return this.generator.nextGaussian();
    }
}
