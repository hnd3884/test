package sun.management;

import javax.management.ObjectName;
import javax.management.MBeanNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryManagerMXBean;

class MemoryManagerImpl extends NotificationEmitterSupport implements MemoryManagerMXBean
{
    private final String name;
    private final boolean isValid;
    private MemoryPoolMXBean[] pools;
    private MBeanNotificationInfo[] notifInfo;
    
    MemoryManagerImpl(final String name) {
        this.notifInfo = null;
        this.name = name;
        this.isValid = true;
        this.pools = null;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean isValid() {
        return this.isValid;
    }
    
    @Override
    public String[] getMemoryPoolNames() {
        final MemoryPoolMXBean[] memoryPools = this.getMemoryPools();
        final String[] array = new String[memoryPools.length];
        for (int i = 0; i < memoryPools.length; ++i) {
            array[i] = memoryPools[i].getName();
        }
        return array;
    }
    
    synchronized MemoryPoolMXBean[] getMemoryPools() {
        if (this.pools == null) {
            this.pools = this.getMemoryPools0();
        }
        return this.pools;
    }
    
    private native MemoryPoolMXBean[] getMemoryPools0();
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        synchronized (this) {
            if (this.notifInfo == null) {
                this.notifInfo = new MBeanNotificationInfo[0];
            }
        }
        return this.notifInfo;
    }
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=MemoryManager", this.getName());
    }
}
