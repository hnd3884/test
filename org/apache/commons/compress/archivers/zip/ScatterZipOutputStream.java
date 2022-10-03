package org.apache.commons.compress.archivers.zip;

import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.Iterator;
import org.apache.commons.compress.utils.BoundedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import java.util.Queue;
import java.io.Closeable;

public class ScatterZipOutputStream implements Closeable
{
    private final Queue<CompressedEntry> items;
    private final ScatterGatherBackingStore backingStore;
    private final StreamCompressor streamCompressor;
    private final AtomicBoolean isClosed;
    private ZipEntryWriter zipEntryWriter;
    
    public ScatterZipOutputStream(final ScatterGatherBackingStore backingStore, final StreamCompressor streamCompressor) {
        this.items = new ConcurrentLinkedQueue<CompressedEntry>();
        this.isClosed = new AtomicBoolean();
        this.backingStore = backingStore;
        this.streamCompressor = streamCompressor;
    }
    
    public void addArchiveEntry(final ZipArchiveEntryRequest zipArchiveEntryRequest) throws IOException {
        try (final InputStream payloadStream = zipArchiveEntryRequest.getPayloadStream()) {
            this.streamCompressor.deflate(payloadStream, zipArchiveEntryRequest.getMethod());
        }
        this.items.add(new CompressedEntry(zipArchiveEntryRequest, this.streamCompressor.getCrc32(), this.streamCompressor.getBytesWrittenForLastEntry(), this.streamCompressor.getBytesRead()));
    }
    
    public void writeTo(final ZipArchiveOutputStream target) throws IOException {
        this.backingStore.closeForWriting();
        try (final InputStream data = this.backingStore.getInputStream()) {
            for (final CompressedEntry compressedEntry : this.items) {
                try (final BoundedInputStream rawStream = new BoundedInputStream(data, compressedEntry.compressedSize)) {
                    target.addRawArchiveEntry(compressedEntry.transferToArchiveEntry(), rawStream);
                }
            }
        }
    }
    
    public ZipEntryWriter zipEntryWriter() throws IOException {
        if (this.zipEntryWriter == null) {
            this.zipEntryWriter = new ZipEntryWriter(this);
        }
        return this.zipEntryWriter;
    }
    
    @Override
    public void close() throws IOException {
        if (!this.isClosed.compareAndSet(false, true)) {
            return;
        }
        try {
            if (this.zipEntryWriter != null) {
                this.zipEntryWriter.close();
            }
            this.backingStore.close();
        }
        finally {
            this.streamCompressor.close();
        }
    }
    
    public static ScatterZipOutputStream fileBased(final File file) throws FileNotFoundException {
        return fileBased(file, -1);
    }
    
    public static ScatterZipOutputStream fileBased(final File file, final int compressionLevel) throws FileNotFoundException {
        final ScatterGatherBackingStore bs = new FileBasedScatterGatherBackingStore(file);
        final StreamCompressor sc = StreamCompressor.create(compressionLevel, bs);
        return new ScatterZipOutputStream(bs, sc);
    }
    
    private static class CompressedEntry
    {
        final ZipArchiveEntryRequest zipArchiveEntryRequest;
        final long crc;
        final long compressedSize;
        final long size;
        
        public CompressedEntry(final ZipArchiveEntryRequest zipArchiveEntryRequest, final long crc, final long compressedSize, final long size) {
            this.zipArchiveEntryRequest = zipArchiveEntryRequest;
            this.crc = crc;
            this.compressedSize = compressedSize;
            this.size = size;
        }
        
        public ZipArchiveEntry transferToArchiveEntry() {
            final ZipArchiveEntry entry = this.zipArchiveEntryRequest.getZipArchiveEntry();
            entry.setCompressedSize(this.compressedSize);
            entry.setSize(this.size);
            entry.setCrc(this.crc);
            entry.setMethod(this.zipArchiveEntryRequest.getMethod());
            return entry;
        }
    }
    
    public static class ZipEntryWriter implements Closeable
    {
        private final Iterator<CompressedEntry> itemsIterator;
        private final InputStream itemsIteratorData;
        
        public ZipEntryWriter(final ScatterZipOutputStream scatter) throws IOException {
            scatter.backingStore.closeForWriting();
            this.itemsIterator = scatter.items.iterator();
            this.itemsIteratorData = scatter.backingStore.getInputStream();
        }
        
        @Override
        public void close() throws IOException {
            if (this.itemsIteratorData != null) {
                this.itemsIteratorData.close();
            }
        }
        
        public void writeNextZipEntry(final ZipArchiveOutputStream target) throws IOException {
            final CompressedEntry compressedEntry = this.itemsIterator.next();
            try (final BoundedInputStream rawStream = new BoundedInputStream(this.itemsIteratorData, compressedEntry.compressedSize)) {
                target.addRawArchiveEntry(compressedEntry.transferToArchiveEntry(), rawStream);
            }
        }
    }
}
