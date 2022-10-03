package javax.ws.rs.core;

import java.util.Set;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.RuntimeType;

public interface Configuration
{
    RuntimeType getRuntimeType();
    
    Map<String, Object> getProperties();
    
    Object getProperty(final String p0);
    
    Collection<String> getPropertyNames();
    
    boolean isEnabled(final Feature p0);
    
    boolean isEnabled(final Class<? extends Feature> p0);
    
    boolean isRegistered(final Object p0);
    
    boolean isRegistered(final Class<?> p0);
    
    Map<Class<?>, Integer> getContracts(final Class<?> p0);
    
    Set<Class<?>> getClasses();
    
    Set<Object> getInstances();
}
