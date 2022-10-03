package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

class NamedEACHelper extends EACHelper
{
    private final String providerName;
    
    NamedEACHelper(final String providerName) {
        this.providerName = providerName;
    }
    
    @Override
    protected Signature createSignature(final String s) throws NoSuchProviderException, NoSuchAlgorithmException {
        return Signature.getInstance(s, this.providerName);
    }
}
