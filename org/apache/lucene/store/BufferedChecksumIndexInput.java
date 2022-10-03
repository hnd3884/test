package org.apache.lucene.store;

import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class BufferedChecksumIndexInput extends ChecksumIndexInput
{
    final IndexInput main;
    final Checksum digest;
    
    public BufferedChecksumIndexInput(final IndexInput main) {
        super("BufferedChecksumIndexInput(" + main + ")");
        this.main = main;
        this.digest = new BufferedChecksum(new CRC32());
    }
    
    @Override
    public byte readByte() throws IOException {
        final byte b = this.main.readByte();
        this.digest.update(b);
        return b;
    }
    
    @Override
    public void readBytes(final byte[] b, final int offset, final int len) throws IOException {
        this.main.readBytes(b, offset, len);
        this.digest.update(b, offset, len);
    }
    
    @Override
    public long getChecksum() {
        return this.digest.getValue();
    }
    
    @Override
    public void close() throws IOException {
        this.main.close();
    }
    
    @Override
    public long getFilePointer() {
        return this.main.getFilePointer();
    }
    
    @Override
    public long length() {
        return this.main.length();
    }
    
    @Override
    public IndexInput clone() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public IndexInput slice(final String sliceDescription, final long offset, final long length) throws IOException {
        throw new UnsupportedOperationException();
    }
}
