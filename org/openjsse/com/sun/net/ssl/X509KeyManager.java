package org.openjsse.com.sun.net.ssl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.Principal;

@Deprecated
public interface X509KeyManager extends KeyManager
{
    String[] getClientAliases(final String p0, final Principal[] p1);
    
    String chooseClientAlias(final String p0, final Principal[] p1);
    
    String[] getServerAliases(final String p0, final Principal[] p1);
    
    String chooseServerAlias(final String p0, final Principal[] p1);
    
    X509Certificate[] getCertificateChain(final String p0);
    
    PrivateKey getPrivateKey(final String p0);
}
