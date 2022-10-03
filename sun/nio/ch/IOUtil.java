package sun.nio.ch;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.FileDescriptor;

public class IOUtil
{
    static final int IOV_MAX;
    
    private IOUtil() {
    }
    
    static int write(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer, final long n, final NativeDispatcher nativeDispatcher) throws IOException {
        if (byteBuffer instanceof DirectBuffer) {
            return writeFromNativeBuffer(fileDescriptor, byteBuffer, n, nativeDispatcher);
        }
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        assert position <= limit;
        final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer((position <= limit) ? (limit - position) : 0);
        try {
            temporaryDirectBuffer.put(byteBuffer);
            temporaryDirectBuffer.flip();
            byteBuffer.position(position);
            final int writeFromNativeBuffer = writeFromNativeBuffer(fileDescriptor, temporaryDirectBuffer, n, nativeDispatcher);
            if (writeFromNativeBuffer > 0) {
                byteBuffer.position(position + writeFromNativeBuffer);
            }
            return writeFromNativeBuffer;
        }
        finally {
            Util.offerFirstTemporaryDirectBuffer(temporaryDirectBuffer);
        }
    }
    
    private static int writeFromNativeBuffer(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer, final long n, final NativeDispatcher nativeDispatcher) throws IOException {
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        assert position <= limit;
        final int n2 = (position <= limit) ? (limit - position) : 0;
        if (n2 == 0) {
            return 0;
        }
        int n3;
        if (n != -1L) {
            n3 = nativeDispatcher.pwrite(fileDescriptor, ((DirectBuffer)byteBuffer).address() + position, n2, n);
        }
        else {
            n3 = nativeDispatcher.write(fileDescriptor, ((DirectBuffer)byteBuffer).address() + position, n2);
        }
        if (n3 > 0) {
            byteBuffer.position(position + n3);
        }
        return n3;
    }
    
    static long write(final FileDescriptor fileDescriptor, final ByteBuffer[] array, final NativeDispatcher nativeDispatcher) throws IOException {
        return write(fileDescriptor, array, 0, array.length, nativeDispatcher);
    }
    
    static long write(final FileDescriptor fileDescriptor, final ByteBuffer[] array, final int n, final int n2, final NativeDispatcher nativeDispatcher) throws IOException {
        final IOVecWrapper value = IOVecWrapper.get(n2);
        boolean b = false;
        int n3 = 0;
        try {
            for (int n4 = n + n2, n5 = n; n5 < n4 && n3 < IOUtil.IOV_MAX; ++n5) {
                ByteBuffer byteBuffer = array[n5];
                int n6 = byteBuffer.position();
                final int limit = byteBuffer.limit();
                assert n6 <= limit;
                final int n7 = (n6 <= limit) ? (limit - n6) : 0;
                if (n7 > 0) {
                    value.setBuffer(n3, byteBuffer, n6, n7);
                    if (!(byteBuffer instanceof DirectBuffer)) {
                        final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer(n7);
                        temporaryDirectBuffer.put(byteBuffer);
                        temporaryDirectBuffer.flip();
                        value.setShadow(n3, temporaryDirectBuffer);
                        byteBuffer.position(n6);
                        byteBuffer = temporaryDirectBuffer;
                        n6 = temporaryDirectBuffer.position();
                    }
                    value.putBase(n3, ((DirectBuffer)byteBuffer).address() + n6);
                    value.putLen(n3, n7);
                    ++n3;
                }
            }
            if (n3 == 0) {
                return 0L;
            }
            long writev;
            final long n8 = writev = nativeDispatcher.writev(fileDescriptor, value.address, n3);
            for (int i = 0; i < n3; ++i) {
                if (writev > 0L) {
                    final ByteBuffer buffer = value.getBuffer(i);
                    final int position = value.getPosition(i);
                    final int remaining = value.getRemaining(i);
                    final int n9 = (writev > remaining) ? remaining : ((int)writev);
                    buffer.position(position + n9);
                    writev -= n9;
                }
                final ByteBuffer shadow = value.getShadow(i);
                if (shadow != null) {
                    Util.offerLastTemporaryDirectBuffer(shadow);
                }
                value.clearRefs(i);
            }
            b = true;
            return n8;
        }
        finally {
            if (!b) {
                for (int j = 0; j < n3; ++j) {
                    final ByteBuffer shadow2 = value.getShadow(j);
                    if (shadow2 != null) {
                        Util.offerLastTemporaryDirectBuffer(shadow2);
                    }
                    value.clearRefs(j);
                }
            }
        }
    }
    
