package org.apache.lucene.bkdtree;

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

final class OfflineLatLonWriter implements LatLonWriter
{
    final Path tempFile;
    final byte[] scratchBytes;
    final ByteArrayDataOutput scratchBytesOutput;
    final OutputStreamDataOutput out;
    final long count;
    private long countWritten;
    private boolean closed;
    
    public OfflineLatLonWriter(final long count) throws IOException {
        this.scratchBytes = new byte[20];
        this.scratchBytesOutput = new ByteArrayDataOutput(this.scratchBytes);
        this.tempFile = Files.createTempFile(OfflineSorter.getDefaultTempDir(), "size" + count + ".", "", (FileAttribute<?>[])new FileAttribute[0]);
        this.out = new OutputStreamDataOutput((OutputStream)new BufferedOutputStream(Files.newOutputStream(this.tempFile, new OpenOption[0])));
        this.count = count;
    }
    
    @Override
    public void append(final int latEnc, final int lonEnc, final long ord, final int docID) throws IOException {
        this.out.writeInt(latEnc);
        this.out.writeInt(lonEnc);
        this.out.writeLong(ord);
        this.out.writeInt(docID);
        ++this.countWritten;
    }
    
    @Override
    public LatLonReader getReader(final long start) throws IOException {
        assert this.closed;
        return new OfflineLatLonReader(this.tempFile, start, this.count - start);
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
        return "OfflineLatLonWriter(count=" + this.count + " tempFile=" + this.tempFile + ")";
    }
}
