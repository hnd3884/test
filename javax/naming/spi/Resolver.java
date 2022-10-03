package javax.naming.spi;

import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.Name;

public interface Resolver
{
    ResolveResult resolveToClass(final Name p0, final Class<? extends Context> p1) throws NamingException;
    
    ResolveResult resolveToClass(final String p0, final Class<? extends Context> p1) throws NamingException;
}
