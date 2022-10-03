package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.Provider;

class ProviderEACHelper extends EACHelper
{
    private final Provider provider;
    
    ProviderEACHelper(final Provider provider) {
        this.provider = provider;
    }
    
    @Override
    protected Signature createSignature(final String s) throws NoSuchAlgorithmException {
        return Signature.getInstance(s, this.provider);
    }
}
