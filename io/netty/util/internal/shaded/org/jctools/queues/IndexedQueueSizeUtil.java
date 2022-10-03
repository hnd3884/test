package io.netty.util.internal.shaded.org.jctools.queues;

public final class IndexedQueueSizeUtil
{
    public static int size(final IndexedQueue iq) {
        long after = iq.lvConsumerIndex();
        long before;
        long currentProducerIndex;
        do {
            before = after;
            currentProducerIndex = iq.lvProducerIndex();
            after = iq.lvConsumerIndex();
        } while (before != after);
        final long size = currentProducerIndex - after;
        if (size > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (size < 0L) {
            return 0;
        }
        if (iq.capacity() != -1 && size > iq.capacity()) {
            return iq.capacity();
        }
        return (int)size;
    }
    
    public static boolean isEmpty(final IndexedQueue iq) {
        return iq.lvConsumerIndex() >= iq.lvProducerIndex();
    }
    
    public interface IndexedQueue
    {
        long lvConsumerIndex();
        
        long lvProducerIndex();
        
        int capacity();
    }
}
