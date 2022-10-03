package sun.security.jgss.spi;

import org.ietf.jgss.Oid;
import org.ietf.jgss.GSSException;
import java.security.Provider;

public interface GSSNameSpi
{
    Provider getProvider();
    
    boolean equals(final GSSNameSpi p0) throws GSSException;
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    byte[] export() throws GSSException;
    
    Oid getMechanism();
    
    String toString();
    
    Oid getStringNameType();
    
    boolean isAnonymousName();
}
