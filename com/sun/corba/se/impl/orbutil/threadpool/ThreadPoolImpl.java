package com.sun.corba.se.impl.orbutil.threadpool;

import com.sun.corba.se.spi.orbutil.threadpool.Work;
import java.io.Closeable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.sun.corba.se.spi.orbutil.threadpool.NoSuchWorkQueueException;
import com.sun.corba.se.spi.monitoring.MonitoredAttribute;
import com.sun.corba.se.spi.monitoring.LongMonitoredAttributeBase;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import java.util.concurrent.atomic.AtomicLong;
import com.sun.corba.se.spi.orbutil.threadpool.WorkQueue;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import java.util.concurrent.atomic.AtomicInteger;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPool;

public class ThreadPoolImpl implements ThreadPool
{
    private static AtomicInteger threadCounter;
    private static final ORBUtilSystemException wrapper;
    private WorkQueue workQueue;
    private int availableWorkerThreads;
    private int currentThreadCount;
    private int minWorkerThreads;
    private int maxWorkerThreads;
    private long inactivityTimeout;
    private boolean boundedThreadPool;
    private AtomicLong processedCount;
    private AtomicLong totalTimeTaken;
    private String name;
    private MonitoredObject threadpoolMonitoredObject;
    private ThreadGroup threadGroup;
    Object workersLock;
    List<WorkerThread> workers;
    
    public ThreadPoolImpl(final ThreadGroup threadGroup, final String name) {
        this.availableWorkerThreads = 0;
        this.currentThreadCount = 0;
        this.minWorkerThreads = 0;
        this.maxWorkerThreads = 0;
        this.boundedThreadPool = false;
        this.processedCount = new AtomicLong(1L);
        this.totalTimeTaken = new AtomicLong(0L);
        this.workersLock = new Object();
        this.workers = new ArrayList<WorkerThread>();
        this.inactivityTimeout = 120000L;
        this.maxWorkerThreads = Integer.MAX_VALUE;
        this.workQueue = new WorkQueueImpl(this);
        this.threadGroup = threadGroup;
        this.name = name;
        this.initializeMonitoring();
    }
    
    public ThreadPoolImpl(final String s) {
        this(Thread.currentThread().getThreadGroup(), s);
    }
    
    public ThreadPoolImpl(final int minWorkerThreads, final int maxWorkerThreads, final long inactivityTimeout, final String name) {
        this.availableWorkerThreads = 0;
        this.currentThreadCount = 0;
        this.minWorkerThreads = 0;
        this.maxWorkerThreads = 0;
        this.boundedThreadPool = false;
        this.processedCount = new AtomicLong(1L);
        this.totalTimeTaken = new AtomicLong(0L);
        this.workersLock = new Object();
        this.workers = new ArrayList<WorkerThread>();
        this.minWorkerThreads = minWorkerThreads;
        this.maxWorkerThreads = maxWorkerThreads;
        this.inactivityTimeout = inactivityTimeout;
        this.boundedThreadPool = true;
        this.workQueue = new WorkQueueImpl(this);
        this.name = name;
        for (int i = 0; i < this.minWorkerThreads; ++i) {
            this.createWorkerThread();
        }
        this.initializeMonitoring();
    }
    
    @Override
    public void close() throws IOException {
        List list = null;
        synchronized (this.workersLock) {
            list = new ArrayList(this.workers);
        }
        for (final WorkerThread workerThread : list) {
            workerThread.close();
            while (workerThread.getState() != Thread.State.TERMINATED) {
                try {
                    workerThread.join();
                }
                catch (final InterruptedException ex) {
                    ThreadPoolImpl.wrapper.interruptedJoinCallWhileClosingThreadPool(ex, workerThread, this);
                }
            }
        }
        this.threadGroup = null;
    }
    
