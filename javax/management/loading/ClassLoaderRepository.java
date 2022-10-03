package javax.management.loading;

public interface ClassLoaderRepository
{
    Class<?> loadClass(final String p0) throws ClassNotFoundException;
    
    Class<?> loadClassWithout(final ClassLoader p0, final String p1) throws ClassNotFoundException;
    
    Class<?> loadClassBefore(final ClassLoader p0, final String p1) throws ClassNotFoundException;
}
