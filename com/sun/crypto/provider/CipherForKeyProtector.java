package com.sun.crypto.provider;

import java.security.Provider;
import javax.crypto.CipherSpi;
import javax.crypto.Cipher;

final class CipherForKeyProtector extends Cipher
{
    protected CipherForKeyProtector(final CipherSpi cipherSpi, final Provider provider, final String s) {
        super(cipherSpi, provider, s);
    }
}
