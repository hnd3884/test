package javax.management.remote.rmi;

import sun.reflect.misc.ReflectUtil;
import java.io.ObjectStreamClass;
import com.sun.jmx.remote.internal.ClientListenerInfo;
import java.rmi.ServerException;
import javax.management.MBeanServerDelegate;
import javax.management.NotificationFilterSupport;
import java.io.NotSerializableException;
import java.rmi.MarshalException;
import java.rmi.UnmarshalException;
import javax.management.remote.NotificationResult;
import com.sun.jmx.remote.internal.ClientNotifForwarder;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.InvalidAttributeValueException;
import javax.management.AttributeNotFoundException;
import java.util.Set;
import javax.management.QueryExp;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import com.sun.jmx.remote.internal.ProxyRef;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import javax.management.Attribute;
import javax.management.AttributeList;
import java.util.Arrays;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.lang.reflect.InvocationTargetException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import javax.naming.InitialContext;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RemoteRef;
import java.lang.reflect.InvocationHandler;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import sun.rmi.server.UnicastRef2;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.lang.reflect.Proxy;
import java.io.ObjectOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Properties;
import com.sun.jmx.remote.internal.IIOPHelper;
import javax.management.InstanceNotFoundException;
import com.sun.jmx.mbeanserver.Util;
import java.rmi.MarshalledObject;
import javax.management.ObjectName;
import java.rmi.NoSuchObjectException;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.naming.NamingException;
import javax.management.Notification;
import javax.management.remote.JMXConnectionNotification;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.Remote;
import java.util.HashMap;
import java.io.IOException;
import com.sun.jmx.remote.util.EnvHelp;
import java.util.Collections;
import com.sun.jmx.remote.internal.ClientCommunicatorAdmin;
import javax.management.NotificationBroadcasterSupport;
import javax.management.MBeanServerConnection;
import java.lang.ref.WeakReference;
import javax.security.auth.Subject;
import java.util.WeakHashMap;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import java.lang.reflect.Constructor;
import com.sun.jmx.remote.util.ClassLogger;
import javax.management.remote.JMXAddressable;
import java.io.Serializable;
import javax.management.remote.JMXConnector;

public class RMIConnector implements JMXConnector, Serializable, JMXAddressable
{
    private static final ClassLogger logger;
    private static final long serialVersionUID = 817323035842634473L;
    private static final String rmiServerImplStubClassName;
    private static final Class<?> rmiServerImplStubClass;
    private static final String rmiConnectionImplStubClassName;
    private static final Class<?> rmiConnectionImplStubClass;
    private static final String pRefClassName = "com.sun.jmx.remote.internal.PRef";
    private static final Constructor<?> proxyRefConstructor;
    private static final String iiopConnectionStubClassName = "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub";
    private static final String proxyStubClassName = "com.sun.jmx.remote.protocol.iiop.ProxyStub";
    private static final String ProxyInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.ProxyInputStream";
    private static final String pInputStreamClassName = "com.sun.jmx.remote.protocol.iiop.PInputStream";
    private static final Class<?> proxyStubClass;
    private static final byte[] base64ToInt;
    private final RMIServer rmiServer;
    private final JMXServiceURL jmxServiceURL;
    private transient Map<String, Object> env;
    private transient ClassLoader defaultClassLoader;
    private transient RMIConnection connection;
    private transient String connectionId;
    private transient long clientNotifSeqNo;
    private transient WeakHashMap<Subject, WeakReference<MBeanServerConnection>> rmbscMap;
    private transient WeakReference<MBeanServerConnection> nullSubjectConnRef;
    private transient RMINotifClient rmiNotifClient;
    private transient long clientNotifCounter;
    private transient boolean connected;
    private transient boolean terminated;
    private transient Exception closeException;
    private transient NotificationBroadcasterSupport connectionBroadcaster;
    private transient ClientCommunicatorAdmin communicatorAdmin;
    private static volatile WeakReference<Object> orb;
    
    private RMIConnector(final RMIServer rmiServer, final JMXServiceURL jmxServiceURL, final Map<String, ?> map) {
        this.clientNotifSeqNo = 0L;
        this.nullSubjectConnRef = null;
        this.clientNotifCounter = 0L;
        if (rmiServer == null && jmxServiceURL == null) {
            throw new IllegalArgumentException("rmiServer and jmxServiceURL both null");
        }
        this.initTransients();
        this.rmiServer = rmiServer;
        this.jmxServiceURL = jmxServiceURL;
        if (map == null) {
            this.env = Collections.emptyMap();
        }
        else {
            EnvHelp.checkAttributes(map);
            this.env = Collections.unmodifiableMap((Map<? extends String, ?>)map);
        }
    }
    
    public RMIConnector(final JMXServiceURL jmxServiceURL, final Map<String, ?> map) {
        this(null, jmxServiceURL, map);
    }
    
    public RMIConnector(final RMIServer rmiServer, final Map<String, ?> map) {
        this(rmiServer, null, map);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append(":");
        if (this.rmiServer != null) {
            sb.append(" rmiServer=").append(this.rmiServer.toString());
        }
        if (this.jmxServiceURL != null) {
            if (this.rmiServer != null) {
                sb.append(",");
            }
            sb.append(" jmxServiceURL=").append(this.jmxServiceURL.toString());
        }
        return sb.toString();
    }
    
    @Override
    public JMXServiceURL getAddress() {
        return this.jmxServiceURL;
    }
    
    @Override
    public void connect() throws IOException {
        this.connect(null);
    }
    
