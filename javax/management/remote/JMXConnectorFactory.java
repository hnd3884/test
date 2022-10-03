package javax.management.remote;

import com.sun.jmx.mbeanserver.Util;
import java.util.StringTokenizer;
import sun.reflect.misc.ReflectUtil;
import java.util.ServiceLoader;
import java.util.Iterator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.net.MalformedURLException;
import java.util.Collections;
import com.sun.jmx.remote.util.EnvHelp;
import java.util.HashMap;
import java.io.IOException;
import java.util.Map;
import com.sun.jmx.remote.util.ClassLogger;

public class JMXConnectorFactory
{
    public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
    public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
    public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
    private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
    private static final ClassLogger logger;
    
    private JMXConnectorFactory() {
    }
    
    public static JMXConnector connect(final JMXServiceURL jmxServiceURL) throws IOException {
        return connect(jmxServiceURL, null);
    }
    
    public static JMXConnector connect(final JMXServiceURL jmxServiceURL, final Map<String, ?> map) throws IOException {
        if (jmxServiceURL == null) {
            throw new NullPointerException("Null JMXServiceURL");
        }
        final JMXConnector jmxConnector = newJMXConnector(jmxServiceURL, map);
        jmxConnector.connect(map);
        return jmxConnector;
    }
    
    private static <K, V> Map<K, V> newHashMap() {
        return new HashMap<K, V>();
    }
    
    private static <K> Map<K, Object> newHashMap(final Map<K, ?> map) {
        return new HashMap<K, Object>((Map<? extends K, ?>)map);
    }
    
