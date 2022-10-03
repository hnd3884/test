package javax.management.remote.rmi;

import java.util.HashSet;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import com.sun.jmx.remote.internal.IIOPHelper;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import javax.management.InstanceNotFoundException;
import javax.management.remote.MBeanServerForwarder;
import com.sun.jmx.remote.security.MBeanServerFileAccessController;
import java.util.HashMap;
import javax.management.remote.JMXConnector;
import com.sun.jmx.remote.util.EnvHelp;
import java.util.Collections;
import java.net.MalformedURLException;
import java.io.IOException;
import javax.management.MBeanServer;
import java.util.Set;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import com.sun.jmx.remote.util.ClassLogger;
import javax.management.remote.JMXConnectorServer;

public class RMIConnectorServer extends JMXConnectorServer
{
    public static final String JNDI_REBIND_ATTRIBUTE = "jmx.remote.jndi.rebind";
    public static final String RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.client.socket.factory";
    public static final String RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE = "jmx.remote.rmi.server.socket.factory";
    private static final char[] intToAlpha;
    private static ClassLogger logger;
    private JMXServiceURL address;
    private RMIServerImpl rmiServerImpl;
    private final Map<String, ?> attributes;
    private ClassLoader defaultClassLoader;
    private String boundJndiUrl;
    private static final int CREATED = 0;
    private static final int STARTED = 1;
    private static final int STOPPED = 2;
    private int state;
    private static final Set<RMIConnectorServer> openedServers;
    
    public RMIConnectorServer(final JMXServiceURL jmxServiceURL, final Map<String, ?> map) throws IOException {
        this(jmxServiceURL, map, null);
    }
    
    public RMIConnectorServer(final JMXServiceURL jmxServiceURL, final Map<String, ?> map, final MBeanServer mBeanServer) throws IOException {
        this(jmxServiceURL, map, null, mBeanServer);
    }
    
    public RMIConnectorServer(final JMXServiceURL address, final Map<String, ?> map, final RMIServerImpl rmiServerImpl, final MBeanServer mBeanServer) throws IOException {
        super(mBeanServer);
        this.defaultClassLoader = null;
        this.state = 0;
        if (address == null) {
            throw new IllegalArgumentException("Null JMXServiceURL");
        }
        if (rmiServerImpl == null) {
            final String protocol = address.getProtocol();
            if (protocol == null || (!protocol.equals("rmi") && !protocol.equals("iiop"))) {
                throw new MalformedURLException("Invalid protocol type: " + protocol);
            }
            final String urlPath = address.getURLPath();
            if (!urlPath.equals("") && !urlPath.equals("/") && !urlPath.startsWith("/jndi/")) {
                throw new MalformedURLException("URL path must be empty or start with /jndi/");
            }
        }
        if (map == null) {
            this.attributes = Collections.emptyMap();
        }
        else {
            EnvHelp.checkAttributes(map);
            this.attributes = Collections.unmodifiableMap((Map<? extends String, ?>)map);
        }
        this.address = address;
        this.rmiServerImpl = rmiServerImpl;
    }
    
    @Override
    public JMXConnector toJMXConnector(final Map<String, ?> map) throws IOException {
        if (!this.isActive()) {
            throw new IllegalStateException("Connector is not active");
        }
        final HashMap hashMap = new HashMap((this.attributes == null) ? Collections.emptyMap() : this.attributes);
        if (map != null) {
            EnvHelp.checkAttributes(map);
            hashMap.putAll(map);
        }
        return new RMIConnector((RMIServer)this.rmiServerImpl.toStub(), EnvHelp.filterAttributes((Map<String, Object>)hashMap));
    }
    