    private void initializeMonitoring() {
        final MonitoredObject rootMonitoredObject = MonitoringFactories.getMonitoringManagerFactory().createMonitoringManager("orb", null).getRootMonitoredObject();
        MonitoredObject monitoredObject = rootMonitoredObject.getChild("threadpool");
        if (monitoredObject == null) {
            monitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject("threadpool", "Monitoring for all ThreadPool instances");
            rootMonitoredObject.addChild(monitoredObject);
        }
        monitoredObject.addChild(this.threadpoolMonitoredObject = MonitoringFactories.getMonitoredObjectFactory().createMonitoredObject(this.name, "Monitoring for a ThreadPool"));
        this.threadpoolMonitoredObject.addAttribute(new LongMonitoredAttributeBase("currentNumberOfThreads", "Current number of total threads in the ThreadPool") {
            @Override
            public Object getValue() {
                return new Long(ThreadPoolImpl.this.currentNumberOfThreads());
            }
        });
        this.threadpoolMonitoredObject.addAttribute(new LongMonitoredAttributeBase("numberOfAvailableThreads", "Current number of total threads in the ThreadPool") {
            @Override
            public Object getValue() {
                return new Long(ThreadPoolImpl.this.numberOfAvailableThreads());
            }
        });
        this.threadpoolMonitoredObject.addAttribute(new LongMonitoredAttributeBase("numberOfBusyThreads", "Number of busy threads in the ThreadPool") {
            @Override
            public Object getValue() {
                return new Long(ThreadPoolImpl.this.numberOfBusyThreads());
            }
        });
        this.threadpoolMonitoredObject.addAttribute(new LongMonitoredAttributeBase("averageWorkCompletionTime", "Average elapsed time taken to complete a work item by the ThreadPool") {
            @Override
            public Object getValue() {
                return new Long(ThreadPoolImpl.this.averageWorkCompletionTime());
            }
        });
        this.threadpoolMonitoredObject.addAttribute(new LongMonitoredAttributeBase("currentProcessedCount", "Number of Work items processed by the ThreadPool") {
            @Override
            public Object getValue() {
                return new Long(ThreadPoolImpl.this.currentProcessedCount());
            }
        });
        this.threadpoolMonitoredObject.addChild(((WorkQueueImpl)this.workQueue).getMonitoredObject());
    }
    
    MonitoredObject getMonitoredObject() {
        return this.threadpoolMonitoredObject;
    }
    
    @Override
    public WorkQueue getAnyWorkQueue() {
        return this.workQueue;
    }
    
    @Override
    public WorkQueue getWorkQueue(final int n) throws NoSuchWorkQueueException {
        if (n != 0) {
            throw new NoSuchWorkQueueException();
        }
        return this.workQueue;
    }
    
    void notifyForAvailableWork(final WorkQueue workQueue) {
        synchronized (workQueue) {
            if (this.availableWorkerThreads < workQueue.workItemsInQueue()) {
                this.createWorkerThread();
            }
            else {
                workQueue.notify();
            }
        }
    }
    
    private Thread createWorkerThreadHelper(final String s) {
        final WorkerThread workerThread = new WorkerThread(this.threadGroup, s);
        synchronized (this.workersLock) {
            this.workers.add(workerThread);
        }
        workerThread.setDaemon(true);
        ThreadPoolImpl.wrapper.workerThreadCreated(workerThread, workerThread.getContextClassLoader());
        workerThread.start();
        return null;
    }
    
