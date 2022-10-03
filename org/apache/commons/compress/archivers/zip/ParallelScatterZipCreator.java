package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutionException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.io.IOException;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import java.util.concurrent.Future;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;
import java.util.concurrent.ExecutorService;
import java.util.Deque;

public class ParallelScatterZipCreator
{
    private final Deque<ScatterZipOutputStream> streams;
    private final ExecutorService es;
    private final ScatterGatherBackingStoreSupplier backingStoreSupplier;
    private final Deque<Future<? extends ScatterZipOutputStream>> futures;
    private final long startedAt;
    private long compressionDoneAt;
    private long scatterDoneAt;
    private final int compressionLevel;
    private final ThreadLocal<ScatterZipOutputStream> tlScatterStreams;
    
    private ScatterZipOutputStream createDeferred(final ScatterGatherBackingStoreSupplier scatterGatherBackingStoreSupplier) throws IOException {
        final ScatterGatherBackingStore bs = scatterGatherBackingStoreSupplier.get();
        final StreamCompressor sc = StreamCompressor.create(this.compressionLevel, bs);
        return new ScatterZipOutputStream(bs, sc);
    }
    
    public ParallelScatterZipCreator() {
        this(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }
    
    public ParallelScatterZipCreator(final ExecutorService executorService) {
        this(executorService, new DefaultBackingStoreSupplier());
    }
    
    public ParallelScatterZipCreator(final ExecutorService executorService, final ScatterGatherBackingStoreSupplier backingStoreSupplier) {
        this(executorService, backingStoreSupplier, -1);
    }
    
    public ParallelScatterZipCreator(final ExecutorService executorService, final ScatterGatherBackingStoreSupplier backingStoreSupplier, final int compressionLevel) throws IllegalArgumentException {
        this.streams = new ConcurrentLinkedDeque<ScatterZipOutputStream>();
        this.futures = new ConcurrentLinkedDeque<Future<? extends ScatterZipOutputStream>>();
        this.startedAt = System.currentTimeMillis();
        this.tlScatterStreams = new ThreadLocal<ScatterZipOutputStream>() {
            @Override
            protected ScatterZipOutputStream initialValue() {
                try {
                    final ScatterZipOutputStream scatterStream = ParallelScatterZipCreator.this.createDeferred(ParallelScatterZipCreator.this.backingStoreSupplier);
                    ParallelScatterZipCreator.this.streams.add(scatterStream);
                    return scatterStream;
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        if ((compressionLevel < 0 || compressionLevel > 9) && compressionLevel != -1) {
            throw new IllegalArgumentException("Compression level is expected between -1~9");
        }
        this.backingStoreSupplier = backingStoreSupplier;
        this.es = executorService;
        this.compressionLevel = compressionLevel;
    }
    
    public void addArchiveEntry(final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier source) {
        this.submitStreamAwareCallable(this.createCallable(zipArchiveEntry, source));
    }
    
    public void addArchiveEntry(final ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
        this.submitStreamAwareCallable(this.createCallable(zipArchiveEntryRequestSupplier));
    }
    
    public final void submit(final Callable<?> callable) {
        this.submitStreamAwareCallable(() -> {
            callable.call();
            return (ScatterZipOutputStream)this.tlScatterStreams.get();
        });
    }
    
    public final void submitStreamAwareCallable(final Callable<? extends ScatterZipOutputStream> callable) {
        this.futures.add(this.es.submit(callable));
    }
    
    public final Callable<ScatterZipOutputStream> createCallable(final ZipArchiveEntry zipArchiveEntry, final InputStreamSupplier source) {
        final int method = zipArchiveEntry.getMethod();
        if (method == -1) {
            throw new IllegalArgumentException("Method must be set on zipArchiveEntry: " + zipArchiveEntry);
        }
        final ZipArchiveEntryRequest zipArchiveEntryRequest = ZipArchiveEntryRequest.createZipArchiveEntryRequest(zipArchiveEntry, source);
        return () -> {
            final ScatterZipOutputStream scatterStream = this.tlScatterStreams.get();
            scatterStream.addArchiveEntry(zipArchiveEntryRequest);
            return scatterStream;
        };
    }
    
    public final Callable<ScatterZipOutputStream> createCallable(final ZipArchiveEntryRequestSupplier zipArchiveEntryRequestSupplier) {
        return () -> {
            final ScatterZipOutputStream scatterStream = this.tlScatterStreams.get();
            scatterStream.addArchiveEntry(zipArchiveEntryRequestSupplier.get());
            return scatterStream;
        };
    }
    
    public void writeTo(final ZipArchiveOutputStream targetStream) throws IOException, InterruptedException, ExecutionException {
        try {
            try {
                for (final Future<?> future : this.futures) {
                    future.get();
                }
            }
            finally {
                this.es.shutdown();
            }
            this.es.awaitTermination(60000L, TimeUnit.SECONDS);
            this.compressionDoneAt = System.currentTimeMillis();
            for (final Future<? extends ScatterZipOutputStream> future2 : this.futures) {
                final ScatterZipOutputStream scatterStream = (ScatterZipOutputStream)future2.get();
                scatterStream.zipEntryWriter().writeNextZipEntry(targetStream);
            }
            for (final ScatterZipOutputStream scatterStream2 : this.streams) {
                scatterStream2.close();
            }
            this.scatterDoneAt = System.currentTimeMillis();
        }
        finally {
            this.closeAll();
        }
    }
    
    public ScatterStatistics getStatisticsMessage() {
        return new ScatterStatistics(this.compressionDoneAt - this.startedAt, this.scatterDoneAt - this.compressionDoneAt);
    }
    
    private void closeAll() {
        for (final ScatterZipOutputStream scatterStream : this.streams) {
            try {
                scatterStream.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private static class DefaultBackingStoreSupplier implements ScatterGatherBackingStoreSupplier
    {
        final AtomicInteger storeNum;
        
        private DefaultBackingStoreSupplier() {
            this.storeNum = new AtomicInteger(0);
        }
        
        @Override
        public ScatterGatherBackingStore get() throws IOException {
            final File tempFile = File.createTempFile("parallelscatter", "n" + this.storeNum.incrementAndGet());
            return new FileBasedScatterGatherBackingStore(tempFile);
        }
    }
}
