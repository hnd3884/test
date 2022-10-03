package sun.management;

import javax.management.ObjectName;
import java.lang.management.MemoryNotificationInfo;
import javax.management.Notification;
import java.lang.management.ManagementFactory;
import javax.management.MBeanNotificationInfo;
import java.lang.management.MemoryUsage;
import sun.misc.VM;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryMXBean;

class MemoryImpl extends NotificationEmitterSupport implements MemoryMXBean
{
    private final VMManagement jvm;
    private static MemoryPoolMXBean[] pools;
    private static MemoryManagerMXBean[] mgrs;
    private static final String notifName = "javax.management.Notification";
    private static final String[] notifTypes;
    private static final String[] notifMsgs;
    private static long seqNumber;
    
    MemoryImpl(final VMManagement jvm) {
        this.jvm = jvm;
    }
    
    @Override
    public int getObjectPendingFinalizationCount() {
        return VM.getFinalRefCount();
    }
    
    @Override
    public void gc() {
        Runtime.getRuntime().gc();
    }
    
    @Override
    public MemoryUsage getHeapMemoryUsage() {
        return this.getMemoryUsage0(true);
    }
    
    @Override
    public MemoryUsage getNonHeapMemoryUsage() {
        return this.getMemoryUsage0(false);
    }
    
    @Override
    public boolean isVerbose() {
        return this.jvm.getVerboseGC();
    }
    
    @Override
    public void setVerbose(final boolean verboseGC) {
        Util.checkControlAccess();
        this.setVerboseGC(verboseGC);
    }
    
    static synchronized MemoryPoolMXBean[] getMemoryPools() {
        if (MemoryImpl.pools == null) {
            MemoryImpl.pools = getMemoryPools0();
        }
        return MemoryImpl.pools;
    }
    
    static synchronized MemoryManagerMXBean[] getMemoryManagers() {
        if (MemoryImpl.mgrs == null) {
            MemoryImpl.mgrs = getMemoryManagers0();
        }
        return MemoryImpl.mgrs;
    }
    
    private static native MemoryPoolMXBean[] getMemoryPools0();
    
    private static native MemoryManagerMXBean[] getMemoryManagers0();
    
    private native MemoryUsage getMemoryUsage0(final boolean p0);
    
    private native void setVerboseGC(final boolean p0);
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[] { new MBeanNotificationInfo(MemoryImpl.notifTypes, "javax.management.Notification", "Memory Notification") };
    }
    
    private static String getNotifMsg(final String s) {
        for (int i = 0; i < MemoryImpl.notifTypes.length; ++i) {
            if (s == MemoryImpl.notifTypes[i]) {
                return MemoryImpl.notifMsgs[i];
            }
        }
        return "Unknown message";
    }
    
    private static long getNextSeqNumber() {
        return ++MemoryImpl.seqNumber;
    }
    
    static void createNotification(final String s, final String s2, final MemoryUsage memoryUsage, final long n) {
        final MemoryImpl memoryImpl = (MemoryImpl)ManagementFactory.getMemoryMXBean();
        if (!memoryImpl.hasListeners()) {
            return;
        }
        final Notification notification = new Notification(s, memoryImpl.getObjectName(), getNextSeqNumber(), System.currentTimeMillis(), getNotifMsg(s));
        notification.setUserData(MemoryNotifInfoCompositeData.toCompositeData(new MemoryNotificationInfo(s2, memoryUsage, n)));
        memoryImpl.sendNotification(notification);
    }
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=Memory");
    }
    
    static {
        MemoryImpl.pools = null;
        MemoryImpl.mgrs = null;
        notifTypes = new String[] { "java.management.memory.threshold.exceeded", "java.management.memory.collection.threshold.exceeded" };
        notifMsgs = new String[] { "Memory usage exceeds usage threshold", "Memory usage exceeds collection usage threshold" };
        MemoryImpl.seqNumber = 0L;
    }
}
