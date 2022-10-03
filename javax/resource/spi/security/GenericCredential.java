package javax.resource.spi.security;

import javax.resource.spi.SecurityException;

public interface GenericCredential
{
    byte[] getCredentialData() throws SecurityException;
    
    String getMechType();
    
    String getName();
    
    boolean equals(final Object p0);
    
    int hashCode();
}
