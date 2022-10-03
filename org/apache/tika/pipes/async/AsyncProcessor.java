package org.apache.tika.pipes.async;

import org.apache.tika.pipes.PipesResult;
import org.apache.tika.pipes.PipesConfigBase;
import org.apache.tika.pipes.PipesClient;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import org.apache.tika.pipes.pipesiterator.PipesIterator;
import java.util.concurrent.TimeUnit;
import org.apache.tika.pipes.PipesException;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.pipes.emitter.EmitterManager;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutorCompletionService;
import org.apache.tika.pipes.emitter.EmitData;
import org.apache.tika.pipes.FetchEmitTuple;
import java.util.concurrent.ArrayBlockingQueue;
import org.slf4j.Logger;
import java.io.Closeable;

public class AsyncProcessor implements Closeable
{
    static final int PARSER_FUTURE_CODE = 1;
    static final int WATCHER_FUTURE_CODE = 3;
    private static final Logger LOG;
    private final ArrayBlockingQueue<FetchEmitTuple> fetchEmitTuples;
    private final ArrayBlockingQueue<EmitData> emitData;
    private final ExecutorCompletionService<Integer> executorCompletionService;
    private final ExecutorService executorService;
    private final AsyncConfig asyncConfig;
    private final AtomicLong totalProcessed;
    private static long MAX_OFFER_WAIT_MS;
    private volatile int numParserThreadsFinished;
    private volatile int numEmitterThreadsFinished;
    private boolean addedEmitterSemaphores;
    boolean isShuttingDown;
    
    public AsyncProcessor(final Path tikaConfigPath) throws TikaException, IOException {
        this.totalProcessed = new AtomicLong(0L);
        this.numParserThreadsFinished = 0;
        this.numEmitterThreadsFinished = 0;
        this.addedEmitterSemaphores = false;
        this.isShuttingDown = false;
        this.asyncConfig = AsyncConfig.load(tikaConfigPath);
        this.fetchEmitTuples = new ArrayBlockingQueue<FetchEmitTuple>(this.asyncConfig.getQueueSize());
        this.emitData = new ArrayBlockingQueue<EmitData>(100);
        this.executorService = Executors.newFixedThreadPool(this.asyncConfig.getNumClients() + this.asyncConfig.getNumEmitters() + 1);
        (this.executorCompletionService = new ExecutorCompletionService<Integer>(this.executorService)).submit(() -> {
            try {
                while (true) {
                    Thread.sleep(500L);
                    this.checkActive();
                }
            }
            catch (final InterruptedException e) {
                return Integer.valueOf(3);
            }
        });
        for (int i = 0; i < this.asyncConfig.getNumClients(); ++i) {
            this.executorCompletionService.submit(new FetchEmitWorker(this.asyncConfig, (ArrayBlockingQueue)this.fetchEmitTuples, (ArrayBlockingQueue)this.emitData));
        }
        final EmitterManager emitterManager = EmitterManager.load(tikaConfigPath);
        for (int j = 0; j < this.asyncConfig.getNumEmitters(); ++j) {
            this.executorCompletionService.submit(new AsyncEmitter(this.asyncConfig, this.emitData, emitterManager));
        }
    }
    
    public synchronized boolean offer(final List<FetchEmitTuple> newFetchEmitTuples, final long offerMs) throws PipesException, InterruptedException {
        if (this.isShuttingDown) {
            throw new IllegalStateException("Can't call offer after calling close() or shutdownNow()");
        }
        if (newFetchEmitTuples.size() > this.asyncConfig.getQueueSize()) {
            throw new OfferLargerThanQueueSize(newFetchEmitTuples.size(), this.asyncConfig.getQueueSize());
        }
        for (long start = System.currentTimeMillis(), elapsed = System.currentTimeMillis() - start; elapsed < offerMs; elapsed = System.currentTimeMillis() - start) {
            if (this.fetchEmitTuples.remainingCapacity() > newFetchEmitTuples.size()) {
                try {
                    this.fetchEmitTuples.addAll((Collection<?>)newFetchEmitTuples);
                    return true;
                }
                catch (final IllegalStateException e) {
                    e.printStackTrace();
                }
            }
            Thread.sleep(100L);
        }
        return false;
    }
    
    public int getCapacity() {
        return this.fetchEmitTuples.remainingCapacity();
    }
    
    public synchronized boolean offer(final FetchEmitTuple t, final long offerMs) throws PipesException, InterruptedException {
        if (this.fetchEmitTuples == null) {
            throw new IllegalStateException("queue hasn't been initialized yet.");
        }
        if (this.isShuttingDown) {
            throw new IllegalStateException("Can't call offer after calling close() or shutdownNow()");
        }
        this.checkActive();
        return this.fetchEmitTuples.offer(t, offerMs, TimeUnit.MILLISECONDS);
    }
    
    public void finished() throws InterruptedException {
        for (int i = 0; i < this.asyncConfig.getNumClients(); ++i) {
            final boolean offered = this.fetchEmitTuples.offer(PipesIterator.COMPLETED_SEMAPHORE, AsyncProcessor.MAX_OFFER_WAIT_MS, TimeUnit.MILLISECONDS);
            if (!offered) {
                throw new RuntimeException("Couldn't offer completed semaphore within " + AsyncProcessor.MAX_OFFER_WAIT_MS + " ms");
            }
        }
    }
    
