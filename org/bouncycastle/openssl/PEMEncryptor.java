package org.bouncycastle.openssl;

public interface PEMEncryptor
{
    String getAlgorithm();
    
    byte[] getIV();
    
    byte[] encrypt(final byte[] p0) throws PEMException;
}
