package javax.management.remote;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.management.MBeanServer;
import java.util.Map;
import com.sun.jmx.remote.util.ClassLogger;

public class JMXConnectorServerFactory
{
    public static final String DEFAULT_CLASS_LOADER = "jmx.remote.default.class.loader";
    public static final String DEFAULT_CLASS_LOADER_NAME = "jmx.remote.default.class.loader.name";
    public static final String PROTOCOL_PROVIDER_PACKAGES = "jmx.remote.protocol.provider.pkgs";
    public static final String PROTOCOL_PROVIDER_CLASS_LOADER = "jmx.remote.protocol.provider.class.loader";
    private static final String PROTOCOL_PROVIDER_DEFAULT_PACKAGE = "com.sun.jmx.remote.protocol";
    private static final ClassLogger logger;
    
    private JMXConnectorServerFactory() {
    }
    
    private static JMXConnectorServer getConnectorServerAsService(final ClassLoader classLoader, final JMXServiceURL jmxServiceURL, final Map<String, ?> map, final MBeanServer mBeanServer) throws IOException {
        final Iterator<JMXConnectorServerProvider> providerIterator = JMXConnectorFactory.getProviderIterator(JMXConnectorServerProvider.class, classLoader);
        IOException ex = null;
        while (providerIterator.hasNext()) {
            try {
                return providerIterator.next().newJMXConnectorServer(jmxServiceURL, map, mBeanServer);
            }
            catch (final JMXProviderException ex2) {
                throw ex2;
            }
            catch (final Exception ex3) {
                if (JMXConnectorServerFactory.logger.traceOn()) {
                    JMXConnectorServerFactory.logger.trace("getConnectorAsService", "URL[" + jmxServiceURL + "] Service provider exception: " + ex3);
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
    
    public static JMXConnectorServer newJMXConnectorServer(final JMXServiceURL jmxServiceURL, final Map<String, ?> map, final MBeanServer mBeanServer) throws IOException {
        HashMap hashMap;
        if (map == null) {
            hashMap = new HashMap();
        }
        else {
            EnvHelp.checkAttributes(map);
            hashMap = new HashMap((Map<? extends K, ? extends V>)map);
        }
        final Class<JMXConnectorServerProvider> clazz = JMXConnectorServerProvider.class;
        final ClassLoader resolveClassLoader = JMXConnectorFactory.resolveClassLoader(hashMap);
        final String protocol = jmxServiceURL.getProtocol();
        JMXConnectorServerProvider jmxConnectorServerProvider = JMXConnectorFactory.getProvider(jmxServiceURL, hashMap, "ServerProvider", clazz, resolveClassLoader);
        Throwable t = null;
        if (jmxConnectorServerProvider == null) {
            if (resolveClassLoader != null) {
                try {
                    final JMXConnectorServer connectorServerAsService = getConnectorServerAsService(resolveClassLoader, jmxServiceURL, hashMap, mBeanServer);
                    if (connectorServerAsService != null) {
                        return connectorServerAsService;
                    }
                }
                catch (final JMXProviderException ex) {
                    throw ex;
                }
                catch (final IOException ex2) {
                    t = ex2;
                }
            }
            jmxConnectorServerProvider = JMXConnectorFactory.getProvider(protocol, "com.sun.jmx.remote.protocol", JMXConnectorFactory.class.getClassLoader(), "ServerProvider", clazz);
        }
        if (jmxConnectorServerProvider != null) {
            return jmxConnectorServerProvider.newJMXConnectorServer(jmxServiceURL, Collections.unmodifiableMap((Map<? extends String, ?>)hashMap), mBeanServer);
        }
        final MalformedURLException ex3 = new MalformedURLException("Unsupported protocol: " + protocol);
        if (t == null) {
            throw ex3;
        }
        throw EnvHelp.initCause(ex3, t);
    }
    
    static {
        logger = new ClassLogger("javax.management.remote.misc", "JMXConnectorServerFactory");
    }
}
