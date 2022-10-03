package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.CipherParameters;

public interface MessageEncryptor
{
    void init(final boolean p0, final CipherParameters p1);
    
    byte[] messageEncrypt(final byte[] p0);
    
    byte[] messageDecrypt(final byte[] p0) throws InvalidCipherTextException;
}
