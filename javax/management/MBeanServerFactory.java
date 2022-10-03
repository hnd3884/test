package javax.management;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import sun.reflect.misc.ReflectUtil;
import java.util.logging.Level;
import java.security.Permission;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.loading.ClassLoaderRepository;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;

public class MBeanServerFactory
{
    private static MBeanServerBuilder builder;
    private static final ArrayList<MBeanServer> mBeanServerList;
    
    private MBeanServerFactory() {
    }
    
    public static void releaseMBeanServer(final MBeanServer mBeanServer) {
        checkPermission("releaseMBeanServer");
        removeMBeanServer(mBeanServer);
    }
    
    public static MBeanServer createMBeanServer() {
        return createMBeanServer(null);
    }
    
    public static MBeanServer createMBeanServer(final String s) {
        checkPermission("createMBeanServer");
        final MBeanServer mBeanServer = newMBeanServer(s);
        addMBeanServer(mBeanServer);
        return mBeanServer;
    }
    
    public static MBeanServer newMBeanServer() {
        return newMBeanServer(null);
    }
    
    public static MBeanServer newMBeanServer(final String s) {
        checkPermission("newMBeanServer");
        final MBeanServerBuilder newMBeanServerBuilder = getNewMBeanServerBuilder();
        synchronized (newMBeanServerBuilder) {
            final MBeanServerDelegate mBeanServerDelegate = newMBeanServerBuilder.newMBeanServerDelegate();
            if (mBeanServerDelegate == null) {
                throw new JMRuntimeException("MBeanServerBuilder.newMBeanServerDelegate() returned null");
            }
            final MBeanServer mBeanServer = newMBeanServerBuilder.newMBeanServer(s, null, mBeanServerDelegate);
            if (mBeanServer == null) {
                throw new JMRuntimeException("MBeanServerBuilder.newMBeanServer() returned null");
            }
            return mBeanServer;
        }
    }
    
    public static synchronized ArrayList<MBeanServer> findMBeanServer(final String s) {
        checkPermission("findMBeanServer");
        if (s == null) {
            return new ArrayList<MBeanServer>(MBeanServerFactory.mBeanServerList);
        }
        final ArrayList list = new ArrayList();
        for (final MBeanServer mBeanServer : MBeanServerFactory.mBeanServerList) {
            if (s.equals(mBeanServerId(mBeanServer))) {
                list.add(mBeanServer);
            }
        }
        return list;
    }
    
    public static ClassLoaderRepository getClassLoaderRepository(final MBeanServer mBeanServer) {
        return mBeanServer.getClassLoaderRepository();
    }
    
    private static String mBeanServerId(final MBeanServer mBeanServer) {
        try {
            return (String)mBeanServer.getAttribute(MBeanServerDelegate.DELEGATE_NAME, "MBeanServerId");
        }
        catch (final JMException ex) {
            JmxProperties.MISC_LOGGER.finest("Ignoring exception while getting MBeanServerId: " + ex);
            return null;
        }
    }
    
    private static void checkPermission(final String s) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new MBeanServerPermission(s));
        }
    }
    
    private static synchronized void addMBeanServer(final MBeanServer mBeanServer) {
        MBeanServerFactory.mBeanServerList.add(mBeanServer);
    }
    
    private static synchronized void removeMBeanServer(final MBeanServer mBeanServer) {
        if (!MBeanServerFactory.mBeanServerList.remove(mBeanServer)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, MBeanServerFactory.class.getName(), "removeMBeanServer(MBeanServer)", "MBeanServer was not in list!");
            throw new IllegalArgumentException("MBeanServer was not in list!");
        }
    }
    
    private static Class<?> loadBuilderClass(final String s) throws ClassNotFoundException {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader.loadClass(s);
        }
        return ReflectUtil.forName(s);
    }
    
    private static MBeanServerBuilder newBuilder(final Class<?> clazz) {
        try {
            return (MBeanServerBuilder)clazz.newInstance();
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new JMRuntimeException("Failed to instantiate a MBeanServerBuilder from " + clazz + ": " + ex2, ex2);
        }
    }
    
    private static synchronized void checkMBeanServerBuilder() {
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("javax.management.builder.initial"));
            try {
                Class<?> loadBuilderClass;
                if (s == null || s.length() == 0) {
                    loadBuilderClass = MBeanServerBuilder.class;
                }
                else {
                    loadBuilderClass = loadBuilderClass(s);
                }
                if (MBeanServerFactory.builder != null && loadBuilderClass == MBeanServerFactory.builder.getClass()) {
                    return;
                }
                MBeanServerFactory.builder = newBuilder(loadBuilderClass);
            }
            catch (final ClassNotFoundException ex) {
                throw new JMRuntimeException("Failed to load MBeanServerBuilder class " + s + ": " + ex, ex);
            }
        }
        catch (final RuntimeException ex2) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanServerFactory.class.getName(), "checkMBeanServerBuilder", "Failed to instantiate MBeanServerBuilder: " + ex2 + "\n\t\tCheck the value of the " + "javax.management.builder.initial" + " property.");
            }
            throw ex2;
        }
    }
    
    private static synchronized MBeanServerBuilder getNewMBeanServerBuilder() {
        checkMBeanServerBuilder();
        return MBeanServerFactory.builder;
    }
    
    static {
        MBeanServerFactory.builder = null;
        mBeanServerList = new ArrayList<MBeanServer>();
    }
}
