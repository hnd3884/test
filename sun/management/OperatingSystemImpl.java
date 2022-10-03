package sun.management;

import com.sun.management.OperatingSystemMXBean;

class OperatingSystemImpl extends BaseOperatingSystemImpl implements OperatingSystemMXBean
{
    private static Object psapiLock;
    
    OperatingSystemImpl(final VMManagement vmManagement) {
        super(vmManagement);
    }
    
    @Override
    public long getCommittedVirtualMemorySize() {
        synchronized (OperatingSystemImpl.psapiLock) {
            return this.getCommittedVirtualMemorySize0();
        }
    }
    
    @Override
    public long getTotalSwapSpaceSize() {
        return this.getTotalSwapSpaceSize0();
    }
    
    @Override
    public long getFreeSwapSpaceSize() {
        return this.getFreeSwapSpaceSize0();
    }
    
    @Override
    public long getProcessCpuTime() {
        return this.getProcessCpuTime0();
    }
    
    @Override
    public long getFreePhysicalMemorySize() {
        return this.getFreePhysicalMemorySize0();
    }
    
    @Override
    public long getTotalPhysicalMemorySize() {
        return this.getTotalPhysicalMemorySize0();
    }
    
    @Override
    public double getSystemCpuLoad() {
        return this.getSystemCpuLoad0();
    }
    
    @Override
    public double getProcessCpuLoad() {
        return this.getProcessCpuLoad0();
    }
    
    private native long getCommittedVirtualMemorySize0();
    
    private native long getFreePhysicalMemorySize0();
    
    private native long getFreeSwapSpaceSize0();
    
    private native double getProcessCpuLoad0();
    
    private native long getProcessCpuTime0();
    
    private native double getSystemCpuLoad0();
    
    private native long getTotalPhysicalMemorySize0();
    
    private native long getTotalSwapSpaceSize0();
    
    private static native void initialize0();
    
    static {
        OperatingSystemImpl.psapiLock = new Object();
        initialize0();
    }
}