    public synchronized boolean checkActive() {
        final Future<Integer> future = this.executorCompletionService.poll();
        if (future != null) {
            try {
                final Integer i = future.get();
                switch (i) {
                    case 1: {
                        ++this.numParserThreadsFinished;
                        AsyncProcessor.LOG.debug("fetchEmitWorker finished, total {}", (Object)this.numParserThreadsFinished);
                        break;
                    }
                    case 2: {
                        ++this.numEmitterThreadsFinished;
                        AsyncProcessor.LOG.debug("emitter thread finished, total {}", (Object)this.numEmitterThreadsFinished);
                        break;
                    }
                    case 3: {
                        AsyncProcessor.LOG.debug("watcher thread finished");
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Don't recognize this future code: " + i);
                    }
                }
            }
            catch (final InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if (this.numParserThreadsFinished == this.asyncConfig.getNumClients() && !this.addedEmitterSemaphores) {
            for (int j = 0; j < this.asyncConfig.getNumEmitters(); ++j) {
                try {
                    final boolean offered = this.emitData.offer(AsyncEmitter.EMIT_DATA_STOP_SEMAPHORE, AsyncProcessor.MAX_OFFER_WAIT_MS, TimeUnit.MILLISECONDS);
                    if (!offered) {
                        throw new RuntimeException("Couldn't offer emit data stop semaphore within " + AsyncProcessor.MAX_OFFER_WAIT_MS + " ms");
                    }
                }
                catch (final InterruptedException e2) {
                    throw new RuntimeException(e2);
                }
            }
            this.addedEmitterSemaphores = true;
        }
        return this.numParserThreadsFinished != this.asyncConfig.getNumClients() || this.numEmitterThreadsFinished != this.asyncConfig.getNumEmitters();
    }
    
    @Override
    public void close() throws IOException {
        this.executorService.shutdownNow();
        this.asyncConfig.getPipesReporter().close();
    }
    
    public long getTotalProcessed() {
        return this.totalProcessed.get();
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)AsyncProcessor.class);
        AsyncProcessor.MAX_OFFER_WAIT_MS = 120000L;
    }
    
    private class FetchEmitWorker implements Callable<Integer>
    {
        private final AsyncConfig asyncConfig;
        private final ArrayBlockingQueue<FetchEmitTuple> fetchEmitTuples;
        private final ArrayBlockingQueue<EmitData> emitDataQueue;
        
        private FetchEmitWorker(final AsyncConfig asyncConfig, final ArrayBlockingQueue<FetchEmitTuple> fetchEmitTuples, final ArrayBlockingQueue<EmitData> emitDataQueue) {
            this.asyncConfig = asyncConfig;
            this.fetchEmitTuples = fetchEmitTuples;
            this.emitDataQueue = emitDataQueue;
        }
        
        @Override
        public Integer call() throws Exception {
            try (final PipesClient pipesClient = new PipesClient(this.asyncConfig)) {
                while (true) {
                    final FetchEmitTuple t = this.fetchEmitTuples.poll(1L, TimeUnit.SECONDS);
                    if (t == null) {
                        if (!AsyncProcessor.LOG.isTraceEnabled()) {
                            continue;
                        }
                        AsyncProcessor.LOG.trace("null fetch emit tuple");
                    }
                    else {
                        if (t == PipesIterator.COMPLETED_SEMAPHORE) {
                            if (AsyncProcessor.LOG.isTraceEnabled()) {
                                AsyncProcessor.LOG.trace("hit completed semaphore");
                            }
                            return 1;
                        }
                        PipesResult result = null;
                        final long start = System.currentTimeMillis();
                        try {
                            result = pipesClient.process(t);
                        }
                        catch (final IOException e) {
                            result = PipesResult.UNSPECIFIED_CRASH;
                        }
                        if (AsyncProcessor.LOG.isTraceEnabled()) {
                            AsyncProcessor.LOG.trace("timer -- pipes client process: {} ms", (Object)(System.currentTimeMillis() - start));
                        }
                        final long offerStart = System.currentTimeMillis();
                        if (result.getStatus() == PipesResult.STATUS.PARSE_SUCCESS || result.getStatus() == PipesResult.STATUS.PARSE_SUCCESS_WITH_EXCEPTION) {
                            final boolean offered = this.emitDataQueue.offer(result.getEmitData(), AsyncProcessor.MAX_OFFER_WAIT_MS, TimeUnit.MILLISECONDS);
                            if (!offered) {
                                throw new RuntimeException("Couldn't offer emit data to queue within " + AsyncProcessor.MAX_OFFER_WAIT_MS + " ms");
                            }
                        }
                        if (AsyncProcessor.LOG.isTraceEnabled()) {
                            AsyncProcessor.LOG.trace("timer -- offered: {} ms", (Object)(System.currentTimeMillis() - offerStart));
                        }
                        final long elapsed = System.currentTimeMillis() - start;
                        this.asyncConfig.getPipesReporter().report(t, result, elapsed);
                        AsyncProcessor.this.totalProcessed.incrementAndGet();
                    }
                }
            }
        }
    }
}
