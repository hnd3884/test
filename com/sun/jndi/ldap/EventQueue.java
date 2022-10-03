package com.sun.jndi.ldap;

import javax.naming.ldap.UnsolicitedNotificationListener;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingListener;
import java.util.Vector;
import java.util.EventObject;

final class EventQueue implements Runnable
{
    private static final boolean debug = false;
    private QueueElement head;
    private QueueElement tail;
    private Thread qThread;
    
    EventQueue() {
        this.head = null;
        this.tail = null;
        (this.qThread = Obj.helper.createThread(this)).setDaemon(true);
        this.qThread.start();
    }
    
    synchronized void enqueue(final EventObject eventObject, final Vector<NamingListener> vector) {
        final QueueElement queueElement = new QueueElement(eventObject, vector);
        if (this.head == null) {
            this.head = queueElement;
            this.tail = queueElement;
        }
        else {
            queueElement.next = this.head;
            this.head.prev = queueElement;
            this.head = queueElement;
        }
        this.notify();
    }
    
    private synchronized QueueElement dequeue() throws InterruptedException {
        while (this.tail == null) {
            this.wait();
        }
        final QueueElement tail = this.tail;
        this.tail = tail.prev;
        if (this.tail == null) {
            this.head = null;
        }
        else {
            this.tail.next = null;
        }
        final QueueElement queueElement = tail;
        final QueueElement queueElement2 = tail;
        final QueueElement queueElement3 = null;
        queueElement2.next = queueElement3;
        queueElement.prev = queueElement3;
        return tail;
    }
    
    @Override
    public void run() {
        try {
            QueueElement dequeue;
            while ((dequeue = this.dequeue()) != null) {
                final EventObject event = dequeue.event;
                final Vector<NamingListener> vector = dequeue.vector;
                for (int i = 0; i < vector.size(); ++i) {
                    if (event instanceof NamingEvent) {
                        ((NamingEvent)event).dispatch((NamingListener)vector.elementAt(i));
                    }
                    else if (event instanceof NamingExceptionEvent) {
                        ((NamingExceptionEvent)event).dispatch((NamingListener)vector.elementAt(i));
                    }
                    else if (event instanceof UnsolicitedNotificationEvent) {
                        ((UnsolicitedNotificationEvent)event).dispatch((UnsolicitedNotificationListener)vector.elementAt(i));
                    }
                }
            }
        }
        catch (final InterruptedException ex) {}
    }
    
    void stop() {
        if (this.qThread != null) {
            this.qThread.interrupt();
            this.qThread = null;
        }
    }
    
    private static class QueueElement
    {
        QueueElement next;
        QueueElement prev;
        EventObject event;
        Vector<NamingListener> vector;
        
        QueueElement(final EventObject event, final Vector<NamingListener> vector) {
            this.next = null;
            this.prev = null;
            this.event = null;
            this.vector = null;
            this.event = event;
            this.vector = vector;
        }
    }
}
