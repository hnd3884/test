package org.apache.tika.pipes.pipesiterator;

import java.util.concurrent.ExecutionException;
import org.apache.tika.exception.TikaTimeoutException;
import org.slf4j.LoggerFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.pipes.emitter.EmitKey;
import org.apache.tika.pipes.fetcher.FetchKey;
import java.util.Iterator;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.config.Param;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaConfigException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import org.apache.tika.pipes.HandlerConfig;
import org.apache.tika.sax.BasicContentHandlerFactory;
import java.util.concurrent.ArrayBlockingQueue;
import org.slf4j.Logger;
import org.apache.tika.config.Initializable;
import org.apache.tika.pipes.FetchEmitTuple;
import java.util.concurrent.Callable;
import org.apache.tika.config.ConfigBase;

public abstract class PipesIterator extends ConfigBase implements Callable<Integer>, Iterable<FetchEmitTuple>, Initializable
{
    public static final long DEFAULT_MAX_WAIT_MS = 300000L;
    public static final int DEFAULT_QUEUE_SIZE = 1000;
    public static final FetchEmitTuple COMPLETED_SEMAPHORE;
    private static final Logger LOGGER;
    private long maxWaitMs;
    private ArrayBlockingQueue<FetchEmitTuple> queue;
    private int queueSize;
    private String fetcherName;
    private String emitterName;
    private FetchEmitTuple.ON_PARSE_EXCEPTION onParseException;
    private BasicContentHandlerFactory.HANDLER_TYPE handlerType;
    private HandlerConfig.PARSE_MODE parseMode;
    private int writeLimit;
    private int maxEmbeddedResources;
    private int added;
    private FutureTask<Integer> futureTask;
    
    public PipesIterator() {
        this.maxWaitMs = 300000L;
        this.queue = null;
        this.queueSize = 1000;
        this.onParseException = FetchEmitTuple.ON_PARSE_EXCEPTION.EMIT;
        this.handlerType = BasicContentHandlerFactory.HANDLER_TYPE.TEXT;
        this.parseMode = HandlerConfig.PARSE_MODE.RMETA;
        this.writeLimit = -1;
        this.maxEmbeddedResources = -1;
        this.added = 0;
    }
    
    public static PipesIterator build(final Path tikaConfigFile) throws IOException, TikaConfigException {
        try (final InputStream is = Files.newInputStream(tikaConfigFile, new OpenOption[0])) {
            return ConfigBase.buildSingle("pipesIterator", PipesIterator.class, is);
        }
    }
    
    public String getFetcherName() {
        return this.fetcherName;
    }
    
    @Field
    public void setFetcherName(final String fetcherName) {
        this.fetcherName = fetcherName;
    }
    
    public String getEmitterName() {
        return this.emitterName;
    }
    
    @Field
    public void setEmitterName(final String emitterName) {
        this.emitterName = emitterName;
    }
    
    @Field
    public void setMaxWaitMs(final long maxWaitMs) {
        this.maxWaitMs = maxWaitMs;
    }
    
    @Field
    public void setQueueSize(final int queueSize) {
        this.queueSize = queueSize;
    }
    
    public FetchEmitTuple.ON_PARSE_EXCEPTION getOnParseException() {
        return this.onParseException;
    }
    
    @Field
    public void setOnParseException(final String onParseException) throws TikaConfigException {
        if ("skip".equalsIgnoreCase(onParseException)) {
            this.setOnParseException(FetchEmitTuple.ON_PARSE_EXCEPTION.SKIP);
        }
        else {
            if (!"emit".equalsIgnoreCase(onParseException)) {
                throw new TikaConfigException("must be either 'skip' or 'emit': " + onParseException);
            }
            this.setOnParseException(FetchEmitTuple.ON_PARSE_EXCEPTION.EMIT);
        }
    }
    
    public void setOnParseException(final FetchEmitTuple.ON_PARSE_EXCEPTION onParseException) {
        this.onParseException = onParseException;
    }
    
    @Field
    public void setHandlerType(final String handlerType) {
        this.handlerType = BasicContentHandlerFactory.parseHandlerType(handlerType, BasicContentHandlerFactory.HANDLER_TYPE.TEXT);
    }
    
