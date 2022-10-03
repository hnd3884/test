package java.lang.management;

import sun.management.MonitorInfoCompositeData;
import javax.management.openmbean.CompositeData;

public class MonitorInfo extends LockInfo
{
    private int stackDepth;
    private StackTraceElement stackFrame;
    
    public MonitorInfo(final String s, final int n, final int stackDepth, final StackTraceElement stackFrame) {
        super(s, n);
        if (stackDepth >= 0 && stackFrame == null) {
            throw new IllegalArgumentException("Parameter stackDepth is " + stackDepth + " but stackFrame is null");
        }
        if (stackDepth < 0 && stackFrame != null) {
            throw new IllegalArgumentException("Parameter stackDepth is " + stackDepth + " but stackFrame is not null");
        }
        this.stackDepth = stackDepth;
        this.stackFrame = stackFrame;
    }
    
    public int getLockedStackDepth() {
        return this.stackDepth;
    }
    
    public StackTraceElement getLockedStackFrame() {
        return this.stackFrame;
    }
    
    public static MonitorInfo from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof MonitorInfoCompositeData) {
            return ((MonitorInfoCompositeData)compositeData).getMonitorInfo();
        }
        MonitorInfoCompositeData.validateCompositeData(compositeData);
        return new MonitorInfo(MonitorInfoCompositeData.getClassName(compositeData), MonitorInfoCompositeData.getIdentityHashCode(compositeData), MonitorInfoCompositeData.getLockedStackDepth(compositeData), MonitorInfoCompositeData.getLockedStackFrame(compositeData));
    }
}
