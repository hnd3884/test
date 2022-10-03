package com.turo.pushy.apns;

import java.io.IOException;
import java.util.Enumeration;
import java.security.GeneralSecurityException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.util.Objects;
import java.security.KeyStore;
import java.io.InputStream;

class P12Util
{
    public static KeyStore.PrivateKeyEntry getFirstPrivateKeyEntryFromP12InputStream(final InputStream p12InputStream, final String password) throws KeyStoreException, IOException {
        Objects.requireNonNull(password, "Password may be blank, but must not be null.");
        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(p12InputStream, password.toCharArray());
        }
        catch (final NoSuchAlgorithmException | CertificateException e) {
            throw new KeyStoreException(e);
        }
        final Enumeration<String> aliases = keyStore.aliases();
        final KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(password.toCharArray());
        while (aliases.hasMoreElements()) {
            final String alias = aliases.nextElement();
            KeyStore.Entry entry;
            try {
                try {
                    entry = keyStore.getEntry(alias, passwordProtection);
                }
                catch (final UnsupportedOperationException e2) {
                    entry = keyStore.getEntry(alias, null);
                }
            }
            catch (final UnrecoverableEntryException | NoSuchAlgorithmException e3) {
                throw new KeyStoreException(e3);
            }
            if (entry instanceof KeyStore.PrivateKeyEntry) {
                return (KeyStore.PrivateKeyEntry)entry;
            }
        }
        throw new KeyStoreException("Key store did not contain any private key entries.");
    }
}
