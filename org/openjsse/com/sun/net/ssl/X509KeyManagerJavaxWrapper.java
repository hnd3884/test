package org.openjsse.com.sun.net.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.openjsse.javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.X509KeyManager;

final class X509KeyManagerJavaxWrapper implements X509KeyManager
{
    private org.openjsse.com.sun.net.ssl.X509KeyManager theX509KeyManager;
    
    X509KeyManagerJavaxWrapper(final org.openjsse.com.sun.net.ssl.X509KeyManager obj) {
        this.theX509KeyManager = obj;
    }
    
    @Override
    public String[] getClientAliases(final String keyType, final Principal[] issuers) {
        return this.theX509KeyManager.getClientAliases(keyType, issuers);
    }
    
    @Override
    public String chooseClientAlias(final String[] keyTypes, final Principal[] issuers, final Socket socket) {
        if (keyTypes == null) {
            return null;
        }
        for (int i = 0; i < keyTypes.length; ++i) {
            final String retval;
            if ((retval = this.theX509KeyManager.chooseClientAlias(keyTypes[i], issuers)) != null) {
                return retval;
            }
        }
        return null;
    }
    
    public String chooseEngineClientAlias(final String[] keyTypes, final Principal[] issuers, final SSLEngine engine) {
        if (keyTypes == null) {
            return null;
        }
        for (int i = 0; i < keyTypes.length; ++i) {
            final String retval;
            if ((retval = this.theX509KeyManager.chooseClientAlias(keyTypes[i], issuers)) != null) {
                return retval;
            }
        }
        return null;
    }
    
    @Override
    public String[] getServerAliases(final String keyType, final Principal[] issuers) {
        return this.theX509KeyManager.getServerAliases(keyType, issuers);
    }
    
    @Override
    public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
        if (keyType == null) {
            return null;
        }
        return this.theX509KeyManager.chooseServerAlias(keyType, issuers);
    }
    
    public String chooseEngineServerAlias(final String keyType, final Principal[] issuers, final SSLEngine engine) {
        if (keyType == null) {
            return null;
        }
        return this.theX509KeyManager.chooseServerAlias(keyType, issuers);
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
