package org.openjsse.sun.security.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509ExtendedKeyManager;

final class AbstractKeyManagerWrapper extends X509ExtendedKeyManager
{
    private final X509KeyManager km;
    
    AbstractKeyManagerWrapper(final X509KeyManager km) {
        this.km = km;
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this.km.getClientAliases(keyType, issuers);
    }
    
    @Override
    public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
        return this.km.chooseClientAlias(keyType, issuers, socket);
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this.km.getServerAliases(keyType, issuers);
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        return this.km.chooseServerAlias(keyType, issuers, socket);
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String alias) {
        return this.km.getCertificateChain(alias);
    }
    
    @Override
    public PrivateKey getPrivateKey(final String alias) {
        return this.km.getPrivateKey(alias);
    }
}
