package org.apache.commons.net.util;

import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.X509ExtendedKeyManager;
import java.security.cert.Certificate;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.security.KeyStoreException;
import java.io.Closeable;
import org.apache.commons.net.io.Util;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.security.GeneralSecurityException;
import javax.net.ssl.KeyManager;
import java.security.KeyStore;

public final class KeyManagerUtils
{
    private static final String DEFAULT_STORE_TYPE;
    
    private KeyManagerUtils() {
    }
    
    public static KeyManager createClientKeyManager(final KeyStore ks, final String keyAlias, final String keyPass) throws GeneralSecurityException {
        final ClientKeyStore cks = new ClientKeyStore(ks, (keyAlias != null) ? keyAlias : findAlias(ks), keyPass);
        return new X509KeyManager(cks);
    }
    
    public static KeyManager createClientKeyManager(final String storeType, final File storePath, final String storePass, final String keyAlias, final String keyPass) throws IOException, GeneralSecurityException {
        final KeyStore ks = loadStore(storeType, storePath, storePass);
        return createClientKeyManager(ks, keyAlias, keyPass);
    }
    
    public static KeyManager createClientKeyManager(final File storePath, final String storePass, final String keyAlias) throws IOException, GeneralSecurityException {
        return createClientKeyManager(KeyManagerUtils.DEFAULT_STORE_TYPE, storePath, storePass, keyAlias, storePass);
    }
    
    public static KeyManager createClientKeyManager(final File storePath, final String storePass) throws IOException, GeneralSecurityException {
        return createClientKeyManager(KeyManagerUtils.DEFAULT_STORE_TYPE, storePath, storePass, null, storePass);
    }
    
    private static KeyStore loadStore(final String storeType, final File storePath, final String storePass) throws KeyStoreException, IOException, GeneralSecurityException {
        final KeyStore ks = KeyStore.getInstance(storeType);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(storePath);
            ks.load(stream, storePass.toCharArray());
        }
        finally {
            Util.closeQuietly(stream);
        }
        return ks;
    }
    
    private static String findAlias(final KeyStore ks) throws KeyStoreException {
        final Enumeration<String> e = ks.aliases();
        while (e.hasMoreElements()) {
            final String entry = e.nextElement();
            if (ks.isKeyEntry(entry)) {
                return entry;
            }
        }
        throw new KeyStoreException("Cannot find a private key entry");
    }
    
    static {
        DEFAULT_STORE_TYPE = KeyStore.getDefaultType();
    }
    
    private static class ClientKeyStore
    {
        private final X509Certificate[] certChain;
        private final PrivateKey key;
        private final String keyAlias;
        
        ClientKeyStore(final KeyStore ks, final String keyAlias, final String keyPass) throws GeneralSecurityException {
            this.keyAlias = keyAlias;
            this.key = (PrivateKey)ks.getKey(this.keyAlias, keyPass.toCharArray());
            final Certificate[] certs = ks.getCertificateChain(this.keyAlias);
            final X509Certificate[] X509certs = new X509Certificate[certs.length];
            for (int i = 0; i < certs.length; ++i) {
                X509certs[i] = (X509Certificate)certs[i];
            }
            this.certChain = X509certs;
        }
        
        final X509Certificate[] getCertificateChain() {
            return this.certChain;
        }
        
        final PrivateKey getPrivateKey() {
            return this.key;
        }
        
        final String getAlias() {
            return this.keyAlias;
        }
    }
    
    private static class X509KeyManager extends X509ExtendedKeyManager
    {
        private final ClientKeyStore keyStore;
        
        X509KeyManager(final ClientKeyStore keyStore) {
            this.keyStore = keyStore;
        }
        
        @Override
        public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
            return this.keyStore.getAlias();
        }
        
        @Override
        public X509Certificate[] getCertificateChain(final String alias) {
            return this.keyStore.getCertificateChain();
        }
        
        @Override
        public String[] getClientAliases(final String keyType, final Principal[] issuers) {
            return new String[] { this.keyStore.getAlias() };
        }
        
        @Override
        public PrivateKey getPrivateKey(final String alias) {
            return this.keyStore.getPrivateKey();
        }
        
        @Override
        public String[] getServerAliases(final String keyType, final Principal[] issuers) {
            return null;
        }
        
        @Override
        public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
            return null;
        }
    }
}
