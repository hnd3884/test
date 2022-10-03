package sun.awt;

import java.awt.AWTEvent;
import java.awt.EventQueue;

class PostEventQueue
{
    private EventQueueItem queueHead;
    private EventQueueItem queueTail;
    private final EventQueue eventQueue;
    private Thread flushThread;
    
    PostEventQueue(final EventQueue eventQueue) {
        this.queueHead = null;
        this.queueTail = null;
        this.flushThread = null;
        this.eventQueue = eventQueue;
    }
    
    public void flush() {
        final Thread currentThread = Thread.currentThread();
        try {
            EventQueueItem eventQueueItem;
            synchronized (this) {
                if (currentThread == this.flushThread) {
                    return;
                }
                while (this.flushThread != null) {
                    this.wait();
                }
                if (this.queueHead == null) {
                    return;
                }
                this.flushThread = currentThread;
                eventQueueItem = this.queueHead;
                final EventQueueItem eventQueueItem2 = null;
                this.queueTail = eventQueueItem2;
                this.queueHead = eventQueueItem2;
            }
            try {
                while (eventQueueItem != null) {
                    this.eventQueue.postEvent(eventQueueItem.event);
                    eventQueueItem = eventQueueItem.next;
                }
            }
            finally {
                synchronized (this) {
                    this.flushThread = null;
                    this.notifyAll();
                }
            }
        }
        catch (final InterruptedException ex) {
            currentThread.interrupt();
        }
    }
    
    void postEvent(final AWTEvent awtEvent) {
        final EventQueueItem eventQueueItem = new EventQueueItem(awtEvent);
        synchronized (this) {
            if (this.queueHead == null) {
                final EventQueueItem eventQueueItem2 = eventQueueItem;
                this.queueTail = eventQueueItem2;
                this.queueHead = eventQueueItem2;
            }
            else {
                this.queueTail.next = eventQueueItem;
                this.queueTail = eventQueueItem;
            }
        }
        SunToolkit.wakeupEventQueue(this.eventQueue, awtEvent.getSource() == AWTAutoShutdown.getInstance());
    }
}
