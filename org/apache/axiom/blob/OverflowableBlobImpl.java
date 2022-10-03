package org.apache.axiom.blob;

import org.apache.axiom.ext.io.ReadFromSupport;
import org.apache.axiom.ext.io.StreamCopyException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

final class OverflowableBlobImpl implements OverflowableBlob
{
    final int chunkSize;
    final WritableBlobFactory overflowBlobFactory;
    byte[][] chunks;
    int chunkIndex;
    int chunkOffset;
    WritableBlob overflowBlob;
    State state;
    
    OverflowableBlobImpl(final int numberOfChunks, final int chunkSize, final WritableBlobFactory overflowBlobFactory) {
        this.state = State.NEW;
        this.chunkSize = chunkSize;
        this.overflowBlobFactory = overflowBlobFactory;
        this.chunks = new byte[numberOfChunks][];
    }
    
    byte[] getCurrentChunk() {
        if (this.chunkOffset == 0) {
            final byte[] chunk = new byte[this.chunkSize];
            return this.chunks[this.chunkIndex] = chunk;
        }
        return this.chunks[this.chunkIndex];
    }
    
    OutputStream switchToOverflowBlob() throws IOException {
        this.overflowBlob = this.overflowBlobFactory.createBlob();
        final OutputStream outputStream = this.overflowBlob.getOutputStream();
        for (int i = 0; i < this.chunkIndex; ++i) {
            outputStream.write(this.chunks[i]);
        }
        if (this.chunkOffset > 0) {
            outputStream.write(this.chunks[this.chunkIndex], 0, this.chunkOffset);
        }
        this.chunks = null;
        return outputStream;
    }
    
    public OutputStream getOutputStream() {
        if (this.state != State.NEW) {
            throw new IllegalStateException();
        }
        this.state = State.UNCOMMITTED;
        return new OutputStreamImpl();
    }
    
    long readFrom(final InputStream in, final long length, final boolean commit) throws StreamCopyException {
        if (this.state == State.COMMITTED) {
            throw new IllegalStateException();
        }
        long read = 0L;
        long toRead = (length == -1L) ? Long.MAX_VALUE : length;
        while (toRead > 0L) {
            int c;
            try {
                int len = this.chunkSize - this.chunkOffset;
                if (len > toRead) {
                    len = (int)toRead;
                }
                c = in.read(this.getCurrentChunk(), this.chunkOffset, len);
            }
            catch (final IOException ex) {
                throw new StreamCopyException(1, ex);
            }
            if (c == -1) {
                break;
            }
            read += c;
            toRead -= c;
            this.chunkOffset += c;
            if (this.chunkOffset != this.chunkSize) {
                continue;
            }
            ++this.chunkIndex;
            this.chunkOffset = 0;
            if (this.chunkIndex != this.chunks.length) {
                continue;
            }
            OutputStream out;
            try {
                out = this.switchToOverflowBlob();
            }
            catch (final IOException ex2) {
                throw new StreamCopyException(2, ex2);
            }
            read += IOUtil.copy(in, out, toRead);
            try {
                out.close();
                break;
            }
            catch (final IOException ex2) {
                throw new StreamCopyException(2, ex2);
            }
        }
        this.state = (commit ? State.COMMITTED : State.UNCOMMITTED);
        return read;
    }
    
    public long readFrom(final InputStream in) throws StreamCopyException {
        if (this.state != State.NEW) {
            throw new IllegalStateException();
        }
        return this.readFrom(in, -1L, true);
    }
    
