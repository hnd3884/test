package org.apache.tika.pipes.async;

import org.apache.tika.pipes.emitter.TikaEmitterException;
import java.io.IOException;
import org.apache.tika.utils.ExceptionUtils;
import org.apache.tika.pipes.emitter.Emitter;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.apache.tika.metadata.Metadata;
import java.util.List;
import org.apache.tika.pipes.emitter.EmitKey;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.time.Instant;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.tika.pipes.emitter.EmitterManager;
import org.slf4j.Logger;
import org.apache.tika.pipes.emitter.EmitData;
import java.util.concurrent.Callable;

public class AsyncEmitter implements Callable<Integer>
{
    static final EmitData EMIT_DATA_STOP_SEMAPHORE;
    static final int EMITTER_FUTURE_CODE = 2;
    private static final Logger LOG;
    private final AsyncConfig asyncConfig;
    private final EmitterManager emitterManager;
    private final ArrayBlockingQueue<EmitData> emitDataQueue;
    Instant lastEmitted;
    
    public AsyncEmitter(final AsyncConfig asyncConfig, final ArrayBlockingQueue<EmitData> emitData, final EmitterManager emitterManager) {
        this.lastEmitted = Instant.now();
        this.asyncConfig = asyncConfig;
        this.emitDataQueue = emitData;
        this.emitterManager = emitterManager;
    }
    
    @Override
    public Integer call() throws Exception {
        final EmitDataCache cache = new EmitDataCache(this.asyncConfig.getEmitMaxEstimatedBytes());
        while (true) {
            final EmitData emitData = this.emitDataQueue.poll(500L, TimeUnit.MILLISECONDS);
            if (emitData == AsyncEmitter.EMIT_DATA_STOP_SEMAPHORE) {
                break;
            }
            if (emitData != null) {
                cache.add(emitData);
            }
            else {
                AsyncEmitter.LOG.trace("Nothing on the async queue");
            }
            AsyncEmitter.LOG.debug("cache size: ({}) bytes and extract count: {}", (Object)cache.estimatedSize, (Object)cache.size);
            final long elapsed = ChronoUnit.MILLIS.between(this.lastEmitted, Instant.now());
            if (elapsed <= this.asyncConfig.getEmitWithinMillis()) {
                continue;
            }
            AsyncEmitter.LOG.debug("{} elapsed > {}, going to emitAll", (Object)elapsed, (Object)this.asyncConfig.getEmitWithinMillis());
            cache.emitAll();
        }
        cache.emitAll();
        return 2;
    }
    
    static {
        EMIT_DATA_STOP_SEMAPHORE = new EmitData(null, null);
        LOG = LoggerFactory.getLogger((Class)AsyncEmitter.class);
    }
    
    private class EmitDataCache
    {
        private final long maxBytes;
        long estimatedSize;
        int size;
        Map<String, List<EmitData>> map;
        
        public EmitDataCache(final long maxBytes) {
            this.estimatedSize = 0L;
            this.size = 0;
            this.map = new HashMap<String, List<EmitData>>();
            this.maxBytes = maxBytes;
        }
        
        void updateEstimatedSize(final long newBytes) {
            this.estimatedSize += newBytes;
        }
        
        void add(final EmitData data) {
            ++this.size;
            final long sz = data.getEstimatedSizeBytes();
            if (this.estimatedSize + sz > this.maxBytes) {
                AsyncEmitter.LOG.debug("estimated size ({}) > maxBytes({}), going to emitAll", (Object)(this.estimatedSize + sz), (Object)this.maxBytes);
                this.emitAll();
            }
            final List<EmitData> cached = this.map.computeIfAbsent(data.getEmitKey().getEmitterName(), k -> new ArrayList());
            this.updateEstimatedSize(sz);
            cached.add(data);
        }
        
        private void emitAll() {
            int emitted = 0;
            AsyncEmitter.LOG.debug("about to emit {} files, {} estimated bytes", (Object)this.size, (Object)this.estimatedSize);
            for (final Map.Entry<String, List<EmitData>> e : this.map.entrySet()) {
                final Emitter emitter = AsyncEmitter.this.emitterManager.getEmitter(e.getKey());
                this.tryToEmit(emitter, e.getValue());
                emitted += e.getValue().size();
            }
            AsyncEmitter.LOG.debug("emitted: {} files", (Object)emitted);
            this.estimatedSize = 0L;
            this.size = 0;
            this.map.clear();
            AsyncEmitter.this.lastEmitted = Instant.now();
        }
        
        private void tryToEmit(final Emitter emitter, final List<EmitData> cachedEmitData) {
            try {
                emitter.emit(cachedEmitData);
            }
            catch (final IOException | TikaEmitterException e) {
                AsyncEmitter.LOG.warn("emitter class ({}): {}", (Object)emitter.getClass(), (Object)ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
