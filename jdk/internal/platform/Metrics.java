package jdk.internal.platform;

public interface Metrics
{
    default Metrics systemMetrics() {
        try {
            return (Metrics)Class.forName("jdk.internal.platform.cgroupv1.Metrics").getMethod("getInstance", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (final ClassNotFoundException ex) {
            return null;
        }
        catch (final ReflectiveOperationException ex2) {
            throw new InternalError(ex2);
        }
    }
    
    String getProvider();
    
    long getCpuUsage();
    
    long[] getPerCpuUsage();
    
    long getCpuUserUsage();
    
    long getCpuSystemUsage();
    
    long getCpuPeriod();
    
    long getCpuQuota();
    
    long getCpuShares();
    
    long getCpuNumPeriods();
    
    long getCpuNumThrottled();
    
    long getCpuThrottledTime();
    
    long getEffectiveCpuCount();
    
    int[] getCpuSetCpus();
    
    int[] getEffectiveCpuSetCpus();
    
    int[] getCpuSetMems();
    
    int[] getEffectiveCpuSetMems();
    
    double getCpuSetMemoryPressure();
    
    boolean isCpuSetMemoryPressureEnabled();
    
    long getMemoryFailCount();
    
    long getMemoryLimit();
    
    long getMemoryMaxUsage();
    
    long getMemoryUsage();
    
    long getKernelMemoryFailCount();
    
    long getKernelMemoryLimit();
    
    long getKernelMemoryMaxUsage();
    
    long getKernelMemoryUsage();
    
    long getTcpMemoryFailCount();
    
    long getTcpMemoryLimit();
    
    long getTcpMemoryMaxUsage();
    
    long getTcpMemoryUsage();
    
    long getMemoryAndSwapFailCount();
    
    long getMemoryAndSwapLimit();
    
    long getMemoryAndSwapMaxUsage();
    
    long getMemoryAndSwapUsage();
    
    boolean isMemoryOOMKillEnabled();
    
    long getMemorySoftLimit();
    
    long getBlkIOServiceCount();
    
    long getBlkIOServiced();
}
