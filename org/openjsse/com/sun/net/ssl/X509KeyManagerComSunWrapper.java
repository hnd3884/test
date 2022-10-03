package org.openjsse.com.sun.net.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.Socket;
import java.security.Principal;

final class X509KeyManagerComSunWrapper implements X509KeyManager
{
    private javax.net.ssl.X509KeyManager theX509KeyManager;
    
    X509KeyManagerComSunWrapper(final javax.net.ssl.X509KeyManager obj) {
        this.theX509KeyManager = obj;
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this.theX509KeyManager.getClientAliases(keyType, issuers);
    }
    
    @Override
    public String chooseClientAlias(final String keyType, final Principal[] issuers) {
        final String[] keyTypes = { keyType };
        return this.theX509KeyManager.chooseClientAlias(keyTypes, issuers, null);
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this.theX509KeyManager.getServerAliases(keyType, issuers);
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers) {
        return this.theX509KeyManager.chooseServerAlias(keyType, issuers, null);
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        return this.theX509KeyManager.getCertificateChain(alias);
    }
    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        return this.theX509KeyManager.getPrivateKey(alias);
    }
}
