package org.bouncycastle.eac.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.KeyFactory;

interface EACHelper
{
    KeyFactory createKeyFactory(final String p0) throws NoSuchProviderException, NoSuchAlgorithmException;
}
