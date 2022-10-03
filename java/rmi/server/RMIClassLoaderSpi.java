package java.rmi.server;

import java.net.MalformedURLException;

public abstract class RMIClassLoaderSpi
{
    public abstract Class<?> loadClass(final String p0, final String p1, final ClassLoader p2) throws MalformedURLException, ClassNotFoundException;
    
    public abstract Class<?> loadProxyClass(final String p0, final String[] p1, final ClassLoader p2) throws MalformedURLException, ClassNotFoundException;
    
    public abstract ClassLoader getClassLoader(final String p0) throws MalformedURLException;
    
    public abstract String getClassAnnotation(final Class<?> p0);
}
