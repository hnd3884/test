package sun.misc;

class QueueElement<T>
{
    QueueElement<T> next;
    QueueElement<T> prev;
    T obj;
    
    QueueElement(final T obj) {
        this.next = null;
        this.prev = null;
        this.obj = null;
        this.obj = obj;
    }
    
    @Override
    public String toString() {
        return "QueueElement[obj=" + this.obj + ((this.prev == null) ? " null" : " prev") + ((this.next == null) ? " null" : " next") + "]";
    }
}
