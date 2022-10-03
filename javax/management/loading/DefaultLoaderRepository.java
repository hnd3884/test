package javax.management.loading;

import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;

@Deprecated
public class DefaultLoaderRepository
{
    public static Class<?> loadClass(final String s) throws ClassNotFoundException {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClass", s);
        return load(null, s);
    }
    
    public static Class<?> loadClassWithout(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultLoaderRepository.class.getName(), "loadClassWithout", s);
        return load(classLoader, s);
    }
    
    private static Class<?> load(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        final Iterator<Object> iterator = MBeanServerFactory.findMBeanServer(null).iterator();
        while (iterator.hasNext()) {
            final ClassLoaderRepository classLoaderRepository = iterator.next().getClassLoaderRepository();
            try {
                return classLoaderRepository.loadClassWithout(classLoader, s);
            }
            catch (final ClassNotFoundException ex) {
                continue;
            }
            break;
        }
        throw new ClassNotFoundException(s);
    }
}
