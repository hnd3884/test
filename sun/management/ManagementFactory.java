package sun.management;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;

class ManagementFactory
{
    private ManagementFactory() {
    }
    
    private static MemoryPoolMXBean createMemoryPool(final String s, final boolean b, final long n, final long n2) {
        return new MemoryPoolImpl(s, b, n, n2);
    }
    
    private static MemoryManagerMXBean createMemoryManager(final String s) {
        return new MemoryManagerImpl(s);
    }
    
    private static GarbageCollectorMXBean createGarbageCollector(final String s, final String s2) {
        return new GarbageCollectorImpl(s);
    }
}