    void createWorkerThread() {
        final String name = this.getName();
        synchronized (this.workQueue) {
            try {
                if (System.getSecurityManager() == null) {
                    this.createWorkerThreadHelper(name);
                }
                else {
                    AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
                        @Override
                        public Object run() {
                            return ThreadPoolImpl.this.createWorkerThreadHelper(name);
                        }
                    });
                }
            }
            catch (final Throwable t) {
                this.decrementCurrentNumberOfThreads();
                ThreadPoolImpl.wrapper.workerThreadCreationFailure(t);
            }
            finally {
                this.incrementCurrentNumberOfThreads();
            }
        }
    }
    
    @Override
    public int minimumNumberOfThreads() {
        return this.minWorkerThreads;
    }
    
    @Override
    public int maximumNumberOfThreads() {
        return this.maxWorkerThreads;
    }
    
    @Override
    public long idleTimeoutForThreads() {
        return this.inactivityTimeout;
    }
    
    @Override
    public int currentNumberOfThreads() {
        synchronized (this.workQueue) {
            return this.currentThreadCount;
        }
    }
    
    void decrementCurrentNumberOfThreads() {
        synchronized (this.workQueue) {
            --this.currentThreadCount;
        }
    }
    
    void incrementCurrentNumberOfThreads() {
        synchronized (this.workQueue) {
            ++this.currentThreadCount;
        }
    }
    
    @Override
    public int numberOfAvailableThreads() {
        synchronized (this.workQueue) {
            return this.availableWorkerThreads;
        }
    }
    
    @Override
    public int numberOfBusyThreads() {
        synchronized (this.workQueue) {
            return this.currentThreadCount - this.availableWorkerThreads;
        }
    }
    
    @Override
    public long averageWorkCompletionTime() {
        synchronized (this.workQueue) {
            return this.totalTimeTaken.get() / this.processedCount.get();
        }
    }
    
    @Override
    public long currentProcessedCount() {
        synchronized (this.workQueue) {
            return this.processedCount.get();
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public int numberOfWorkQueues() {
        return 1;
    }
    
    private static synchronized int getUniqueThreadId() {
        return ThreadPoolImpl.threadCounter.incrementAndGet();
    }
    
    void decrementNumberOfAvailableThreads() {
        synchronized (this.workQueue) {
            --this.availableWorkerThreads;
        }
    }
    
    void incrementNumberOfAvailableThreads() {
        synchronized (this.workQueue) {
            ++this.availableWorkerThreads;
        }
    }
    
    static {
        ThreadPoolImpl.threadCounter = new AtomicInteger(0);
        wrapper = ORBUtilSystemException.get("rpc.transport");
    }
    
    private class WorkerThread extends Thread implements Closeable
    {
        private Work currentWork;
        private int threadId;
        private volatile boolean closeCalled;
        private String threadPoolName;
        private StringBuffer workerThreadName;
        
        WorkerThread(final ThreadGroup threadGroup, final String threadPoolName) {
            super(threadGroup, "Idle");
            this.threadId = 0;
            this.closeCalled = false;
            this.workerThreadName = new StringBuffer();
            this.threadId = getUniqueThreadId();
            this.threadPoolName = threadPoolName;
            this.setName(this.composeWorkerThreadName(threadPoolName, "Idle"));
        }
        
        @Override
        public synchronized void close() {
            this.closeCalled = true;
            this.interrupt();
        }
        
        private void resetClassLoader() {
        }
        
        private void performWork() {
            final long currentTimeMillis = System.currentTimeMillis();
            try {
                this.currentWork.doWork();
            }
            catch (final Throwable t) {
                ThreadPoolImpl.wrapper.workerThreadDoWorkThrowable(this, t);
            }
            ThreadPoolImpl.this.totalTimeTaken.addAndGet(System.currentTimeMillis() - currentTimeMillis);
            ThreadPoolImpl.this.processedCount.incrementAndGet();
        }
        
        @Override
        public void run() {
            try {
                while (!this.closeCalled) {
                    try {
                        this.currentWork = ((WorkQueueImpl)ThreadPoolImpl.this.workQueue).requestWork(ThreadPoolImpl.this.inactivityTimeout);
                        if (this.currentWork == null) {
                            continue;
                        }
                    }
                    catch (final InterruptedException ex) {
                        ThreadPoolImpl.wrapper.workQueueThreadInterrupted(ex, this.getName(), this.closeCalled);
                        continue;
                    }
                    catch (final Throwable t) {
                        ThreadPoolImpl.wrapper.workerThreadThrowableFromRequestWork(this, t, ThreadPoolImpl.this.workQueue.getName());
                        continue;
                    }
                    this.performWork();
                    this.currentWork = null;
                    this.resetClassLoader();
                }
            }
            catch (final Throwable t2) {
                ThreadPoolImpl.wrapper.workerThreadCaughtUnexpectedThrowable(this, t2);
                synchronized (ThreadPoolImpl.this.workersLock) {
                    ThreadPoolImpl.this.workers.remove(this);
                }
            }
            finally {
                synchronized (ThreadPoolImpl.this.workersLock) {
                    ThreadPoolImpl.this.workers.remove(this);
                }
            }
        }
        
        private String composeWorkerThreadName(final String s, final String s2) {
            this.workerThreadName.setLength(0);
            this.workerThreadName.append("p: ").append(s);
            this.workerThreadName.append("; w: ").append(s2);
            return this.workerThreadName.toString();
        }
    }
}
