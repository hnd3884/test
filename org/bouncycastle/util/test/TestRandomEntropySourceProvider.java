package org.bouncycastle.util.test;

import org.bouncycastle.crypto.prng.EntropySource;
import java.security.SecureRandom;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

public class TestRandomEntropySourceProvider implements EntropySourceProvider
{
    private final SecureRandom _sr;
    private final boolean _predictionResistant;
    
    public TestRandomEntropySourceProvider(final boolean predictionResistant) {
        this._sr = new SecureRandom();
        this._predictionResistant = predictionResistant;
    }
    
    public EntropySource get(final int n) {
        return new EntropySource() {
            public boolean isPredictionResistant() {
                return TestRandomEntropySourceProvider.this._predictionResistant;
            }
            
            public byte[] getEntropy() {
                final byte[] array = new byte[(n + 7) / 8];
                TestRandomEntropySourceProvider.this._sr.nextBytes(array);
                return array;
            }
            
            public int entropySize() {
                return n;
            }
        };
    }
}
