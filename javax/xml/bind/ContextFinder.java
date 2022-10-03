package javax.xml.bind;

import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.Iterator;
import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PrivilegedAction;
import java.security.AccessController;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.lang.reflect.Method;
import java.util.Map;
import java.net.URL;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

class ContextFinder
{
    private static final Logger logger;
    private static final String PLATFORM_DEFAULT_FACTORY_CLASS = "com.sun.xml.internal.bind.v2.ContextFactory";
    
    private static void handleInvocationTargetException(final InvocationTargetException x) throws JAXBException {
        final Throwable t = x.getTargetException();
        if (t != null) {
            if (t instanceof JAXBException) {
                throw (JAXBException)t;
            }
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
        }
    }
    
    private static JAXBException handleClassCastException(final Class originalType, final Class targetType) {
        final URL targetTypeURL = which(targetType);
        return new JAXBException(Messages.format("JAXBContext.IllegalCast", getClassClassLoader(originalType).getResource("javax/xml/bind/JAXBContext.class"), targetTypeURL));
    }
    
    static JAXBContext newInstance(final String contextPath, final String className, final ClassLoader classLoader, final Map properties) throws JAXBException {
        try {
            final Class spFactory = safeLoadClass(className, classLoader);
            return newInstance(contextPath, spFactory, classLoader, properties);
        }
        catch (final ClassNotFoundException x) {
            throw new JAXBException(Messages.format("ContextFinder.ProviderNotFound", className), x);
        }
        catch (final RuntimeException x2) {
            throw x2;
        }
        catch (final Exception x3) {
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", className, x3), x3);
        }
    }
    
