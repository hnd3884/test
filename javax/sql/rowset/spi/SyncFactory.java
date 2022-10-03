package javax.sql.rowset.spi;

import javax.naming.NotContextException;
import javax.naming.Binding;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.logging.Level;
import java.security.AccessControlException;
import sun.reflect.misc.ReflectUtil;
import com.sun.rowset.providers.RIOptimisticProvider;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.security.PrivilegedActionException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.PropertyPermission;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Properties;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.naming.Context;
import java.sql.SQLPermission;

public class SyncFactory
{
    public static final String ROWSET_SYNC_PROVIDER = "rowset.provider.classname";
    public static final String ROWSET_SYNC_VENDOR = "rowset.provider.vendor";
    public static final String ROWSET_SYNC_PROVIDER_VERSION = "rowset.provider.version";
    private static String ROWSET_PROPERTIES;
    private static final SQLPermission SET_SYNCFACTORY_PERMISSION;
    private static Context ic;
    private static volatile Logger rsLogger;
    private static Hashtable<String, SyncProvider> implementations;
    private static String colon;
    private static String strFileSep;
    private static boolean debug;
    private static int providerImplIndex;
    private static boolean lazyJNDICtxRefresh;
    
    private SyncFactory() {
    }
    
    public static synchronized void registerProvider(final String classname) throws SyncFactoryException {
        final ProviderImpl providerImpl = new ProviderImpl();
        providerImpl.setClassname(classname);
        initMapIfNecessary();
        SyncFactory.implementations.put(classname, providerImpl);
    }
    
    public static SyncFactory getSyncFactory() {
        return SyncFactoryHolder.factory;
    }
    
    public static synchronized void unregisterProvider(final String s) throws SyncFactoryException {
        initMapIfNecessary();
        if (SyncFactory.implementations.containsKey(s)) {
            SyncFactory.implementations.remove(s);
        }
    }
    