    @Field
    public void setWriteLimit(final int writeLimit) {
        this.writeLimit = writeLimit;
    }
    
    @Field
    public void setMaxEmbeddedResources(final int maxEmbeddedResources) {
        this.maxEmbeddedResources = maxEmbeddedResources;
    }
    
    @Field
    public void setParseMode(final String parseModeString) {
        this.setParseMode(HandlerConfig.PARSE_MODE.parseMode(parseModeString));
    }
    
    public void setParseMode(final HandlerConfig.PARSE_MODE parsePARSEMode) {
        this.parseMode = parsePARSEMode;
    }
    
    @Override
    public Integer call() throws Exception {
        this.enqueue();
        this.tryToAdd(PipesIterator.COMPLETED_SEMAPHORE);
        return this.added;
    }
    
    protected HandlerConfig getHandlerConfig() {
        return new HandlerConfig(this.handlerType, this.parseMode, this.writeLimit, this.maxEmbeddedResources);
    }
    
    protected abstract void enqueue() throws IOException, TimeoutException, InterruptedException;
    
    protected void tryToAdd(final FetchEmitTuple p) throws InterruptedException, TimeoutException {
        ++this.added;
        final boolean offered = this.queue.offer(p, this.maxWaitMs, TimeUnit.MILLISECONDS);
        if (!offered) {
            throw new TimeoutException("timed out while offering");
        }
    }
    
    @Override
    public void initialize(final Map<String, Param> params) throws TikaConfigException {
    }
    
    @Override
    public void checkInitialization(final InitializableProblemHandler problemHandler) throws TikaConfigException {
    }
    
    @Override
    public Iterator<FetchEmitTuple> iterator() {
        if (this.futureTask != null) {
            throw new IllegalStateException("Can't call iterator more than once!");
        }
        this.futureTask = new FutureTask<Integer>(this);
        this.queue = new ArrayBlockingQueue<FetchEmitTuple>(this.queueSize);
        new Thread(this.futureTask).start();
        return new TupleIterator();
    }
    
    static {
        COMPLETED_SEMAPHORE = new FetchEmitTuple(null, null, null, null, null, null);
        LOGGER = LoggerFactory.getLogger((Class)PipesIterator.class);
    }
    
    private class TupleIterator implements Iterator<FetchEmitTuple>
    {
        FetchEmitTuple next;
        
        private TupleIterator() {
            this.next = null;
        }
        
        @Override
        public boolean hasNext() {
            if (this.next == null) {
                this.next = this.pollNext();
            }
            return this.next != PipesIterator.COMPLETED_SEMAPHORE;
        }
        
        @Override
        public FetchEmitTuple next() {
            if (this.next == PipesIterator.COMPLETED_SEMAPHORE) {
                throw new IllegalStateException("don't call next() after hasNext() has returned false!");
            }
            final FetchEmitTuple ret = this.next;
            this.next = this.pollNext();
            return ret;
        }
        
        private FetchEmitTuple pollNext() throws TikaTimeoutException {
            FetchEmitTuple t = null;
            final long start = System.currentTimeMillis();
            try {
                for (long elapsed = System.currentTimeMillis() - start; t == null && elapsed < PipesIterator.this.maxWaitMs; t = PipesIterator.this.queue.poll(100L, TimeUnit.MILLISECONDS), elapsed = System.currentTimeMillis() - start) {
                    this.checkThreadOk();
                }
            }
            catch (final InterruptedException e) {
                PipesIterator.LOGGER.warn("interrupted");
                return PipesIterator.COMPLETED_SEMAPHORE;
            }
            if (t == null) {
                throw new TikaTimeoutException("waited longer than " + PipesIterator.this.maxWaitMs + "ms for the next tuple");
            }
            return t;
        }
        
        private void checkThreadOk() throws InterruptedException {
            if (PipesIterator.this.futureTask.isDone()) {
                try {
                    PipesIterator.this.futureTask.get();
                }
                catch (final ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                }
            }
        }
    }
}
