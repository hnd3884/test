package org.apache.commons.collections4;

import java.util.LinkedList;
import org.apache.commons.collections4.queue.TransformedQueue;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.apache.commons.collections4.queue.UnmodifiableQueue;
import java.util.Queue;

public class QueueUtils
{
    public static final Queue EMPTY_QUEUE;
    
    private QueueUtils() {
    }
    
    public static <E> Queue<E> unmodifiableQueue(final Queue<? extends E> queue) {
        return UnmodifiableQueue.unmodifiableQueue(queue);
    }
    
    public static <E> Queue<E> predicatedQueue(final Queue<E> queue, final Predicate<? super E> predicate) {
        return PredicatedQueue.predicatedQueue(queue, predicate);
    }
    
    public static <E> Queue<E> transformingQueue(final Queue<E> queue, final Transformer<? super E, ? extends E> transformer) {
        return TransformedQueue.transformingQueue(queue, transformer);
    }
    
    public static <E> Queue<E> emptyQueue() {
        return QueueUtils.EMPTY_QUEUE;
    }
    
    static {
        EMPTY_QUEUE = UnmodifiableQueue.unmodifiableQueue((Queue<?>)new LinkedList<Object>());
    }
}
