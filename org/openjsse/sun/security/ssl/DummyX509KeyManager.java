package org.openjsse.sun.security.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.X509ExtendedKeyManager;

final class DummyX509KeyManager extends X509ExtendedKeyManager
{
    static final X509ExtendedKeyManager INSTANCE;
    
    private DummyX509KeyManager() {
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return null;
    }
    
    @Override
    public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
        return null;
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] keyTypes, final Principal[] issuers, final SSLEngine engine) {
        return null;
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return null;
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return null;
    }
    
    @Override
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        return null;
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        return null;
    }
    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        return null;
    }
    
    static {
        INSTANCE = new DummyX509KeyManager();
    }
}
