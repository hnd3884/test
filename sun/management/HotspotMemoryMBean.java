package sun.management;

import sun.management.counter.Counter;
import java.util.List;

public interface HotspotMemoryMBean
{
    List<Counter> getInternalMemoryCounters();
}
