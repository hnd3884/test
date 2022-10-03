package sun.nio.ch;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.nio.channels.SelectableChannel;
import sun.misc.JavaIOFileDescriptorAccess;
import sun.misc.SharedSecrets;
import java.io.IOException;
import java.io.FileDescriptor;

class FileDispatcherImpl extends FileDispatcher
{
    private static final boolean fastFileTransfer;
    private final boolean append;
    
    FileDispatcherImpl(final boolean append) {
        this.append = append;
    }
    
    FileDispatcherImpl() {
        this(false);
    }
    
    @Override
    boolean needsPositionLock() {
        return true;
    }
    
    @Override
    int read(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return read0(fileDescriptor, n, n2);
    }
    
    @Override
    int pread(final FileDescriptor fileDescriptor, final long n, final int n2, final long n3) throws IOException {
        return pread0(fileDescriptor, n, n2, n3);
    }
    
    @Override
    long readv(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return readv0(fileDescriptor, n, n2);
    }
    
    @Override
    int write(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return write0(fileDescriptor, n, n2, this.append);
    }
    
    @Override
    int pwrite(final FileDescriptor fileDescriptor, final long n, final int n2, final long n3) throws IOException {
        return pwrite0(fileDescriptor, n, n2, n3);
    }
    
    @Override
    long writev(final FileDescriptor fileDescriptor, final long n, final int n2) throws IOException {
        return writev0(fileDescriptor, n, n2, this.append);
    }
    
    @Override
    long seek(final FileDescriptor fileDescriptor, final long n) throws IOException {
        return seek0(fileDescriptor, n);
    }
    
    @Override
    int force(final FileDescriptor fileDescriptor, final boolean b) throws IOException {
        return force0(fileDescriptor, b);
    }
    
    @Override
    int truncate(final FileDescriptor fileDescriptor, final long n) throws IOException {
        return truncate0(fileDescriptor, n);
    }
    
    @Override
    long size(final FileDescriptor fileDescriptor) throws IOException {
        return size0(fileDescriptor);
    }
    
    @Override
    int lock(final FileDescriptor fileDescriptor, final boolean b, final long n, final long n2, final boolean b2) throws IOException {
        return lock0(fileDescriptor, b, n, n2, b2);
    }
    
    @Override
    void release(final FileDescriptor fileDescriptor, final long n, final long n2) throws IOException {
        release0(fileDescriptor, n, n2);
    }
    
    @Override
    void close(final FileDescriptor fileDescriptor) throws IOException {
        close0(fileDescriptor);
    }
    
    @Override
    FileDescriptor duplicateForMapping(final FileDescriptor fileDescriptor) throws IOException {
        final JavaIOFileDescriptorAccess javaIOFileDescriptorAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        final FileDescriptor fileDescriptor2 = new FileDescriptor();
        javaIOFileDescriptorAccess.setHandle(fileDescriptor2, duplicateHandle(javaIOFileDescriptorAccess.getHandle(fileDescriptor)));
        return fileDescriptor2;
    }
    
    @Override
    boolean canTransferToDirectly(final SelectableChannel selectableChannel) {
        return FileDispatcherImpl.fastFileTransfer && selectableChannel.isBlocking();
    }
    
    @Override
    boolean transferToDirectlyNeedsPositionLock() {
        return true;
    }
    
    static boolean isFastFileTransferRequested() {
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("jdk.nio.enableFastFileTransfer");
            }
        });
        return "".equals(s) || Boolean.parseBoolean(s);
    }
    
    static native int read0(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    static native int pread0(final FileDescriptor p0, final long p1, final int p2, final long p3) throws IOException;
    
    static native long readv0(final FileDescriptor p0, final long p1, final int p2) throws IOException;
    
    static native int write0(final FileDescriptor p0, final long p1, final int p2, final boolean p3) throws IOException;
    
    static native int pwrite0(final FileDescriptor p0, final long p1, final int p2, final long p3) throws IOException;
    
    static native long writev0(final FileDescriptor p0, final long p1, final int p2, final boolean p3) throws IOException;
    
    static native long seek0(final FileDescriptor p0, final long p1) throws IOException;
    
    static native int force0(final FileDescriptor p0, final boolean p1) throws IOException;
    
    static native int truncate0(final FileDescriptor p0, final long p1) throws IOException;
    
    static native long size0(final FileDescriptor p0) throws IOException;
    
    static native int lock0(final FileDescriptor p0, final boolean p1, final long p2, final long p3, final boolean p4) throws IOException;
    
    static native void release0(final FileDescriptor p0, final long p1, final long p2) throws IOException;
    
    static native void close0(final FileDescriptor p0) throws IOException;
    
    static native void closeByHandle(final long p0) throws IOException;
    
    static native long duplicateHandle(final long p0) throws IOException;
    
    static {
        IOUtil.load();
        fastFileTransfer = isFastFileTransferRequested();
    }
}
