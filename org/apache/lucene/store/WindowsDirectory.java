package org.apache.lucene.store;

import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Path;

public class WindowsDirectory extends FSDirectory
{
    private static final int DEFAULT_BUFFERSIZE = 4096;
    
    public WindowsDirectory(final Path path, final LockFactory lockFactory) throws IOException {
        super(path, lockFactory);
    }
    
    public WindowsDirectory(final Path path) throws IOException {
        this(path, (LockFactory)FSLockFactory.getDefault());
    }
    
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        return (IndexInput)new WindowsIndexInput(this.getDirectory().resolve(name), Math.max(BufferedIndexInput.bufferSize(context), 4096));
    }
    
    private static native long open(final String p0) throws IOException;
    
    private static native int read(final long p0, final byte[] p1, final int p2, final int p3, final long p4) throws IOException;
    
    private static native void close(final long p0) throws IOException;
    
    private static native long length(final long p0) throws IOException;
    
    static {
        System.loadLibrary("WindowsDirectory");
    }
    
    static class WindowsIndexInput extends BufferedIndexInput
    {
        private final long fd;
        private final long length;
        boolean isClone;
        boolean isOpen;
        
        public WindowsIndexInput(final Path file, final int bufferSize) throws IOException {
            super("WindowsIndexInput(path=\"" + file + "\")", bufferSize);
            this.fd = open(file.toString());
            this.length = length(this.fd);
            this.isOpen = true;
        }
        
        protected void readInternal(final byte[] b, final int offset, final int length) throws IOException {
            int bytesRead;
            try {
                bytesRead = read(this.fd, b, offset, length, this.getFilePointer());
            }
            catch (final IOException ioe) {
                throw new IOException(ioe.getMessage() + ": " + this, ioe);
            }
            if (bytesRead != length) {
                throw new EOFException("read past EOF: " + this);
            }
        }
        
        protected void seekInternal(final long pos) throws IOException {
        }
        
        public synchronized void close() throws IOException {
            if (!this.isClone && this.isOpen) {
                close(this.fd);
                this.isOpen = false;
            }
        }
        
        public long length() {
            return this.length;
        }
        
        public WindowsIndexInput clone() {
            final WindowsIndexInput clone = (WindowsIndexInput)super.clone();
            clone.isClone = true;
            return clone;
        }
    }
}
