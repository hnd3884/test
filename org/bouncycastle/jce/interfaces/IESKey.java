package org.bouncycastle.jce.interfaces;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Key;

public interface IESKey extends Key
{
    PublicKey getPublic();
    
    PrivateKey getPrivate();
}
