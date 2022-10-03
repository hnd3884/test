package org.apache.lucene.store;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileDescriptor;
import java.nio.channels.FileChannel;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import org.apache.lucene.util.SuppressForbidden;
import java.io.IOException;
import java.nio.file.Path;

public class NativeUnixDirectory extends FSDirectory
{
    private static final long ALIGN = 512L;
    private static final long ALIGN_NOT_MASK = -512L;
    public static final int DEFAULT_MERGE_BUFFER_SIZE = 262144;
    public static final long DEFAULT_MIN_BYTES_DIRECT = 10485760L;
    private final int mergeBufferSize;
    private final long minBytesDirect;
    private final Directory delegate;
    
    public NativeUnixDirectory(final Path path, final int mergeBufferSize, final long minBytesDirect, final LockFactory lockFactory, final Directory delegate) throws IOException {
        super(path, lockFactory);
        if (((long)mergeBufferSize & 0x200L) != 0x0L) {
            throw new IllegalArgumentException("mergeBufferSize must be 0 mod 512 (got: " + mergeBufferSize + ")");
        }
        this.mergeBufferSize = mergeBufferSize;
        this.minBytesDirect = minBytesDirect;
        this.delegate = delegate;
    }
    
    public NativeUnixDirectory(final Path path, final LockFactory lockFactory, final Directory delegate) throws IOException {
        this(path, 262144, 10485760L, lockFactory, delegate);
    }
    
