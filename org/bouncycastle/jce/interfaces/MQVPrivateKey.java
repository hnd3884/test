package org.bouncycastle.jce.interfaces;

import java.security.PublicKey;
import java.security.PrivateKey;

public interface MQVPrivateKey extends PrivateKey
{
    PrivateKey getStaticPrivateKey();
    
    PrivateKey getEphemeralPrivateKey();
    
    PublicKey getEphemeralPublicKey();
}
