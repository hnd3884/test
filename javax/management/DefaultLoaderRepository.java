package javax.management;

@Deprecated
public class DefaultLoaderRepository
{
    public static Class<?> loadClass(final String s) throws ClassNotFoundException {
        return javax.management.loading.DefaultLoaderRepository.loadClass(s);
    }
    
    public static Class<?> loadClassWithout(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        return javax.management.loading.DefaultLoaderRepository.loadClassWithout(classLoader, s);
    }
}
