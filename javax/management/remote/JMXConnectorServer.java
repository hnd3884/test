package javax.management.remote;

import javax.management.Notification;
import javax.management.MBeanNotificationInfo;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanRegistration;
import javax.management.NotificationBroadcasterSupport;

public abstract class JMXConnectorServer extends NotificationBroadcasterSupport implements JMXConnectorServerMBean, MBeanRegistration, JMXAddressable
{
    public static final String AUTHENTICATOR = "jmx.remote.authenticator";
    private MBeanServer mbeanServer;
    private ObjectName myName;
    private final List<String> connectionIds;
    private static final int[] sequenceNumberLock;
    private static long sequenceNumber;
    
    public JMXConnectorServer() {
        this((MBeanServer)null);
    }
    
    public JMXConnectorServer(final MBeanServer mbeanServer) {
        this.mbeanServer = null;
        this.connectionIds = new ArrayList<String>();
        this.mbeanServer = mbeanServer;
    }
    
    public synchronized MBeanServer getMBeanServer() {
        return this.mbeanServer;
    }
    
    @Override
    public synchronized void setMBeanServerForwarder(final MBeanServerForwarder mbeanServer) {
        if (mbeanServer == null) {
            throw new IllegalArgumentException("Invalid null argument: mbsf");
        }
        if (this.mbeanServer != null) {
            mbeanServer.setMBeanServer(this.mbeanServer);
        }
        this.mbeanServer = mbeanServer;
    }
    
    @Override
    public String[] getConnectionIds() {
        synchronized (this.connectionIds) {
            return this.connectionIds.toArray(new String[this.connectionIds.size()]);
        }
    }
    
    @Override
    public JMXConnector toJMXConnector(final Map<String, ?> map) throws IOException {
        if (!this.isActive()) {
            throw new IllegalStateException("Connector is not active");
        }
        return JMXConnectorFactory.newJMXConnector(this.getAddress(), map);
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[] { new MBeanNotificationInfo(new String[] { "jmx.remote.connection.opened", "jmx.remote.connection.closed", "jmx.remote.connection.failed" }, JMXConnectionNotification.class.getName(), "A client connection has been opened or closed") };
    }
    
    protected void connectionOpened(final String s, final String s2, final Object o) {
        if (s == null) {
            throw new NullPointerException("Illegal null argument");
        }
        synchronized (this.connectionIds) {
            this.connectionIds.add(s);
        }
        this.sendNotification("jmx.remote.connection.opened", s, s2, o);
    }
    
    protected void connectionClosed(final String s, final String s2, final Object o) {
        if (s == null) {
            throw new NullPointerException("Illegal null argument");
        }
        synchronized (this.connectionIds) {
            this.connectionIds.remove(s);
        }
        this.sendNotification("jmx.remote.connection.closed", s, s2, o);
    }
    
    protected void connectionFailed(final String s, final String s2, final Object o) {
        if (s == null) {
            throw new NullPointerException("Illegal null argument");
        }
        synchronized (this.connectionIds) {
            this.connectionIds.remove(s);
        }
        this.sendNotification("jmx.remote.connection.failed", s, s2, o);
    }
    
    private void sendNotification(final String s, final String s2, final String s3, final Object o) {
        this.sendNotification(new JMXConnectionNotification(s, this.getNotificationSource(), s2, nextSequenceNumber(), s3, o));
    }
    
    private synchronized Object getNotificationSource() {
        if (this.myName != null) {
            return this.myName;
        }
        return this;
    }
    
    private static long nextSequenceNumber() {
        synchronized (JMXConnectorServer.sequenceNumberLock) {
            return JMXConnectorServer.sequenceNumber++;
        }
    }
    
    @Override
    public synchronized ObjectName preRegister(final MBeanServer mbeanServer, final ObjectName myName) {
        if (mbeanServer == null || myName == null) {
            throw new NullPointerException("Null MBeanServer or ObjectName");
        }
        if (this.mbeanServer == null) {
            this.mbeanServer = mbeanServer;
            this.myName = myName;
        }
        return myName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
    }
    
    @Override
    public synchronized void preDeregister() throws Exception {
        if (this.myName != null && this.isActive()) {
            this.stop();
            this.myName = null;
        }
    }
    
    @Override
    public void postDeregister() {
        this.myName = null;
    }
    
    static {
        sequenceNumberLock = new int[0];
    }
}
