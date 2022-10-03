package sun.management.jmxremote;

import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.security.Principal;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import javax.security.auth.Subject;
import javax.management.remote.JMXAuthenticator;
import java.rmi.RemoteException;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.server.UnicastServerRef;
import com.sun.jmx.remote.internal.RMIExporter;
import java.net.MalformedURLException;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RemoteObject;
import sun.rmi.server.UnicastRef;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import java.io.IOException;
import sun.management.FileSystem;
import java.io.File;
import javax.management.remote.JMXConnectorServerFactory;
import java.net.UnknownHostException;
import java.net.InetAddress;
import javax.management.remote.JMXServiceURL;
import javax.management.MBeanServer;
import java.util.Map;
import sun.management.ConnectorAddressLink;
import java.util.HashMap;
import java.lang.management.ManagementFactory;
import java.util.StringTokenizer;
import sun.management.AgentConfigurationError;
import java.util.Properties;
import sun.management.Agent;
import javax.management.remote.JMXConnectorServer;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import com.sun.jmx.remote.util.ClassLogger;
import java.rmi.registry.Registry;

public final class ConnectorBootstrap
{
    private static Registry registry;
    private static final ClassLogger log;
    
    public static void unexportRegistry() {
        try {
            if (ConnectorBootstrap.registry != null) {
                UnicastRemoteObject.unexportObject(ConnectorBootstrap.registry, true);
                ConnectorBootstrap.registry = null;
            }
        }
        catch (final NoSuchObjectException ex) {}
    }
    
    public static synchronized JMXConnectorServer initialize() {
        final Properties loadManagementProperties = Agent.loadManagementProperties();
        if (loadManagementProperties == null) {
            return null;
        }
        return startRemoteConnectorServer(loadManagementProperties.getProperty("com.sun.management.jmxremote.port"), loadManagementProperties);
    }
    
    public static synchronized JMXConnectorServer initialize(final String s, final Properties properties) {
        return startRemoteConnectorServer(s, properties);
    }
    
