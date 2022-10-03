package org.apache.commons.math3.random;

public interface RandomGenerator
{
    void setSeed(final int p0);
    
    void setSeed(final int[] p0);
    
    void setSeed(final long p0);
    
    void nextBytes(final byte[] p0);
    
    int nextInt();
    
    int nextInt(final int p0);
    
    long nextLong();
    
    boolean nextBoolean();
    
    float nextFloat();
    
    double nextDouble();
    
    double nextGaussian();
}