    static int read(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer, final long n, final NativeDispatcher nativeDispatcher) throws IOException {
        if (byteBuffer.isReadOnly()) {
            throw new IllegalArgumentException("Read-only buffer");
        }
        if (byteBuffer instanceof DirectBuffer) {
            return readIntoNativeBuffer(fileDescriptor, byteBuffer, n, nativeDispatcher);
        }
        final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer(byteBuffer.remaining());
        try {
            final int intoNativeBuffer = readIntoNativeBuffer(fileDescriptor, temporaryDirectBuffer, n, nativeDispatcher);
            temporaryDirectBuffer.flip();
            if (intoNativeBuffer > 0) {
                byteBuffer.put(temporaryDirectBuffer);
            }
            return intoNativeBuffer;
        }
        finally {
            Util.offerFirstTemporaryDirectBuffer(temporaryDirectBuffer);
        }
    }
    
    private static int readIntoNativeBuffer(final FileDescriptor fileDescriptor, final ByteBuffer byteBuffer, final long n, final NativeDispatcher nativeDispatcher) throws IOException {
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        assert position <= limit;
        final int n2 = (position <= limit) ? (limit - position) : 0;
        if (n2 == 0) {
            return 0;
        }
        int n3;
        if (n != -1L) {
            n3 = nativeDispatcher.pread(fileDescriptor, ((DirectBuffer)byteBuffer).address() + position, n2, n);
        }
        else {
            n3 = nativeDispatcher.read(fileDescriptor, ((DirectBuffer)byteBuffer).address() + position, n2);
        }
        if (n3 > 0) {
            byteBuffer.position(position + n3);
        }
        return n3;
    }
    
    static long read(final FileDescriptor fileDescriptor, final ByteBuffer[] array, final NativeDispatcher nativeDispatcher) throws IOException {
        return read(fileDescriptor, array, 0, array.length, nativeDispatcher);
    }
    
    static long read(final FileDescriptor fileDescriptor, final ByteBuffer[] array, final int n, final int n2, final NativeDispatcher nativeDispatcher) throws IOException {
        final IOVecWrapper value = IOVecWrapper.get(n2);
        boolean b = false;
        int n3 = 0;
        try {
            for (int n4 = n + n2, n5 = n; n5 < n4 && n3 < IOUtil.IOV_MAX; ++n5) {
                ByteBuffer byteBuffer = array[n5];
                if (byteBuffer.isReadOnly()) {
                    throw new IllegalArgumentException("Read-only buffer");
                }
                int n6 = byteBuffer.position();
                final int limit = byteBuffer.limit();
                assert n6 <= limit;
                final int n7 = (n6 <= limit) ? (limit - n6) : 0;
                if (n7 > 0) {
                    value.setBuffer(n3, byteBuffer, n6, n7);
                    if (!(byteBuffer instanceof DirectBuffer)) {
                        final ByteBuffer temporaryDirectBuffer = Util.getTemporaryDirectBuffer(n7);
                        value.setShadow(n3, temporaryDirectBuffer);
                        byteBuffer = temporaryDirectBuffer;
                        n6 = temporaryDirectBuffer.position();
                    }
                    value.putBase(n3, ((DirectBuffer)byteBuffer).address() + n6);
                    value.putLen(n3, n7);
                    ++n3;
                }
            }
            if (n3 == 0) {
                return 0L;
            }
            long readv;
            final long n8 = readv = nativeDispatcher.readv(fileDescriptor, value.address, n3);
            for (int i = 0; i < n3; ++i) {
                final ByteBuffer shadow = value.getShadow(i);
                if (readv > 0L) {
                    final ByteBuffer buffer = value.getBuffer(i);
                    final int remaining = value.getRemaining(i);
                    final int n9 = (readv > remaining) ? remaining : ((int)readv);
                    if (shadow == null) {
                        buffer.position(value.getPosition(i) + n9);
                    }
                    else {
                        shadow.limit(shadow.position() + n9);
                        buffer.put(shadow);
                    }
                    readv -= n9;
                }
                if (shadow != null) {
                    Util.offerLastTemporaryDirectBuffer(shadow);
                }
                value.clearRefs(i);
            }
            b = true;
            return n8;
        }
        finally {
            if (!b) {
                for (int j = 0; j < n3; ++j) {
                    final ByteBuffer shadow2 = value.getShadow(j);
                    if (shadow2 != null) {
                        Util.offerLastTemporaryDirectBuffer(shadow2);
                    }
                    value.clearRefs(j);
                }
            }
        }
    }
    
    public static FileDescriptor newFD(final int n) {
        final FileDescriptor fileDescriptor = new FileDescriptor();
        setfdVal(fileDescriptor, n);
        return fileDescriptor;
    }
    
    static native boolean randomBytes(final byte[] p0);
    
    static native long makePipe(final boolean p0);
    
    static native boolean drain(final int p0) throws IOException;
    
    public static native void configureBlocking(final FileDescriptor p0, final boolean p1) throws IOException;
    
    public static native int fdVal(final FileDescriptor p0);
    
    static native void setfdVal(final FileDescriptor p0, final int p1);
    
    static native int fdLimit();
    
    static native int iovMax();
    
    static native void initIDs();
    
    public static void load() {
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                System.loadLibrary("nio");
                return null;
            }
        });
        initIDs();
        IOV_MAX = iovMax();
    }
}
