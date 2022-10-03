package com.maverick.ssh.components;

import com.maverick.ssh.SshException;
import java.math.BigInteger;

public interface SshRsaPublicKey extends SshPublicKey
{
    BigInteger getModulus();
    
    BigInteger getPublicExponent();
    
    int getVersion();
    
    BigInteger doPublic(final BigInteger p0) throws SshException;
}
