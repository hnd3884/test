package org.apache.commons.math3.random;

import java.util.Random;

public class RandomAdaptor extends Random implements RandomGenerator
{
    private static final long serialVersionUID = 2306581345647615033L;
    private final RandomGenerator randomGenerator;
    
    private RandomAdaptor() {
        this.randomGenerator = null;
    }
    
    public RandomAdaptor(final RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }
    
    public static Random createAdaptor(final RandomGenerator randomGenerator) {
        return new RandomAdaptor(randomGenerator);
    }
    
    @Override
    public boolean nextBoolean() {
        return this.randomGenerator.nextBoolean();
    }
    
    @Override
    public void nextBytes(final byte[] bytes) {
        this.randomGenerator.nextBytes(bytes);
    }
    
    @Override
    public double nextDouble() {
        return this.randomGenerator.nextDouble();
    }
    
    @Override
    public float nextFloat() {
        return this.randomGenerator.nextFloat();
    }
    
    @Override
    public double nextGaussian() {
        return this.randomGenerator.nextGaussian();
    }
    
    @Override
    public int nextInt() {
        return this.randomGenerator.nextInt();
    }
    
    @Override
    public int nextInt(final int n) {
        return this.randomGenerator.nextInt(n);
    }
    
    @Override
    public long nextLong() {
        return this.randomGenerator.nextLong();
    }
    
    public void setSeed(final int seed) {
        if (this.randomGenerator != null) {
            this.randomGenerator.setSeed(seed);
        }
    }
    
    public void setSeed(final int[] seed) {
        if (this.randomGenerator != null) {
            this.randomGenerator.setSeed(seed);
        }
    }
    
    @Override
    public void setSeed(final long seed) {
        if (this.randomGenerator != null) {
            this.randomGenerator.setSeed(seed);
        }
    }
}
