package org.bouncycastle.eac.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.KeyFactory;

class NamedEACHelper implements EACHelper
{
    private final String providerName;
    
    NamedEACHelper(final String providerName) {
        this.providerName = providerName;
    }
    
    public KeyFactory createKeyFactory(final String s) throws NoSuchProviderException, NoSuchAlgorithmException {
        return KeyFactory.getInstance(s, this.providerName);
    }
}
