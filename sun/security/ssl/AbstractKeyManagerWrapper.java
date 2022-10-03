package sun.security.ssl;

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
    public String[] getClientAliases(final String s, final Principal[] array) {
        return this.km.getClientAliases(s, array);
    }
    
    @Override
    public String chooseClientAlias(final String[] array, final Principal[] array2, final Socket socket) {
        return this.km.chooseClientAlias(array, array2, socket);
    }
    
    @Override
    public String[] getServerAliases(final String s, final Principal[] array) {
        return this.km.getServerAliases(s, array);
    }
    
    @Override
    public String chooseServerAlias(final String s, final Principal[] array, final Socket socket) {
        return this.km.chooseServerAlias(s, array, socket);
    }
    
    @Override
    public X509Certificate[] getCertificateChain(final String s) {
        return this.km.getCertificateChain(s);
    }
    
    @Override
    public PrivateKey getPrivateKey(final String s) {
        return this.km.getPrivateKey(s);
    }
}
