package sun.management;

import sun.management.counter.Counter;
import java.util.List;

public interface HotspotClassLoadingMBean
{
    long getLoadedClassSize();
    
    long getUnloadedClassSize();
    
    long getClassLoadingTime();
    
    long getMethodDataSize();
    
    long getInitializedClassCount();
    
    long getClassInitializationTime();
    
    long getClassVerificationTime();
    
    List<Counter> getInternalClassLoadingCounters();
}
