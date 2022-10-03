package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import java.util.Random;

public class RandomGeneratorFactory
{
    private RandomGeneratorFactory() {
    }
    
    public static RandomGenerator createRandomGenerator(final Random rng) {
        return new RandomGenerator() {
            public void setSeed(final int seed) {
                rng.setSeed(seed);
            }
            
            public void setSeed(final int[] seed) {
                rng.setSeed(RandomGeneratorFactory.convertToLong(seed));
            }
            
            public void setSeed(final long seed) {
                rng.setSeed(seed);
            }
            
            public void nextBytes(final byte[] bytes) {
                rng.nextBytes(bytes);
            }
            
            public int nextInt() {
                return rng.nextInt();
            }
            
            public int nextInt(final int n) {
                if (n <= 0) {
                    throw new NotStrictlyPositiveException(n);
                }
                return rng.nextInt(n);
            }
            
            public long nextLong() {
                return rng.nextLong();
            }
            
            public boolean nextBoolean() {
                return rng.nextBoolean();
            }
            
            public float nextFloat() {
                return rng.nextFloat();
            }
            
            public double nextDouble() {
                return rng.nextDouble();
            }
            
            public double nextGaussian() {
                return rng.nextGaussian();
            }
        };
    }
    
    public static long convertToLong(final int[] seed) {
        final long prime = 4294967291L;
        long combined = 0L;
        for (final int s : seed) {
            combined = combined * 4294967291L + s;
        }
        return combined;
    }
}