    public NativeUnixDirectory(final Path path, final Directory delegate) throws IOException {
        this(path, 262144, 10485760L, (LockFactory)FSLockFactory.getDefault(), delegate);
    }
    
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        if (context.context != IOContext.Context.MERGE || context.mergeInfo.estimatedMergeBytes < this.minBytesDirect || this.fileLength(name) < this.minBytesDirect) {
            return this.delegate.openInput(name, context);
        }
        return new NativeUnixIndexInput(this.getDirectory().resolve(name), this.mergeBufferSize);
    }
    
    public IndexOutput createOutput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        if (context.context != IOContext.Context.MERGE || context.mergeInfo.estimatedMergeBytes < this.minBytesDirect) {
            return this.delegate.createOutput(name, context);
        }
        return new NativeUnixIndexOutput(this.getDirectory().resolve(name), this.mergeBufferSize);
    }
    
    @SuppressForbidden(reason = "java.io.File: native API requires old-style FileDescriptor")
    private static final class NativeUnixIndexOutput extends IndexOutput
    {
        private final ByteBuffer buffer;
        private final FileOutputStream fos;
        private final FileChannel channel;
        private final int bufferSize;
        private int bufferPos;
        private long filePos;
        private long fileLength;
        private boolean isOpen;
        
        public NativeUnixIndexOutput(final Path path, final int bufferSize) throws IOException {
            super("NativeUnixIndexOutput(path=\"" + path.toString() + "\")");
            final FileDescriptor fd = NativePosixUtil.open_direct(path.toString(), false);
            this.fos = new FileOutputStream(fd);
            this.channel = this.fos.getChannel();
            this.buffer = ByteBuffer.allocateDirect(bufferSize);
            this.bufferSize = bufferSize;
            this.isOpen = true;
        }
        
        public void writeByte(final byte b) throws IOException {
            assert this.bufferPos == this.buffer.position() : "bufferPos=" + this.bufferPos + " vs buffer.position()=" + this.buffer.position();
            this.buffer.put(b);
            if (++this.bufferPos == this.bufferSize) {
                this.dump();
            }
        }
        
        public void writeBytes(final byte[] src, int offset, final int len) throws IOException {
            int toWrite = len;
            while (true) {
                final int left = this.bufferSize - this.bufferPos;
                if (left > toWrite) {
                    break;
                }
                this.buffer.put(src, offset, left);
                toWrite -= left;
                offset += left;
                this.bufferPos = this.bufferSize;
                this.dump();
            }
            this.buffer.put(src, offset, toWrite);
            this.bufferPos += toWrite;
        }
        
        private void dump() throws IOException {
            this.buffer.flip();
            final long limit = this.filePos + this.buffer.limit();
            if (limit > this.fileLength) {
                this.fileLength = limit;
            }
            this.buffer.limit((int)(this.buffer.limit() + 512L - 1L & 0xFFFFFFFFFFFFFE00L));
            assert ((long)this.buffer.limit() & 0xFFFFFFFFFFFFFE00L) == this.buffer.limit() : "limit=" + this.buffer.limit() + " vs " + ((long)this.buffer.limit() & 0xFFFFFFFFFFFFFE00L);
            assert (this.filePos & 0xFFFFFFFFFFFFFE00L) == this.filePos;
            this.channel.write(this.buffer, this.filePos);
            this.filePos += this.bufferPos;
            this.bufferPos = 0;
            this.buffer.clear();
        }
        
        public long getFilePointer() {
            return this.filePos + this.bufferPos;
        }
        
        public long getChecksum() throws IOException {
            throw new UnsupportedOperationException("this directory currently does not work at all!");
        }
        
        public void close() throws IOException {
            if (this.isOpen) {
                this.isOpen = false;
                try {
                    this.dump();
                }
                finally {
                    try {
                        this.channel.truncate(this.fileLength);
                    }
                    finally {
                        try {
                            this.channel.close();
                        }
                        finally {
                            this.fos.close();
                        }
                    }
                }
            }
        }
    }
    
    @SuppressForbidden(reason = "java.io.File: native API requires old-style FileDescriptor")
    private static final class NativeUnixIndexInput extends IndexInput
    {
        private final ByteBuffer buffer;
        private final FileInputStream fis;
        private final FileChannel channel;
        private final int bufferSize;
        private boolean isOpen;
        private boolean isClone;
        private long filePos;
        private int bufferPos;
        
        public NativeUnixIndexInput(final Path path, final int bufferSize) throws IOException {
            super("NativeUnixIndexInput(path=\"" + path + "\")");
            final FileDescriptor fd = NativePosixUtil.open_direct(path.toString(), true);
            this.fis = new FileInputStream(fd);
            this.channel = this.fis.getChannel();
            this.bufferSize = bufferSize;
            this.buffer = ByteBuffer.allocateDirect(bufferSize);
            this.isOpen = true;
            this.isClone = false;
            this.filePos = -bufferSize;
            this.bufferPos = bufferSize;
        }
        
        public NativeUnixIndexInput(final NativeUnixIndexInput other) throws IOException {
            super(other.toString());
            this.fis = null;
            this.channel = other.channel;
            this.bufferSize = other.bufferSize;
            this.buffer = ByteBuffer.allocateDirect(this.bufferSize);
            this.filePos = -this.bufferSize;
            this.bufferPos = this.bufferSize;
            this.isOpen = true;
            this.isClone = true;
            this.seek(other.getFilePointer());
        }
        
        public void close() throws IOException {
            if (this.isOpen && !this.isClone) {
                try {
                    this.channel.close();
                }
                finally {
                    if (!this.isClone) {
                        this.fis.close();
                    }
                }
            }
        }
        
        public long getFilePointer() {
            return this.filePos + this.bufferPos;
        }
        
        public void seek(final long pos) throws IOException {
            if (pos != this.getFilePointer()) {
                final long alignedPos = pos & 0xFFFFFFFFFFFFFE00L;
                this.filePos = alignedPos - this.bufferSize;
                final int delta = (int)(pos - alignedPos);
                if (delta != 0) {
                    this.refill();
                    this.buffer.position(delta);
                    this.bufferPos = delta;
                }
                else {
                    this.bufferPos = this.bufferSize;
                }
            }
        }
        
        public long length() {
            try {
                return this.channel.size();
            }
            catch (final IOException ioe) {
                throw new RuntimeException("IOException during length(): " + this, ioe);
            }
        }
        
        public byte readByte() throws IOException {
            if (this.bufferPos == this.bufferSize) {
                this.refill();
            }
            assert this.bufferPos == this.buffer.position() : "bufferPos=" + this.bufferPos + " vs buffer.position()=" + this.buffer.position();
            ++this.bufferPos;
            return this.buffer.get();
        }
        
        private void refill() throws IOException {
            this.buffer.clear();
            this.filePos += this.bufferSize;
            this.bufferPos = 0;
            assert (this.filePos & 0xFFFFFFFFFFFFFE00L) == this.filePos : "filePos=" + this.filePos + " anded=" + (this.filePos & 0xFFFFFFFFFFFFFE00L);
            int n;
            try {
                n = this.channel.read(this.buffer, this.filePos);
            }
            catch (final IOException ioe) {
                throw new IOException(ioe.getMessage() + ": " + this, ioe);
            }
            if (n < 0) {
                throw new EOFException("read past EOF: " + this);
            }
            this.buffer.rewind();
        }
        
        public void readBytes(final byte[] dst, int offset, final int len) throws IOException {
            int toRead = len;
            while (true) {
                final int left = this.bufferSize - this.bufferPos;
                if (left >= toRead) {
                    break;
                }
                this.buffer.get(dst, offset, left);
                toRead -= left;
                offset += left;
                this.refill();
            }
            this.buffer.get(dst, offset, toRead);
            this.bufferPos += toRead;
        }
        
        public NativeUnixIndexInput clone() {
            try {
                return new NativeUnixIndexInput(this);
            }
            catch (final IOException ioe) {
                throw new RuntimeException("IOException during clone: " + this, ioe);
            }
        }
        
        public IndexInput slice(final String sliceDescription, final long offset, final long length) throws IOException {
            return (IndexInput)BufferedIndexInput.wrap(sliceDescription, (IndexInput)this, offset, length);
        }
    }
}
