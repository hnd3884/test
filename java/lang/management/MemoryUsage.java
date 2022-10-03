package java.lang.management;

import sun.management.MemoryUsageCompositeData;
import javax.management.openmbean.CompositeData;

public class MemoryUsage
{
    private final long init;
    private final long used;
    private final long committed;
    private final long max;
    
    public MemoryUsage(final long init, final long used, final long committed, final long max) {
        if (init < -1L) {
            throw new IllegalArgumentException("init parameter = " + init + " is negative but not -1.");
        }
        if (max < -1L) {
            throw new IllegalArgumentException("max parameter = " + max + " is negative but not -1.");
        }
        if (used < 0L) {
            throw new IllegalArgumentException("used parameter = " + used + " is negative.");
        }
        if (committed < 0L) {
            throw new IllegalArgumentException("committed parameter = " + committed + " is negative.");
        }
        if (used > committed) {
            throw new IllegalArgumentException("used = " + used + " should be <= committed = " + committed);
        }
        if (max >= 0L && committed > max) {
            throw new IllegalArgumentException("committed = " + committed + " should be < max = " + max);
        }
        this.init = init;
        this.used = used;
        this.committed = committed;
        this.max = max;
    }
    
    private MemoryUsage(final CompositeData compositeData) {
        MemoryUsageCompositeData.validateCompositeData(compositeData);
        this.init = MemoryUsageCompositeData.getInit(compositeData);
        this.used = MemoryUsageCompositeData.getUsed(compositeData);
        this.committed = MemoryUsageCompositeData.getCommitted(compositeData);
        this.max = MemoryUsageCompositeData.getMax(compositeData);
    }
    
    public long getInit() {
        return this.init;
    }
    
    public long getUsed() {
        return this.used;
    }
    
    public long getCommitted() {
        return this.committed;
    }
    
    public long getMax() {
        return this.max;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("init = " + this.init + "(" + (this.init >> 10) + "K) ");
        sb.append("used = " + this.used + "(" + (this.used >> 10) + "K) ");
        sb.append("committed = " + this.committed + "(" + (this.committed >> 10) + "K) ");
        sb.append("max = " + this.max + "(" + (this.max >> 10) + "K)");
        return sb.toString();
    }
    
    public static MemoryUsage from(final CompositeData compositeData) {
        if (compositeData == null) {
            return null;
        }
        if (compositeData instanceof MemoryUsageCompositeData) {
            return ((MemoryUsageCompositeData)compositeData).getMemoryUsage();
        }
        return new MemoryUsage(compositeData);
    }
}
