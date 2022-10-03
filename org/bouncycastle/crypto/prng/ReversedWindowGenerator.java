package org.bouncycastle.crypto.prng;

public class ReversedWindowGenerator implements RandomGenerator
{
    private final RandomGenerator generator;
    private byte[] window;
    private int windowCount;
    
    public ReversedWindowGenerator(final RandomGenerator generator, final int n) {
        if (generator == null) {
            throw new IllegalArgumentException("generator cannot be null");
        }
        if (n < 2) {
            throw new IllegalArgumentException("windowSize must be at least 2");
        }
        this.generator = generator;
        this.window = new byte[n];
    }
    
    public void addSeedMaterial(final byte[] array) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(array);
        }
    }
    
    public void addSeedMaterial(final long n) {
        synchronized (this) {
            this.windowCount = 0;
            this.generator.addSeedMaterial(n);
        }
    }
    
    public void nextBytes(final byte[] array) {
        this.doNextBytes(array, 0, array.length);
    }
    
    public void nextBytes(final byte[] array, final int n, final int n2) {
        this.doNextBytes(array, n, n2);
    }
    
    private void doNextBytes(final byte[] array, final int n, final int n2) {
        synchronized (this) {
            int n3;
            byte[] window;
            int windowCount;
            for (int i = 0; i < n2; n3 = n + i++, window = this.window, windowCount = this.windowCount - 1, this.windowCount = windowCount, array[n3] = window[windowCount]) {
                if (this.windowCount < 1) {
                    this.generator.nextBytes(this.window, 0, this.window.length);
                    this.windowCount = this.window.length;
                }
            }
        }
    }
}
