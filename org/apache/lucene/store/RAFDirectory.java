package org.apache.lucene.store;

import java.io.EOFException;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.lucene.util.SuppressForbidden;

@SuppressForbidden(reason = "java.io.File: RAFDirectory is legacy API")
public class RAFDirectory extends FSDirectory
{
    public RAFDirectory(final Path path, final LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
        path.toFile();
    }
    
    public RAFDirectory(final Path path) throws IOException {
        this(path, (LockFactory)FSLockFactory.getDefault());
    }
    
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final File path = this.directory.resolve(name).toFile();
        final RandomAccessFile raf = new RandomAccessFile(path, "r");
        return (IndexInput)new RAFIndexInput("SimpleFSIndexInput(path=\"" + path.getPath() + "\")", raf, context);
    }
    
    @SuppressForbidden(reason = "java.io.File: RAFDirectory is legacy API")
    static final class RAFIndexInput extends BufferedIndexInput
    {
        private static final int CHUNK_SIZE = 8192;
        protected final RandomAccessFile file;
        boolean isClone;
        protected final long off;
        protected final long end;
        
        public RAFIndexInput(final String resourceDesc, final RandomAccessFile file, final IOContext context) throws IOException {
            super(resourceDesc, context);
            this.isClone = false;
            this.file = file;
            this.off = 0L;
            this.end = file.length();
        }
        
        public RAFIndexInput(final String resourceDesc, final RandomAccessFile file, final long off, final long length, final int bufferSize) {
            super(resourceDesc, bufferSize);
            this.isClone = false;
            this.file = file;
            this.off = off;
            this.end = off + length;
            this.isClone = true;
        }
        
        public void close() throws IOException {
            if (!this.isClone) {
                this.file.close();
            }
        }
        
        public RAFIndexInput clone() {
            final RAFIndexInput clone = (RAFIndexInput)super.clone();
            clone.isClone = true;
            return clone;
        }
        
        public IndexInput slice(final String sliceDescription, final long offset, final long length) throws IOException {
            if (offset < 0L || length < 0L || offset + length > this.length()) {
                throw new IllegalArgumentException("slice() " + sliceDescription + " out of bounds: " + this);
            }
            return (IndexInput)new RAFIndexInput(sliceDescription, this.file, this.off + offset, length, this.getBufferSize());
        }
        
        public final long length() {
            return this.end - this.off;
        }
        
        protected void readInternal(final byte[] b, final int offset, final int len) throws IOException {
            synchronized (this.file) {
                final long position = this.off + this.getFilePointer();
                this.file.seek(position);
                int total = 0;
                if (position + len > this.end) {
                    throw new EOFException("read past EOF: " + this);
                }
                try {
                    while (total < len) {
                        final int toRead = Math.min(8192, len - total);
                        final int i = this.file.read(b, offset + total, toRead);
                        if (i < 0) {
                            throw new EOFException("read past EOF: " + this + " off: " + offset + " len: " + len + " total: " + total + " chunkLen: " + toRead + " end: " + this.end);
                        }
                        assert i > 0 : "RandomAccessFile.read with non zero-length toRead must always read at least one byte";
                        total += i;
                    }
                    assert total == len;
                }
                catch (final IOException ioe) {
                    throw new IOException(ioe.getMessage() + ": " + this, ioe);
                }
            }
        }
        
        protected void seekInternal(final long pos) throws IOException {
            if (pos > this.length()) {
                throw new EOFException("read past EOF: pos=" + pos + " vs length=" + this.length() + ": " + this);
            }
        }
        
        boolean isFDValid() throws IOException {
            return this.file.getFD().valid();
        }
    }
}
