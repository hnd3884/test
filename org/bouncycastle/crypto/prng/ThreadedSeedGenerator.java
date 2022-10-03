package org.bouncycastle.crypto.prng;

public class ThreadedSeedGenerator
{
    public byte[] generateSeed(final int n, final boolean b) {
        return new SeedGenerator().generateSeed(n, b);
    }
    
    private class SeedGenerator implements Runnable
    {
        private volatile int counter;
        private volatile boolean stop;
        
        private SeedGenerator() {
            this.counter = 0;
            this.stop = false;
        }
        
        public void run() {
            while (!this.stop) {
                ++this.counter;
            }
        }
        
        public byte[] generateSeed(final int n, final boolean b) {
            final Thread thread = new Thread(this);
            final byte[] array = new byte[n];
            this.counter = 0;
            this.stop = false;
            int counter = 0;
            thread.start();
            int n2;
            if (b) {
                n2 = n;
            }
            else {
                n2 = n * 8;
            }
            for (int i = 0; i < n2; ++i) {
                while (this.counter == counter) {
                    try {
                        Thread.sleep(1L);
                    }
                    catch (final InterruptedException ex) {}
                }
                counter = this.counter;
                if (b) {
                    array[i] = (byte)(counter & 0xFF);
                }
                else {
                    final int n3 = i / 8;
                    array[n3] = (byte)(array[n3] << 1 | (counter & 0x1));
                }
            }
            this.stop = true;
            return array;
        }
    }
}
