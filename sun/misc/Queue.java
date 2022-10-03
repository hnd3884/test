package sun.misc;

import java.util.Enumeration;

public class Queue<T>
{
    int length;
    QueueElement<T> head;
    QueueElement<T> tail;
    
    public Queue() {
        this.length = 0;
        this.head = null;
        this.tail = null;
    }
    
    public synchronized void enqueue(final T t) {
        final QueueElement queueElement = new QueueElement((T)t);
        if (this.head == null) {
            this.head = queueElement;
            this.tail = queueElement;
            this.length = 1;
        }
        else {
            queueElement.next = (QueueElement<T>)this.head;
            this.head.prev = queueElement;
            this.head = queueElement;
            ++this.length;
        }
        this.notify();
    }
    
    public T dequeue() throws InterruptedException {
        return this.dequeue(0L);
    }
    
    public synchronized T dequeue(final long n) throws InterruptedException {
        while (this.tail == null) {
            this.wait(n);
        }
        final QueueElement<T> tail = this.tail;
        this.tail = tail.prev;
        if (this.tail == null) {
            this.head = null;
        }
        else {
            this.tail.next = null;
        }
        --this.length;
        return tail.obj;
    }
    
    public synchronized boolean isEmpty() {
        return this.tail == null;
    }
    
    public final synchronized Enumeration<T> elements() {
        return new LIFOQueueEnumerator<T>(this);
    }
    
    public final synchronized Enumeration<T> reverseElements() {
        return new FIFOQueueEnumerator<T>(this);
    }
    
    public synchronized void dump(final String s) {
        System.err.println(">> " + s);
        System.err.println("[" + this.length + " elt(s); head = " + ((this.head == null) ? "null" : (this.head.obj + "")) + " tail = " + ((this.tail == null) ? "null" : (this.tail.obj + "")));
        QueueElement<T> queueElement = this.head;
        QueueElement<T> queueElement2 = null;
        while (queueElement != null) {
            System.err.println("  " + queueElement);
            queueElement2 = queueElement;
            queueElement = queueElement.next;
        }
        if (queueElement2 != this.tail) {
            System.err.println("  tail != last: " + this.tail + ", " + queueElement2);
        }
        System.err.println("]");
    }
}