    @Override
    public synchronized void start() throws IOException {
        final boolean traceOn = RMIConnectorServer.logger.traceOn();
        if (this.state == 1) {
            if (traceOn) {
                RMIConnectorServer.logger.trace("start", "already started");
            }
            return;
        }
        if (this.state == 2) {
            if (traceOn) {
                RMIConnectorServer.logger.trace("start", "already stopped");
            }
            throw new IOException("The server has been stopped.");
        }
        if (this.getMBeanServer() == null) {
            throw new IllegalStateException("This connector server is not attached to an MBean server");
        }
        if (this.attributes != null) {
            final String s = (String)this.attributes.get("jmx.remote.x.access.file");
            if (s != null) {
                MBeanServerFileAccessController mBeanServerForwarder;
                try {
                    mBeanServerForwarder = new MBeanServerFileAccessController(s);
                }
                catch (final IOException ex) {
                    throw EnvHelp.initCause(new IllegalArgumentException(ex.getMessage()), ex);
                }
                this.setMBeanServerForwarder(mBeanServerForwarder);
            }
        }
        try {
            if (traceOn) {
                RMIConnectorServer.logger.trace("start", "setting default class loader");
            }
            this.defaultClassLoader = EnvHelp.resolveServerClassLoader(this.attributes, this.getMBeanServer());
        }
        catch (final InstanceNotFoundException ex2) {
            throw EnvHelp.initCause(new IllegalArgumentException("ClassLoader not found: " + ex2), ex2);
        }
        if (traceOn) {
            RMIConnectorServer.logger.trace("start", "setting RMIServer object");
        }
        RMIServerImpl rmiServerImpl;
        if (this.rmiServerImpl != null) {
            rmiServerImpl = this.rmiServerImpl;
        }
        else {
            rmiServerImpl = this.newServer();
        }
        rmiServerImpl.setMBeanServer(this.getMBeanServer());
        rmiServerImpl.setDefaultClassLoader(this.defaultClassLoader);
        rmiServerImpl.setRMIConnectorServer(this);
        rmiServerImpl.export();
        try {
            if (traceOn) {
                RMIConnectorServer.logger.trace("start", "getting RMIServer object to export");
            }
            final RMIServer objectToBind = objectToBind(rmiServerImpl, this.attributes);
            if (this.address != null && this.address.getURLPath().startsWith("/jndi/")) {
                final String substring = this.address.getURLPath().substring(6);
                if (traceOn) {
                    RMIConnectorServer.logger.trace("start", "Using external directory: " + substring);
                }
                final boolean computeBooleanFromString = EnvHelp.computeBooleanFromString((String)this.attributes.get("jmx.remote.jndi.rebind"));
                if (traceOn) {
                    RMIConnectorServer.logger.trace("start", "jmx.remote.jndi.rebind=" + computeBooleanFromString);
                }
                try {
                    if (traceOn) {
                        RMIConnectorServer.logger.trace("start", "binding to " + substring);
                    }
                    this.bind(substring, EnvHelp.mapToHashtable(this.attributes), objectToBind, computeBooleanFromString);
                    this.boundJndiUrl = substring;
                }
                catch (final NamingException ex3) {
                    throw newIOException("Cannot bind to URL [" + substring + "]: " + ex3, ex3);
                }
            }
            else {
                if (traceOn) {
                    RMIConnectorServer.logger.trace("start", "Encoding URL");
                }
                this.encodeStubInAddress(objectToBind, this.attributes);
                if (traceOn) {
                    RMIConnectorServer.logger.trace("start", "Encoded URL: " + this.address);
                }
            }
        }
        catch (final Exception ex4) {
            try {
                rmiServerImpl.close();
            }
            catch (final Exception ex5) {}
            if (ex4 instanceof RuntimeException) {
                throw (RuntimeException)ex4;
            }
            if (ex4 instanceof IOException) {
                throw (IOException)ex4;
            }
            throw newIOException("Got unexpected exception while starting the connector server: " + ex4, ex4);
        }
        this.rmiServerImpl = rmiServerImpl;
        synchronized (RMIConnectorServer.openedServers) {
            RMIConnectorServer.openedServers.add(this);
        }
        this.state = 1;
        if (traceOn) {
            RMIConnectorServer.logger.trace("start", "Connector Server Address = " + this.address);
            RMIConnectorServer.logger.trace("start", "started.");
        }
    }
    