    public static JMXConnector newJMXConnector(final JMXServiceURL jmxServiceURL, final Map<String, ?> map) throws IOException {
        Object o;
        if (map == null) {
            o = newHashMap();
        }
        else {
            EnvHelp.checkAttributes(map);
            o = newHashMap(map);
        }
        final ClassLoader resolveClassLoader = resolveClassLoader((Map<String, ?>)o);
        final Class<JMXConnectorProvider> clazz = JMXConnectorProvider.class;
        final String protocol = jmxServiceURL.getProtocol();
        JMXConnectorProvider jmxConnectorProvider = getProvider(jmxServiceURL, (Map<String, Object>)o, "ClientProvider", clazz, resolveClassLoader);
        Throwable t = null;
        if (jmxConnectorProvider == null) {
            if (resolveClassLoader != null) {
                try {
                    final JMXConnector connectorAsService = getConnectorAsService(resolveClassLoader, jmxServiceURL, (Map<String, ?>)o);
                    if (connectorAsService != null) {
                        return connectorAsService;
                    }
                }
                catch (final JMXProviderException ex) {
                    throw ex;
                }
                catch (final IOException ex2) {
                    t = ex2;
                }
            }
            jmxConnectorProvider = getProvider(protocol, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ClientProvider", clazz);
        }
        if (jmxConnectorProvider != null) {
            return jmxConnectorProvider.newJMXConnector(jmxServiceURL, Collections.unmodifiableMap((Map<? extends String, ?>)o));
        }
        final MalformedURLException ex3 = new MalformedURLException("Unsupported protocol: " + protocol);
        if (t == null) {
            throw ex3;
        }
        throw EnvHelp.initCause(ex3, t);
    }
    
    private static String resolvePkgs(final Map<String, ?> map) throws JMXProviderException {
        Object o = null;
        if (map != null) {
            o = map.get("jmx.remote.protocol.provider.pkgs");
        }
        if (o == null) {
            o = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty("jmx.remote.protocol.provider.pkgs");
                }
            });
        }
        if (o == null) {
            return null;
        }
        if (!(o instanceof String)) {
            throw new JMXProviderException("Value of jmx.remote.protocol.provider.pkgs parameter is not a String: " + ((String)o).getClass().getName());
        }
        final String s = (String)o;
        if (s.trim().equals("")) {
            return null;
        }
        if (s.startsWith("|") || s.endsWith("|") || s.indexOf("||") >= 0) {
            throw new JMXProviderException("Value of jmx.remote.protocol.provider.pkgs contains an empty element: " + s);
        }
        return s;
    }
    
    static <T> T getProvider(final JMXServiceURL jmxServiceURL, final Map<String, Object> map, final String s, final Class<T> clazz, final ClassLoader classLoader) throws IOException {
        final String protocol = jmxServiceURL.getProtocol();
        final String resolvePkgs = resolvePkgs(map);
        Object provider = null;
        if (resolvePkgs != null) {
            provider = getProvider(protocol, resolvePkgs, classLoader, s, clazz);
            if (provider != null) {
                map.put("jmx.remote.protocol.provider.class.loader", (classLoader != provider.getClass().getClassLoader()) ? wrap(classLoader) : classLoader);
            }
        }
        return (T)provider;
    }
    
    static <T> Iterator<T> getProviderIterator(final Class<T> clazz, final ClassLoader classLoader) {
        return ServiceLoader.load(clazz, classLoader).iterator();
    }
    
    private static ClassLoader wrap(final ClassLoader classLoader) {
        return (classLoader != null) ? AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new ClassLoader(classLoader) {
                    @Override
                    protected Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
                        ReflectUtil.checkPackageAccess(s);
                        return super.loadClass(s, b);
                    }
                };
            }
        }) : null;
    }
    
    private static JMXConnector getConnectorAsService(final ClassLoader classLoader, final JMXServiceURL jmxServiceURL, final Map<String, ?> map) throws IOException {
        final Iterator<JMXConnectorProvider> providerIterator = getProviderIterator(JMXConnectorProvider.class, classLoader);
        IOException ex = null;
        while (providerIterator.hasNext()) {
            final JMXConnectorProvider jmxConnectorProvider = providerIterator.next();
            try {
                return jmxConnectorProvider.newJMXConnector(jmxServiceURL, map);
            }
            catch (final JMXProviderException ex2) {
                throw ex2;
            }
            catch (final Exception ex3) {
                if (JMXConnectorFactory.logger.traceOn()) {
                    JMXConnectorFactory.logger.trace("getConnectorAsService", "URL[" + jmxServiceURL + "] Service provider exception: " + ex3);
                }
                if (ex3 instanceof MalformedURLException || ex != null) {
                    continue;
                }
                if (ex3 instanceof IOException) {
                    ex = (IOException)ex3;
                }
                else {
                    ex = EnvHelp.initCause(new IOException(ex3.getMessage()), ex3);
                }
                continue;
            }
            break;
        }
        if (ex == null) {
            return null;
        }
        throw ex;
    }
    
    static <T> T getProvider(final String s, final String s2, final ClassLoader classLoader, final String s3, final Class<T> clazz) throws IOException {
        final StringTokenizer stringTokenizer = new StringTokenizer(s2, "|");
        while (stringTokenizer.hasMoreTokens()) {
            final String string = stringTokenizer.nextToken() + "." + protocol2package(s) + "." + s3;
            Class<?> forName;
            try {
                forName = Class.forName(string, true, classLoader);
            }
            catch (final ClassNotFoundException ex) {
                continue;
            }
            if (!clazz.isAssignableFrom(forName)) {
                throw new JMXProviderException("Provider class does not implement " + clazz.getName() + ": " + forName.getName());
            }
            final Class clazz2 = Util.cast(forName);
            try {
                return (T)clazz2.newInstance();
            }
            catch (final Exception ex2) {
                throw new JMXProviderException("Exception when instantiating provider [" + string + "]", ex2);
            }
            break;
        }
        return null;
    }
    
    static ClassLoader resolveClassLoader(final Map<String, ?> map) {
        ClassLoader contextClassLoader = null;
        if (map != null) {
            try {
                contextClassLoader = (ClassLoader)map.get("jmx.remote.protocol.provider.class.loader");
            }
            catch (final ClassCastException ex) {
                throw new IllegalArgumentException("The ClassLoader supplied in the environment map using the jmx.remote.protocol.provider.class.loader attribute is not an instance of java.lang.ClassLoader");
            }
        }
        if (contextClassLoader == null) {
            contextClassLoader = Thread.currentThread().getContextClassLoader();
        }
        return contextClassLoader;
    }
    
    private static String protocol2package(final String s) {
        return s.replace('+', '.').replace('-', '_');
    }
    
    static {
        logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorFactory");
    }
}
