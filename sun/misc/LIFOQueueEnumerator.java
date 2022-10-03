package sun.misc;

import java.util.NoSuchElementException;
import java.util.Enumeration;

final class LIFOQueueEnumerator<T> implements Enumeration<T>
{
    Queue<T> queue;
    QueueElement<T> cursor;
    
    LIFOQueueEnumerator(final Queue<T> queue) {
        this.queue = queue;
        this.cursor = queue.head;
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
                this.cursor = this.cursor.next;
                return cursor.obj;
            }
        }
        throw new NoSuchElementException("LIFOQueueEnumerator");
    }
}
