package com.maverick.ssh.components;

import java.io.IOException;
import java.math.BigInteger;

public interface SshRsaPrivateKey extends SshPrivateKey
{
    BigInteger getModulus();
    
    BigInteger getPrivateExponent();
    
    byte[] sign(final byte[] p0) throws IOException;
}
