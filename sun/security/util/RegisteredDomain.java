package sun.security.util;

import java.util.Optional;

public interface RegisteredDomain
{
    String name();
    
    Type type();
    
    String publicSuffix();
    
    default Optional<RegisteredDomain> from(final String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        return Optional.ofNullable(sun.net.RegisteredDomain.registeredDomain(s));
    }
    
    public enum Type
    {
        ICANN, 
        PRIVATE;
    }
}
