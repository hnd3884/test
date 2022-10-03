package sun.management;

import javax.management.ObjectName;
import sun.misc.Unsafe;
import java.lang.management.OperatingSystemMXBean;

public class BaseOperatingSystemImpl implements OperatingSystemMXBean
{
    private final VMManagement jvm;
    private static final Unsafe unsafe;
    private double[] loadavg;
    
    protected BaseOperatingSystemImpl(final VMManagement jvm) {
        this.loadavg = new double[1];
        this.jvm = jvm;
    }
    
    @Override
    public String getName() {
        return this.jvm.getOsName();
    }
    
    @Override
    public String getArch() {
        return this.jvm.getOsArch();
    }
    
    @Override
    public String getVersion() {
        return this.jvm.getOsVersion();
    }
    
    @Override
    public int getAvailableProcessors() {
        return this.jvm.getAvailableProcessors();
    }
    
    @Override
    public double getSystemLoadAverage() {
        if (BaseOperatingSystemImpl.unsafe.getLoadAverage(this.loadavg, 1) == 1) {
            return this.loadavg[0];
        }
        return -1.0;
    }
    
    @Override
    public ObjectName getObjectName() {
        return Util.newObjectName("java.lang:type=OperatingSystem");
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
