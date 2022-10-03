package com.unboundid.util.parallel;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.unboundid.util.StaticUtils;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.BlockingQueue;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class AsynchronousParallelProcessor<I, O>
{
    private final BlockingQueue<I> pendingQueue;
    private final ParallelProcessor<I, O> parallelProcessor;
    private final ResultProcessor<I, O> resultProcessor;
    private final InvokerThread invokerThread;
    private final AtomicBoolean shutdown;
    private final AtomicReference<Throwable> invocationException;
    
    public AsynchronousParallelProcessor(final BlockingQueue<I> pendingQueue, final ParallelProcessor<I, O> parallelProcessor, final ResultProcessor<I, O> resultProcessor) {
        this.shutdown = new AtomicBoolean(false);
        this.invocationException = new AtomicReference<Throwable>();
        this.pendingQueue = pendingQueue;
        this.parallelProcessor = parallelProcessor;
        this.resultProcessor = resultProcessor;
        (this.invokerThread = new InvokerThread()).start();
    }
    
    public AsynchronousParallelProcessor(final BlockingQueue<I> pendingQueue, final ParallelProcessor<I, O> parallelProcessor, final BlockingQueue<Result<I, O>> outputQueue) {
        this(pendingQueue, parallelProcessor, (ResultProcessor)new OutputEnqueuer((BlockingQueue)outputQueue));
    }
    
    public synchronized void submit(final I input) throws InterruptedException {
        if (this.shutdown.get()) {
            throw new IllegalStateException("cannot call submit() after shutdown()");
        }
        final Throwable resultProcessingError = this.invocationException.get();
        if (resultProcessingError != null) {
            this.shutdown();
            StaticUtils.throwErrorOrRuntimeException(resultProcessingError);
        }
        this.pendingQueue.put(input);
    }
    
    public synchronized void shutdown() throws InterruptedException {
        if (this.shutdown.getAndSet(true)) {
            return;
        }
        this.invokerThread.join();
        this.parallelProcessor.shutdown();
    }
    
    private static final class OutputEnqueuer<I, O> implements ResultProcessor<I, O>
    {
        private final BlockingQueue<Result<I, O>> outputQueue;
        
        private OutputEnqueuer(final BlockingQueue<Result<I, O>> outputQueue) {
            this.outputQueue = outputQueue;
        }
        
        @Override
        public void processResult(final Result<I, O> ioResult) throws Exception {
            this.outputQueue.put(ioResult);
        }
    }
    
    private final class InvokerThread extends Thread
    {
        private InvokerThread() {
            super("Asynchronous Parallel Processor");
            this.setDaemon(true);
        }
        
        @Override
        public void run() {
            while (true) {
                if (AsynchronousParallelProcessor.this.shutdown.get()) {
                    if (AsynchronousParallelProcessor.this.pendingQueue.isEmpty()) {
                        break;
                    }
                }
                try {
                    final I item = AsynchronousParallelProcessor.this.pendingQueue.poll(100L, TimeUnit.MILLISECONDS);
                    if (item == null) {
                        continue;
                    }
                    final List<I> items = new ArrayList<I>(1 + AsynchronousParallelProcessor.this.pendingQueue.size());
                    items.add(item);
                    AsynchronousParallelProcessor.this.pendingQueue.drainTo(items);
                    final List<Result<I, O>> results = AsynchronousParallelProcessor.this.parallelProcessor.processAll(items);
                    for (final Result<I, O> result : results) {
                        AsynchronousParallelProcessor.this.resultProcessor.processResult(result);
                    }
                }
                catch (final Throwable e) {
                    Debug.debugException(e);
                    AsynchronousParallelProcessor.this.invocationException.compareAndSet(null, e);
                }
            }
        }
    }
}
