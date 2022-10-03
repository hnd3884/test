package org.bouncycastle.crypto.prng;

import java.security.SecureRandom;

public class BasicEntropySourceProvider implements EntropySourceProvider
{
    private final SecureRandom _sr;
    private final boolean _predictionResistant;
    
    public BasicEntropySourceProvider(final SecureRandom sr, final boolean predictionResistant) {
        this._sr = sr;
        this._predictionResistant = predictionResistant;
    }
    
    public EntropySource get(final int n) {
        return new EntropySource() {
            public boolean isPredictionResistant() {
                return BasicEntropySourceProvider.this._predictionResistant;
            }
            
            public byte[] getEntropy() {
                if (BasicEntropySourceProvider.this._sr instanceof SP800SecureRandom || BasicEntropySourceProvider.this._sr instanceof X931SecureRandom) {
                    final byte[] array = new byte[(n + 7) / 8];
                    BasicEntropySourceProvider.this._sr.nextBytes(array);
                    return array;
                }
                return BasicEntropySourceProvider.this._sr.generateSeed((n + 7) / 8);
            }
            
            public int entropySize() {
                return n;
            }
        };
    }
}
