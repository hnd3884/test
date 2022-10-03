package org.bouncycastle.crypto.prng;

import org.bouncycastle.crypto.Digest;

public class DigestRandomGenerator implements RandomGenerator
{
    private static long CYCLE_COUNT;
    private long stateCounter;
    private long seedCounter;
    private Digest digest;
    private byte[] state;
    private byte[] seed;
    
    public DigestRandomGenerator(final Digest digest) {
        this.digest = digest;
        this.seed = new byte[digest.getDigestSize()];
        this.seedCounter = 1L;
        this.state = new byte[digest.getDigestSize()];
        this.stateCounter = 1L;
    }
    
    public void addSeedMaterial(final byte[] array) {
        synchronized (this) {
            this.digestUpdate(array);
            this.digestUpdate(this.seed);
            this.digestDoFinal(this.seed);
        }
    }
    
    public void addSeedMaterial(final long n) {
        synchronized (this) {
            this.digestAddCounter(n);
            this.digestUpdate(this.seed);
            this.digestDoFinal(this.seed);
        }
    }
    
    public void nextBytes(final byte[] array) {
        this.nextBytes(array, 0, array.length);
    }
    
    public void nextBytes(final byte[] array, final int n, final int n2) {
        synchronized (this) {
            int n3 = 0;
            this.generateState();
            for (int n4 = n + n2, i = n; i != n4; ++i) {
                if (n3 == this.state.length) {
                    this.generateState();
                    n3 = 0;
                }
                array[i] = this.state[n3++];
            }
        }
    }
    
    private void cycleSeed() {
        this.digestUpdate(this.seed);
        this.digestAddCounter(this.seedCounter++);
        this.digestDoFinal(this.seed);
    }
    
    private void generateState() {
        this.digestAddCounter(this.stateCounter++);
        this.digestUpdate(this.state);
        this.digestUpdate(this.seed);
        this.digestDoFinal(this.state);
        if (this.stateCounter % DigestRandomGenerator.CYCLE_COUNT == 0L) {
            this.cycleSeed();
        }
    }
    
    private void digestAddCounter(long n) {
        for (int i = 0; i != 8; ++i) {
            this.digest.update((byte)n);
            n >>>= 8;
        }
    }
    
    private void digestUpdate(final byte[] array) {
        this.digest.update(array, 0, array.length);
    }
    
    private void digestDoFinal(final byte[] array) {
        this.digest.doFinal(array, 0);
    }
    
    static {
        DigestRandomGenerator.CYCLE_COUNT = 10L;
    }
}
