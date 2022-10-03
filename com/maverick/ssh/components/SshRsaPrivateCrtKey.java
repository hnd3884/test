package com.maverick.ssh.components;

import com.maverick.ssh.SshException;
import java.math.BigInteger;

public interface SshRsaPrivateCrtKey extends SshRsaPrivateKey
{
    BigInteger getPublicExponent();
    
    BigInteger getPrimeP();
    
    BigInteger getPrimeQ();
    
    BigInteger getPrimeExponentP();
    
    BigInteger getPrimeExponentQ();
    
    BigInteger getCrtCoefficient();
    
    BigInteger doPrivate(final BigInteger p0) throws SshException;
}
