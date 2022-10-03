package sun.nio.ch;

import java.io.IOException;
import java.io.FileDescriptor;

abstract class NativeDispatcher
{
    abstract int read(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    boolean needsPositionLock() {
        return false;
    }
    
    int pread(final FileDescriptor fileDescriptor, final long n, final int n2, final long n3) throws IOException {
        throw new IOException("Operation Unsupported");
    }
    
    abstract long readv(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    abstract int write(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    int pwrite(final FileDescriptor fileDescriptor, final long n, final int n2, final long n3) throws IOException {
        throw new IOException("Operation Unsupported");
    }
    
    abstract long writev(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    abstract void close(final FileDescriptor p0) throws IOException;
    
    void preClose(final FileDescriptor fileDescriptor) throws IOException {
    }
}
