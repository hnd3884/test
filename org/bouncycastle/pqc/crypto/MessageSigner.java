package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;

public interface MessageSigner
{
    void init(final boolean p0, final CipherParameters p1);
    
    byte[] generateSignature(final byte[] p0);
    
    boolean verifySignature(final byte[] p0, final byte[] p1);
}
