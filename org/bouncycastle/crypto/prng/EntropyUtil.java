package org.bouncycastle.crypto.prng;

public class EntropyUtil
{
    public static byte[] generateSeed(final EntropySource entropySource, final int n) {
        final byte[] array = new byte[n];
        if (n * 8 <= entropySource.entropySize()) {
            System.arraycopy(entropySource.getEntropy(), 0, array, 0, array.length);
        }
        else {
            for (int n2 = entropySource.entropySize() / 8, i = 0; i < array.length; i += n2) {
                final byte[] entropy = entropySource.getEntropy();
                if (entropy.length <= array.length - i) {
                    System.arraycopy(entropy, 0, array, i, entropy.length);
                }
                else {
                    System.arraycopy(entropy, 0, array, i, array.length - i);
                }
            }
        }
        return array;
    }
}
