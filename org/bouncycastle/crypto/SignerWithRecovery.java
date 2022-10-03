package org.bouncycastle.crypto;

public interface SignerWithRecovery extends Signer
{
    boolean hasFullMessage();
    
    byte[] getRecoveredMessage();
    
    void updateWithRecoveredMessage(final byte[] p0) throws InvalidCipherTextException;
}