    @Override
    public synchronized void connect(final Map<String, ?> map) throws IOException {
        final boolean traceOn = RMIConnector.logger.traceOn();
        String s = traceOn ? ("[" + this.toString() + "]") : null;
        if (this.terminated) {
            RMIConnector.logger.trace("connect", s + " already closed.");
            throw new IOException("Connector closed");
        }
        if (this.connected) {
            RMIConnector.logger.trace("connect", s + " already connected.");
            return;
        }
        try {
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " connecting...");
            }
            final HashMap env = new HashMap((this.env == null) ? Collections.emptyMap() : this.env);
            if (map != null) {
                EnvHelp.checkAttributes(map);
                env.putAll(map);
            }
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " finding stub...");
            }
            final RMIServer rmiServer = (this.rmiServer != null) ? this.rmiServer : this.findRMIServer(this.jmxServiceURL, env);
            final boolean computeBooleanFromString = EnvHelp.computeBooleanFromString((String)env.get("jmx.remote.x.check.stub"));
            if (computeBooleanFromString) {
                checkStub(rmiServer, RMIConnector.rmiServerImplStubClass);
            }
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " connecting stub...");
            }
            final RMIServer connectStub = connectStub(rmiServer, env);
            s = (traceOn ? ("[" + this.toString() + "]") : null);
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " getting connection...");
            }
            final Object value = env.get("jmx.remote.credentials");
            try {
                this.connection = getConnection(connectStub, value, computeBooleanFromString);
            }
            catch (final RemoteException ex) {
                if (this.jmxServiceURL != null) {
                    final String protocol = this.jmxServiceURL.getProtocol();
                    final String urlPath = this.jmxServiceURL.getURLPath();
                    if ("rmi".equals(protocol) && urlPath.startsWith("/jndi/iiop:")) {
                        final MalformedURLException ex2 = new MalformedURLException("Protocol is rmi but JNDI scheme is iiop: " + this.jmxServiceURL);
                        ex2.initCause(ex);
                        throw ex2;
                    }
                }
                throw ex;
            }
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " getting class loader...");
            }
            env.put("jmx.remote.default.class.loader", this.defaultClassLoader = EnvHelp.resolveClientClassLoader(env));
            this.rmiNotifClient = new RMINotifClient(this.defaultClassLoader, env);
            this.env = env;
            this.communicatorAdmin = new RMIClientCommunicatorAdmin(EnvHelp.getConnectionCheckPeriod(env));
            this.connected = true;
            this.connectionId = this.getConnectionId();
            this.sendNotification(new JMXConnectionNotification("jmx.remote.connection.opened", this, this.connectionId, this.clientNotifSeqNo++, "Successful connection", null));
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " done...");
            }
        }
        catch (final IOException ex3) {
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " failed to connect: " + ex3);
            }
            throw ex3;
        }
        catch (final RuntimeException ex4) {
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " failed to connect: " + ex4);
            }
            throw ex4;
        }
        catch (final NamingException ex5) {
            final String string = "Failed to retrieve RMIServer stub: " + ex5;
            if (traceOn) {
                RMIConnector.logger.trace("connect", s + " " + string);
            }
            throw EnvHelp.initCause(new IOException(string), ex5);
        }
    }
    
    @Override
    public synchronized String getConnectionId() throws IOException {
        if (this.terminated || !this.connected) {
            if (RMIConnector.logger.traceOn()) {
                RMIConnector.logger.trace("getConnectionId", "[" + this.toString() + "] not connected.");
            }
            throw new IOException("Not connected");
        }
        return this.connection.getConnectionId();
    }
    
    @Override
    public synchronized MBeanServerConnection getMBeanServerConnection() throws IOException {
        return this.getMBeanServerConnection(null);
    }
    
    @Override
    public synchronized MBeanServerConnection getMBeanServerConnection(final Subject subject) throws IOException {
        if (this.terminated) {
            if (RMIConnector.logger.traceOn()) {
                RMIConnector.logger.trace("getMBeanServerConnection", "[" + this.toString() + "] already closed.");
            }
            throw new IOException("Connection closed");
        }
        if (!this.connected) {
            if (RMIConnector.logger.traceOn()) {
                RMIConnector.logger.trace("getMBeanServerConnection", "[" + this.toString() + "] is not connected.");
            }
            throw new IOException("Not connected");
        }
        return this.getConnectionWithSubject(subject);
    }
    
    @Override
    public void addConnectionNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        if (notificationListener == null) {
            throw new NullPointerException("listener");
        }
        this.connectionBroadcaster.addNotificationListener(notificationListener, notificationFilter, o);
    }
    
    @Override
    public void removeConnectionNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        if (notificationListener == null) {
            throw new NullPointerException("listener");
        }
        this.connectionBroadcaster.removeNotificationListener(notificationListener);
    }
    
    @Override
    public void removeConnectionNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        if (notificationListener == null) {
            throw new NullPointerException("listener");
        }
        this.connectionBroadcaster.removeNotificationListener(notificationListener, notificationFilter, o);
    }
    
    private void sendNotification(final Notification notification) {
        this.connectionBroadcaster.sendNotification(notification);
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.close(false);
    }
    
    private synchronized void close(final boolean b) throws IOException {
        final boolean traceOn = RMIConnector.logger.traceOn();
        final boolean debugOn = RMIConnector.logger.debugOn();
        final String s = traceOn ? ("[" + this.toString() + "]") : null;
        if (!b) {
            if (this.terminated) {
                if (this.closeException == null) {
                    if (traceOn) {
                        RMIConnector.logger.trace("close", s + " already closed.");
                    }
                    return;
                }
            }
            else {
                this.terminated = true;
            }
        }
        if (this.closeException != null && traceOn && traceOn) {
            RMIConnector.logger.trace("close", s + " had failed: " + this.closeException);
            RMIConnector.logger.trace("close", s + " attempting to close again.");
        }
        String connectionId = null;
        if (this.connected) {
            connectionId = this.connectionId;
        }
        this.closeException = null;
        if (traceOn) {
            RMIConnector.logger.trace("close", s + " closing.");
        }
        if (this.communicatorAdmin != null) {
            this.communicatorAdmin.terminate();
        }
        if (this.rmiNotifClient != null) {
            try {
                this.rmiNotifClient.terminate();
                if (traceOn) {
                    RMIConnector.logger.trace("close", s + " RMI Notification client terminated.");
                }
            }
            catch (final RuntimeException closeException) {
                this.closeException = closeException;
                if (traceOn) {
                    RMIConnector.logger.trace("close", s + " Failed to terminate RMI Notification client: " + closeException);
                }
                if (debugOn) {
                    RMIConnector.logger.debug("close", closeException);
                }
            }
        }
        if (this.connection != null) {
            try {
                this.connection.close();
                if (traceOn) {
                    RMIConnector.logger.trace("close", s + " closed.");
                }
            }
            catch (final NoSuchObjectException ex) {}
            catch (final IOException closeException2) {
                this.closeException = closeException2;
                if (traceOn) {
                    RMIConnector.logger.trace("close", s + " Failed to close RMIServer: " + closeException2);
                }
                if (debugOn) {
                    RMIConnector.logger.debug("close", closeException2);
                }
            }
        }
        this.rmbscMap.clear();
        if (connectionId != null) {
            this.sendNotification(new JMXConnectionNotification("jmx.remote.connection.closed", this, connectionId, this.clientNotifSeqNo++, "Client has been closed", null));
        }
        if (this.closeException == null) {
            return;
        }
        if (traceOn) {
            RMIConnector.logger.trace("close", s + " failed to close: " + this.closeException);
        }
        if (this.closeException instanceof IOException) {
            throw (IOException)this.closeException;
        }
        if (this.closeException instanceof RuntimeException) {
            throw (RuntimeException)this.closeException;
        }
        throw EnvHelp.initCause(new IOException("Failed to close: " + this.closeException), this.closeException);
    }
    
    private Integer addListenerWithSubject(final ObjectName objectName, final MarshalledObject<NotificationFilter> marshalledObject, final Subject subject, final boolean b) throws InstanceNotFoundException, IOException {
        final boolean debugOn = RMIConnector.logger.debugOn();
        if (debugOn) {
            RMIConnector.logger.debug("addListenerWithSubject", "(ObjectName,MarshalledObject,Subject)");
        }
        final Integer[] addListenersWithSubjects = this.addListenersWithSubjects(new ObjectName[] { objectName }, Util.cast(new MarshalledObject[] { marshalledObject }), new Subject[] { subject }, b);
        if (debugOn) {
            RMIConnector.logger.debug("addListenerWithSubject", "listenerID=" + addListenersWithSubjects[0]);
        }
        return addListenersWithSubjects[0];
    }
    
    private Integer[] addListenersWithSubjects(final ObjectName[] array, final MarshalledObject<NotificationFilter>[] array2, final Subject[] array3, final boolean b) throws InstanceNotFoundException, IOException {
        final boolean debugOn = RMIConnector.logger.debugOn();
        if (debugOn) {
            RMIConnector.logger.debug("addListenersWithSubjects", "(ObjectName[],MarshalledObject[],Subject[])");
        }
        final ClassLoader pushDefaultClassLoader = this.pushDefaultClassLoader();
        Integer[] array4 = null;
        Label_0131: {
            try {
                array4 = this.connection.addNotificationListeners(array, array2, array3);
            }
            catch (final NoSuchObjectException ex) {
                if (b) {
                    this.communicatorAdmin.gotIOException(ex);
                    array4 = this.connection.addNotificationListeners(array, array2, array3);
                    break Label_0131;
                }
                throw ex;
            }
            catch (final IOException ex2) {
                this.communicatorAdmin.gotIOException(ex2);
            }
            finally {
                this.popDefaultClassLoader(pushDefaultClassLoader);
            }
        }
        if (debugOn) {
            RMIConnector.logger.debug("addListenersWithSubjects", "registered " + ((array4 == null) ? 0 : array4.length) + " listener(s)");
        }
        return array4;
    }
    
    static RMIServer connectStub(final RMIServer rmiServer, final Map<String, ?> map) throws IOException {
        if (IIOPHelper.isStub(rmiServer)) {
            try {
                IIOPHelper.getOrb(rmiServer);
            }
            catch (final UnsupportedOperationException ex) {
                IIOPHelper.connect(rmiServer, resolveOrb(map));
            }
        }
        return rmiServer;
    }
    
    static Object resolveOrb(final Map<String, ?> map) throws IOException {
        if (map != null) {
            final Object value = map.get("java.naming.corba.orb");
            if (value != null && !IIOPHelper.isOrb(value)) {
                throw new IllegalArgumentException("java.naming.corba.orb must be an instance of org.omg.CORBA.ORB.");
            }
            if (value != null) {
                return value;
            }
        }
        final Object o = (RMIConnector.orb == null) ? null : RMIConnector.orb.get();
        if (o != null) {
            return o;
        }
        final Object orb = IIOPHelper.createOrb(null, null);
        RMIConnector.orb = new WeakReference<Object>(orb);
        return orb;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.rmiServer == null && this.jmxServiceURL == null) {
            throw new InvalidObjectException("rmiServer and jmxServiceURL both null");
        }
        this.initTransients();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.rmiServer == null && this.jmxServiceURL == null) {
            throw new InvalidObjectException("rmiServer and jmxServiceURL both null.");
        }
        connectStub(this.rmiServer, this.env);
        objectOutputStream.defaultWriteObject();
    }
    
    private void initTransients() {
        this.rmbscMap = new WeakHashMap<Subject, WeakReference<MBeanServerConnection>>();
        this.connected = false;
        this.terminated = false;
        this.connectionBroadcaster = new NotificationBroadcasterSupport();
    }
    
    private static void checkStub(Remote remote, final Class<?> clazz) {
        if (remote.getClass() != clazz) {
            if (!Proxy.isProxyClass(remote.getClass())) {
                throw new SecurityException("Expecting a " + clazz.getName() + " stub!");
            }
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(remote);
            if (invocationHandler.getClass() != RemoteObjectInvocationHandler.class) {
                throw new SecurityException("Expecting a dynamic proxy instance with a " + RemoteObjectInvocationHandler.class.getName() + " invocation handler!");
            }
            remote = (Remote)invocationHandler;
        }
        final RemoteRef ref = ((RemoteObject)remote).getRef();
        if (((UnicastRef2)ref).getClass() != UnicastRef2.class) {
            throw new SecurityException("Expecting a " + UnicastRef2.class.getName() + " remote reference in stub!");
        }
        final RMIClientSocketFactory clientSocketFactory = ((UnicastRef2)ref).getLiveRef().getClientSocketFactory();
        if (clientSocketFactory == null || clientSocketFactory.getClass() != SslRMIClientSocketFactory.class) {
            throw new SecurityException("Expecting a " + SslRMIClientSocketFactory.class.getName() + " RMI client socket factory in stub!");
        }
    }
    
    private RMIServer findRMIServer(final JMXServiceURL jmxServiceURL, final Map<String, Object> map) throws NamingException, IOException {
        final boolean iiopURL = RMIConnectorServer.isIiopURL(jmxServiceURL, true);
        if (iiopURL) {
            map.put("java.naming.corba.orb", resolveOrb(map));
        }
        final String urlPath = jmxServiceURL.getURLPath();
        int n = urlPath.indexOf(59);
        if (n < 0) {
            n = urlPath.length();
        }
        if (urlPath.startsWith("/jndi/")) {
            return this.findRMIServerJNDI(urlPath.substring(6, n), map, iiopURL);
        }
        if (urlPath.startsWith("/stub/")) {
            return this.findRMIServerJRMP(urlPath.substring(6, n), map, iiopURL);
        }
        if (!urlPath.startsWith("/ior/")) {
            throw new MalformedURLException("URL path must begin with /jndi/ or /stub/ or /ior/: " + urlPath);
        }
        if (!IIOPHelper.isAvailable()) {
            throw new IOException("iiop protocol not available");
        }
        return this.findRMIServerIIOP(urlPath.substring(5, n), map, iiopURL);
    }
    
    private RMIServer findRMIServerJNDI(final String s, final Map<String, ?> map, final boolean b) throws NamingException {
        final InitialContext initialContext = new InitialContext(EnvHelp.mapToHashtable((Map<Object, Object>)map));
        final Object lookup = initialContext.lookup(s);
        initialContext.close();
        if (b) {
            return narrowIIOPServer(lookup);
        }
        return narrowJRMPServer(lookup);
    }
    
    private static RMIServer narrowJRMPServer(final Object o) {
        return (RMIServer)o;
    }
    
    private static RMIServer narrowIIOPServer(final Object o) {
        try {
            return IIOPHelper.narrow(o, RMIServer.class);
        }
        catch (final ClassCastException ex) {
            if (RMIConnector.logger.traceOn()) {
                RMIConnector.logger.trace("narrowIIOPServer", "Failed to narrow objref=" + o + ": " + ex);
            }
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("narrowIIOPServer", ex);
            }
            return null;
        }
    }
    
    private RMIServer findRMIServerIIOP(final String s, final Map<String, ?> map, final boolean b) {
        return IIOPHelper.narrow(IIOPHelper.stringToObject(map.get("java.naming.corba.orb"), s), RMIServer.class);
    }
    
    private RMIServer findRMIServerJRMP(final String s, final Map<String, ?> map, final boolean b) throws IOException {
        byte[] base64ToByteArray;
        try {
            base64ToByteArray = base64ToByteArray(s);
        }
        catch (final IllegalArgumentException ex) {
            throw new MalformedURLException("Bad BASE64 encoding: " + ex.getMessage());
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(base64ToByteArray);
        final ClassLoader resolveClientClassLoader = EnvHelp.resolveClientClassLoader(map);
        final ObjectInputStream objectInputStream = (resolveClientClassLoader == null) ? new ObjectInputStream(byteArrayInputStream) : new ObjectInputStreamWithLoader(byteArrayInputStream, resolveClientClassLoader);
        Object object;
        try {
            object = objectInputStream.readObject();
        }
        catch (final ClassNotFoundException ex2) {
            throw new MalformedURLException("Class not found: " + ex2);
        }
        return (RMIServer)object;
    }
    
    private MBeanServerConnection getConnectionWithSubject(final Subject subject) {
        MBeanServerConnection mBeanServerConnection;
        if (subject == null) {
            if (this.nullSubjectConnRef == null || (mBeanServerConnection = this.nullSubjectConnRef.get()) == null) {
                mBeanServerConnection = new RemoteMBeanServerConnection(null);
                this.nullSubjectConnRef = new WeakReference<MBeanServerConnection>(mBeanServerConnection);
            }
        }
        else {
            final WeakReference weakReference = this.rmbscMap.get(subject);
            if (weakReference == null || (mBeanServerConnection = (MBeanServerConnection)weakReference.get()) == null) {
                mBeanServerConnection = new RemoteMBeanServerConnection(subject);
                this.rmbscMap.put(subject, new WeakReference<MBeanServerConnection>(mBeanServerConnection));
            }
        }
        return mBeanServerConnection;
    }
    
    private static RMIConnection shadowJrmpStub(final RemoteObject remoteObject) throws InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
        return (RMIConnection)RMIConnector.rmiConnectionImplStubClass.getConstructor(RemoteRef.class).newInstance((RemoteRef)RMIConnector.proxyRefConstructor.newInstance(remoteObject.getRef()));
    }
    
    private static RMIConnection shadowIiopStub(final Object o) throws InstantiationException, IllegalAccessException {
        RMIConnection doPrivileged;
        try {
            doPrivileged = AccessController.doPrivileged((PrivilegedExceptionAction<RMIConnection>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    return RMIConnector.proxyStubClass.newInstance();
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new InternalError();
        }
        IIOPHelper.setDelegate(doPrivileged, IIOPHelper.getDelegate(o));
        return doPrivileged;
    }
    
    private static RMIConnection getConnection(final RMIServer rmiServer, final Object o, final boolean b) throws IOException {
        final RMIConnection client = rmiServer.newClient(o);
        if (b) {
            checkStub(client, RMIConnector.rmiConnectionImplStubClass);
        }
        try {
            if (client.getClass() == RMIConnector.rmiConnectionImplStubClass) {
                return shadowJrmpStub((RemoteObject)client);
            }
            if (client.getClass().getName().equals("org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub")) {
                return shadowIiopStub(client);
            }
            RMIConnector.logger.trace("getConnection", "Did not wrap " + client.getClass() + " to foil stack search for classes: class loading semantics may be incorrect");
        }
        catch (final Exception ex) {
            RMIConnector.logger.error("getConnection", "Could not wrap " + client.getClass() + " to foil stack search for classes: class loading semantics may be incorrect: " + ex);
            RMIConnector.logger.debug("getConnection", ex);
        }
        return client;
    }
    
    private static byte[] base64ToByteArray(final String s) {
        final int length = s.length();
        final int n = length / 4;
        if (4 * n != length) {
            throw new IllegalArgumentException("String length must be a multiple of four.");
        }
        int n2 = 0;
        int n3 = n;
        if (length != 0) {
            if (s.charAt(length - 1) == '=') {
                ++n2;
                --n3;
            }
            if (s.charAt(length - 2) == '=') {
                ++n2;
            }
        }
        final byte[] array = new byte[3 * n - n2];
        int n4 = 0;
        int n5 = 0;
        for (int i = 0; i < n3; ++i) {
            final int base64toInt = base64toInt(s.charAt(n4++));
            final int base64toInt2 = base64toInt(s.charAt(n4++));
            final int base64toInt3 = base64toInt(s.charAt(n4++));
            final int base64toInt4 = base64toInt(s.charAt(n4++));
            array[n5++] = (byte)(base64toInt << 2 | base64toInt2 >> 4);
            array[n5++] = (byte)(base64toInt2 << 4 | base64toInt3 >> 2);
            array[n5++] = (byte)(base64toInt3 << 6 | base64toInt4);
        }
        if (n2 != 0) {
            final int base64toInt5 = base64toInt(s.charAt(n4++));
            final int base64toInt6 = base64toInt(s.charAt(n4++));
            array[n5++] = (byte)(base64toInt5 << 2 | base64toInt6 >> 4);
            if (n2 == 1) {
                array[n5++] = (byte)(base64toInt6 << 4 | base64toInt(s.charAt(n4++)) >> 2);
            }
        }
        return array;
    }
    
    private static int base64toInt(final char c) {
        int n;
        if (c >= RMIConnector.base64ToInt.length) {
            n = -1;
        }
        else {
            n = RMIConnector.base64ToInt[c];
        }
        if (n < 0) {
            throw new IllegalArgumentException("Illegal character " + c);
        }
        return n;
    }
    
    private ClassLoader pushDefaultClassLoader() {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
        if (this.defaultClassLoader != null) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    currentThread.setContextClassLoader(RMIConnector.this.defaultClassLoader);
                    return null;
                }
            });
        }
        return contextClassLoader;
    }
    
    private void popDefaultClassLoader(final ClassLoader classLoader) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                Thread.currentThread().setContextClassLoader(classLoader);
                return null;
            }
        });
    }
    
    private static String objects(final Object[] array) {
        if (array == null) {
            return "null";
        }
        return Arrays.asList(array).toString();
    }
    
    private static String strings(final String[] array) {
        return objects(array);
    }
    
    static String getAttributesNames(final AttributeList list) {
        return (list != null) ? list.asList().stream().map((Function<? super Object, ?>)Attribute::getName).collect((Collector<? super Object, ?, String>)Collectors.joining(", ", "[", "]")) : "[]";
    }
    
    static {
        logger = new ClassLogger("javax.management.remote.rmi", "RMIConnector");
        rmiServerImplStubClassName = RMIServer.class.getName() + "Impl_Stub";
        rmiConnectionImplStubClassName = RMIConnection.class.getName() + "Impl_Stub";
        final PrivilegedExceptionAction<Constructor<?>> privilegedExceptionAction = new PrivilegedExceptionAction<Constructor<?>>() {
            final /* synthetic */ byte[] val$pRefByteCode = NoCallStackClassLoader.stringToBytes("\u00ca\u00feº¾\u0000\u0000\u0000.\u0000\u0017\n\u0000\u0005\u0000\r\t\u0000\u0004\u0000\u000e\u000b\u0000\u000f\u0000\u0010\u0007\u0000\u0011\u0007\u0000\u0012\u0001\u0000\u0006<init>\u0001\u0000\u001e(Ljava/rmi/server/RemoteRef;)V\u0001\u0000\u0004Code\u0001\u0000\u0006invoke\u0001\u0000S(Ljava/rmi/Remote;Ljava/lang/reflect/Method;[Ljava/lang/Object;J)Ljava/lang/Object;\u0001\u0000\nExceptions\u0007\u0000\u0013\f\u0000\u0006\u0000\u0007\f\u0000\u0014\u0000\u0015\u0007\u0000\u0016\f\u0000\t\u0000\n\u0001\u0000 com/sun/jmx/remote/internal/PRef\u0001\u0000$com/sun/jmx/remote/internal/ProxyRef\u0001\u0000\u0013java/lang/Exception\u0001\u0000\u0003ref\u0001\u0000\u001bLjava/rmi/server/RemoteRef;\u0001\u0000\u0019java/rmi/server/RemoteRef\u0000!\u0000\u0004\u0000\u0005\u0000\u0000\u0000\u0000\u0000\u0002\u0000\u0001\u0000\u0006\u0000\u0007\u0000\u0001\u0000\b\u0000\u0000\u0000\u0012\u0000\u0002\u0000\u0002\u0000\u0000\u0000\u0006*+·\u0000\u0001±\u0000\u0000\u0000\u0000\u0000\u0001\u0000\t\u0000\n\u0000\u0002\u0000\b\u0000\u0000\u0000\u001b\u0000\u0006\u0000\u0006\u0000\u0000\u0000\u000f*´\u0000\u0002+,-\u0016\u0004¹\u0000\u0003\u0006\u0000°\u0000\u0000\u0000\u0000\u0000\u000b\u0000\u0000\u0000\u0004\u0000\u0001\u0000\f\u0000\u0000");
            
            @Override
            public Constructor<?> run() throws Exception {
                final Class<RMIConnector> clazz = RMIConnector.class;
                return new NoCallStackClassLoader("com.sun.jmx.remote.internal.PRef", this.val$pRefByteCode, new String[] { ProxyRef.class.getName() }, clazz.getClassLoader(), clazz.getProtectionDomain()).loadClass("com.sun.jmx.remote.internal.PRef").getConstructor(RemoteRef.class);
            }
        };
        Class<?> forName;
        try {
            forName = Class.forName(RMIConnector.rmiServerImplStubClassName);
        }
        catch (final Exception ex) {
            RMIConnector.logger.error("<clinit>", "Failed to instantiate " + RMIConnector.rmiServerImplStubClassName + ": " + ex);
            RMIConnector.logger.debug("<clinit>", ex);
            forName = null;
        }
        rmiServerImplStubClass = forName;
        Class<?> forName2;
        Constructor proxyRefConstructor2;
        try {
            forName2 = Class.forName(RMIConnector.rmiConnectionImplStubClassName);
            proxyRefConstructor2 = AccessController.doPrivileged((PrivilegedExceptionAction<Constructor>)privilegedExceptionAction);
        }
        catch (final Exception ex2) {
            RMIConnector.logger.error("<clinit>", "Failed to initialize proxy reference constructor for " + RMIConnector.rmiConnectionImplStubClassName + ": " + ex2);
            RMIConnector.logger.debug("<clinit>", ex2);
            forName2 = null;
            proxyRefConstructor2 = null;
        }
        rmiConnectionImplStubClass = forName2;
        proxyRefConstructor = proxyRefConstructor2;
        final byte[] stringToBytes = NoCallStackClassLoader.stringToBytes("\u00ca\u00feº¾\u0000\u0000\u00003\u0000+\n\u0000\f\u0000\u0018\u0007\u0000\u0019\n\u0000\f\u0000\u001a\n\u0000\u0002\u0000\u001b\u0007\u0000\u001c\n\u0000\u0005\u0000\u001d\n\u0000\u0005\u0000\u001e\n\u0000\u0005\u0000\u001f\n\u0000\u0002\u0000 \n\u0000\f\u0000!\u0007\u0000\"\u0007\u0000#\u0001\u0000\u0006<init>\u0001\u0000\u0003()V\u0001\u0000\u0004Code\u0001\u0000\u0007_invoke\u0001\u0000K(Lorg/omg/CORBA/portable/OutputStream;)Lorg/omg/CORBA/portable/InputStream;\u0001\u0000\rStackMapTable\u0007\u0000\u001c\u0001\u0000\nExceptions\u0007\u0000$\u0001\u0000\r_releaseReply\u0001\u0000'(Lorg/omg/CORBA/portable/InputStream;)V\f\u0000\r\u0000\u000e\u0001\u0000-com/sun/jmx/remote/protocol/iiop/PInputStream\f\u0000\u0010\u0000\u0011\f\u0000\r\u0000\u0017\u0001\u0000+org/omg/CORBA/portable/ApplicationException\f\u0000%\u0000&\f\u0000'\u0000(\f\u0000\r\u0000)\f\u0000*\u0000&\f\u0000\u0016\u0000\u0017\u0001\u0000*com/sun/jmx/remote/protocol/iiop/ProxyStub\u0001\u0000<org/omg/stub/javax/management/remote/rmi/_RMIConnection_Stub\u0001\u0000)org/omg/CORBA/portable/RemarshalException\u0001\u0000\u000egetInputStream\u0001\u0000&()Lorg/omg/CORBA/portable/InputStream;\u0001\u0000\u0005getId\u0001\u0000\u0014()Ljava/lang/String;\u0001\u00009(Ljava/lang/String;Lorg/omg/CORBA/portable/InputStream;)V\u0001\u0000\u0015getProxiedInputStream\u0000!\u0000\u000b\u0000\f\u0000\u0000\u0000\u0000\u0000\u0003\u0000\u0001\u0000\r\u0000\u000e\u0000\u0001\u0000\u000f\u0000\u0000\u0000\u0011\u0000\u0001\u0000\u0001\u0000\u0000\u0000\u0005*·\u0000\u0001±\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u0010\u0000\u0011\u0000\u0002\u0000\u000f\u0000\u0000\u0000G\u0000\u0004\u0000\u0004\u0000\u0000\u0000'»\u0000\u0002Y*+·\u0000\u0003·\u0000\u0004°M»\u0000\u0002Y,¶\u0000\u0006·\u0000\u0004N»\u0000\u0005Y,¶\u0000\u0007-·\u0000\b¿\u0000\u0001\u0000\u0000\u0000\f\u0000\r\u0000\u0005\u0000\u0001\u0000\u0012\u0000\u0000\u0000\u0006\u0000\u0001M\u0007\u0000\u0013\u0000\u0014\u0000\u0000\u0000\u0006\u0000\u0002\u0000\u0005\u0000\u0015\u0000\u0001\u0000\u0016\u0000\u0017\u0000\u0001\u0000\u000f\u0000\u0000\u0000'\u0000\u0002\u0000\u0002\u0000\u0000\u0000\u0012+\u00c6\u0000\u000b+\u00c0\u0000\u0002¶\u0000\tL*+·\u0000\n±\u0000\u0000\u0000\u0001\u0000\u0012\u0000\u0000\u0000\u0003\u0000\u0001\f\u0000\u0000");
        final byte[] stringToBytes2 = NoCallStackClassLoader.stringToBytes("\u00ca\u00feº¾\u0000\u0000\u00003\u0000\u001e\n\u0000\u0007\u0000\u000f\t\u0000\u0006\u0000\u0010\n\u0000\u0011\u0000\u0012\n\u0000\u0006\u0000\u0013\n\u0000\u0014\u0000\u0015\u0007\u0000\u0016\u0007\u0000\u0017\u0001\u0000\u0006<init>\u0001\u0000'(Lorg/omg/CORBA/portable/InputStream;)V\u0001\u0000\u0004Code\u0001\u0000\bread_any\u0001\u0000\u0015()Lorg/omg/CORBA/Any;\u0001\u0000\nread_value\u0001\u0000)(Ljava/lang/Class;)Ljava/io/Serializable;\f\u0000\b\u0000\t\f\u0000\u0018\u0000\u0019\u0007\u0000\u001a\f\u0000\u000b\u0000\f\f\u0000\u001b\u0000\u001c\u0007\u0000\u001d\f\u0000\r\u0000\u000e\u0001\u0000-com/sun/jmx/remote/protocol/iiop/PInputStream\u0001\u00001com/sun/jmx/remote/protocol/iiop/ProxyInputStream\u0001\u0000\u0002in\u0001\u0000$Lorg/omg/CORBA/portable/InputStream;\u0001\u0000\"org/omg/CORBA/portable/InputStream\u0001\u0000\u0006narrow\u0001\u0000*()Lorg/omg/CORBA_2_3/portable/InputStream;\u0001\u0000&org/omg/CORBA_2_3/portable/InputStream\u0000!\u0000\u0006\u0000\u0007\u0000\u0000\u0000\u0000\u0000\u0003\u0000\u0001\u0000\b\u0000\t\u0000\u0001\u0000\n\u0000\u0000\u0000\u0012\u0000\u0002\u0000\u0002\u0000\u0000\u0000\u0006*+·\u0000\u0001±\u0000\u0000\u0000\u0000\u0000\u0001\u0000\u000b\u0000\f\u0000\u0001\u0000\n\u0000\u0000\u0000\u0014\u0000\u0001\u0000\u0001\u0000\u0000\u0000\b*´\u0000\u0002¶\u0000\u0003°\u0000\u0000\u0000\u0000\u0000\u0001\u0000\r\u0000\u000e\u0000\u0001\u0000\n\u0000\u0000\u0000\u0015\u0000\u0002\u0000\u0002\u0000\u0000\u0000\t*¶\u0000\u0004+¶\u0000\u0005°\u0000\u0000\u0000\u0000\u0000\u0000");
        final String[] array = { "com.sun.jmx.remote.protocol.iiop.ProxyStub", "com.sun.jmx.remote.protocol.iiop.PInputStream" };
        final byte[][] array2 = { stringToBytes, stringToBytes2 };
        final String[] array3 = { "org.omg.stub.javax.management.remote.rmi._RMIConnection_Stub", "com.sun.jmx.remote.protocol.iiop.ProxyInputStream" };
        if (IIOPHelper.isAvailable()) {
            final PrivilegedExceptionAction<Class<?>> privilegedExceptionAction2 = new PrivilegedExceptionAction<Class<?>>() {
                @Override
                public Class<?> run() throws Exception {
                    final Class<RMIConnector> clazz = RMIConnector.class;
                    return new NoCallStackClassLoader(array, array2, array3, clazz.getClassLoader(), clazz.getProtectionDomain()).loadClass("com.sun.jmx.remote.protocol.iiop.ProxyStub");
                }
            };
            Class proxyStubClass2;
            try {
                proxyStubClass2 = AccessController.doPrivileged((PrivilegedExceptionAction<Class>)privilegedExceptionAction2);
            }
            catch (final Exception ex3) {
                RMIConnector.logger.error("<clinit>", "Unexpected exception making shadow IIOP stub class: " + ex3);
                RMIConnector.logger.debug("<clinit>", ex3);
                proxyStubClass2 = null;
            }
            proxyStubClass = proxyStubClass2;
        }
        else {
            proxyStubClass = null;
        }
        base64ToInt = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
        RMIConnector.orb = null;
    }
    
    private class RemoteMBeanServerConnection implements MBeanServerConnection
    {
        private Subject delegationSubject;
        
        public RemoteMBeanServerConnection(final RMIConnector rmiConnector) {
            this(rmiConnector, null);
        }
        
        public RemoteMBeanServerConnection(final Subject delegationSubject) {
            this.delegationSubject = delegationSubject;
        }
        
        @Override
        public ObjectInstance createMBean(final String s, final ObjectName objectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("createMBean(String,ObjectName)", "className=" + s + ", name=" + objectName);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.createMBean(s, objectName, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.createMBean(s, objectName, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName)", "className=" + s + ", name=" + objectName + ", loaderName=" + objectName2 + ")");
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.createMBean(s, objectName, objectName2, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.createMBean(s, objectName, objectName2, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public ObjectInstance createMBean(final String s, final ObjectName objectName, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("createMBean(String,ObjectName,Object[],String[])", "className=" + s + ", name=" + objectName + ", signature=" + strings(array2));
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)array);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.createMBean(s, objectName, marshalledObject, array2, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.createMBean(s, objectName, marshalledObject, array2, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "className=" + s + ", name=" + objectName + ", loaderName=" + objectName2 + ", signature=" + strings(array2));
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)array);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.createMBean(s, objectName, objectName2, marshalledObject, array2, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.createMBean(s, objectName, objectName2, marshalledObject, array2, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public void unregisterMBean(final ObjectName objectName) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("unregisterMBean", "name=" + objectName);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                RMIConnector.this.connection.unregisterMBean(objectName, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.unregisterMBean(objectName, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public ObjectInstance getObjectInstance(final ObjectName objectName) throws InstanceNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("getObjectInstance", "name=" + objectName);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.getObjectInstance(objectName, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.getObjectInstance(objectName, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public Set<ObjectInstance> queryMBeans(final ObjectName objectName, final QueryExp queryExp) throws IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("queryMBeans", "name=" + objectName + ", query=" + queryExp);
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)queryExp);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.queryMBeans(objectName, marshalledObject, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.queryMBeans(objectName, marshalledObject, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public Set<ObjectName> queryNames(final ObjectName objectName, final QueryExp queryExp) throws IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("queryNames", "name=" + objectName + ", query=" + queryExp);
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)queryExp);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.queryNames(objectName, marshalledObject, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.queryNames(objectName, marshalledObject, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public boolean isRegistered(final ObjectName objectName) throws IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("isRegistered", "name=" + objectName);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.isRegistered(objectName, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.isRegistered(objectName, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public Integer getMBeanCount() throws IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("getMBeanCount", "");
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.getMBeanCount(this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public Object getAttribute(final ObjectName objectName, final String s) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("getAttribute", "name=" + objectName + ", attribute=" + s);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.getAttribute(objectName, s, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.getAttribute(objectName, s, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public AttributeList getAttributes(final ObjectName objectName, final String[] array) throws InstanceNotFoundException, ReflectionException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("getAttributes", "name=" + objectName + ", attributes=" + strings(array));
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.getAttributes(objectName, array, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.getAttributes(objectName, array, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public void setAttribute(final ObjectName objectName, final Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("setAttribute", "name=" + objectName + ", attribute name=" + attribute.getName());
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)attribute);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                RMIConnector.this.connection.setAttribute(objectName, marshalledObject, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.setAttribute(objectName, marshalledObject, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public AttributeList setAttributes(final ObjectName objectName, final AttributeList list) throws InstanceNotFoundException, ReflectionException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("setAttributes", "name=" + objectName + ", attribute names=" + RMIConnector.getAttributesNames(list));
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)list);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.setAttributes(objectName, marshalledObject, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.setAttributes(objectName, marshalledObject, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public Object invoke(final ObjectName objectName, final String s, final Object[] array, final String[] array2) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("invoke", "name=" + objectName + ", operationName=" + s + ", signature=" + strings(array2));
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)array);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.invoke(objectName, s, marshalledObject, array2, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.invoke(objectName, s, marshalledObject, array2, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public String getDefaultDomain() throws IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("getDefaultDomain", "");
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.getDefaultDomain(this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public String[] getDomains() throws IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("getDomains", "");
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.getDomains(this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.getDomains(this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public MBeanInfo getMBeanInfo(final ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("getMBeanInfo", "name=" + objectName);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.getMBeanInfo(objectName, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.getMBeanInfo(objectName, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public boolean isInstanceOf(final ObjectName objectName, final String s) throws InstanceNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("isInstanceOf", "name=" + objectName + ", className=" + s);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                return RMIConnector.this.connection.isInstanceOf(objectName, s, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                return RMIConnector.this.connection.isInstanceOf(objectName, s, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public void addNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + objectName + ", listener=" + objectName2 + ", filter=" + notificationFilter + ", handback=" + o);
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)notificationFilter);
            final MarshalledObject marshalledObject2 = new MarshalledObject((T)o);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                RMIConnector.this.connection.addNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.addNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName)", "name=" + objectName + ", listener=" + objectName2);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                RMIConnector.this.connection.removeNotificationListener(objectName, objectName2, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.removeNotificationListener(objectName, objectName2, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "name=" + objectName + ", listener=" + objectName2 + ", filter=" + notificationFilter + ", handback=" + o);
            }
            final MarshalledObject marshalledObject = new MarshalledObject((T)notificationFilter);
            final MarshalledObject marshalledObject2 = new MarshalledObject((T)o);
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                RMIConnector.this.connection.removeNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.removeNotificationListener(objectName, objectName2, marshalledObject, marshalledObject2, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public void addNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("addNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + objectName + ", listener=" + notificationListener + ", filter=" + notificationFilter + ", handback=" + o);
            }
            RMIConnector.this.rmiNotifClient.addNotificationListener(RMIConnector.this.addListenerWithSubject(objectName, new MarshalledObject((T)notificationFilter), this.delegationSubject, true), objectName, notificationListener, notificationFilter, o, this.delegationSubject);
        }
        
        @Override
        public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
            final boolean debugOn = RMIConnector.logger.debugOn();
            if (debugOn) {
                RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener)", "name=" + objectName + ", listener=" + notificationListener);
            }
            final Integer[] removeNotificationListener = RMIConnector.this.rmiNotifClient.removeNotificationListener(objectName, notificationListener);
            if (debugOn) {
                RMIConnector.logger.debug("removeNotificationListener", "listenerIDs=" + objects(removeNotificationListener));
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                RMIConnector.this.connection.removeNotificationListeners(objectName, removeNotificationListener, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.removeNotificationListeners(objectName, removeNotificationListener, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
        
        @Override
        public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
            final boolean debugOn = RMIConnector.logger.debugOn();
            if (debugOn) {
                RMIConnector.logger.debug("removeNotificationListener(ObjectName,NotificationListener,NotificationFilter,Object)", "name=" + objectName + ", listener=" + notificationListener + ", filter=" + notificationFilter + ", handback=" + o);
            }
            final Integer removeNotificationListener = RMIConnector.this.rmiNotifClient.removeNotificationListener(objectName, notificationListener, notificationFilter, o);
            if (debugOn) {
                RMIConnector.logger.debug("removeNotificationListener", "listenerID=" + removeNotificationListener);
            }
            final ClassLoader access$100 = RMIConnector.this.pushDefaultClassLoader();
            try {
                RMIConnector.this.connection.removeNotificationListeners(objectName, new Integer[] { removeNotificationListener }, this.delegationSubject);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.removeNotificationListeners(objectName, new Integer[] { removeNotificationListener }, this.delegationSubject);
            }
            finally {
                RMIConnector.this.popDefaultClassLoader(access$100);
            }
        }
    }
    
    private class RMINotifClient extends ClientNotifForwarder
    {
        public RMINotifClient(final ClassLoader classLoader, final Map<String, ?> map) {
            super(classLoader, map);
        }
        
        @Override
        protected NotificationResult fetchNotifs(final long n, final int n2, final long n3) throws IOException, ClassNotFoundException {
            int n4 = 0;
            try {
                return RMIConnector.this.connection.fetchNotifications(n, n2, n3);
            }
            catch (final IOException ex) {
                this.rethrowDeserializationException(ex);
                try {
                    RMIConnector.this.communicatorAdmin.gotIOException(ex);
                }
                catch (final IOException ex2) {
                    boolean b = false;
                    synchronized (this) {
                        if (RMIConnector.this.terminated) {
                            throw ex;
                        }
                        if (n4 != 0) {
                            b = true;
                        }
                    }
                    if (b) {
                        RMIConnector.this.sendNotification(new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifSeqNo++, "Failed to communicate with the server: " + ex.toString(), ex));
                        try {
                            RMIConnector.this.close(true);
                        }
                        catch (final Exception ex3) {}
                        throw ex;
                    }
                    n4 = 1;
                }
                return RMIConnector.this.connection.fetchNotifications(n, n2, n3);
            }
        }
        
        private void rethrowDeserializationException(final IOException ex) throws ClassNotFoundException, IOException {
            if (ex instanceof UnmarshalException) {
                throw ex;
            }
            if (ex instanceof MarshalException) {
                final MarshalException ex2 = (MarshalException)ex;
                if (ex2.detail instanceof NotSerializableException) {
                    throw (NotSerializableException)ex2.detail;
                }
            }
        }
        
        @Override
        protected Integer addListenerForMBeanRemovedNotif() throws IOException, InstanceNotFoundException {
            final NotificationFilterSupport notificationFilterSupport = new NotificationFilterSupport();
            notificationFilterSupport.enableType("JMX.mbean.unregistered");
            final MarshalledObject marshalledObject = new MarshalledObject(notificationFilterSupport);
            final ObjectName[] array = { MBeanServerDelegate.DELEGATE_NAME };
            final MarshalledObject[] array2 = Util.cast(new MarshalledObject[] { marshalledObject });
            final Subject[] array3 = { null };
            Integer[] array4;
            try {
                array4 = RMIConnector.this.connection.addNotificationListeners(array, array2, array3);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                array4 = RMIConnector.this.connection.addNotificationListeners(array, array2, array3);
            }
            return array4[0];
        }
        
        @Override
        protected void removeListenerForMBeanRemovedNotif(final Integer n) throws IOException, InstanceNotFoundException, ListenerNotFoundException {
            try {
                RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { n }, null);
            }
            catch (final IOException ex) {
                RMIConnector.this.communicatorAdmin.gotIOException(ex);
                RMIConnector.this.connection.removeNotificationListeners(MBeanServerDelegate.DELEGATE_NAME, new Integer[] { n }, null);
            }
        }
        
        @Override
        protected void lostNotifs(final String s, final long n) {
            RMIConnector.this.sendNotification(new JMXConnectionNotification("jmx.remote.connection.notifs.lost", RMIConnector.this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifCounter++, s, n));
        }
    }
    
    private class RMIClientCommunicatorAdmin extends ClientCommunicatorAdmin
    {
        public RMIClientCommunicatorAdmin(final long n) {
            super(n);
        }
        
        @Override
        public void gotIOException(final IOException ex) throws IOException {
            if (ex instanceof NoSuchObjectException) {
                super.gotIOException(ex);
                return;
            }
            try {
                RMIConnector.this.connection.getDefaultDomain(null);
            }
            catch (final IOException ex2) {
                boolean b = false;
                synchronized (this) {
                    if (!RMIConnector.this.terminated) {
                        RMIConnector.this.terminated = true;
                        b = true;
                    }
                }
                if (b) {
                    RMIConnector.this.sendNotification(new JMXConnectionNotification("jmx.remote.connection.failed", this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifSeqNo++, "Failed to communicate with the server: " + ex.toString(), ex));
                    try {
                        RMIConnector.this.close(true);
                    }
                    catch (final Exception ex3) {}
                }
            }
            if (ex instanceof ServerException) {
                final Throwable detail = ((ServerException)ex).detail;
                if (detail instanceof IOException) {
                    throw (IOException)detail;
                }
                if (detail instanceof RuntimeException) {
                    throw (RuntimeException)detail;
                }
            }
            throw ex;
        }
        
        public void reconnectNotificationListeners(final ClientListenerInfo[] array) throws IOException {
            final int length = array.length;
            ClientListenerInfo[] array2 = new ClientListenerInfo[length];
            final Subject[] array3 = new Subject[length];
            final ObjectName[] array4 = new ObjectName[length];
            final NotificationListener[] array5 = new NotificationListener[length];
            final NotificationFilter[] array6 = new NotificationFilter[length];
            final MarshalledObject[] array7 = Util.cast(new MarshalledObject[length]);
            final Object[] array8 = new Object[length];
            for (int i = 0; i < length; ++i) {
                array3[i] = array[i].getDelegationSubject();
                array4[i] = array[i].getObjectName();
                array5[i] = array[i].getListener();
                array6[i] = array[i].getNotificationFilter();
                array7[i] = new MarshalledObject(array6[i]);
                array8[i] = array[i].getHandback();
            }
            try {
                final Integer[] access$1500 = RMIConnector.this.addListenersWithSubjects(array4, array7, array3, false);
                for (int j = 0; j < length; ++j) {
                    array2[j] = new ClientListenerInfo(access$1500[j], array4[j], array5[j], array6[j], array8[j], array3[j]);
                }
                RMIConnector.this.rmiNotifClient.postReconnection(array2);
            }
            catch (final InstanceNotFoundException ex) {
                int n = 0;
                for (int k = 0; k < length; ++k) {
                    try {
                        array2[n++] = new ClientListenerInfo(RMIConnector.this.addListenerWithSubject(array4[k], new MarshalledObject(array6[k]), array3[k], false), array4[k], array5[k], array6[k], array8[k], array3[k]);
                    }
                    catch (final InstanceNotFoundException ex2) {
                        RMIConnector.logger.warning("reconnectNotificationListeners", "Can't reconnect listener for " + array4[k]);
                    }
                }
                if (n != length) {
                    final ClientListenerInfo[] array9 = array2;
                    array2 = new ClientListenerInfo[n];
                    System.arraycopy(array9, 0, array2, 0, n);
                }
                RMIConnector.this.rmiNotifClient.postReconnection(array2);
            }
        }
        
        @Override
        protected void checkConnection() throws IOException {
            if (RMIConnector.logger.debugOn()) {
                RMIConnector.logger.debug("RMIClientCommunicatorAdmin-checkConnection", "Calling the method getDefaultDomain.");
            }
            RMIConnector.this.connection.getDefaultDomain(null);
        }
        
        @Override
        protected void doStart() throws IOException {
            RMIServer rmiServer;
            try {
                rmiServer = ((RMIConnector.this.rmiServer != null) ? RMIConnector.this.rmiServer : RMIConnector.this.findRMIServer(RMIConnector.this.jmxServiceURL, RMIConnector.this.env));
            }
            catch (final NamingException ex) {
                throw new IOException("Failed to get a RMI stub: " + ex);
            }
            RMIConnector.this.connection = RMIConnector.connectStub(rmiServer, RMIConnector.this.env).newClient(RMIConnector.this.env.get("jmx.remote.credentials"));
            this.reconnectNotificationListeners(RMIConnector.this.rmiNotifClient.preReconnection());
            RMIConnector.this.connectionId = RMIConnector.this.getConnectionId();
            RMIConnector.this.sendNotification(new JMXConnectionNotification("jmx.remote.connection.opened", this, RMIConnector.this.connectionId, RMIConnector.this.clientNotifSeqNo++, "Reconnected to server", null));
        }
        
        @Override
        protected void doStop() {
            try {
                RMIConnector.this.close();
            }
            catch (final IOException ex) {
                RMIConnector.logger.warning("RMIClientCommunicatorAdmin-doStop", "Failed to call the method close():" + ex);
                RMIConnector.logger.debug("RMIClientCommunicatorAdmin-doStop", ex);
            }
        }
    }
    
    private static final class ObjectInputStreamWithLoader extends ObjectInputStream
    {
        private final ClassLoader loader;
        
        ObjectInputStreamWithLoader(final InputStream inputStream, final ClassLoader loader) throws IOException {
            super(inputStream);
            this.loader = loader;
        }
        
        @Override
        protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
            final String name = objectStreamClass.getName();
            ReflectUtil.checkPackageAccess(name);
            return Class.forName(name, false, this.loader);
        }
    }
}
