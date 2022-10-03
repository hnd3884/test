package org.bouncycastle.crypto;

public interface AsymmetricCipherKeyPairGenerator
{
    void init(final KeyGenerationParameters p0);
    
    AsymmetricCipherKeyPair generateKeyPair();
}
