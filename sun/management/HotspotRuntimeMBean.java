package sun.management;

import sun.management.counter.Counter;
import java.util.List;

public interface HotspotRuntimeMBean
{
    long getSafepointCount();
    
    long getTotalSafepointTime();
    
    long getSafepointSyncTime();
    
    List<Counter> getInternalRuntimeCounters();
}
