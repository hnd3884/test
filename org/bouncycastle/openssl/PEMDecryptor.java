package org.bouncycastle.openssl;

public interface PEMDecryptor
{
    byte[] decrypt(final byte[] p0, final byte[] p1) throws PEMException;
}
