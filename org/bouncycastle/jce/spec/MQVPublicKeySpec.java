package org.bouncycastle.jce.spec;

import java.security.PublicKey;
import org.bouncycastle.jce.interfaces.MQVPublicKey;
import java.security.spec.KeySpec;

public class MQVPublicKeySpec implements KeySpec, MQVPublicKey
{
    private PublicKey staticKey;
    private PublicKey ephemeralKey;
    
    public MQVPublicKeySpec(final PublicKey staticKey, final PublicKey ephemeralKey) {
        this.staticKey = staticKey;
        this.ephemeralKey = ephemeralKey;
    }
    
    public PublicKey getStaticKey() {
        return this.staticKey;
    }
    
    public PublicKey getEphemeralKey() {
        return this.ephemeralKey;
    }
    
    public String getAlgorithm() {
        return "ECMQV";
    }
    
    public String getFormat() {
        return null;
    }
    
    public byte[] getEncoded() {
        return null;
    }
}
