package org.openjsse.sun.security.util;

import java.util.Optional;

public interface RegisteredDomain
{
    String name();
    
    Type type();
    
    String publicSuffix();
    
    default Optional<RegisteredDomain> from(final String domain) {
        return Optional.ofNullable(DomainName.registeredDomain(domain));
    }
    
    public enum Type
    {
        ICANN, 
        PRIVATE;
    }
}
