package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.EphemeralKeyPair;
import org.bouncycastle.crypto.KeyEncoder;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class EphemeralKeyPairGenerator
{
    private AsymmetricCipherKeyPairGenerator gen;
    private KeyEncoder keyEncoder;
    
    public EphemeralKeyPairGenerator(final AsymmetricCipherKeyPairGenerator gen, final KeyEncoder keyEncoder) {
        this.gen = gen;
        this.keyEncoder = keyEncoder;
    }
    
    public EphemeralKeyPair generate() {
        return new EphemeralKeyPair(this.gen.generateKeyPair(), this.keyEncoder);
    }
}
