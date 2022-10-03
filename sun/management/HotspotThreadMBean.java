package sun.management;

import sun.management.counter.Counter;
import java.util.List;
import java.util.Map;

public interface HotspotThreadMBean
{
    int getInternalThreadCount();
    
    Map<String, Long> getInternalThreadCpuTimes();
    
    List<Counter> getInternalThreadingCounters();
}
