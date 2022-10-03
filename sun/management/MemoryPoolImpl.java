package sun.management;

import javax.management.ObjectName;
import java.lang.management.MemoryUsage;
import java.lang.management.MemoryType;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;

class MemoryPoolImpl implements MemoryPoolMXBean
{
    private final String name;
    private final boolean isHeap;
    private final boolean isValid;
    private final boolean collectionThresholdSupported;
    private final boolean usageThresholdSupported;
    private MemoryManagerMXBean[] managers;
    private long usageThreshold;
    private long collectionThreshold;
    private boolean usageSensorRegistered;
    private boolean gcSensorRegistered;
    private Sensor usageSensor;
    private Sensor gcSensor;
    
    MemoryPoolImpl(final String name, final boolean isHeap, final long usageThreshold, final long collectionThreshold) {
        this.name = name;
        this.isHeap = isHeap;
        this.isValid = true;
        this.managers = null;
        this.usageThreshold = usageThreshold;
        this.collectionThreshold = collectionThreshold;
        this.usageThresholdSupported = (usageThreshold >= 0L);
        this.collectionThresholdSupported = (collectionThreshold >= 0L);
        this.usageSensor = new PoolSensor(this, name + " usage sensor");
        this.gcSensor = new CollectionSensor(this, name + " collection sensor");
        this.usageSensorRegistered = false;
        this.gcSensorRegistered = false;
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
    public MemoryType getType() {
        if (this.isHeap) {
            return MemoryType.HEAP;
        }
        return MemoryType.NON_HEAP;
    }
    
    @Override
    public MemoryUsage getUsage() {
        return this.getUsage0();
    }
    
    @Override
    public synchronized MemoryUsage getPeakUsage() {
        return this.getPeakUsage0();
    }
    
    @Override
    public synchronized long getUsageThreshold() {
        if (!this.isUsageThresholdSupported()) {
            throw new UnsupportedOperationException("Usage threshold is not supported");
        }
        return this.usageThreshold;
    }
    
    @Override
    public void setUsageThreshold(final long usageThreshold) {
        if (!this.isUsageThresholdSupported()) {
            throw new UnsupportedOperationException("Usage threshold is not supported");
        }
        Util.checkControlAccess();
        final MemoryUsage usage0 = this.getUsage0();
        if (usageThreshold < 0L) {
            throw new IllegalArgumentException("Invalid threshold: " + usageThreshold);
        }
        if (usage0.getMax() != -1L && usageThreshold > usage0.getMax()) {
            throw new IllegalArgumentException("Invalid threshold: " + usageThreshold + " must be <= maxSize. Committed = " + usage0.getCommitted() + " Max = " + usage0.getMax());
        }
        synchronized (this) {
            if (!this.usageSensorRegistered) {
                this.usageSensorRegistered = true;
                this.setPoolUsageSensor(this.usageSensor);
            }
            this.setUsageThreshold0(this.usageThreshold, usageThreshold);
            this.usageThreshold = usageThreshold;
        }
    }
    
    private synchronized MemoryManagerMXBean[] getMemoryManagers() {
        if (this.managers == null) {
            this.managers = this.getMemoryManagers0();
        }
        return this.managers;
    }
    
    @Override
    public String[] getMemoryManagerNames() {
        final MemoryManagerMXBean[] memoryManagers = this.getMemoryManagers();
        final String[] array = new String[memoryManagers.length];
        for (int i = 0; i < memoryManagers.length; ++i) {
            array[i] = memoryManagers[i].getName();
        }
        return array;
    }
    
    @Override
    public void resetPeakUsage() {
        Util.checkControlAccess();
        synchronized (this) {
            this.resetPeakUsage0();
        }
    }
    
    @Override
    public boolean isUsageThresholdExceeded() {
        if (!this.isUsageThresholdSupported()) {
            throw new UnsupportedOperationException("Usage threshold is not supported");
        }
        return this.usageThreshold != 0L && (this.getUsage0().getUsed() >= this.usageThreshold || this.usageSensor.isOn());
    }
    
    @Override
    public long getUsageThresholdCount() {
        if (!this.isUsageThresholdSupported()) {
            throw new UnsupportedOperationException("Usage threshold is not supported");
        }
        return this.usageSensor.getCount();
    }
    
    @Override
    public boolean isUsageThresholdSupported() {
        return this.usageThresholdSupported;
    }
    
    @Override
    public synchronized long getCollectionUsageThreshold() {
        if (!this.isCollectionUsageThresholdSupported()) {
            throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
        }
        return this.collectionThreshold;
    }
    
    @Override
    public void setCollectionUsageThreshold(final long collectionThreshold) {
        if (!this.isCollectionUsageThresholdSupported()) {
            throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
        }
        Util.checkControlAccess();
        final MemoryUsage usage0 = this.getUsage0();
        if (collectionThreshold < 0L) {
            throw new IllegalArgumentException("Invalid threshold: " + collectionThreshold);
        }
        if (usage0.getMax() != -1L && collectionThreshold > usage0.getMax()) {
            throw new IllegalArgumentException("Invalid threshold: " + collectionThreshold + " > max (" + usage0.getMax() + ").");
        }
        synchronized (this) {
            if (!this.gcSensorRegistered) {
                this.gcSensorRegistered = true;
                this.setPoolCollectionSensor(this.gcSensor);
            }
            this.setCollectionThreshold0(this.collectionThreshold, collectionThreshold);
            this.collectionThreshold = collectionThreshold;
        }
    }
    
    @Override
    public boolean isCollectionUsageThresholdExceeded() {
        if (!this.isCollectionUsageThresholdSupported()) {
            throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
        }
        if (this.collectionThreshold == 0L) {
            return false;
        }
        final MemoryUsage collectionUsage0 = this.getCollectionUsage0();
        return this.gcSensor.isOn() || (collectionUsage0 != null && collectionUsage0.getUsed() >= this.collectionThreshold);
    }
    
    @Override
    public long getCollectionUsageThresholdCount() {
        if (!this.isCollectionUsageThresholdSupported()) {
            throw new UnsupportedOperationException("CollectionUsage threshold is not supported");
        }
        return this.gcSensor.getCount();
    }
    
    @Override
    public MemoryUsage getCollectionUsage() {
        return this.getCollectionUsage0();
    }
    
    @Override
    public boolean isCollectionUsageThresholdSupported() {
        return this.collectionThresholdSupported;
    }
    
    private native MemoryUsage getUsage0();
    
    private native MemoryUsage getPeakUsage0();
    
    private native MemoryUsage getCollectionUsage0();
    
    private native void setUsageThreshold0(final long p0, final long p1);
    
    private native void setCollectionThreshold0(final long p0, final long p1);
    
    private native void resetPeakUsage0();
    
    private native MemoryManagerMXBean[] getMemoryManagers0();
    
    private native void setPoolUsageSensor(final Sensor p0);
    
    private native void setPoolCollectionSensor(final Sensor p0);
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=MemoryPool", this.getName());
    }
    
    class PoolSensor extends Sensor
    {
        MemoryPoolImpl pool;
        
        PoolSensor(final MemoryPoolImpl pool, final String s) {
            super(s);
            this.pool = pool;
        }
        
        @Override
        void triggerAction(final MemoryUsage memoryUsage) {
            MemoryImpl.createNotification("java.management.memory.threshold.exceeded", this.pool.getName(), memoryUsage, this.getCount());
        }
        
        @Override
        void triggerAction() {
        }
        
        @Override
        void clearAction() {
        }
    }
    
    class CollectionSensor extends Sensor
    {
        MemoryPoolImpl pool;
        
        CollectionSensor(final MemoryPoolImpl pool, final String s) {
            super(s);
            this.pool = pool;
        }
        
        @Override
        void triggerAction(final MemoryUsage memoryUsage) {
            MemoryImpl.createNotification("java.management.memory.collection.threshold.exceeded", this.pool.getName(), memoryUsage, MemoryPoolImpl.this.gcSensor.getCount());
        }
        
        @Override
        void triggerAction() {
        }
        
        @Override
        void clearAction() {
        }
    }
}
