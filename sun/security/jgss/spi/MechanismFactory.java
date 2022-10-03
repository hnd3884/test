package sun.security.jgss.spi;

import org.ietf.jgss.GSSException;
import java.security.Provider;
import org.ietf.jgss.Oid;

public interface MechanismFactory
{
    Oid getMechanismOid();
    
    Provider getProvider();
    
    Oid[] getNameTypes() throws GSSException;
    
    GSSCredentialSpi getCredentialElement(final GSSNameSpi p0, final int p1, final int p2, final int p3) throws GSSException;
    
    GSSNameSpi getNameElement(final String p0, final Oid p1) throws GSSException;
    
    GSSNameSpi getNameElement(final byte[] p0, final Oid p1) throws GSSException;
    
    GSSContextSpi getMechanismContext(final GSSNameSpi p0, final GSSCredentialSpi p1, final int p2) throws GSSException;
    
    GSSContextSpi getMechanismContext(final GSSCredentialSpi p0) throws GSSException;
    
    GSSContextSpi getMechanismContext(final byte[] p0) throws GSSException;
}
