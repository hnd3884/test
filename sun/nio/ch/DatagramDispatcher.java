package sun.nio.ch;

import java.io.IOException;
import java.io.FileDescriptor;

class DatagramDispatcher extends NativeDispatcher
{
    @Override
    int read(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return read0(fileDescriptor, n, n2);
    }
    
    @Override
    long readv(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return readv0(fileDescriptor, n, n2);
    }
    
    @Override
    int write(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return write0(fileDescriptor, n, n2);
    }
    
    @Override
    long writev(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return writev0(fileDescriptor, n, n2);
    }
    
    @Override
    void close(final FileDescriptor fileDescriptor) throws IOException {
        SocketDispatcher.close0(fileDescriptor);
    }
    
    static native int read0(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    static native long readv0(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    static native int write0(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    static native long writev0(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    static {
        IOUtil.load();
    }
}
