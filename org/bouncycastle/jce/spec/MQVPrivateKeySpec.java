package org.bouncycastle.jce.spec;

import java.security.PublicKey;
import java.security.PrivateKey;
import org.bouncycastle.jce.interfaces.MQVPrivateKey;
import java.security.spec.KeySpec;

public class MQVPrivateKeySpec implements KeySpec, MQVPrivateKey
{
    private PrivateKey staticPrivateKey;
    private PrivateKey ephemeralPrivateKey;
    private PublicKey ephemeralPublicKey;
    
    public MQVPrivateKeySpec(final PrivateKey privateKey, final PrivateKey privateKey2) {
        this(privateKey, privateKey2, null);
    }
    
    public MQVPrivateKeySpec(final PrivateKey staticPrivateKey, final PrivateKey ephemeralPrivateKey, final PublicKey ephemeralPublicKey) {
        this.staticPrivateKey = staticPrivateKey;
        this.ephemeralPrivateKey = ephemeralPrivateKey;
        this.ephemeralPublicKey = ephemeralPublicKey;
    }
    
    public PrivateKey getStaticPrivateKey() {
        return this.staticPrivateKey;
    }
    
    public PrivateKey getEphemeralPrivateKey() {
        return this.ephemeralPrivateKey;
    }
    
    public PublicKey getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
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
