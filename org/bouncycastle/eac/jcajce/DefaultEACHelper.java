package org.bouncycastle.eac.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.KeyFactory;

class DefaultEACHelper implements EACHelper
{
    public KeyFactory createKeyFactory(final String s) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(s);
    }
}
