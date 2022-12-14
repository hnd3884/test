package org.bouncycastle.crypto;

public class EphemeralKeyPair
{
    private AsymmetricCipherKeyPair keyPair;
    private KeyEncoder publicKeyEncoder;
    
    public EphemeralKeyPair(final AsymmetricCipherKeyPair keyPair, final KeyEncoder publicKeyEncoder) {
        this.keyPair = keyPair;
        this.publicKeyEncoder = publicKeyEncoder;
    }
    
    public AsymmetricCipherKeyPair getKeyPair() {
        return this.keyPair;
    }
    
    public byte[] getEncodedPublicKey() {
        return this.publicKeyEncoder.getEncoded(this.keyPair.getPublic());
    }
}
