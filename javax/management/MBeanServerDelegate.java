package javax.management;

import com.sun.jmx.mbeanserver.Util;
import java.net.UnknownHostException;
import com.sun.jmx.defaults.JmxProperties;
import java.net.InetAddress;

public class MBeanServerDelegate implements MBeanServerDelegateMBean, NotificationEmitter
{
    private String mbeanServerId;
    private final NotificationBroadcasterSupport broadcaster;
    private static long oldStamp;
    private final long stamp;
    private long sequenceNumber;
    private static final MBeanNotificationInfo[] notifsInfo;
    public static final ObjectName DELEGATE_NAME;
    
    public MBeanServerDelegate() {
        this.sequenceNumber = 1L;
        this.stamp = getStamp();
        this.broadcaster = new NotificationBroadcasterSupport();
    }
    
    @Override
    public synchronized String getMBeanServerId() {
        if (this.mbeanServerId == null) {
            String hostName;
            try {
                hostName = InetAddress.getLocalHost().getHostName();
            }
            catch (final UnknownHostException ex) {
                JmxProperties.MISC_LOGGER.finest("Can't get local host name, using \"localhost\" instead. Cause is: " + ex);
                hostName = "localhost";
            }
            this.mbeanServerId = hostName + "_" + this.stamp;
        }
        return this.mbeanServerId;
    }
    
    @Override
    public String getSpecificationName() {
        return "Java Management Extensions";
    }
    
    @Override
    public String getSpecificationVersion() {
        return "1.4";
    }
    
    @Override
    public String getSpecificationVendor() {
        return "Oracle Corporation";
    }
    
    @Override
    public String getImplementationName() {
        return "JMX";
    }
    
    @Override
    public String getImplementationVersion() {
        try {
            return System.getProperty("java.runtime.version");
        }
        catch (final SecurityException ex) {
            return "";
        }
    }
    
    @Override
    public String getImplementationVendor() {
        return "Oracle Corporation";
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        final int length = MBeanServerDelegate.notifsInfo.length;
        final MBeanNotificationInfo[] array = new MBeanNotificationInfo[length];
        System.arraycopy(MBeanServerDelegate.notifsInfo, 0, array, 0, length);
        return array;
    }
    
    @Override
    public synchronized void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws IllegalArgumentException {
        this.broadcaster.addNotificationListener(notificationListener, notificationFilter, o);
    }
    
    @Override
    public synchronized void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(notificationListener, notificationFilter, o);
    }
    
    @Override
    public synchronized void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        this.broadcaster.removeNotificationListener(notificationListener);
    }
    
    public void sendNotification(final Notification notification) {
        if (notification.getSequenceNumber() < 1L) {
            synchronized (this) {
                notification.setSequenceNumber(this.sequenceNumber++);
            }
        }
        this.broadcaster.sendNotification(notification);
    }
    
    private static synchronized long getStamp() {
        long currentTimeMillis = System.currentTimeMillis();
        if (MBeanServerDelegate.oldStamp >= currentTimeMillis) {
            currentTimeMillis = MBeanServerDelegate.oldStamp + 1L;
        }
        return MBeanServerDelegate.oldStamp = currentTimeMillis;
    }
    
    static {
        MBeanServerDelegate.oldStamp = 0L;
        (notifsInfo = new MBeanNotificationInfo[1])[0] = new MBeanNotificationInfo(new String[] { "JMX.mbean.unregistered", "JMX.mbean.registered" }, "javax.management.MBeanServerNotification", "Notifications sent by the MBeanServerDelegate MBean");
        DELEGATE_NAME = Util.newObjectName("JMImplementation:type=MBeanServerDelegate");
    }
}