    public InputStream getInputStream() throws IOException {
        if (this.state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        if (this.overflowBlob != null) {
            return this.overflowBlob.getInputStream();
        }
        return new InputStreamImpl();
    }
    
    public void writeTo(final OutputStream out) throws StreamCopyException {
        if (this.state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        if (this.overflowBlob != null) {
            this.overflowBlob.writeTo(out);
        }
        else {
            try {
                for (int i = 0; i < this.chunkIndex; ++i) {
                    out.write(this.chunks[i]);
                }
                if (this.chunkOffset > 0) {
                    out.write(this.chunks[this.chunkIndex], 0, this.chunkOffset);
                }
            }
            catch (final IOException ex) {
                throw new StreamCopyException(2, ex);
            }
        }
    }
    
    public long getSize() {
        if (this.state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        if (this.overflowBlob != null) {
            return this.overflowBlob.getSize();
        }
        return this.chunkIndex * this.chunkSize + this.chunkOffset;
    }
    
    public void release() throws IOException {
        if (this.overflowBlob != null) {
            this.overflowBlob.release();
            this.overflowBlob = null;
        }
        this.state = State.RELEASED;
    }
    
    public WritableBlob getOverflowBlob() {
        return this.overflowBlob;
    }
    
    class OutputStreamImpl extends OutputStream implements ReadFromSupport
    {
        private OutputStream overflowOutputStream;
        
        @Override
        public void write(final byte[] b, int off, int len) throws IOException {
            if (OverflowableBlobImpl.this.state != State.UNCOMMITTED) {
                throw new IllegalStateException();
            }
            if (this.overflowOutputStream != null) {
                this.overflowOutputStream.write(b, off, len);
            }
            else if (len > (OverflowableBlobImpl.this.chunks.length - OverflowableBlobImpl.this.chunkIndex) * OverflowableBlobImpl.this.chunkSize - OverflowableBlobImpl.this.chunkOffset) {
                (this.overflowOutputStream = OverflowableBlobImpl.this.switchToOverflowBlob()).write(b, off, len);
            }
            else {
                while (len > 0) {
                    final byte[] chunk = OverflowableBlobImpl.this.getCurrentChunk();
                    final int c = Math.min(len, OverflowableBlobImpl.this.chunkSize - OverflowableBlobImpl.this.chunkOffset);
                    System.arraycopy(b, off, chunk, OverflowableBlobImpl.this.chunkOffset, c);
                    len -= c;
                    off += c;
                    final OverflowableBlobImpl this$0 = OverflowableBlobImpl.this;
                    this$0.chunkOffset += c;
                    if (OverflowableBlobImpl.this.chunkOffset == OverflowableBlobImpl.this.chunkSize) {
                        final OverflowableBlobImpl this$2 = OverflowableBlobImpl.this;
                        ++this$2.chunkIndex;
                        OverflowableBlobImpl.this.chunkOffset = 0;
                    }
                }
            }
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.write(new byte[] { (byte)b }, 0, 1);
        }
        
        @Override
        public void close() throws IOException {
            if (this.overflowOutputStream != null) {
                this.overflowOutputStream.close();
            }
            OverflowableBlobImpl.this.state = State.COMMITTED;
        }
        
        public long readFrom(final InputStream in, final long length) throws StreamCopyException {
            return OverflowableBlobImpl.this.readFrom(in, length, false);
        }
    }
    
    class InputStreamImpl extends InputStream
    {
        private int currentChunkIndex;
        private int currentChunkOffset;
        private int markChunkIndex;
        private int markChunkOffset;
        
        @Override
        public int available() throws IOException {
            return (OverflowableBlobImpl.this.chunkIndex - this.currentChunkIndex) * OverflowableBlobImpl.this.chunkSize + OverflowableBlobImpl.this.chunkOffset - this.currentChunkOffset;
        }
        
        @Override
        public int read(final byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            int read = 0;
            while (len > 0 && (this.currentChunkIndex != OverflowableBlobImpl.this.chunkIndex || this.currentChunkOffset != OverflowableBlobImpl.this.chunkOffset)) {
                int c;
                if (this.currentChunkIndex == OverflowableBlobImpl.this.chunkIndex) {
                    c = Math.min(len, OverflowableBlobImpl.this.chunkOffset - this.currentChunkOffset);
                }
                else {
                    c = Math.min(len, OverflowableBlobImpl.this.chunkSize - this.currentChunkOffset);
                }
                System.arraycopy(OverflowableBlobImpl.this.chunks[this.currentChunkIndex], this.currentChunkOffset, b, off, c);
                len -= c;
                off += c;
                this.currentChunkOffset += c;
                read += c;
                if (this.currentChunkOffset == OverflowableBlobImpl.this.chunkSize) {
                    ++this.currentChunkIndex;
                    this.currentChunkOffset = 0;
                }
            }
            if (read == 0) {
                return -1;
            }
            return read;
        }
        
        @Override
        public int read(final byte[] b) throws IOException {
            return this.read(b, 0, b.length);
        }
        
        @Override
        public int read() throws IOException {
            final byte[] b = { 0 };
            return (this.read(b) == -1) ? -1 : (b[0] & 0xFF);
        }
        
        @Override
        public boolean markSupported() {
            return true;
        }
        
        @Override
        public void mark(final int readlimit) {
            this.markChunkIndex = this.currentChunkIndex;
            this.markChunkOffset = this.currentChunkOffset;
        }
        
        @Override
        public void reset() throws IOException {
            this.currentChunkIndex = this.markChunkIndex;
            this.currentChunkOffset = this.markChunkOffset;
        }
        
        @Override
        public long skip(final long n) throws IOException {
            final int available = this.available();
            final int c = (n < available) ? ((int)n) : available;
            final int newOffset = this.currentChunkOffset + c;
            final int chunkDelta = newOffset / OverflowableBlobImpl.this.chunkSize;
            this.currentChunkIndex += chunkDelta;
            this.currentChunkOffset = newOffset - chunkDelta * OverflowableBlobImpl.this.chunkSize;
            return c;
        }
        
        @Override
        public void close() throws IOException {
        }
    }
}
