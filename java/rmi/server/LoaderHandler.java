package java.rmi.server;

import java.net.URL;
import java.net.MalformedURLException;

@Deprecated
public interface LoaderHandler
{
    public static final String packagePrefix = "sun.rmi.server";
    
    @Deprecated
    Class<?> loadClass(final String p0) throws MalformedURLException, ClassNotFoundException;
    
    @Deprecated
    Class<?> loadClass(final URL p0, final String p1) throws MalformedURLException, ClassNotFoundException;
    
    @Deprecated
    Object getSecurityContext(final ClassLoader p0);
}
