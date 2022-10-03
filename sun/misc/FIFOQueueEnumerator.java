package sun.misc;

import java.util.NoSuchElementException;
import java.util.Enumeration;

final class FIFOQueueEnumerator<T> implements Enumeration<T>
{
    Queue<T> queue;
    QueueElement<T> cursor;
    
    FIFOQueueEnumerator(final Queue<T> queue) {
        this.queue = queue;
        this.cursor = queue.tail;
    }
    
    @Override
    public boolean hasMoreElements() {
        return this.cursor != null;
    }
    
    @Override
    public T nextElement() {
        synchronized (this.queue) {
            if (this.cursor != null) {
                final QueueElement<T> cursor = this.cursor;
                this.cursor = this.cursor.prev;
                return cursor.obj;
            }
        }
        throw new NoSuchElementException("FIFOQueueEnumerator");
    }
}
