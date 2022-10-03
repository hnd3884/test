package java.net;

import java.security.Principal;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import java.util.List;

public abstract class SecureCacheResponse extends CacheResponse
{
    public abstract String getCipherSuite();
    
    public abstract List<Certificate> getLocalCertificateChain();
    
    public abstract List<Certificate> getServerCertificateChain() throws SSLPeerUnverifiedException;
    
    public abstract Principal getPeerPrincipal() throws SSLPeerUnverifiedException;
    
    public abstract Principal getLocalPrincipal();
}
