package javax.management.remote.rmi;

import com.sun.jmx.remote.internal.ArrayNotificationBuffer;
import java.util.Set;
import java.security.Principal;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.RemoteServer;
import java.util.Iterator;
import javax.security.auth.Subject;
import com.sun.jmx.remote.security.JMXPluggableAuthenticator;
import javax.management.remote.JMXAuthenticator;
import java.rmi.Remote;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import com.sun.jmx.remote.internal.NotificationBuffer;
import java.util.Map;
import javax.management.MBeanServer;
import java.lang.ref.WeakReference;
import java.util.List;
import com.sun.jmx.remote.util.ClassLogger;
import java.io.Closeable;

public abstract class RMIServerImpl implements Closeable, RMIServer
{
    private static final ClassLogger logger;
    private final List<WeakReference<RMIConnection>> clientList;
    private ClassLoader cl;
    private MBeanServer mbeanServer;
    private final Map<String, ?> env;
    private RMIConnectorServer connServer;
    private static int connectionIdNumber;
    private NotificationBuffer notifBuffer;
    
    public RMIServerImpl(final Map<String, ?> map) {
        this.clientList = new ArrayList<WeakReference<RMIConnection>>();
        this.env = ((map == null) ? Collections.emptyMap() : map);
    }
    
    void setRMIConnectorServer(final RMIConnectorServer connServer) throws IOException {
        this.connServer = connServer;
    }
    
    protected abstract void export() throws IOException;
    
    public abstract Remote toStub() throws IOException;
    
    public synchronized void setDefaultClassLoader(final ClassLoader cl) {
        this.cl = cl;
    }
    
    public synchronized ClassLoader getDefaultClassLoader() {
        return this.cl;
    }
    
    public synchronized void setMBeanServer(final MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }
    
    public synchronized MBeanServer getMBeanServer() {
        return this.mbeanServer;
    }
    
    @Override
    public String getVersion() {
        try {
            return "1.0 java_runtime_" + System.getProperty("java.runtime.version");
        }
        catch (final SecurityException ex) {
            return "1.0 ";
        }
    }
    
    @Override
    public RMIConnection newClient(final Object o) throws IOException {
        return this.doNewClient(o);
    }
    
    RMIConnection doNewClient(final Object o) throws IOException {
        final boolean traceOn = RMIServerImpl.logger.traceOn();
        if (traceOn) {
            RMIServerImpl.logger.trace("newClient", "making new client");
        }
        if (this.getMBeanServer() == null) {
            throw new IllegalStateException("Not attached to an MBean server");
        }
        Subject authenticate = null;
        JMXAuthenticator jmxAuthenticator = (JMXAuthenticator)this.env.get("jmx.remote.authenticator");
        if (jmxAuthenticator == null && (this.env.get("jmx.remote.x.password.file") != null || this.env.get("jmx.remote.x.login.config") != null)) {
            jmxAuthenticator = new JMXPluggableAuthenticator(this.env);
        }
        if (jmxAuthenticator != null) {
            if (traceOn) {
                RMIServerImpl.logger.trace("newClient", "got authenticator: " + jmxAuthenticator.getClass().getName());
            }
            try {
                authenticate = jmxAuthenticator.authenticate(o);
            }
            catch (final SecurityException ex) {
                RMIServerImpl.logger.trace("newClient", "Authentication failed: " + ex);
                throw ex;
            }
        }
        if (traceOn) {
            if (authenticate != null) {
                RMIServerImpl.logger.trace("newClient", "subject is not null");
            }
            else {
                RMIServerImpl.logger.trace("newClient", "no subject");
            }
        }
        final String connectionId = makeConnectionId(this.getProtocol(), authenticate);
        if (traceOn) {
            RMIServerImpl.logger.trace("newClient", "making new connection: " + connectionId);
        }
        final RMIConnection client = this.makeClient(connectionId, authenticate);
        this.dropDeadReferences();
        final WeakReference weakReference = new WeakReference<RMIConnection>(client);
        synchronized (this.clientList) {
            this.clientList.add((WeakReference<RMIConnection>)weakReference);
        }
        this.connServer.connectionOpened(connectionId, "Connection opened", null);
        synchronized (this.clientList) {
            if (!this.clientList.contains(weakReference)) {
                throw new IOException("The connection is refused.");
            }
        }
        if (traceOn) {
            RMIServerImpl.logger.trace("newClient", "new connection done: " + connectionId);
        }
        return client;
    }
    
    protected abstract RMIConnection makeClient(final String p0, final Subject p1) throws IOException;
    
    protected abstract void closeClient(final RMIConnection p0) throws IOException;
    
    protected abstract String getProtocol();
    
