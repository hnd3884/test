package org.apache.lucene.rangetree;

import org.apache.lucene.util.IOUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import org.apache.lucene.util.OfflineSorter;
import org.apache.lucene.store.OutputStreamDataOutput;
import org.apache.lucene.store.ByteArrayDataOutput;
import java.nio.file.Path;

final class OfflineSliceWriter implements SliceWriter
{
    final Path tempFile;
    final byte[] scratchBytes;
    final ByteArrayDataOutput scratchBytesOutput;
    final OutputStreamDataOutput out;
    final long count;
    private boolean closed;
    private long countWritten;
    
    public OfflineSliceWriter(final long count) throws IOException {
        this.scratchBytes = new byte[20];
        this.scratchBytesOutput = new ByteArrayDataOutput(this.scratchBytes);
        this.tempFile = Files.createTempFile(OfflineSorter.getDefaultTempDir(), "size" + count + ".", "", (FileAttribute<?>[])new FileAttribute[0]);
        this.out = new OutputStreamDataOutput((OutputStream)new BufferedOutputStream(Files.newOutputStream(this.tempFile, new OpenOption[0])));
        this.count = count;
    }
    
    @Override
    public void append(final long value, final long ord, final int docID) throws IOException {
        this.out.writeLong(value);
        this.out.writeLong(ord);
        this.out.writeInt(docID);
        ++this.countWritten;
    }
    
    @Override
    public SliceReader getReader(final long start) throws IOException {
        assert this.closed;
        return new OfflineSliceReader(this.tempFile, start, this.count - start);
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
        this.out.close();
        if (this.count != this.countWritten) {
            throw new IllegalStateException("wrote " + this.countWritten + " values, but expected " + this.count);
        }
    }
    
    @Override
    public void destroy() throws IOException {
        IOUtils.rm(new Path[] { this.tempFile });
    }
    
    @Override
    public String toString() {
        return "OfflineSliceWriter(count=" + this.count + " tempFile=" + this.tempFile + ")";
    }
}
