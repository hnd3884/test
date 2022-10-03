package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import java.util.LinkedList;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;

public class WorkQueueImpl implements WorkQueue
{
    private ThreadPool workerThreadPool;
    private LinkedList theWorkQueue;
    private long workItemsAdded;
    private long workItemsDequeued;
    private long totalTimeInQueue;
    private String name;
    private MonitoredObject workqueueMonitoredObject;
    
    public WorkQueueImpl() {
        this.theWorkQueue = new LinkedList();
        this.workItemsAdded = 0L;
        this.workItemsDequeued = 1L;
        this.totalTimeInQueue = 0L;
        this.name = "default-workqueue";
        this.initializeMonitoring();
    }
    
    public WorkQueueImpl(final ThreadPool threadPool) {
        this(threadPool, "default-workqueue");
    }
    
    public WorkQueueImpl(final ThreadPool workerThreadPool, final String name) {
        this.theWorkQueue = new LinkedList();
        this.workItemsAdded = 0L;
        this.workItemsDequeued = 1L;
        this.totalTimeInQueue = 0L;
        this.workerThreadPool = workerThreadPool;
        this.name = name;
        this.initializeMonitoring();
    }
    
    private void initializeMonitoring() {
        (this.workqueueMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.name, "Monitoring for a Work Queue")).addAttribute(new LongMonitoredAttributeBase("totalWorkItemsAdded", "Total number of Work items added to the Queue") {
            @Override
            public Object getValue() {
                return new Long(WorkQueueImpl.this.totalWorkItemsAdded());
            }
        });
        this.workqueueMonitoredObject.addAttribute(new LongMonitoredAttributeBase("workItemsInQueue", "Number of Work items in the Queue to be processed") {
            @Override
            public Object getValue() {
                return new Long(WorkQueueImpl.this.workItemsInQueue());
            }
        });
        this.workqueueMonitoredObject.addAttribute(new LongMonitoredAttributeBase("averageTimeInQueue", "Average time a work item waits in the work queue") {
            @Override
            public Object getValue() {
                return new Long(WorkQueueImpl.this.averageTimeInQueue());
            }
        });
    }
    
    MonitoredObject getMonitoredObject() {
        return this.workqueueMonitoredObject;
    }
    
    @Override
    public synchronized void addWork(final Work work) {
        ++this.workItemsAdded;
        work.setEnqueueTime(System.currentTimeMillis());
        this.theWorkQueue.addLast(work);
        ((ThreadPoolImpl)this.workerThreadPool).notifyForAvailableWork(this);
    }
    
    synchronized Work requestWork(final long n) throws TimeoutException, InterruptedException {
        ((ThreadPoolImpl)this.workerThreadPool).incrementNumberOfAvailableThreads();
        if (this.theWorkQueue.size() != 0) {
            final Work work = this.theWorkQueue.removeFirst();
            this.totalTimeInQueue += System.currentTimeMillis() - work.getEnqueueTime();
            ++this.workItemsDequeued;
            ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
            return work;
        }
        try {
            long n2 = n;
            final long n3 = System.currentTimeMillis() + n;
            do {
                this.wait(n2);
                if (this.theWorkQueue.size() != 0) {
                    final Work work2 = this.theWorkQueue.removeFirst();
                    this.totalTimeInQueue += System.currentTimeMillis() - work2.getEnqueueTime();
                    ++this.workItemsDequeued;
                    ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
                    return work2;
                }
                n2 = n3 - System.currentTimeMillis();
            } while (n2 > 0L);
            ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
            throw new TimeoutException();
        }
        catch (final InterruptedException ex) {
            ((ThreadPoolImpl)this.workerThreadPool).decrementNumberOfAvailableThreads();
            throw ex;
        }
    }
    
    @Override
    public void setThreadPool(final ThreadPool workerThreadPool) {
        this.workerThreadPool = workerThreadPool;
    }
    
    @Override
    public ThreadPool getThreadPool() {
        return this.workerThreadPool;
    }
    
    @Override
    public long totalWorkItemsAdded() {
        return this.workItemsAdded;
    }
    
    @Override
    public int workItemsInQueue() {
        return this.theWorkQueue.size();
    }
    
    @Override
    public synchronized long averageTimeInQueue() {
        return this.totalTimeInQueue / this.workItemsDequeued;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
