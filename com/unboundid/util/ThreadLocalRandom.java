package com.unboundid.util;

import java.util.Random;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ThreadLocalRandom
{
    private static final Random SEED_RANDOM;
    private static final ThreadLocal<Random> INSTANCES;
    
    private ThreadLocalRandom() {
    }
    
    public static Random get() {
        Random r = ThreadLocalRandom.INSTANCES.get();
        if (r == null) {
            final long seed;
            synchronized (ThreadLocalRandom.SEED_RANDOM) {
                seed = ThreadLocalRandom.SEED_RANDOM.nextLong();
            }
            r = new Random(seed);
            ThreadLocalRandom.INSTANCES.set(r);
        }
        return r;
    }
    
    static {
        SEED_RANDOM = new Random();
        INSTANCES = new ThreadLocal<Random>();
    }
}
