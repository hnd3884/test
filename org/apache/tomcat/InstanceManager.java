package org.apache.tomcat;

import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;

public interface InstanceManager
{
    Object newInstance(final Class<?> p0) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException;
    
    Object newInstance(final String p0) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException;
    
    Object newInstance(final String p0, final ClassLoader p1) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException;
    
    void newInstance(final Object p0) throws IllegalAccessException, InvocationTargetException, NamingException;
    
    void destroyInstance(final Object p0) throws IllegalAccessException, InvocationTargetException;
}
