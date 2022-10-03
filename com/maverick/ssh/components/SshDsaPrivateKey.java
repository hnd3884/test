package com.maverick.ssh.components;

import java.io.IOException;
import java.math.BigInteger;

public interface SshDsaPrivateKey extends SshPrivateKey
{
    BigInteger getX();
    
    byte[] sign(final byte[] p0) throws IOException;
    
    SshDsaPublicKey getPublicKey();
}
