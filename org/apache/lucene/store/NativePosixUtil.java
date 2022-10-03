package org.apache.lucene.store;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.FileDescriptor;

public final class NativePosixUtil
{
    public static final int NORMAL = 0;
    public static final int SEQUENTIAL = 1;
    public static final int RANDOM = 2;
    public static final int WILLNEED = 3;
    public static final int DONTNEED = 4;
    public static final int NOREUSE = 5;
    
    private static native int posix_fadvise(final FileDescriptor p0, final long p1, final long p2, final int p3) throws IOException;
    
    public static native int posix_madvise(final ByteBuffer p0, final int p1) throws IOException;
    
    public static native int madvise(final ByteBuffer p0, final int p1) throws IOException;
    
    public static native FileDescriptor open_direct(final String p0, final boolean p1) throws IOException;
    
    public static native long pread(final FileDescriptor p0, final long p1, final ByteBuffer p2) throws IOException;
    
    public static void advise(final FileDescriptor fd, final long offset, final long len, final int advise) throws IOException {
        final int code = posix_fadvise(fd, offset, len, advise);
        if (code != 0) {
            throw new RuntimeException("posix_fadvise failed code=" + code);
        }
    }
    
    static {
        System.loadLibrary("NativePosixUtil");
    }
}