    public static synchronized JMXConnectorServer startRemoteConnectorServer(final String s, final Properties properties) {
        int int1;
        try {
            int1 = Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", ex, new String[] { s });
        }
        if (int1 < 0) {
            throw new AgentConfigurationError("agent.err.invalid.jmxremote.port", new String[] { s });
        }
        int int2 = 0;
        final String property = properties.getProperty("com.sun.management.jmxremote.rmi.port");
        try {
            if (property != null) {
                int2 = Integer.parseInt(property);
            }
        }
        catch (final NumberFormatException ex2) {
            throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", ex2, new String[] { property });
        }
        if (int2 < 0) {
            throw new AgentConfigurationError("agent.err.invalid.jmxremote.rmi.port", new String[] { property });
        }
        final String property2 = properties.getProperty("com.sun.management.jmxremote.authenticate", "true");
        final boolean booleanValue = Boolean.valueOf(property2);
        final String property3 = properties.getProperty("com.sun.management.jmxremote.ssl", "true");
        final boolean booleanValue2 = Boolean.valueOf(property3);
        final String property4 = properties.getProperty("com.sun.management.jmxremote.registry.ssl", "false");
        final boolean booleanValue3 = Boolean.valueOf(property4);
        final String property5 = properties.getProperty("com.sun.management.jmxremote.ssl.enabled.cipher.suites");
        String[] array = null;
        if (property5 != null) {
            final StringTokenizer stringTokenizer = new StringTokenizer(property5, ",");
            final int countTokens = stringTokenizer.countTokens();
            array = new String[countTokens];
            for (int i = 0; i < countTokens; ++i) {
                array[i] = stringTokenizer.nextToken();
            }
        }
        final String property6 = properties.getProperty("com.sun.management.jmxremote.ssl.enabled.protocols");
        String[] array2 = null;
        if (property6 != null) {
            final StringTokenizer stringTokenizer2 = new StringTokenizer(property6, ",");
            final int countTokens2 = stringTokenizer2.countTokens();
            array2 = new String[countTokens2];
            for (int j = 0; j < countTokens2; ++j) {
                array2[j] = stringTokenizer2.nextToken();
            }
        }
        final String property7 = properties.getProperty("com.sun.management.jmxremote.ssl.need.client.auth", "false");
        final boolean booleanValue4 = Boolean.valueOf(property7);
        final String property8 = properties.getProperty("com.sun.management.jmxremote.ssl.config.file");
        String property9 = null;
        String property10 = null;
        String property11 = null;
        if (booleanValue) {
            property9 = properties.getProperty("com.sun.management.jmxremote.login.config");
            if (property9 == null) {
                property10 = properties.getProperty("com.sun.management.jmxremote.password.file", getDefaultFileName("jmxremote.password"));
                checkPasswordFile(property10);
            }
            property11 = properties.getProperty("com.sun.management.jmxremote.access.file", getDefaultFileName("jmxremote.access"));
            checkAccessFile(property11);
        }
        final String property12 = properties.getProperty("com.sun.management.jmxremote.host");
        if (ConnectorBootstrap.log.debugOn()) {
            ConnectorBootstrap.log.debug("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.starting") + "\n\t" + "com.sun.management.jmxremote.port" + "=" + int1 + ((property12 == null) ? "" : ("\n\tcom.sun.management.jmxremote.host=" + property12)) + "\n\t" + "com.sun.management.jmxremote.rmi.port" + "=" + int2 + "\n\t" + "com.sun.management.jmxremote.ssl" + "=" + booleanValue2 + "\n\t" + "com.sun.management.jmxremote.registry.ssl" + "=" + booleanValue3 + "\n\t" + "com.sun.management.jmxremote.ssl.config.file" + "=" + property8 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.cipher.suites" + "=" + property5 + "\n\t" + "com.sun.management.jmxremote.ssl.enabled.protocols" + "=" + property6 + "\n\t" + "com.sun.management.jmxremote.ssl.need.client.auth" + "=" + booleanValue4 + "\n\t" + "com.sun.management.jmxremote.authenticate" + "=" + booleanValue + (booleanValue ? ((property9 == null) ? ("\n\tcom.sun.management.jmxremote.password.file=" + property10) : ("\n\tcom.sun.management.jmxremote.login.config=" + property9)) : ("\n\t" + Agent.getText("jmxremote.ConnectorBootstrap.noAuthentication"))) + (booleanValue ? ("\n\tcom.sun.management.jmxremote.access.file=" + property11) : "") + "");
        }
        final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        JMXConnectorServer jmxConnectorServer;
        JMXServiceURL jmxRemoteURL;
        try {
            final JMXConnectorServerData exportMBeanServer = exportMBeanServer(platformMBeanServer, int1, int2, booleanValue2, booleanValue3, property8, array, array2, booleanValue4, booleanValue, property9, property10, property11, property12);
            jmxConnectorServer = exportMBeanServer.jmxConnectorServer;
            jmxRemoteURL = exportMBeanServer.jmxRemoteURL;
            ConnectorBootstrap.log.config("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.ready", jmxRemoteURL.toString()));
        }
        catch (final Exception ex3) {
            throw new AgentConfigurationError("agent.err.exception", ex3, new String[] { ex3.toString() });
        }
        try {
            final HashMap hashMap = new HashMap();
            hashMap.put("remoteAddress", jmxRemoteURL.toString());
            hashMap.put("authenticate", property2);
            hashMap.put("ssl", property3);
            hashMap.put("sslRegistry", property4);
            hashMap.put("sslNeedClientAuth", property7);
            ConnectorAddressLink.exportRemote(hashMap);
        }
        catch (final Exception ex4) {
            ConnectorBootstrap.log.debug("startRemoteConnectorServer", ex4);
        }
        return jmxConnectorServer;
    }
    
    public static JMXConnectorServer startLocalConnectorServer() {
        System.setProperty("java.rmi.server.randomIDs", "true");
        final HashMap hashMap = new HashMap();
        hashMap.put("com.sun.jmx.remote.rmi.exporter", new PermanentExporter());
        hashMap.put("jmx.remote.rmi.server.credential.types", new String[] { String[].class.getName(), String.class.getName() });
        String hostAddress = "localhost";
        InetAddress byName = null;
        try {
            byName = InetAddress.getByName(hostAddress);
            hostAddress = byName.getHostAddress();
        }
        catch (final UnknownHostException ex) {}
        if (byName == null || !byName.isLoopbackAddress()) {
            hostAddress = "127.0.0.1";
        }
        final MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            final JMXServiceURL jmxServiceURL = new JMXServiceURL("rmi", hostAddress, 0);
            Properties managementProperties = Agent.getManagementProperties();
            if (managementProperties == null) {
                managementProperties = new Properties();
            }
            if (Boolean.valueOf(managementProperties.getProperty("com.sun.management.jmxremote.local.only", "true"))) {
                hashMap.put("jmx.remote.rmi.server.socket.factory", new LocalRMIServerSocketFactory());
            }
            final JMXConnectorServer jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, hashMap, platformMBeanServer);
            jmxConnectorServer.start();
            return jmxConnectorServer;
        }
        catch (final Exception ex2) {
            throw new AgentConfigurationError("agent.err.exception", ex2, new String[] { ex2.toString() });
        }
    }
    
    private static void checkPasswordFile(final String s) {
        if (s == null || s.length() == 0) {
            throw new AgentConfigurationError("agent.err.password.file.notset");
        }
        final File file = new File(s);
        if (!file.exists()) {
            throw new AgentConfigurationError("agent.err.password.file.notfound", new String[] { s });
        }
        if (!file.canRead()) {
            throw new AgentConfigurationError("agent.err.password.file.not.readable", new String[] { s });
        }
        final FileSystem open = FileSystem.open();
        try {
            if (open.supportsFileSecurity(file) && !open.isAccessUserOnly(file)) {
                ConnectorBootstrap.log.config("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.password.readonly", s));
                throw new AgentConfigurationError("agent.err.password.file.access.notrestricted", new String[] { s });
            }
        }
        catch (final IOException ex) {
            throw new AgentConfigurationError("agent.err.password.file.read.failed", ex, new String[] { s });
        }
    }
    
    private static void checkAccessFile(final String s) {
        if (s == null || s.length() == 0) {
            throw new AgentConfigurationError("agent.err.access.file.notset");
        }
        final File file = new File(s);
        if (!file.exists()) {
            throw new AgentConfigurationError("agent.err.access.file.notfound", new String[] { s });
        }
        if (!file.canRead()) {
            throw new AgentConfigurationError("agent.err.access.file.not.readable", new String[] { s });
        }
    }
    
    private static void checkRestrictedFile(final String s) {
        if (s == null || s.length() == 0) {
            throw new AgentConfigurationError("agent.err.file.not.set");
        }
        final File file = new File(s);
        if (!file.exists()) {
            throw new AgentConfigurationError("agent.err.file.not.found", new String[] { s });
        }
        if (!file.canRead()) {
            throw new AgentConfigurationError("agent.err.file.not.readable", new String[] { s });
        }
        final FileSystem open = FileSystem.open();
        try {
            if (open.supportsFileSecurity(file) && !open.isAccessUserOnly(file)) {
                ConnectorBootstrap.log.config("startRemoteConnectorServer", Agent.getText("jmxremote.ConnectorBootstrap.file.readonly", s));
                throw new AgentConfigurationError("agent.err.file.access.not.restricted", new String[] { s });
            }
        }
        catch (final IOException ex) {
            throw new AgentConfigurationError("agent.err.file.read.failed", ex, new String[] { s });
        }
    }
    
    private static String getDefaultFileName(final String s) {
        final String separator = File.separator;
        return System.getProperty("java.home") + separator + "lib" + separator + "management" + separator + s;
    }
    
    private static SslRMIServerSocketFactory createSslRMIServerSocketFactory(final String s, final String[] array, final String[] array2, final boolean b, final String s2) {
        if (s == null) {
            return new HostAwareSslSocketFactory(array, array2, b, s2);
        }
        checkRestrictedFile(s);
        try {
            final Properties properties = new Properties();
            try (final FileInputStream fileInputStream = new FileInputStream(s)) {
                properties.load(new BufferedInputStream(fileInputStream));
            }
            final String property = properties.getProperty("javax.net.ssl.keyStore");
            final String property2 = properties.getProperty("javax.net.ssl.keyStorePassword", "");
            final String property3 = properties.getProperty("javax.net.ssl.trustStore");
            final String property4 = properties.getProperty("javax.net.ssl.trustStorePassword", "");
            char[] charArray = null;
            if (property2.length() != 0) {
                charArray = property2.toCharArray();
            }
            char[] charArray2 = null;
            if (property4.length() != 0) {
                charArray2 = property4.toCharArray();
            }
            KeyStore instance = null;
            if (property != null) {
                instance = KeyStore.getInstance(KeyStore.getDefaultType());
                try (final FileInputStream fileInputStream2 = new FileInputStream(property)) {
                    instance.load(fileInputStream2, charArray);
                }
            }
            final KeyManagerFactory instance2 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            instance2.init(instance, charArray);
            KeyStore instance3 = null;
            if (property3 != null) {
                instance3 = KeyStore.getInstance(KeyStore.getDefaultType());
                try (final FileInputStream fileInputStream3 = new FileInputStream(property3)) {
                    instance3.load(fileInputStream3, charArray2);
                }
            }
            final TrustManagerFactory instance4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            instance4.init(instance3);
            final SSLContext instance5 = SSLContext.getInstance("SSL");
            instance5.init(instance2.getKeyManagers(), instance4.getTrustManagers(), null);
            return new HostAwareSslSocketFactory(instance5, array, array2, b, s2);
        }
        catch (final Exception ex) {
            throw new AgentConfigurationError("agent.err.exception", ex, new String[] { ex.toString() });
        }
    }
    
    private static JMXConnectorServerData exportMBeanServer(final MBeanServer mBeanServer, final int n, final int n2, final boolean b, final boolean b2, final String s, final String[] array, final String[] array2, final boolean b3, final boolean b4, final String s2, final String s3, final String s4, final String s5) throws IOException, MalformedURLException {
        System.setProperty("java.rmi.server.randomIDs", "true");
        final JMXServiceURL jmxServiceURL = new JMXServiceURL("rmi", s5, n2);
        final HashMap hashMap = new HashMap();
        final PermanentExporter permanentExporter = new PermanentExporter();
        hashMap.put("com.sun.jmx.remote.rmi.exporter", permanentExporter);
        hashMap.put("jmx.remote.rmi.server.credential.types", new String[] { String[].class.getName(), String.class.getName() });
        final boolean b5 = s5 != null && !b;
        if (b4) {
            if (s2 != null) {
                hashMap.put("jmx.remote.x.login.config", s2);
            }
            if (s3 != null) {
                hashMap.put("jmx.remote.x.password.file", s3);
            }
            hashMap.put("jmx.remote.x.access.file", s4);
            if (hashMap.get("jmx.remote.x.password.file") != null || hashMap.get("jmx.remote.x.login.config") != null) {
                hashMap.put("jmx.remote.authenticator", new AccessFileCheckerAuthenticator(hashMap));
            }
        }
        RMIClientSocketFactory rmiClientSocketFactory = null;
        RMIServerSocketFactory sslRMIServerSocketFactory = null;
        if (b || b2) {
            rmiClientSocketFactory = new SslRMIClientSocketFactory();
            sslRMIServerSocketFactory = createSslRMIServerSocketFactory(s, array, array2, b3, s5);
        }
        if (b) {
            hashMap.put("jmx.remote.rmi.client.socket.factory", rmiClientSocketFactory);
            hashMap.put("jmx.remote.rmi.server.socket.factory", sslRMIServerSocketFactory);
        }
        if (b5) {
            sslRMIServerSocketFactory = new HostAwareSocketFactory(s5);
            hashMap.put("jmx.remote.rmi.server.socket.factory", sslRMIServerSocketFactory);
        }
        JMXConnectorServer jmxConnectorServer = null;
        try {
            jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxServiceURL, hashMap, mBeanServer);
            jmxConnectorServer.start();
        }
        catch (final IOException ex) {
            if (jmxConnectorServer == null || jmxConnectorServer.getAddress() == null) {
                throw new AgentConfigurationError("agent.err.connector.server.io.error", ex, new String[] { jmxServiceURL.toString() });
            }
            throw new AgentConfigurationError("agent.err.connector.server.io.error", ex, new String[] { jmxConnectorServer.getAddress().toString() });
        }
        if (b2) {
            ConnectorBootstrap.registry = new SingleEntryRegistry(n, rmiClientSocketFactory, sslRMIServerSocketFactory, "jmxrmi", permanentExporter.firstExported);
        }
        else if (b5) {
            ConnectorBootstrap.registry = new SingleEntryRegistry(n, rmiClientSocketFactory, sslRMIServerSocketFactory, "jmxrmi", permanentExporter.firstExported);
        }
        else {
            ConnectorBootstrap.registry = new SingleEntryRegistry(n, "jmxrmi", permanentExporter.firstExported);
        }
        return new JMXConnectorServerData(jmxConnectorServer, new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", jmxServiceURL.getHost(), ((UnicastRef)((RemoteObject)ConnectorBootstrap.registry).getRef()).getLiveRef().getPort())));
    }
    
    private ConnectorBootstrap() {
    }
    
    static {
        ConnectorBootstrap.registry = null;
        log = new ClassLogger(ConnectorBootstrap.class.getPackage().getName(), "ConnectorBootstrap");
    }
    
    private static class JMXConnectorServerData
    {
        JMXConnectorServer jmxConnectorServer;
        JMXServiceURL jmxRemoteURL;
        
        public JMXConnectorServerData(final JMXConnectorServer jmxConnectorServer, final JMXServiceURL jmxRemoteURL) {
            this.jmxConnectorServer = jmxConnectorServer;
            this.jmxRemoteURL = jmxRemoteURL;
        }
    }
    
    private static class PermanentExporter implements RMIExporter
    {
        Remote firstExported;
        
        @Override
        public Remote exportObject(final Remote firstExported, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
            synchronized (this) {
                if (this.firstExported == null) {
                    this.firstExported = firstExported;
                }
            }
            UnicastServerRef unicastServerRef;
            if (rmiClientSocketFactory == null && rmiServerSocketFactory == null) {
                unicastServerRef = new UnicastServerRef(n);
            }
            else {
                unicastServerRef = new UnicastServerRef2(n, rmiClientSocketFactory, rmiServerSocketFactory);
            }
            return unicastServerRef.exportObject(firstExported, null, true);
        }
        
        @Override
        public boolean unexportObject(final Remote remote, final boolean b) throws NoSuchObjectException {
            return UnicastRemoteObject.unexportObject(remote, b);
        }
    }
    
    private static class AccessFileCheckerAuthenticator implements JMXAuthenticator
    {
        private final Map<String, Object> environment;
        private final Properties properties;
        private final String accessFile;
        
        public AccessFileCheckerAuthenticator(final Map<String, Object> environment) throws IOException {
            this.environment = environment;
            this.accessFile = environment.get("jmx.remote.x.access.file");
            this.properties = propertiesFromFile(this.accessFile);
        }
        
        @Override
        public Subject authenticate(final Object o) {
            final Subject authenticate = new JMXPluggableAuthenticator(this.environment).authenticate(o);
            this.checkAccessFileEntries(authenticate);
            return authenticate;
        }
        
        private void checkAccessFileEntries(final Subject subject) {
            if (subject == null) {
                throw new SecurityException("Access denied! No matching entries found in the access file [" + this.accessFile + "] as the authenticated Subject is null");
            }
            final Set<Principal> principals = subject.getPrincipals();
            final Iterator<Principal> iterator = principals.iterator();
            while (iterator.hasNext()) {
                if (this.properties.containsKey(iterator.next().getName())) {
                    return;
                }
            }
            final HashSet set = new HashSet();
            final Iterator<Principal> iterator2 = principals.iterator();
            while (iterator2.hasNext()) {
                set.add(iterator2.next().getName());
            }
            throw new SecurityException("Access denied! No entries found in the access file [" + this.accessFile + "] for any of the authenticated identities " + set);
        }
        
        private static Properties propertiesFromFile(final String s) throws IOException {
            final Properties properties = new Properties();
            if (s == null) {
                return properties;
            }
            try (final FileInputStream fileInputStream = new FileInputStream(s)) {
                properties.load(fileInputStream);
            }
            return properties;
        }
    }
    
    private static class HostAwareSocketFactory implements RMIServerSocketFactory
    {
        private final String bindAddress;
        
        private HostAwareSocketFactory(final String bindAddress) {
            this.bindAddress = bindAddress;
        }
        
        @Override
        public ServerSocket createServerSocket(final int n) throws IOException {
            if (this.bindAddress == null) {
                return new ServerSocket(n);
            }
            try {
                return new ServerSocket(n, 0, InetAddress.getByName(this.bindAddress));
            }
            catch (final UnknownHostException ex) {
                return new ServerSocket(n);
            }
        }
    }
    
    private static class HostAwareSslSocketFactory extends SslRMIServerSocketFactory
    {
        private final String bindAddress;
        private final String[] enabledCipherSuites;
        private final String[] enabledProtocols;
        private final boolean needClientAuth;
        private final SSLContext context;
        
        private HostAwareSslSocketFactory(final String[] array, final String[] array2, final boolean b, final String s) throws IllegalArgumentException {
            this(null, array, array2, b, s);
        }
        
        private HostAwareSslSocketFactory(final SSLContext context, final String[] enabledCipherSuites, final String[] enabledProtocols, final boolean needClientAuth, final String bindAddress) throws IllegalArgumentException {
            this.context = context;
            this.bindAddress = bindAddress;
            this.enabledProtocols = enabledProtocols;
            this.enabledCipherSuites = enabledCipherSuites;
            this.needClientAuth = needClientAuth;
            checkValues(context, enabledCipherSuites, enabledProtocols);
        }
        
        @Override
        public ServerSocket createServerSocket(final int n) throws IOException {
            if (this.bindAddress != null) {
                try {
                    return new SslServerSocket(n, 0, InetAddress.getByName(this.bindAddress), this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth);
                }
                catch (final UnknownHostException ex) {
                    return new SslServerSocket(n, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth);
                }
            }
            return new SslServerSocket(n, this.context, this.enabledCipherSuites, this.enabledProtocols, this.needClientAuth);
        }
        
        private static void checkValues(final SSLContext sslContext, final String[] enabledCipherSuites, final String[] enabledProtocols) throws IllegalArgumentException {
            final SSLSocketFactory sslSocketFactory = (SSLSocketFactory)((sslContext == null) ? SSLSocketFactory.getDefault() : sslContext.getSocketFactory());
            SSLSocket sslSocket = null;
            Label_0061: {
                if (enabledCipherSuites == null) {
                    if (enabledProtocols == null) {
                        break Label_0061;
                    }
                }
                try {
                    sslSocket = (SSLSocket)sslSocketFactory.createSocket();
                }
                catch (final Exception ex) {
                    throw (IllegalArgumentException)new IllegalArgumentException("Unable to check if the cipher suites and protocols to enable are supported").initCause(ex);
                }
            }
            if (enabledCipherSuites != null) {
                sslSocket.setEnabledCipherSuites(enabledCipherSuites);
            }
            if (enabledProtocols != null) {
                sslSocket.setEnabledProtocols(enabledProtocols);
            }
        }
    }
    
    private static class SslServerSocket extends ServerSocket
    {
        private static SSLSocketFactory defaultSSLSocketFactory;
        private final String[] enabledCipherSuites;
        private final String[] enabledProtocols;
        private final boolean needClientAuth;
        private final SSLContext context;
        
        private SslServerSocket(final int n, final SSLContext context, final String[] enabledCipherSuites, final String[] enabledProtocols, final boolean needClientAuth) throws IOException {
            super(n);
            this.enabledProtocols = enabledProtocols;
            this.enabledCipherSuites = enabledCipherSuites;
            this.needClientAuth = needClientAuth;
            this.context = context;
        }
        
        private SslServerSocket(final int n, final int n2, final InetAddress inetAddress, final SSLContext context, final String[] enabledCipherSuites, final String[] enabledProtocols, final boolean needClientAuth) throws IOException {
            super(n, n2, inetAddress);
            this.enabledProtocols = enabledProtocols;
            this.enabledCipherSuites = enabledCipherSuites;
            this.needClientAuth = needClientAuth;
            this.context = context;
        }
        
        @Override
        public Socket accept() throws IOException {
            final SSLSocketFactory sslSocketFactory = (this.context == null) ? getDefaultSSLSocketFactory() : this.context.getSocketFactory();
            final Socket accept = super.accept();
            final SSLSocket sslSocket = (SSLSocket)sslSocketFactory.createSocket(accept, accept.getInetAddress().getHostName(), accept.getPort(), true);
            sslSocket.setUseClientMode(false);
            if (this.enabledCipherSuites != null) {
                sslSocket.setEnabledCipherSuites(this.enabledCipherSuites);
            }
            if (this.enabledProtocols != null) {
                sslSocket.setEnabledProtocols(this.enabledProtocols);
            }
            sslSocket.setNeedClientAuth(this.needClientAuth);
            return sslSocket;
        }
        
        private static synchronized SSLSocketFactory getDefaultSSLSocketFactory() {
            if (SslServerSocket.defaultSSLSocketFactory == null) {
                return SslServerSocket.defaultSSLSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            }
            return SslServerSocket.defaultSSLSocketFactory;
        }
    }
    
    public interface PropertyNames
    {
        public static final String PORT = "com.sun.management.jmxremote.port";
        public static final String HOST = "com.sun.management.jmxremote.host";
        public static final String RMI_PORT = "com.sun.management.jmxremote.rmi.port";
        public static final String CONFIG_FILE_NAME = "com.sun.management.config.file";
        public static final String USE_LOCAL_ONLY = "com.sun.management.jmxremote.local.only";
        public static final String USE_SSL = "com.sun.management.jmxremote.ssl";
        public static final String USE_REGISTRY_SSL = "com.sun.management.jmxremote.registry.ssl";
        public static final String USE_AUTHENTICATION = "com.sun.management.jmxremote.authenticate";
        public static final String PASSWORD_FILE_NAME = "com.sun.management.jmxremote.password.file";
        public static final String ACCESS_FILE_NAME = "com.sun.management.jmxremote.access.file";
        public static final String LOGIN_CONFIG_NAME = "com.sun.management.jmxremote.login.config";
        public static final String SSL_ENABLED_CIPHER_SUITES = "com.sun.management.jmxremote.ssl.enabled.cipher.suites";
        public static final String SSL_ENABLED_PROTOCOLS = "com.sun.management.jmxremote.ssl.enabled.protocols";
        public static final String SSL_NEED_CLIENT_AUTH = "com.sun.management.jmxremote.ssl.need.client.auth";
        public static final String SSL_CONFIG_FILE_NAME = "com.sun.management.jmxremote.ssl.config.file";
    }
    
    public interface DefaultValues
    {
        public static final String PORT = "0";
        public static final String CONFIG_FILE_NAME = "management.properties";
        public static final String USE_SSL = "true";
        public static final String USE_LOCAL_ONLY = "true";
        public static final String USE_REGISTRY_SSL = "false";
        public static final String USE_AUTHENTICATION = "true";
        public static final String PASSWORD_FILE_NAME = "jmxremote.password";
        public static final String ACCESS_FILE_NAME = "jmxremote.access";
        public static final String SSL_NEED_CLIENT_AUTH = "false";
    }
}
