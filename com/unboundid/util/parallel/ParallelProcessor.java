package com.unboundid.util.parallel;

import com.unboundid.util.Debug;
import java.util.Iterator;
import java.util.ArrayList;
import com.unboundid.util.LDAPSDKThreadFactory;
import com.unboundid.util.Validator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Semaphore;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ParallelProcessor<I, O>
{
    private final Processor<I, O> processor;
    private final List<Thread> workers;
    private final int minPerThread;
    private final Semaphore workerSemaphore;
    private final AtomicReference<List<? extends I>> inputItems;
    private final AtomicReference<List<Result<I, O>>> outputItems;
    private final AtomicInteger nextToProcess;
    private volatile CountDownLatch processingCompleteSignal;
    private final AtomicBoolean shutdown;
    
    public ParallelProcessor(final Processor<I, O> processor, final int totalThreads, final int minPerThread) {
        this(processor, null, totalThreads, minPerThread);
    }
    
    public ParallelProcessor(final Processor<I, O> processor, final ThreadFactory threadFactory, final int totalThreads, final int minPerThread) {
        this.workerSemaphore = new Semaphore(0);
        this.inputItems = new AtomicReference<List<? extends I>>();
        this.outputItems = new AtomicReference<List<Result<I, O>>>();
        this.nextToProcess = new AtomicInteger();
        this.shutdown = new AtomicBoolean();
        Validator.ensureNotNull(processor);
        Validator.ensureTrue(totalThreads >= 1, "ParallelProcessor.totalThreads must be at least 1.");
        Validator.ensureTrue(totalThreads <= 1000, "ParallelProcessor.totalThreads must not be greater than 1000.");
        Validator.ensureTrue(minPerThread >= 1, "ParallelProcessor.minPerThread must be at least 1.");
        this.processor = processor;
        this.minPerThread = minPerThread;
        ThreadFactory tf;
        if (threadFactory == null) {
            tf = new LDAPSDKThreadFactory("ParallelProcessor-Worker", true);
        }
        else {
            tf = threadFactory;
        }
        final int numExtraThreads = totalThreads - 1;
        final List<Thread> workerList = new ArrayList<Thread>(numExtraThreads);
        for (int i = 0; i < numExtraThreads; ++i) {
            final Thread worker = tf.newThread(new Worker());
            workerList.add(worker);
            worker.start();
        }
        this.workers = workerList;
    }
    
    public synchronized ArrayList<Result<I, O>> processAll(final List<? extends I> items) throws InterruptedException, IllegalStateException {
        if (this.shutdown.get()) {
            throw new IllegalStateException("cannot call processAll() after shutdown()");
        }
        Validator.ensureNotNull(items);
        final int extraThreads = Math.min(items.size() / this.minPerThread - 1, this.workers.size());
        if (extraThreads <= 0) {
            final ArrayList<Result<I, O>> output = new ArrayList<Result<I, O>>(items.size());
            for (final I item : items) {
                output.add(this.process(item));
            }
            return output;
        }
        this.processingCompleteSignal = new CountDownLatch(extraThreads);
        this.inputItems.set(items);
        final ArrayList<Result<I, O>> output = new ArrayList<Result<I, O>>(items.size());
        for (int i = 0; i < items.size(); ++i) {
            output.add(null);
        }
        this.outputItems.set(output);
        this.nextToProcess.set(0);
        this.workerSemaphore.release(extraThreads);
        this.processInParallel();
        this.processingCompleteSignal.await();
        return output;
    }
    
    public synchronized void shutdown() throws InterruptedException {
        if (this.shutdown.getAndSet(true)) {
            return;
        }
        this.workerSemaphore.release(this.workers.size());
        for (final Thread worker : this.workers) {
            worker.join();
        }
    }
    
    private void processInParallel() {
        try {
            final List<? extends I> items = this.inputItems.get();
            final List<Result<I, O>> outputs = this.outputItems.get();
            final int size = items.size();
            int next;
            while ((next = this.nextToProcess.getAndIncrement()) < size) {
                final I input = (I)items.get(next);
                outputs.set(next, this.process(input));
            }
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    private ProcessResult process(final I input) {
        O output = null;
        Throwable failureCause = null;
        try {
            output = this.processor.process(input);
        }
        catch (final Throwable e) {
            failureCause = e;
        }
        return new ProcessResult((Object)input, (Object)output, failureCause);
    }
    
    private final class Worker implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    ParallelProcessor.this.workerSemaphore.acquire();
                }
                catch (final InterruptedException e) {
                    Debug.debugException(e);
                    Thread.currentThread().interrupt();
                }
                if (ParallelProcessor.this.shutdown.get()) {
                    break;
                }
                try {
                    ParallelProcessor.this.processInParallel();
                }
                finally {
                    ParallelProcessor.this.processingCompleteSignal.countDown();
                }
            }
        }
    }
    
    private final class ProcessResult implements Result<I, O>
    {
        private final I inputItem;
        private final O outputItem;
        private final Throwable failureCause;
        
        private ProcessResult(final I inputItem, final O outputItem, final Throwable failureCause) {
            this.inputItem = inputItem;
            this.outputItem = outputItem;
            this.failureCause = failureCause;
        }
        
        @Override
        public I getInput() {
            return this.inputItem;
        }
        
        @Override
        public O getOutput() {
            return this.outputItem;
        }
        
        @Override
        public Throwable getFailureCause() {
            return this.failureCause;
        }
    }
}
