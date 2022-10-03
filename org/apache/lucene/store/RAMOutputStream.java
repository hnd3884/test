package org.apache.lucene.store;

import java.util.Collections;
import org.apache.lucene.util.Accountables;
import java.util.Collection;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.lucene.util.Accountable;

public class RAMOutputStream extends IndexOutput implements Accountable
{
    static final int BUFFER_SIZE = 1024;
    private final RAMFile file;
    private byte[] currentBuffer;
    private int currentBufferIndex;
    private int bufferPosition;
    private long bufferStart;
    private int bufferLength;
    private final Checksum crc;
    
    public RAMOutputStream() {
        this("noname", new RAMFile(), false);
    }
    
    public RAMOutputStream(final RAMFile f, final boolean checksum) {
        this("noname", f, checksum);
    }
    
    public RAMOutputStream(final String name, final RAMFile f, final boolean checksum) {
        super("RAMOutputStream(name=\"" + name + "\")");
        this.file = f;
        this.currentBufferIndex = -1;
        this.currentBuffer = null;
        if (checksum) {
            this.crc = new BufferedChecksum(new CRC32());
        }
        else {
            this.crc = null;
        }
    }
    
    public void writeTo(final DataOutput out) throws IOException {
        this.flush();
        final long end = this.file.length;
        long pos = 0L;
        int buffer = 0;
        while (pos < end) {
            int length = 1024;
            final long nextPos = pos + length;
            if (nextPos > end) {
                length = (int)(end - pos);
            }
            out.writeBytes(this.file.getBuffer(buffer++), length);
            pos = nextPos;
        }
    }
    
    public void writeTo(final byte[] bytes, final int offset) throws IOException {
        this.flush();
        final long end = this.file.length;
        long pos = 0L;
        int buffer = 0;
        int bytesUpto = offset;
        while (pos < end) {
            int length = 1024;
            final long nextPos = pos + length;
            if (nextPos > end) {
                length = (int)(end - pos);
            }
            System.arraycopy(this.file.getBuffer(buffer++), 0, bytes, bytesUpto, length);
            bytesUpto += length;
            pos = nextPos;
        }
    }
    
    public void reset() {
        this.currentBuffer = null;
        this.currentBufferIndex = -1;
        this.bufferPosition = 0;
        this.bufferStart = 0L;
        this.bufferLength = 0;
        this.file.setLength(0L);
        if (this.crc != null) {
            this.crc.reset();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
    }
    
    @Override
    public void writeByte(final byte b) throws IOException {
        if (this.bufferPosition == this.bufferLength) {
            ++this.currentBufferIndex;
            this.switchCurrentBuffer();
        }
        if (this.crc != null) {
            this.crc.update(b);
        }
        this.currentBuffer[this.bufferPosition++] = b;
    }
    
    @Override
    public void writeBytes(final byte[] b, int offset, int len) throws IOException {
        assert b != null;
        if (this.crc != null) {
            this.crc.update(b, offset, len);
        }
        while (len > 0) {
            if (this.bufferPosition == this.bufferLength) {
                ++this.currentBufferIndex;
                this.switchCurrentBuffer();
            }
            final int remainInBuffer = this.currentBuffer.length - this.bufferPosition;
            final int bytesToCopy = (len < remainInBuffer) ? len : remainInBuffer;
            System.arraycopy(b, offset, this.currentBuffer, this.bufferPosition, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            this.bufferPosition += bytesToCopy;
        }
    }
    
    private final void switchCurrentBuffer() {
        if (this.currentBufferIndex == this.file.numBuffers()) {
            this.currentBuffer = this.file.addBuffer(1024);
        }
        else {
            this.currentBuffer = this.file.getBuffer(this.currentBufferIndex);
        }
        this.bufferPosition = 0;
        this.bufferStart = 1024L * this.currentBufferIndex;
        this.bufferLength = this.currentBuffer.length;
    }
    
    private void setFileLength() {
        final long pointer = this.bufferStart + this.bufferPosition;
        if (pointer > this.file.length) {
            this.file.setLength(pointer);
        }
    }
    
    protected void flush() throws IOException {
        this.setFileLength();
    }
    
    @Override
    public long getFilePointer() {
        return (this.currentBufferIndex < 0) ? 0L : (this.bufferStart + this.bufferPosition);
    }
    
    @Override
    public long ramBytesUsed() {
        return this.file.numBuffers() * 1024L;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return Collections.singleton(Accountables.namedAccountable("file", this.file));
    }
    
    @Override
    public long getChecksum() throws IOException {
        if (this.crc == null) {
            throw new IllegalStateException("internal RAMOutputStream created with checksum disabled");
        }
        return this.crc.getValue();
    }
}
