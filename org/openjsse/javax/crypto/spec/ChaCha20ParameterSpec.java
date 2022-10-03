package org.openjsse.javax.crypto.spec;

import java.util.Objects;
import java.security.spec.AlgorithmParameterSpec;

public final class ChaCha20ParameterSpec implements AlgorithmParameterSpec
{
    private static final int NONCE_LENGTH = 12;
    private final byte[] nonce;
    private final int counter;
    
    public ChaCha20ParameterSpec(final byte[] nonce, final int counter) {
        this.counter = counter;
        Objects.requireNonNull(nonce, "Nonce must be non-null");
        this.nonce = nonce.clone();
        if (this.nonce.length != 12) {
            throw new IllegalArgumentException("Nonce must be 12-bytes in length");
        }
    }
    
    public byte[] getNonce() {
        return this.nonce.clone();
    }
    
    public int getCounter() {
        return this.counter;
    }
}