    protected void clientClosed(final RMIConnection rmiConnection) throws IOException {
        final boolean debugOn = RMIServerImpl.logger.debugOn();
        if (debugOn) {
            RMIServerImpl.logger.trace("clientClosed", "client=" + rmiConnection);
        }
        if (rmiConnection == null) {
            throw new NullPointerException("Null client");
        }
        synchronized (this.clientList) {
            this.dropDeadReferences();
            final Iterator<WeakReference<RMIConnection>> iterator = this.clientList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().get() == rmiConnection) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (debugOn) {
            RMIServerImpl.logger.trace("clientClosed", "closing client.");
        }
        this.closeClient(rmiConnection);
        if (debugOn) {
            RMIServerImpl.logger.trace("clientClosed", "sending notif");
        }
        this.connServer.connectionClosed(rmiConnection.getConnectionId(), "Client connection closed", null);
        if (debugOn) {
            RMIServerImpl.logger.trace("clientClosed", "done");
        }
    }
    
    @Override
    public synchronized void close() throws IOException {
        final boolean traceOn = RMIServerImpl.logger.traceOn();
        final boolean debugOn = RMIServerImpl.logger.debugOn();
        if (traceOn) {
            RMIServerImpl.logger.trace("close", "closing");
        }
        IOException ex = null;
        try {
            if (debugOn) {
                RMIServerImpl.logger.debug("close", "closing Server");
            }
            this.closeServer();
        }
        catch (final IOException ex2) {
            if (traceOn) {
                RMIServerImpl.logger.trace("close", "Failed to close server: " + ex2);
            }
            if (debugOn) {
                RMIServerImpl.logger.debug("close", ex2);
            }
            ex = ex2;
        }
        if (debugOn) {
            RMIServerImpl.logger.debug("close", "closing Clients");
        }
        while (true) {
            synchronized (this.clientList) {
                if (debugOn) {
                    RMIServerImpl.logger.debug("close", "droping dead references");
                }
                this.dropDeadReferences();
                if (debugOn) {
                    RMIServerImpl.logger.debug("close", "client count: " + this.clientList.size());
                }
                if (this.clientList.size() == 0) {
                    break;
                }
                final Iterator<WeakReference<RMIConnection>> iterator = this.clientList.iterator();
                while (iterator.hasNext()) {
                    final RMIConnection rmiConnection = iterator.next().get();
                    iterator.remove();
                    if (rmiConnection != null) {
                        try {
                            rmiConnection.close();
                        }
                        catch (final IOException ex3) {
                            if (traceOn) {
                                RMIServerImpl.logger.trace("close", "Failed to close client: " + ex3);
                            }
                            if (debugOn) {
                                RMIServerImpl.logger.debug("close", ex3);
                            }
                            if (ex == null) {
                                ex = ex3;
                            }
                        }
                        break;
                    }
                }
            }
        }
        if (this.notifBuffer != null) {
            this.notifBuffer.dispose();
        }
        if (ex != null) {
            if (traceOn) {
                RMIServerImpl.logger.trace("close", "close failed.");
            }
            throw ex;
        }
        if (traceOn) {
            RMIServerImpl.logger.trace("close", "closed.");
        }
    }
    
    protected abstract void closeServer() throws IOException;
    
    private static synchronized String makeConnectionId(final String s, final Subject subject) {
        ++RMIServerImpl.connectionIdNumber;
        String s2 = "";
        try {
            s2 = RemoteServer.getClientHost();
            if (s2.contains(":")) {
                s2 = "[" + s2 + "]";
            }
        }
        catch (final ServerNotActiveException ex) {
            RMIServerImpl.logger.trace("makeConnectionId", "getClientHost", ex);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(s).append(":");
        if (s2.length() > 0) {
            sb.append("//").append(s2);
        }
        sb.append(" ");
        if (subject != null) {
            final Set<Principal> principals = subject.getPrincipals();
            String s3 = "";
            final Iterator<Principal> iterator = principals.iterator();
            while (iterator.hasNext()) {
                sb.append(s3).append(iterator.next().getName().replace(' ', '_').replace(';', ':'));
                s3 = ";";
            }
        }
        sb.append(" ").append(RMIServerImpl.connectionIdNumber);
        if (RMIServerImpl.logger.traceOn()) {
            RMIServerImpl.logger.trace("newConnectionId", "connectionId=" + (Object)sb);
        }
        return sb.toString();
    }
    
    private void dropDeadReferences() {
        synchronized (this.clientList) {
            final Iterator<WeakReference<RMIConnection>> iterator = this.clientList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().get() == null) {
                    iterator.remove();
                }
            }
        }
    }
    
    synchronized NotificationBuffer getNotifBuffer() {
        if (this.notifBuffer == null) {
            this.notifBuffer = ArrayNotificationBuffer.getNotificationBuffer(this.mbeanServer, this.env);
        }
        return this.notifBuffer;
    }
    
    static {
        logger = new ClassLogger("javax.management.remote.rmi", "RMIServerImpl");
    }
}
