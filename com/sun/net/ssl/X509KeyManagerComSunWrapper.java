package com.sun.net.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.net.Socket;
import java.security.Principal;

final class X509KeyManagerComSunWrapper implements X509KeyManager
{
    private javax.net.ssl.X509KeyManager theX509KeyManager;
    
    X509KeyManagerComSunWrapper(final javax.net.ssl.X509KeyManager theX509KeyManager) {
        this.theX509KeyManager = theX509KeyManager;
    }
    
    @Override
    public String[] getClientAliases(final String s, final Principal[] array) {
        return this.theX509KeyManager.getClientAliases(s, array);
    }
    
    @Override
    public String chooseClientAlias(final String s, final Principal[] array) {
        return this.theX509KeyManager.chooseClientAlias(new String[] { s }, array, null);
    }
    
    @Override
    public String[] getServerAliases(final String s, final Principal[] array) {
        return this.theX509KeyManager.getServerAliases(s, array);
    }
    
    @Override
    public String chooseServerAlias(final String s, final Principal[] array) {
        return this.theX509KeyManager.chooseServerAlias(s, array, null);
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
