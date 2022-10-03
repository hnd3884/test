package org.apache.commons.lang.math;

import java.util.Random;

public class RandomUtils
{
    public static final Random JVM_RANDOM;
    
    public static int nextInt() {
        return nextInt(RandomUtils.JVM_RANDOM);
    }
    
    public static int nextInt(final Random rnd) {
        return rnd.nextInt();
    }
    
    public static int nextInt(final int n) {
        return nextInt(RandomUtils.JVM_RANDOM, n);
    }
    
    public static int nextInt(final Random rnd, final int n) {
        return rnd.nextInt(n);
    }
    
    public static long nextLong() {
        return nextLong(RandomUtils.JVM_RANDOM);
    }
    
    public static long nextLong(final Random rnd) {
        return rnd.nextLong();
    }
    
    public static boolean nextBoolean() {
        return nextBoolean(RandomUtils.JVM_RANDOM);
    }
    
    public static boolean nextBoolean(final Random rnd) {
        return rnd.nextBoolean();
    }
    
    public static float nextFloat() {
        return nextFloat(RandomUtils.JVM_RANDOM);
    }
    
    public static float nextFloat(final Random rnd) {
        return rnd.nextFloat();
    }
    
    public static double nextDouble() {
        return nextDouble(RandomUtils.JVM_RANDOM);
    }
    
    public static double nextDouble(final Random rnd) {
        return rnd.nextDouble();
    }
    
    static {
        JVM_RANDOM = new JVMRandom();
    }
}
