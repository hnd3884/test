package com.maverick.ssh.components;

import java.math.BigInteger;

public interface SshDsaPublicKey extends SshPublicKey
{
    BigInteger getP();
    
    BigInteger getQ();
    
    BigInteger getG();
    
    BigInteger getY();
}
