package com.sun.net.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.Principal;
import javax.net.ssl.X509KeyManager;

final class X509KeyManagerJavaxWrapper implements X509KeyManager
{
    private com.sun.net.ssl.X509KeyManager theX509KeyManager;
    
    X509KeyManagerJavaxWrapper(final com.sun.net.ssl.X509KeyManager theX509KeyManager) {
        this.theX509KeyManager = theX509KeyManager;
    }
    
    @Override
    public String[] getClientAliases(final String s, final Principal[] array) {
        return this.theX509KeyManager.getClientAliases(s, array);
    }
    
    @Override
    public String chooseClientAlias(final String[] array, final Principal[] array2, final Socket socket) {
        if (array == null) {
            return null;
        }
        for (int i = 0; i < array.length; ++i) {
            final String chooseClientAlias;
            if ((chooseClientAlias = this.theX509KeyManager.chooseClientAlias(array[i], array2)) != null) {
                return chooseClientAlias;
            }
        }
        return null;
    }
    
    public String chooseEngineClientAlias(final String[] array, final Principal[] array2, final SSLEngine sslEngine) {
        if (array == null) {
            return null;
        }
        for (int i = 0; i < array.length; ++i) {
            final String chooseClientAlias;
            if ((chooseClientAlias = this.theX509KeyManager.chooseClientAlias(array[i], array2)) != null) {
                return chooseClientAlias;
            }
        }
        return null;
    }
    
    @Override
    public String[] getServerAliases(final String s, final Principal[] array) {
        return this.theX509KeyManager.getServerAliases(s, array);
    }
    
    @Override
    public String chooseServerAlias(final String s, final Principal[] array, final Socket socket) {
        if (s == null) {
            return null;
        }
        return this.theX509KeyManager.chooseServerAlias(s, array);
    }
    
    public String chooseEngineServerAlias(final String s, final Principal[] array, final SSLEngine sslEngine) {
        if (s == null) {
            return null;
        }
        return this.theX509KeyManager.chooseServerAlias(s, array);
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String s) {
        return this.theX509KeyManager.getCertificateChain(s);
    }
    
    @Override
    public PrivateKey getPrivateKey(final String s) {
        return this.theX509KeyManager.getPrivateKey(s);
    }
}
