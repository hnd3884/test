package org.jfree.threads;

import java.util.Iterator;
import java.util.ArrayList;

public class ReaderWriterLock
{
    private ArrayList waiters;
    
    public ReaderWriterLock() {
        this.waiters = new ArrayList();
    }
    
    private int firstWriter() {
        final Iterator e = this.waiters.iterator();
        int index = 0;
        while (e.hasNext()) {
            final ReaderWriterNode node = e.next();
            if (node.state == 1) {
                return index;
            }
            ++index;
        }
        return Integer.MAX_VALUE;
    }
    
    private int getIndex(final Thread t) {
        final Iterator e = this.waiters.iterator();
        int index = 0;
        while (e.hasNext()) {
            final ReaderWriterNode node = e.next();
            if (node.t == t) {
                return index;
            }
            ++index;
        }
        return -1;
    }
    
    public synchronized void lockRead() {
        final Thread me = Thread.currentThread();
        final int index = this.getIndex(me);
        ReaderWriterNode node;
        if (index == -1) {
            node = new ReaderWriterNode(null, me, 0);
            this.waiters.add(node);
        }
        else {
            node = this.waiters.get(index);
        }
        while (this.getIndex(me) > this.firstWriter()) {
            try {
                this.wait();
            }
            catch (final Exception e) {
                System.err.println("ReaderWriterLock.lockRead(): exception.");
                System.err.print(e.getMessage());
            }
        }
        final ReaderWriterNode readerWriterNode = node;
        ++readerWriterNode.nAcquires;
    }
    
    public synchronized void lockWrite() {
        final Thread me = Thread.currentThread();
        final int index = this.getIndex(me);
        ReaderWriterNode node;
        if (index == -1) {
            node = new ReaderWriterNode(null, me, 1);
            this.waiters.add(node);
        }
        else {
            node = this.waiters.get(index);
            if (node.state == 0) {
                throw new IllegalArgumentException("Upgrade lock");
            }
            node.state = 1;
        }
        while (this.getIndex(me) != 0) {
            try {
                this.wait();
            }
            catch (final Exception e) {
                System.err.println("ReaderWriterLock.lockWrite(): exception.");
                System.err.print(e.getMessage());
            }
        }
        final ReaderWriterNode readerWriterNode = node;
        ++readerWriterNode.nAcquires;
    }
    
    public synchronized void unlock() {
        final Thread me = Thread.currentThread();
        final int index = this.getIndex(me);
        if (index > this.firstWriter()) {
            throw new IllegalArgumentException("Lock not held");
        }
        final ReaderWriterNode readerWriterNode;
        final ReaderWriterNode node = readerWriterNode = this.waiters.get(index);
        --readerWriterNode.nAcquires;
        if (node.nAcquires == 0) {
            this.waiters.remove(index);
        }
        this.notifyAll();
    }
    
    private static class ReaderWriterNode
    {
        protected static final int READER = 0;
        protected static final int WRITER = 1;
        protected Thread t;
        protected int state;
        protected int nAcquires;
        
        private ReaderWriterNode(final Thread t, final int state) {
            this.t = t;
            this.state = state;
            this.nAcquires = 0;
        }
    }
    
    static class 1
    {
    }
}
