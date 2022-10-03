package org.bouncycastle.eac.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.KeyFactory;
import java.security.Provider;

class ProviderEACHelper implements EACHelper
{
    private final Provider provider;
    
    ProviderEACHelper(final Provider provider) {
        this.provider = provider;
    }
    
    public KeyFactory createKeyFactory(final String s) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(s, this.provider);
    }
}