    @Override
    public void stop() throws IOException {
        final boolean traceOn = RMIConnectorServer.logger.traceOn();
        synchronized (this) {
            if (this.state == 2) {
                if (traceOn) {
                    RMIConnectorServer.logger.trace("stop", "already stopped.");
                }
                return;
            }
            if (this.state == 0 && traceOn) {
                RMIConnectorServer.logger.trace("stop", "not started yet.");
            }
            if (traceOn) {
                RMIConnectorServer.logger.trace("stop", "stopping.");
            }
            this.state = 2;
        }
        synchronized (RMIConnectorServer.openedServers) {
            RMIConnectorServer.openedServers.remove(this);
        }
        IOException ioException = null;
        if (this.rmiServerImpl != null) {
            try {
                if (traceOn) {
                    RMIConnectorServer.logger.trace("stop", "closing RMI server.");
                }
                this.rmiServerImpl.close();
            }
            catch (final IOException ex) {
                if (traceOn) {
                    RMIConnectorServer.logger.trace("stop", "failed to close RMI server: " + ex);
                }
                if (RMIConnectorServer.logger.debugOn()) {
                    RMIConnectorServer.logger.debug("stop", ex);
                }
                ioException = ex;
            }
        }
        if (this.boundJndiUrl != null) {
            try {
                if (traceOn) {
                    RMIConnectorServer.logger.trace("stop", "unbind from external directory: " + this.boundJndiUrl);
                }
                final InitialContext initialContext = new InitialContext(EnvHelp.mapToHashtable(this.attributes));
                initialContext.unbind(this.boundJndiUrl);
                initialContext.close();
            }
            catch (final NamingException ex2) {
                if (traceOn) {
                    RMIConnectorServer.logger.trace("stop", "failed to unbind RMI server: " + ex2);
                }
                if (RMIConnectorServer.logger.debugOn()) {
                    RMIConnectorServer.logger.debug("stop", ex2);
                }
                if (ioException == null) {
                    ioException = newIOException("Cannot bind to URL: " + ex2, ex2);
                }
            }
        }
        if (ioException != null) {
            throw ioException;
        }
        if (traceOn) {
            RMIConnectorServer.logger.trace("stop", "stopped");
        }
    }
    
    @Override
    public synchronized boolean isActive() {
        return this.state == 1;
    }
    
    @Override
    public JMXServiceURL getAddress() {
        if (!this.isActive()) {
            return null;
        }
        return this.address;
    }
    
    @Override
    public Map<String, ?> getAttributes() {
        return Collections.unmodifiableMap((Map<? extends String, ?>)EnvHelp.filterAttributes(this.attributes));
    }
    
    @Override
    public synchronized void setMBeanServerForwarder(final MBeanServerForwarder mBeanServerForwarder) {
        super.setMBeanServerForwarder(mBeanServerForwarder);
        if (this.rmiServerImpl != null) {
            this.rmiServerImpl.setMBeanServer(this.getMBeanServer());
        }
    }
    
    @Override
    protected void connectionOpened(final String s, final String s2, final Object o) {
        super.connectionOpened(s, s2, o);
    }
    
    @Override
    protected void connectionClosed(final String s, final String s2, final Object o) {
        super.connectionClosed(s, s2, o);
    }
    
    @Override
    protected void connectionFailed(final String s, final String s2, final Object o) {
        super.connectionFailed(s, s2, o);
    }
    
    void bind(final String s, final Hashtable<?, ?> hashtable, final RMIServer rmiServer, final boolean b) throws NamingException, MalformedURLException {
        final InitialContext initialContext = new InitialContext(hashtable);
        if (b) {
            initialContext.rebind(s, rmiServer);
        }
        else {
            initialContext.bind(s, rmiServer);
        }
        initialContext.close();
    }
    
    RMIServerImpl newServer() throws IOException {
        final boolean iiopURL = isIiopURL(this.address, true);
        int port;
        if (this.address == null) {
            port = 0;
        }
        else {
            port = this.address.getPort();
        }
        if (iiopURL) {
            return newIIOPServer(this.attributes);
        }
        return newJRMPServer(this.attributes, port);
    }
    
    private void encodeStubInAddress(final RMIServer rmiServer, final Map<String, ?> map) throws IOException {
        String protocol;
        String s;
        int port;
        if (this.address == null) {
            if (IIOPHelper.isStub(rmiServer)) {
                protocol = "iiop";
            }
            else {
                protocol = "rmi";
            }
            s = null;
            port = 0;
        }
        else {
            protocol = this.address.getProtocol();
            s = (this.address.getHost().equals("") ? null : this.address.getHost());
            port = this.address.getPort();
        }
        this.address = new JMXServiceURL(protocol, s, port, encodeStub(rmiServer, map));
    }
    
