package javax.net.ssl;

import java.security.Principal;

public abstract class X509ExtendedKeyManager implements X509KeyManager
{
    protected X509ExtendedKeyManager() {
    }
    
    public String chooseEngineClientAlias(final String[] array, final Principal[] array2, final SSLEngine sslEngine) {
        return null;
    }
    
    public String chooseEngineServerAlias(final String s, final Principal[] array, final SSLEngine sslEngine) {
        return null;
    }
}