    static JAXBContext newInstance(final String contextPath, final Class spFactory, final ClassLoader classLoader, final Map properties) throws JAXBException {
        try {
            Object context = null;
            try {
                final Method m = spFactory.getMethod("createContext", String.class, ClassLoader.class, Map.class);
                context = m.invoke(null, contextPath, classLoader, properties);
            }
            catch (final NoSuchMethodException ex) {}
            if (context == null) {
                final Method m = spFactory.getMethod("createContext", String.class, ClassLoader.class);
                context = m.invoke(null, contextPath, classLoader);
            }
            if (!(context instanceof JAXBContext)) {
                throw handleClassCastException(context.getClass(), JAXBContext.class);
            }
            return (JAXBContext)context;
        }
        catch (final InvocationTargetException x) {
            handleInvocationTargetException(x);
            Throwable e = x;
            if (x.getTargetException() != null) {
                e = x.getTargetException();
            }
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, e), e);
        }
        catch (final RuntimeException x2) {
            throw x2;
        }
        catch (final Exception x3) {
            throw new JAXBException(Messages.format("ContextFinder.CouldNotInstantiate", spFactory, x3), x3);
        }
    }
    
    static JAXBContext newInstance(final Class[] classes, final Map properties, final String className) throws JAXBException {
        final ClassLoader cl = getContextClassLoader();
        Class spi;
        try {
            spi = safeLoadClass(className, cl);
        }
        catch (final ClassNotFoundException e) {
            throw new JAXBException(e);
        }
        if (ContextFinder.logger.isLoggable(Level.FINE)) {
            ContextFinder.logger.log(Level.FINE, "loaded {0} from {1}", new Object[] { className, which(spi) });
        }
        return newInstance(classes, properties, spi);
    }
    
    static JAXBContext newInstance(final Class[] classes, final Map properties, final Class spFactory) throws JAXBException {
        Method m;
        try {
            m = spFactory.getMethod("createContext", Class[].class, Map.class);
        }
        catch (final NoSuchMethodException e) {
            throw new JAXBException(e);
        }
        try {
            final Object context = m.invoke(null, classes, properties);
            if (!(context instanceof JAXBContext)) {
                throw handleClassCastException(context.getClass(), JAXBContext.class);
            }
            return (JAXBContext)context;
        }
        catch (final IllegalAccessException e2) {
            throw new JAXBException(e2);
        }
        catch (final InvocationTargetException e3) {
            handleInvocationTargetException(e3);
            Throwable x = e3;
            if (e3.getTargetException() != null) {
                x = e3.getTargetException();
            }
            throw new JAXBException(x);
        }
    }
    
    static JAXBContext find(final String factoryId, final String contextPath, final ClassLoader classLoader, final Map properties) throws JAXBException {
        final String jaxbContextFQCN = JAXBContext.class.getName();
        final StringTokenizer packages = new StringTokenizer(contextPath, ":");
        if (!packages.hasMoreTokens()) {
            throw new JAXBException(Messages.format("ContextFinder.NoPackageInContextPath"));
        }
        ContextFinder.logger.fine("Searching jaxb.properties");
        while (packages.hasMoreTokens()) {
            final String packageName = packages.nextToken(":").replace('.', '/');
            final StringBuilder propFileName = new StringBuilder().append(packageName).append("/jaxb.properties");
            final Properties props = loadJAXBProperties(classLoader, propFileName.toString());
            if (props != null) {
                if (props.containsKey(factoryId)) {
                    final String factoryClassName = props.getProperty(factoryId);
                    return newInstance(contextPath, factoryClassName, classLoader, properties);
                }
                throw new JAXBException(Messages.format("ContextFinder.MissingProperty", packageName, factoryId));
            }
        }
        ContextFinder.logger.fine("Searching the system property");
        String factoryClassName = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("javax.xml.bind.context.factory"));
        if (factoryClassName != null) {
            return newInstance(contextPath, factoryClassName, classLoader, properties);
        }
        factoryClassName = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(jaxbContextFQCN));
        if (factoryClassName != null) {
            return newInstance(contextPath, factoryClassName, classLoader, properties);
        }
        final Class jaxbContext = lookupJaxbContextUsingOsgiServiceLoader();
        if (jaxbContext != null) {
            ContextFinder.logger.fine("OSGi environment detected");
            return newInstance(contextPath, jaxbContext, classLoader, properties);
        }
        ContextFinder.logger.fine("Searching META-INF/services");
        BufferedReader r = null;
        try {
            final StringBuilder resource = new StringBuilder().append("META-INF/services/").append(jaxbContextFQCN);
            final InputStream resourceStream = classLoader.getResourceAsStream(resource.toString());
            if (resourceStream != null) {
                r = new BufferedReader(new InputStreamReader(resourceStream, "UTF-8"));
                factoryClassName = r.readLine();
                if (factoryClassName != null) {
                    factoryClassName = factoryClassName.trim();
                }
                r.close();
                return newInstance(contextPath, factoryClassName, classLoader, properties);
            }
            ContextFinder.logger.log(Level.FINE, "Unable to load:{0}", resource.toString());
        }
        catch (final UnsupportedEncodingException e) {
            throw new JAXBException(e);
        }
        catch (final IOException e2) {
            throw new JAXBException(e2);
        }
        finally {
            try {
                if (r != null) {
                    r.close();
                }
            }
            catch (final IOException ex) {
                Logger.getLogger(ContextFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ContextFinder.logger.fine("Trying to create the platform default provider");
        return newInstance(contextPath, "com.sun.xml.internal.bind.v2.ContextFactory", classLoader, properties);
    }
    
    static JAXBContext find(final Class[] classes, final Map properties) throws JAXBException {
        final String jaxbContextFQCN = JAXBContext.class.getName();
        for (final Class c : classes) {
            final ClassLoader classLoader = getClassClassLoader(c);
            final Package pkg = c.getPackage();
            if (pkg != null) {
                final String packageName = pkg.getName().replace('.', '/');
                final String resourceName = packageName + "/jaxb.properties";
                ContextFinder.logger.log(Level.FINE, "Trying to locate {0}", resourceName);
                final Properties props = loadJAXBProperties(classLoader, resourceName);
                if (props == null) {
                    ContextFinder.logger.fine("  not found");
                }
                else {
                    ContextFinder.logger.fine("  found");
                    if (props.containsKey("javax.xml.bind.context.factory")) {
                        final String factoryClassName = props.getProperty("javax.xml.bind.context.factory").trim();
                        return newInstance(classes, properties, factoryClassName);
                    }
                    throw new JAXBException(Messages.format("ContextFinder.MissingProperty", packageName, "javax.xml.bind.context.factory"));
                }
            }
        }
        ContextFinder.logger.log(Level.FINE, "Checking system property {0}", "javax.xml.bind.context.factory");
        String factoryClassName = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("javax.xml.bind.context.factory"));
        if (factoryClassName != null) {
            ContextFinder.logger.log(Level.FINE, "  found {0}", factoryClassName);
            return newInstance(classes, properties, factoryClassName);
        }
        ContextFinder.logger.fine("  not found");
        ContextFinder.logger.log(Level.FINE, "Checking system property {0}", jaxbContextFQCN);
        factoryClassName = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(jaxbContextFQCN));
        if (factoryClassName != null) {
            ContextFinder.logger.log(Level.FINE, "  found {0}", factoryClassName);
            return newInstance(classes, properties, factoryClassName);
        }
        ContextFinder.logger.fine("  not found");
        final Class jaxbContext = lookupJaxbContextUsingOsgiServiceLoader();
        if (jaxbContext != null) {
            ContextFinder.logger.fine("OSGi environment detected");
            return newInstance(classes, properties, jaxbContext);
        }
        ContextFinder.logger.fine("Checking META-INF/services");
        BufferedReader r = null;
        try {
            final String resource = "META-INF/services/" + jaxbContextFQCN;
            final ClassLoader classLoader2 = getContextClassLoader();
            URL resourceURL;
            if (classLoader2 == null) {
                resourceURL = ClassLoader.getSystemResource(resource);
            }
            else {
                resourceURL = classLoader2.getResource(resource);
            }
            if (resourceURL != null) {
                ContextFinder.logger.log(Level.FINE, "Reading {0}", resourceURL);
                r = new BufferedReader(new InputStreamReader(resourceURL.openStream(), "UTF-8"));
                factoryClassName = r.readLine();
                if (factoryClassName != null) {
                    factoryClassName = factoryClassName.trim();
                }
                return newInstance(classes, properties, factoryClassName);
            }
            ContextFinder.logger.log(Level.FINE, "Unable to find: {0}", resource);
        }
        catch (final UnsupportedEncodingException e) {
            throw new JAXBException(e);
        }
        catch (final IOException e2) {
            throw new JAXBException(e2);
        }
        finally {
            if (r != null) {
                try {
                    r.close();
                }
                catch (final IOException ex) {
                    ContextFinder.logger.log(Level.FINE, "Unable to close stream", ex);
                }
            }
        }
        ContextFinder.logger.fine("Trying to create the platform default provider");
        return newInstance(classes, properties, "com.sun.xml.internal.bind.v2.ContextFactory");
    }
    
    private static Class lookupJaxbContextUsingOsgiServiceLoader() {
        try {
            final Class target = Class.forName("com.sun.org.glassfish.hk2.osgiresourcelocator.ServiceLoader");
            final Method m = target.getMethod("lookupProviderClasses", Class.class);
            final Iterator iter = ((Iterable)m.invoke(null, JAXBContext.class)).iterator();
            return iter.hasNext() ? iter.next() : null;
        }
        catch (final Exception e) {
            ContextFinder.logger.log(Level.FINE, "Unable to find from OSGi: javax.xml.bind.JAXBContext");
            return null;
        }
    }
    
    private static Properties loadJAXBProperties(final ClassLoader classLoader, final String propFileName) throws JAXBException {
        Properties props = null;
        try {
            URL url;
            if (classLoader == null) {
                url = ClassLoader.getSystemResource(propFileName);
            }
            else {
                url = classLoader.getResource(propFileName);
            }
            if (url != null) {
                ContextFinder.logger.log(Level.FINE, "loading props from {0}", url);
                props = new Properties();
                final InputStream is = url.openStream();
                props.load(is);
                is.close();
            }
        }
        catch (final IOException ioe) {
            ContextFinder.logger.log(Level.FINE, "Unable to load " + propFileName, ioe);
            throw new JAXBException(ioe.toString(), ioe);
        }
        return props;
    }
    
    static URL which(final Class clazz, ClassLoader loader) {
        final String classnameAsResource = clazz.getName().replace('.', '/') + ".class";
        if (loader == null) {
            loader = getSystemClassLoader();
        }
        return loader.getResource(classnameAsResource);
    }
    
    static URL which(final Class clazz) {
        return which(clazz, getClassClassLoader(clazz));
    }
    
    private static Class safeLoadClass(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        ContextFinder.logger.log(Level.FINE, "Trying to load {0}", className);
        try {
            final SecurityManager s = System.getSecurityManager();
            if (s != null) {
                final int i = className.lastIndexOf(46);
                if (i != -1) {
                    s.checkPackageAccess(className.substring(0, i));
                }
            }
            if (classLoader == null) {
                return Class.forName(className);
            }
            return classLoader.loadClass(className);
        }
        catch (final SecurityException se) {
            if ("com.sun.xml.internal.bind.v2.ContextFactory".equals(className)) {
                return Class.forName(className);
            }
            throw se;
        }
    }
    
    private static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return Thread.currentThread().getContextClassLoader();
            }
        });
    }
    
    private static ClassLoader getClassClassLoader(final Class c) {
        if (System.getSecurityManager() == null) {
            return c.getClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return c.getClassLoader();
            }
        });
    }
    
    private static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        }
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return ClassLoader.getSystemClassLoader();
            }
        });
    }
    
    static {
        logger = Logger.getLogger("javax.xml.bind");
        try {
            if (AccessController.doPrivileged((PrivilegedAction<Object>)new GetPropertyAction("jaxb.debug")) != null) {
                ContextFinder.logger.setUseParentHandlers(false);
                ContextFinder.logger.setLevel(Level.ALL);
                final ConsoleHandler handler = new ConsoleHandler();
                handler.setLevel(Level.ALL);
                ContextFinder.logger.addHandler(handler);
            }
        }
        catch (final Throwable t) {}
    }
}
