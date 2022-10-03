package com.azul.crs.client.service;

import java.util.ArrayList;
import com.azul.crs.shared.Utils;
import java.util.Iterator;
import java.util.Collection;
import com.azul.crs.client.Client;
import com.azul.crs.client.PerformanceMetrics;
import java.util.concurrent.TimeUnit;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QueueService<T> implements ClientService
{
    private static final int DEFAULT_MAX_QUEUE_SIZE = 5000;
    private static final int DEFAULT_MAX_WORKERS = 3;
    private static final int DEFAULT_MAX_BATCH_SIZE = 1000;
    private static final long DEFAULT_ADD_TIMEOUT = 500L;
    private volatile boolean stopping;
    private volatile boolean cancelled;
    private final BlockingQueue<T> queue;
    private final List<Thread> workerThreads;
    private final List<Worker> workers;
    private final int maxQueueSize;
    private final int maxWorkers;
    private final int maxBatchSize;
    private final long addTimeout;
    private final ProcessBatch<T> processBatch;
    private final String name;
    private final Object syncOrderMonitor;
    private final Object syncFinishNotifier;
    private final T syncMarker;
    private final AtomicInteger syncCount;
    
    private QueueService(final int maxQueueSize, final int maxWorkers, final int maxBatchSize, final long addTimeout, final T syncMarker, final ProcessBatch<T> processBatch, final String name) {
        this.syncOrderMonitor = new Object();
        this.syncFinishNotifier = new Object();
        this.syncCount = new AtomicInteger(0);
        this.maxQueueSize = maxQueueSize;
        this.maxWorkers = maxWorkers;
        this.maxBatchSize = maxBatchSize;
        this.addTimeout = addTimeout;
        this.syncMarker = syncMarker;
        this.processBatch = processBatch;
        this.queue = new LinkedBlockingDeque<T>(maxQueueSize);
        this.workerThreads = new LinkedList<Thread>();
        this.workers = new LinkedList<Worker>();
        this.name = name;
    }
    
    public void add(final T item) {
        if (this.cancelled || this.stopping) {
            return;
        }
        try {
            this.queue.offer(item, this.addTimeout, TimeUnit.MILLISECONDS);
            PerformanceMetrics.logEventQueueLength(this.queue.size());
        }
        catch (final InterruptedException ie) {
            if (!Client.isVMShutdownInitiated()) {
                this.logger().error("Queue failed to enqueue item: queueSize=" + this.queue.size() + ", maxQueueSize=" + this.maxQueueSize + ", timeout=" + this.addTimeout + ", item=" + item, new Object[0]);
            }
        }
    }
    
    public void addAll(final Collection<T> items) {
        if (this.stopping) {
            return;
        }
        try {
            for (final T item : items) {
                this.queue.offer(item, this.addTimeout, TimeUnit.MILLISECONDS);
            }
            PerformanceMetrics.logEventQueueLength(this.queue.size());
        }
        catch (final InterruptedException ie) {
            if (!Client.isVMShutdownInitiated()) {
                this.logger().error("Queue failed to enqueue item: queueSize=" + this.queue.size() + ", maxQueueSize=" + this.maxQueueSize + ", timeout=" + this.addTimeout + ", number of items=" + items.size(), new Object[0]);
            }
        }
    }
    
    @Override
    public synchronized void start() {
        if (this.stopping || this.cancelled) {
            throw new IllegalStateException(this.serviceName() + " is stopping or cancelled");
        }
        for (int i = 0; i < this.maxWorkers; ++i) {
            final Worker w = new Worker(String.valueOf(i));
            final Thread t = new Thread(w);
            this.workerThreads.add(t);
            this.workers.add(w);
            t.setDaemon(true);
            t.setName("CRSQW-" + this.name + i);
            t.start();
        }
    }
    
    public void sync(final long deadline) {
        if (this.cancelled) {
            return;
        }
        if (Utils.elapsedTimeMillis(deadline) >= 0L) {
            this.logger().debug("%s sync missed deadline", this.name);
            return;
        }
        synchronized (this.syncOrderMonitor) {
            boolean syncMarkerAdded = false;
            this.syncCount.set(this.maxWorkers);
            synchronized (this.syncFinishNotifier) {
                this.logger().trace("%s sync start", this.name);
                while (!(syncMarkerAdded = this.queue.offer(this.syncMarker)) && Utils.currentTimeCount() < deadline) {
                    Utils.sleep(10L);
                }
                if (syncMarkerAdded) {
                    try {
                        final long timeout = -Utils.elapsedTimeMillis(deadline);
                        if (timeout > 0L) {
                            this.syncFinishNotifier.wait(timeout);
                        }
                    }
                    catch (final InterruptedException ex) {}
                    if (this.syncCount.get() > 0) {
                        this.logger().warning("%s sync timeout waiting response. %d workers not finished", this.name, this.syncCount.get());
                        this.syncFinishNotifier.notifyAll();
                    }
                }
                else {
                    this.logger().warning("%s sync timeout waiting to initiate queue sync", this.name);
                }
            }
        }
    }
    
    @Override
    public void stop(final long deadline) {
        if (this.stopping) {
            return;
        }
        this.stopping = true;
        this.sync(deadline);
    }
    
    public void cancel() {
        this.cancelled = true;
    }
    
    public static class Builder<T>
    {
        private int maxQueueSize;
        private int maxWorkers;
        private int maxBatchSize;
        private long addTimeout;
        private ProcessBatch<T> processBatch;
        private T stopMarker;
        private String name;
        
        public Builder() {
            this.maxQueueSize = 5000;
            this.maxWorkers = 3;
            this.maxBatchSize = 1000;
            this.addTimeout = 500L;
            this.name = "<unnamed>";
        }
        
        public Builder<T> maxQueueSize(final int maxQueueSize) {
            this.maxQueueSize = maxQueueSize;
            return this;
        }
        
        public Builder<T> maxWorkers(final int maxWorkers) {
            this.maxWorkers = maxWorkers;
            return this;
        }
        
        public Builder<T> maxBatchSize(final int maxBatchSize) {
            this.maxBatchSize = maxBatchSize;
            return this;
        }
        
        public Builder<T> addTimeout(final long addTimeout) {
            this.addTimeout = addTimeout;
            return this;
        }
        
        public Builder<T> processBatch(final ProcessBatch<T> processBatch) {
            this.processBatch = processBatch;
            return this;
        }
        
        public Builder<T> stopMarker(final T stopMarker) {
            this.stopMarker = stopMarker;
            return this;
        }
        
        public Builder<T> name(final String name) {
            this.name = name;
            return this;
        }
        
        private void notNull(final Object o) {
            o.getClass();
        }
        
        QueueService<T> build() {
            this.notNull(this.processBatch);
            this.notNull(this.stopMarker);
            return new QueueService<T>(this.maxQueueSize, this.maxWorkers, this.maxBatchSize, this.addTimeout, this.stopMarker, this.processBatch, this.name, null);
        }
    }
    
    protected class Worker implements Runnable
    {
        private final String workerId;
        
        public Worker(final String workerId) {
            this.workerId = workerId;
        }
        
        private void sync() {
            try {
                synchronized (QueueService.this.syncFinishNotifier) {
                    if (QueueService.this.syncCount.decrementAndGet() > 0) {
                        while (!QueueService.this.queue.offer(QueueService.this.syncMarker)) {
                            Thread.sleep(10L);
                        }
                        QueueService.this.syncFinishNotifier.wait();
                    }
                    else {
                        QueueService.this.syncFinishNotifier.notifyAll();
                    }
                }
            }
            catch (final InterruptedException ex) {}
        }
        
        @Override
        public void run() {
            final List<T> batch = new ArrayList<T>(QueueService.this.maxBatchSize);
            for (boolean running = true; running; running = !QueueService.this.stopping) {
                int batchSize = 0;
                T item;
                try {
                    item = QueueService.this.queue.take();
                }
                catch (final InterruptedException ignored) {
                    break;
                }
                if (item != QueueService.this.syncMarker) {
                    batch.add(item);
                    ++batchSize;
                    while (batchSize < QueueService.this.maxBatchSize) {
                        item = (T)QueueService.this.queue.poll();
                        if (item == QueueService.this.syncMarker) {
                            break;
                        }
                        if (item == null) {
                            break;
                        }
                        batch.add(item);
                        ++batchSize;
                    }
                    QueueService.this.processBatch.process(this.workerId, batch);
                    batch.clear();
                }
                if (item == QueueService.this.syncMarker) {
                    this.sync();
                }
            }
        }
    }
    
    public interface ProcessBatch<T>
    {
        void process(final String p0, final Collection<T> p1);
    }
}
