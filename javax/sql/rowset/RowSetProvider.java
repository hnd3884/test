package javax.sql.rowset;

import java.security.AccessControlContext;
import java.util.PropertyPermission;
import java.security.Permission;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.AccessControlException;
import java.sql.SQLException;
import sun.reflect.misc.ReflectUtil;

public class RowSetProvider
{
    private static final String ROWSET_DEBUG_PROPERTY = "javax.sql.rowset.RowSetProvider.debug";
    private static final String ROWSET_FACTORY_IMPL = "com.sun.rowset.RowSetFactoryImpl";
    private static final String ROWSET_FACTORY_NAME = "javax.sql.rowset.RowSetFactory";
    private static boolean debug;
    
    protected RowSetProvider() {
    }
    
    public static RowSetFactory newFactory() throws SQLException {
        RowSetFactory rowSetFactory = null;
        String systemProperty = null;
        try {
            trace("Checking for Rowset System Property...");
            systemProperty = getSystemProperty("javax.sql.rowset.RowSetFactory");
            if (systemProperty != null) {
                trace("Found system property, value=" + systemProperty);
                rowSetFactory = (RowSetFactory)ReflectUtil.newInstance(getFactoryClass(systemProperty, null, true));
            }
        }
        catch (final Exception ex) {
            throw new SQLException("RowSetFactory: " + systemProperty + " could not be instantiated: ", ex);
        }
        if (rowSetFactory == null) {
            final RowSetFactory loadViaServiceLoader = loadViaServiceLoader();
            rowSetFactory = ((loadViaServiceLoader == null) ? newFactory("com.sun.rowset.RowSetFactoryImpl", null) : loadViaServiceLoader);
        }
        return rowSetFactory;
    }
    
    public static RowSetFactory newFactory(final String s, final ClassLoader classLoader) throws SQLException {
        trace("***In newInstance()");
        if (s == null) {
            throw new SQLException("Error: factoryClassName cannot be null");
        }
        try {
            ReflectUtil.checkPackageAccess(s);
        }
        catch (final AccessControlException ex) {
            throw new SQLException("Access Exception", ex);
        }
        try {
            final Class<?> factoryClass = getFactoryClass(s, classLoader, false);
            final RowSetFactory rowSetFactory = (RowSetFactory)factoryClass.newInstance();
            if (RowSetProvider.debug) {
                trace("Created new instance of " + factoryClass + " using ClassLoader: " + classLoader);
            }
            return rowSetFactory;
        }
        catch (final ClassNotFoundException ex2) {
            throw new SQLException("Provider " + s + " not found", ex2);
        }
        catch (final Exception ex3) {
            throw new SQLException("Provider " + s + " could not be instantiated: " + ex3, ex3);
        }
    }
    
    private static ClassLoader getContextClassLoader() throws SecurityException {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) {
                    classLoader = ClassLoader.getSystemClassLoader();
                }
                return classLoader;
            }
        });
    }
    
    private static Class<?> getFactoryClass(final String s, ClassLoader contextClassLoader, final boolean b) throws ClassNotFoundException {
        try {
            if (contextClassLoader != null) {
                return contextClassLoader.loadClass(s);
            }
            contextClassLoader = getContextClassLoader();
            if (contextClassLoader == null) {
                throw new ClassNotFoundException();
            }
            return contextClassLoader.loadClass(s);
        }
        catch (final ClassNotFoundException ex) {
            if (b) {
                return Class.forName(s, true, RowSetFactory.class.getClassLoader());
            }
            throw ex;
        }
    }
    
    private static RowSetFactory loadViaServiceLoader() throws SQLException {
        RowSetFactory rowSetFactory = null;
        try {
            trace("***in loadViaServiceLoader():");
            final Iterator<RowSetFactory> iterator = ServiceLoader.load(RowSetFactory.class).iterator();
            if (iterator.hasNext()) {
                final RowSetFactory rowSetFactory2 = iterator.next();
                trace(" Loading done by the java.util.ServiceLoader :" + rowSetFactory2.getClass().getName());
                rowSetFactory = rowSetFactory2;
            }
        }
        catch (final ServiceConfigurationError serviceConfigurationError) {
            throw new SQLException("RowSetFactory: Error locating RowSetFactory using Service Loader API: " + serviceConfigurationError, serviceConfigurationError);
        }
        return rowSetFactory;
    }
    
    private static String getSystemProperty(final String s) {
        String s2 = null;
        try {
            s2 = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty(s);
                }
            }, null, new PropertyPermission(s, "read"));
        }
        catch (final SecurityException ex) {
            trace("error getting " + s + ":  " + ex);
            if (RowSetProvider.debug) {
                ex.printStackTrace();
            }
        }
        return s2;
    }
    
    private static void trace(final String s) {
        if (RowSetProvider.debug) {
            System.err.println("###RowSets: " + s);
        }
    }
    
    static {
        RowSetProvider.debug = true;
        final String systemProperty = getSystemProperty("javax.sql.rowset.RowSetProvider.debug");
        RowSetProvider.debug = (systemProperty != null && !"false".equals(systemProperty));
    }
}
