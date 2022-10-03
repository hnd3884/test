package io.netty.channel.unix;

import java.io.File;
import java.nio.ByteBuffer;
import java.io.IOException;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FileDescriptor
{
    private static final AtomicIntegerFieldUpdater<FileDescriptor> stateUpdater;
    private static final int STATE_CLOSED_MASK = 1;
    private static final int STATE_INPUT_SHUTDOWN_MASK = 2;
    private static final int STATE_OUTPUT_SHUTDOWN_MASK = 4;
    private static final int STATE_ALL_MASK = 7;
    volatile int state;
    final int fd;
    
    public FileDescriptor(final int fd) {
        ObjectUtil.checkPositiveOrZero(fd, "fd");
        this.fd = fd;
    }
    
    public final int intValue() {
        return this.fd;
    }
    
    protected boolean markClosed() {
        while (true) {
            final int state = this.state;
            if (isClosed(state)) {
                return false;
            }
            if (this.casState(state, state | 0x7)) {
                return true;
            }
        }
    }
    
    public void close() throws IOException {
        if (this.markClosed()) {
            final int res = close(this.fd);
            if (res < 0) {
                throw Errors.newIOException("close", res);
            }
        }
    }
    
    public boolean isOpen() {
        return !isClosed(this.state);
    }
    
    public final int write(final ByteBuffer buf, final int pos, final int limit) throws IOException {
        final int res = write(this.fd, buf, pos, limit);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("write", res);
    }
    
    public final int writeAddress(final long address, final int pos, final int limit) throws IOException {
        final int res = writeAddress(this.fd, address, pos, limit);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("writeAddress", res);
    }
    
    public final long writev(final ByteBuffer[] buffers, final int offset, final int length, final long maxBytesToWrite) throws IOException {
        final long res = writev(this.fd, buffers, offset, Math.min(Limits.IOV_MAX, length), maxBytesToWrite);
        if (res >= 0L) {
            return res;
        }
        return Errors.ioResult("writev", (int)res);
    }
    
    public final long writevAddresses(final long memoryAddress, final int length) throws IOException {
        final long res = writevAddresses(this.fd, memoryAddress, length);
        if (res >= 0L) {
            return res;
        }
        return Errors.ioResult("writevAddresses", (int)res);
    }
    
    public final int read(final ByteBuffer buf, final int pos, final int limit) throws IOException {
        final int res = read(this.fd, buf, pos, limit);
        if (res > 0) {
            return res;
        }
        if (res == 0) {
            return -1;
        }
        return Errors.ioResult("read", res);
    }
    
    public final int readAddress(final long address, final int pos, final int limit) throws IOException {
        final int res = readAddress(this.fd, address, pos, limit);
        if (res > 0) {
            return res;
        }
        if (res == 0) {
            return -1;
        }
        return Errors.ioResult("readAddress", res);
    }
    
    @Override
    public String toString() {
        return "FileDescriptor{fd=" + this.fd + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof FileDescriptor && this.fd == ((FileDescriptor)o).fd);
    }
    
    @Override
    public int hashCode() {
        return this.fd;
    }
    
    public static FileDescriptor from(final String path) throws IOException {
        final int res = open(ObjectUtil.checkNotNull(path, "path"));
        if (res < 0) {
            throw Errors.newIOException("open", res);
        }
        return new FileDescriptor(res);
    }
    
    public static FileDescriptor from(final File file) throws IOException {
        return from(ObjectUtil.checkNotNull(file, "file").getPath());
    }
    
    public static FileDescriptor[] pipe() throws IOException {
        final long res = newPipe();
        if (res < 0L) {
            throw Errors.newIOException("newPipe", (int)res);
        }
        return new FileDescriptor[] { new FileDescriptor((int)(res >>> 32)), new FileDescriptor((int)res) };
    }
    
    final boolean casState(final int expected, final int update) {
        return FileDescriptor.stateUpdater.compareAndSet(this, expected, update);
    }
    
    static boolean isClosed(final int state) {
        return (state & 0x1) != 0x0;
    }
    
    static boolean isInputShutdown(final int state) {
        return (state & 0x2) != 0x0;
    }
    
    static boolean isOutputShutdown(final int state) {
        return (state & 0x4) != 0x0;
    }
    
    static int inputShutdown(final int state) {
        return state | 0x2;
    }
    
    static int outputShutdown(final int state) {
        return state | 0x4;
    }
    
    private static native int open(final String p0);
    
    private static native int close(final int p0);
    
    private static native int write(final int p0, final ByteBuffer p1, final int p2, final int p3);
    
    private static native int writeAddress(final int p0, final long p1, final int p2, final int p3);
    
    private static native long writev(final int p0, final ByteBuffer[] p1, final int p2, final int p3, final long p4);
    
    private static native long writevAddresses(final int p0, final long p1, final int p2);
    
    private static native int read(final int p0, final ByteBuffer p1, final int p2, final int p3);
    
    private static native int readAddress(final int p0, final long p1, final int p2, final int p3);
    
    private static native long newPipe();
    
    static {
        stateUpdater = AtomicIntegerFieldUpdater.newUpdater(FileDescriptor.class, "state");
    }
}