    static boolean isIiopURL(final JMXServiceURL jmxServiceURL, final boolean b) throws MalformedURLException {
        final String protocol = jmxServiceURL.getProtocol();
        if (protocol.equals("rmi")) {
            return false;
        }
        if (protocol.equals("iiop")) {
            return true;
        }
        if (b) {
            throw new MalformedURLException("URL must have protocol \"rmi\" or \"iiop\": \"" + protocol + "\"");
        }
        return false;
    }
    
    static String encodeStub(final RMIServer rmiServer, final Map<String, ?> map) throws IOException {
        if (IIOPHelper.isStub(rmiServer)) {
            return "/ior/" + encodeIIOPStub(rmiServer, map);
        }
        return "/stub/" + encodeJRMPStub(rmiServer, map);
    }
    
    static String encodeJRMPStub(final RMIServer rmiServer, final Map<String, ?> map) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(rmiServer);
        objectOutputStream.close();
        return byteArrayToBase64(byteArrayOutputStream.toByteArray());
    }
    
    static String encodeIIOPStub(final RMIServer rmiServer, final Map<String, ?> map) throws IOException {
        try {
            return IIOPHelper.objectToString(IIOPHelper.getOrb(rmiServer), rmiServer);
        }
        catch (final RuntimeException ex) {
            throw newIOException(ex.getMessage(), ex);
        }
    }
    
    private static RMIServer objectToBind(final RMIServerImpl rmiServerImpl, final Map<String, ?> map) throws IOException {
        return RMIConnector.connectStub((RMIServer)rmiServerImpl.toStub(), map);
    }
    
    private static RMIServerImpl newJRMPServer(final Map<String, ?> map, final int n) throws IOException {
        return new RMIJRMPServerImpl(n, (RMIClientSocketFactory)map.get("jmx.remote.rmi.client.socket.factory"), (RMIServerSocketFactory)map.get("jmx.remote.rmi.server.socket.factory"), map);
    }
    
    private static RMIServerImpl newIIOPServer(final Map<String, ?> map) throws IOException {
        return new RMIIIOPServerImpl(map);
    }
    
    private static String byteArrayToBase64(final byte[] array) {
        final int length = array.length;
        final int n = length / 3;
        final int n2 = length - 3 * n;
        final StringBuilder sb = new StringBuilder(4 * ((length + 2) / 3));
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            final int n4 = array[n3++] & 0xFF;
            final int n5 = array[n3++] & 0xFF;
            final int n6 = array[n3++] & 0xFF;
            sb.append(RMIConnectorServer.intToAlpha[n4 >> 2]);
            sb.append(RMIConnectorServer.intToAlpha[(n4 << 4 & 0x3F) | n5 >> 4]);
            sb.append(RMIConnectorServer.intToAlpha[(n5 << 2 & 0x3F) | n6 >> 6]);
            sb.append(RMIConnectorServer.intToAlpha[n6 & 0x3F]);
        }
        if (n2 != 0) {
            final int n7 = array[n3++] & 0xFF;
            sb.append(RMIConnectorServer.intToAlpha[n7 >> 2]);
            if (n2 == 1) {
                sb.append(RMIConnectorServer.intToAlpha[n7 << 4 & 0x3F]);
                sb.append("==");
            }
            else {
                final int n8 = array[n3++] & 0xFF;
                sb.append(RMIConnectorServer.intToAlpha[(n7 << 4 & 0x3F) | n8 >> 4]);
                sb.append(RMIConnectorServer.intToAlpha[n8 << 2 & 0x3F]);
                sb.append('=');
            }
        }
        return sb.toString();
    }
    
    private static IOException newIOException(final String s, final Throwable t) {
        return EnvHelp.initCause(new IOException(s), t);
    }
    
    static {
        intToAlpha = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        RMIConnectorServer.logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectorServer");
        openedServers = new HashSet<RMIConnectorServer>();
    }
}
