package javax.ws.rs.core;

import java.util.Map;

public interface Configurable<C extends Configurable>
{
    Configuration getConfiguration();
    
    C property(final String p0, final Object p1);
    
    C register(final Class<?> p0);
    
    C register(final Class<?> p0, final int p1);
    
    C register(final Class<?> p0, final Class<?>... p1);
    
    C register(final Class<?> p0, final Map<Class<?>, Integer> p1);
    
    C register(final Object p0);
    
    C register(final Object p0, final int p1);
    
    C register(final Object p0, final Class<?>... p1);
    
    C register(final Object p0, final Map<Class<?>, Integer> p1);
}
