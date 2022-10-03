package org.apache.lucene.store;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.io.IOException;
import java.nio.file.Path;

public class SimpleFSDirectory extends FSDirectory
{
    public SimpleFSDirectory(final Path path, final LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
    }
    
    public SimpleFSDirectory(final Path path) throws IOException {
        this(path, FSLockFactory.getDefault());
    }
    
    @Override
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final Path path = this.directory.resolve(name);
        final SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.READ);
        return new SimpleFSIndexInput("SimpleFSIndexInput(path=\"" + path + "\")", channel, context);
    }
    
    static final class SimpleFSIndexInput extends BufferedIndexInput
    {
        private static final int CHUNK_SIZE = 16384;
        protected final SeekableByteChannel channel;
        boolean isClone;
        protected final long off;
        protected final long end;
        private ByteBuffer byteBuf;
        
        public SimpleFSIndexInput(final String resourceDesc, final SeekableByteChannel channel, final IOContext context) throws IOException {
            super(resourceDesc, context);
            this.isClone = false;
            this.channel = channel;
            this.off = 0L;
            this.end = channel.size();
        }
        
        public SimpleFSIndexInput(final String resourceDesc, final SeekableByteChannel channel, final long off, final long length, final int bufferSize) {
            super(resourceDesc, bufferSize);
            this.isClone = false;
            this.channel = channel;
            this.off = off;
            this.end = off + length;
            this.isClone = true;
        }
        
        @Override
        public void close() throws IOException {
            if (!this.isClone) {
                this.channel.close();
            }
        }
        
        @Override
        public SimpleFSIndexInput clone() {
            final SimpleFSIndexInput clone = (SimpleFSIndexInput)super.clone();
            clone.isClone = true;
            return clone;
        }
        
        @Override
        public IndexInput slice(final String sliceDescription, final long offset, final long length) throws IOException {
            if (offset < 0L || length < 0L || offset + length > this.length()) {
                throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: " + this);
            }
            return new SimpleFSIndexInput(this.getFullSliceDescription(sliceDescription), this.channel, this.off + offset, length, this.getBufferSize());
        }
        
        @Override
        public final long length() {
            return this.end - this.off;
        }
        
        @Override
        protected void newBuffer(final byte[] newBuffer) {
            super.newBuffer(newBuffer);
            this.byteBuf = ByteBuffer.wrap(newBuffer);
        }
        
        @Override
        protected void readInternal(final byte[] b, final int offset, final int len) throws IOException {
            ByteBuffer bb;
            if (b == this.buffer) {
                assert this.byteBuf != null;
                bb = this.byteBuf;
                this.byteBuf.clear().position(offset);
            }
            else {
                bb = ByteBuffer.wrap(b, offset, len);
            }
            synchronized (this.channel) {
                long pos = this.getFilePointer() + this.off;
                if (pos + len > this.end) {
                    throw new EOFException("read past EOF: " + this);
                }
                try {
                    this.channel.position(pos);
                    int readLength;
                    int i;
                    for (readLength = len; readLength > 0; readLength -= i) {
                        final int toRead = Math.min(16384, readLength);
                        bb.limit(bb.position() + toRead);
                        assert bb.remaining() == toRead;
                        i = this.channel.read(bb);
                        if (i < 0) {
                            throw new EOFException("read past EOF: " + this + " off: " + offset + " len: " + len + " pos: " + pos + " chunkLen: " + toRead + " end: " + this.end);
                        }
                        assert i > 0 : "SeekableByteChannel.read with non zero-length bb.remaining() must always read at least one byte (Channel is in blocking mode, see spec of ReadableByteChannel)";
                        pos += i;
                    }
                    assert readLength == 0;
                }
                catch (final IOException ioe) {
                    throw new IOException(ioe.getMessage() + ": " + this, ioe);
                }
            }
        }
        
        @Override
        protected void seekInternal(final long pos) throws IOException {
            if (pos > this.length()) {
                throw new EOFException("read past EOF: pos=" + pos + " vs length=" + this.length() + ": " + this);
            }
        }
    }
}
