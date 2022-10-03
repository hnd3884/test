package sun.security.jgss.spi;

import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import java.security.Provider;

public interface GSSCredentialSpi
{
    Provider getProvider();
    
    void dispose() throws GSSException;
    
    GSSNameSpi getName() throws GSSException;
    
    int getInitLifetime() throws GSSException;
    
    int getAcceptLifetime() throws GSSException;
    
    boolean isInitiatorCredential() throws GSSException;
    
    boolean isAcceptorCredential() throws GSSException;
    
    Oid getMechanism();
    
    GSSCredentialSpi impersonate(final GSSNameSpi p0) throws GSSException;
}
