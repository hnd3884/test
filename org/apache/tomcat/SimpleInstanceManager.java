package org.apache.tomcat;

import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;

public class SimpleInstanceManager implements InstanceManager
{
    @Override
    public Object newInstance(final Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, NoSuchMethodException {
        return this.prepareInstance(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
    }
    
    @Override
    public Object newInstance(final String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        return this.prepareInstance(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
    }
    
    @Override
    public Object newInstance(final String fqcn, final ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        final Class<?> clazz = classLoader.loadClass(fqcn);
        return this.prepareInstance(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
    }
    
    @Override
    public void newInstance(final Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
    }
    
    @Override
    public void destroyInstance(final Object o) throws IllegalAccessException, InvocationTargetException {
    }
    
    private Object prepareInstance(final Object o) {
        return o;
    }
}
