package sun.security.ssl;

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
    public String[] getClientAliases(final String s, final Principal[] array) {
        return null;
    }
    
    @Override
    public String chooseClientAlias(final String[] array, final Principal[] array2, final Socket socket) {
        return null;
    }
    
    @Override
    public String chooseEngineClientAlias(final String[] array, final Principal[] array2, final SSLEngine sslEngine) {
        return null;
    }
    
    @Override
    public String[] getServerAliases(final String s, final Principal[] array) {
        return null;
    }
    
    @Override
    public String chooseServerAlias(final String s, final Principal[] array, final Socket socket) {
        return null;
    }
    
    @Override
    public String chooseEngineServerAlias(final String s, final Principal[] array, final SSLEngine sslEngine) {
        return null;
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String s) {
        return null;
    }
    
    @Override
    public PrivateKey getPrivateKey(final String s) {
        return null;
    }
    
    static {
        INSTANCE = new DummyX509KeyManager();
    }
}
