package sun.management;

import javax.management.ObjectName;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import com.sun.management.GarbageCollectionNotificationInfo;
import javax.management.Notification;
import javax.management.MBeanNotificationInfo;
import com.sun.management.GcInfo;
import java.util.Iterator;
import java.util.List;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.ManagementFactory;
import com.sun.management.GarbageCollectorMXBean;

class GarbageCollectorImpl extends MemoryManagerImpl implements GarbageCollectorMXBean
{
    private String[] poolNames;
    private GcInfoBuilder gcInfoBuilder;
    private static final String notifName = "javax.management.Notification";
    private static final String[] gcNotifTypes;
    private static long seqNumber;
    
    GarbageCollectorImpl(final String s) {
        super(s);
        this.poolNames = null;
    }
    
    @Override
    public native long getCollectionCount();
    
    @Override
    public native long getCollectionTime();
    
    synchronized String[] getAllPoolNames() {
        if (this.poolNames == null) {
            final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            this.poolNames = new String[memoryPoolMXBeans.size()];
            int n = 0;
            final Iterator iterator = memoryPoolMXBeans.iterator();
            while (iterator.hasNext()) {
                this.poolNames[n++] = ((MemoryPoolMXBean)iterator.next()).getName();
            }
        }
        return this.poolNames;
    }
    
    private synchronized GcInfoBuilder getGcInfoBuilder() {
        if (this.gcInfoBuilder == null) {
            this.gcInfoBuilder = new GcInfoBuilder(this, this.getAllPoolNames());
        }
        return this.gcInfoBuilder;
    }
    
    @Override
    public GcInfo getLastGcInfo() {
        return this.getGcInfoBuilder().getLastGcInfo();
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[] { new MBeanNotificationInfo(GarbageCollectorImpl.gcNotifTypes, "javax.management.Notification", "GC Notification") };
    }
    
    private static long getNextSeqNumber() {
        return ++GarbageCollectorImpl.seqNumber;
    }
    
    void createGCNotification(final long n, final String s, final String s2, final String s3, final GcInfo gcInfo) {
        if (!this.hasListeners()) {
            return;
        }
        final Notification notification = new Notification("com.sun.management.gc.notification", this.getObjectName(), getNextSeqNumber(), n, s);
        notification.setUserData(GarbageCollectionNotifInfoCompositeData.toCompositeData(new GarbageCollectionNotificationInfo(s, s2, s3, gcInfo)));
        this.sendNotification(notification);
    }
    
    @Override
    public synchronized void addNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) {
        final boolean hasListeners = this.hasListeners();
        super.addNotificationListener(notificationListener, notificationFilter, o);
        final boolean hasListeners2 = this.hasListeners();
        if (!hasListeners && hasListeners2) {
            this.setNotificationEnabled(this, true);
        }
    }
    
    @Override
    public synchronized void removeNotificationListener(final NotificationListener notificationListener) throws ListenerNotFoundException {
        final boolean hasListeners = this.hasListeners();
        super.removeNotificationListener(notificationListener);
        final boolean hasListeners2 = this.hasListeners();
        if (hasListeners && !hasListeners2) {
            this.setNotificationEnabled(this, false);
        }
    }
    
    @Override
    public synchronized void removeNotificationListener(final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws ListenerNotFoundException {
        final boolean hasListeners = this.hasListeners();
        super.removeNotificationListener(notificationListener, notificationFilter, o);
        final boolean hasListeners2 = this.hasListeners();
        if (hasListeners && !hasListeners2) {
            this.setNotificationEnabled(this, false);
        }
    }
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=GarbageCollector", this.getName());
    }
    
    native void setNotificationEnabled(final GarbageCollectorMXBean p0, final boolean p1);
    
    static {
        gcNotifTypes = new String[] { "com.sun.management.gc.notification" };
        GarbageCollectorImpl.seqNumber = 0L;
    }
}