    private static synchronized void initMapIfNecessary() throws SyncFactoryException {
        final Properties properties = new Properties();
        if (SyncFactory.implementations == null) {
            SyncFactory.implementations = new Hashtable<String, SyncProvider>();
            try {
                String rowset_PROPERTIES;
                try {
                    rowset_PROPERTIES = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                        @Override
                        public String run() {
                            return System.getProperty("rowset.properties");
                        }
                    }, null, new PropertyPermission("rowset.properties", "read"));
                }
                catch (final Exception ex) {
                    System.out.println("errorget rowset.properties: " + ex);
                    rowset_PROPERTIES = null;
                }
                if (rowset_PROPERTIES != null) {
                    SyncFactory.ROWSET_PROPERTIES = rowset_PROPERTIES;
                    try (final FileInputStream fileInputStream = new FileInputStream(SyncFactory.ROWSET_PROPERTIES)) {
                        properties.load(fileInputStream);
                    }
                    parseProperties(properties);
                }
                SyncFactory.ROWSET_PROPERTIES = "javax" + SyncFactory.strFileSep + "sql" + SyncFactory.strFileSep + "rowset" + SyncFactory.strFileSep + "rowset.properties";
                Thread.currentThread().getContextClassLoader();
                try {
                    AccessController.doPrivileged(() -> {
                        final InputStream inputStream = (classLoader == null) ? ClassLoader.getSystemResourceAsStream(SyncFactory.ROWSET_PROPERTIES) : classLoader.getResourceAsStream(SyncFactory.ROWSET_PROPERTIES);
                        try {
                            if (inputStream == null) {
                                new SyncFactoryException("Resource " + SyncFactory.ROWSET_PROPERTIES + " not found");
                                throw;
                            }
                            else {
                                properties2.load(inputStream);
                            }
                        }
                        catch (final Throwable t4) {
                            throw t4;
                        }
                        finally {
                            if (inputStream != null) {
                                final Throwable t5;
                                if (t5 != null) {
                                    try {
                                        inputStream.close();
                                    }
                                    catch (final Throwable t6) {
                                        t5.addSuppressed(t6);
                                    }
                                }
                                else {
                                    inputStream.close();
                                }
                            }
                        }
                        return null;
                    });
                }
                catch (final PrivilegedActionException ex2) {
                    final Exception exception = ex2.getException();
                    if (exception instanceof SyncFactoryException) {
                        throw (SyncFactoryException)exception;
                    }
                    final SyncFactoryException ex3 = new SyncFactoryException();
                    ex3.initCause(ex2.getException());
                    throw ex3;
                }
                parseProperties(properties);
            }
            catch (final FileNotFoundException ex4) {
                throw new SyncFactoryException("Cannot locate properties file: " + ex4);
            }
            catch (final IOException ex5) {
                throw new SyncFactoryException("IOException: " + ex5);
            }
            properties.clear();
            String s;
            try {
                s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                    @Override
                    public String run() {
                        return System.getProperty("rowset.provider.classname");
                    }
                }, null, new PropertyPermission("rowset.provider.classname", "read"));
            }
            catch (final Exception ex6) {
                s = null;
            }
            if (s != null) {
                int n = 0;
                if (s.indexOf(SyncFactory.colon) > 0) {
                    final StringTokenizer stringTokenizer = new StringTokenizer(s, SyncFactory.colon);
                    while (stringTokenizer.hasMoreElements()) {
                        ((Hashtable<String, String>)properties).put("rowset.provider.classname." + n, stringTokenizer.nextToken());
                        ++n;
                    }
                }
                else {
                    ((Hashtable<String, String>)properties).put("rowset.provider.classname", s);
                }
                parseProperties(properties);
            }
        }
    }
    
    private static void parseProperties(final Properties properties) {
        final Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            final String s = (String)propertyNames.nextElement();
            final int length = s.length();
            if (s.startsWith("rowset.provider.classname")) {
                final ProviderImpl providerImpl = new ProviderImpl();
                providerImpl.setIndex(SyncFactory.providerImplIndex++);
                String[] array;
                if (length == "rowset.provider.classname".length()) {
                    array = getPropertyNames(false);
                }
                else {
                    array = getPropertyNames(true, s.substring(length - 1));
                }
                final String property = properties.getProperty(array[0]);
                providerImpl.setClassname(property);
                providerImpl.setVendor(properties.getProperty(array[1]));
                providerImpl.setVersion(properties.getProperty(array[2]));
                SyncFactory.implementations.put(property, providerImpl);
            }
        }
    }
    
    private static String[] getPropertyNames(final boolean b) {
        return getPropertyNames(b, null);
    }
    
    private static String[] getPropertyNames(final boolean b, final String s) {
        final String s2 = ".";
        final String[] array = { "rowset.provider.classname", "rowset.provider.vendor", "rowset.provider.version" };
        if (b) {
            for (int i = 0; i < array.length; ++i) {
                array[i] = array[i] + s2 + s;
            }
            return array;
        }
        return array;
    }
    
    private static void showImpl(final ProviderImpl providerImpl) {
        System.out.println("Provider implementation:");
        System.out.println("Classname: " + providerImpl.getClassname());
        System.out.println("Vendor: " + providerImpl.getVendor());
        System.out.println("Version: " + providerImpl.getVersion());
        System.out.println("Impl index: " + providerImpl.getIndex());
    }
    
    public static SyncProvider getInstance(final String s) throws SyncFactoryException {
        if (s == null) {
            throw new SyncFactoryException("The providerID cannot be null");
        }
        initMapIfNecessary();
        initJNDIContext();
        if (SyncFactory.implementations.get(s) == null) {
            return new RIOptimisticProvider();
        }
        try {
            ReflectUtil.checkPackageAccess(s);
        }
        catch (final AccessControlException ex) {
            final SyncFactoryException ex2 = new SyncFactoryException();
            ex2.initCause(ex);
            throw ex2;
        }
        try {
            final Class<?> forName = Class.forName(s, true, Thread.currentThread().getContextClassLoader());
            if (forName != null) {
                return (SyncProvider)forName.newInstance();
            }
            return new RIOptimisticProvider();
        }
        catch (final IllegalAccessException ex3) {
            throw new SyncFactoryException("IllegalAccessException: " + ex3.getMessage());
        }
        catch (final InstantiationException ex4) {
            throw new SyncFactoryException("InstantiationException: " + ex4.getMessage());
        }
        catch (final ClassNotFoundException ex5) {
            throw new SyncFactoryException("ClassNotFoundException: " + ex5.getMessage());
        }
    }
    
    public static Enumeration<SyncProvider> getRegisteredProviders() throws SyncFactoryException {
        initMapIfNecessary();
        return SyncFactory.implementations.elements();
    }
    
    public static void setLogger(final Logger rsLogger) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SyncFactory.SET_SYNCFACTORY_PERMISSION);
        }
        if (rsLogger == null) {
            throw new NullPointerException("You must provide a Logger");
        }
        SyncFactory.rsLogger = rsLogger;
    }
    
    public static void setLogger(final Logger rsLogger, final Level level) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SyncFactory.SET_SYNCFACTORY_PERMISSION);
        }
        if (rsLogger == null) {
            throw new NullPointerException("You must provide a Logger");
        }
        rsLogger.setLevel(level);
        SyncFactory.rsLogger = rsLogger;
    }
    
    public static Logger getLogger() throws SyncFactoryException {
        final Logger rsLogger = SyncFactory.rsLogger;
        if (rsLogger == null) {
            throw new SyncFactoryException("(SyncFactory) : No logger has been set");
        }
        return rsLogger;
    }
    
    public static synchronized void setJNDIContext(final Context ic) throws SyncFactoryException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SyncFactory.SET_SYNCFACTORY_PERMISSION);
        }
        if (ic == null) {
            throw new SyncFactoryException("Invalid JNDI context supplied");
        }
        SyncFactory.ic = ic;
    }
    
    private static synchronized void initJNDIContext() throws SyncFactoryException {
        if (SyncFactory.ic != null && !SyncFactory.lazyJNDICtxRefresh) {
            try {
                parseProperties(parseJNDIContext());
                SyncFactory.lazyJNDICtxRefresh = true;
            }
            catch (final NamingException ex) {
                ex.printStackTrace();
                throw new SyncFactoryException("SPI: NamingException: " + ex.getExplanation());
            }
            catch (final Exception ex2) {
                ex2.printStackTrace();
                throw new SyncFactoryException("SPI: Exception: " + ex2.getMessage());
            }
        }
    }
    
    private static Properties parseJNDIContext() throws NamingException {
        final NamingEnumeration<Binding> listBindings = SyncFactory.ic.listBindings("");
        final Properties properties = new Properties();
        enumerateBindings(listBindings, properties);
        return properties;
    }
    
    private static void enumerateBindings(final NamingEnumeration<?> namingEnumeration, final Properties properties) throws NamingException {
        int n = 0;
        try {
            while (namingEnumeration.hasMore()) {
                final Binding binding = namingEnumeration.next();
                final String name = binding.getName();
                final Object object = binding.getObject();
                if (!(SyncFactory.ic.lookup(name) instanceof Context) && SyncFactory.ic.lookup(name) instanceof SyncProvider) {
                    n = 1;
                }
                if (n != 0) {
                    ((Hashtable<String, String>)properties).put("rowset.provider.classname", ((SyncProvider)object).getProviderID());
                    n = 0;
                }
            }
        }
        catch (final NotContextException ex) {
            namingEnumeration.next();
            enumerateBindings(namingEnumeration, properties);
        }
    }
    
    static {
        SyncFactory.ROWSET_PROPERTIES = "rowset.properties";
        SET_SYNCFACTORY_PERMISSION = new SQLPermission("setSyncFactory");
        SyncFactory.colon = ":";
        SyncFactory.strFileSep = "/";
        SyncFactory.debug = false;
        SyncFactory.providerImplIndex = 0;
        SyncFactory.lazyJNDICtxRefresh = false;
    }
    
    private static class SyncFactoryHolder
    {
        static final SyncFactory factory;
        
        static {
            factory = new SyncFactory(null);
        }
    }
}
