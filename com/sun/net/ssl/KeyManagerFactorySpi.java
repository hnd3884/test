package com.sun.net.ssl;

import java.security.UnrecoverableKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.KeyStore;

@Deprecated
public abstract class KeyManagerFactorySpi
{
    protected abstract void engineInit(final KeyStore p0, final char[] p1) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException;
    
    protected abstract KeyManager[] engineGetKeyManagers();
}
