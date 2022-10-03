package com.lowagie.text.pdf;

import java.security.AccessController;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.nio.ByteBuffer;
import java.nio.BufferUnderflowException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;

public class MappedRandomAccessFile
{
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel channel;
    
    public MappedRandomAccessFile(final String filename, final String mode) throws IOException {
        this.mappedByteBuffer = null;
        this.channel = null;
        if (mode.equals("rw")) {
            this.init(new RandomAccessFile(filename, mode).getChannel(), FileChannel.MapMode.READ_WRITE);
        }
        else {
            this.init(new FileInputStream(filename).getChannel(), FileChannel.MapMode.READ_ONLY);
        }
    }
    
    private void init(final FileChannel channel, final FileChannel.MapMode mapMode) throws IOException {
        this.channel = channel;
        (this.mappedByteBuffer = channel.map(mapMode, 0L, channel.size())).load();
    }
    
    public FileChannel getChannel() {
        return this.channel;
    }
    
    public int read() {
        try {
            final byte b = this.mappedByteBuffer.get();
            final int n = b & 0xFF;
            return n;
        }
        catch (final BufferUnderflowException e) {
            return -1;
        }
    }
    
    public int read(final byte[] bytes, final int off, int len) {
        final int pos = this.mappedByteBuffer.position();
        final int limit = this.mappedByteBuffer.limit();
        if (pos == limit) {
            return -1;
        }
        final int newlimit = pos + len - off;
        if (newlimit > limit) {
            len = limit - pos;
        }
        this.mappedByteBuffer.get(bytes, off, len);
        return len;
    }
    
    public long getFilePointer() {
        return this.mappedByteBuffer.position();
    }
    
    public void seek(final long pos) {
        this.mappedByteBuffer.position((int)pos);
    }
    
    public long length() {
        return this.mappedByteBuffer.limit();
    }
    
    public void close() throws IOException {
        clean(this.mappedByteBuffer);
        this.mappedByteBuffer = null;
        if (this.channel != null) {
            this.channel.close();
        }
        this.channel = null;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
    
    public static boolean clean(final ByteBuffer buffer) {
        return buffer != null && buffer.isDirect() && (cleanJava9(buffer) || cleanOldsJDK(buffer));
    }
    
    private static boolean cleanJava9(final ByteBuffer buffer) {
        final Boolean b = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                Boolean success = Boolean.FALSE;
                try {
                    final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
                    theUnsafeField.setAccessible(true);
                    final Object theUnsafe = theUnsafeField.get(null);
                    final Method invokeCleanerMethod = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
                    invokeCleanerMethod.invoke(theUnsafe, buffer);
                    success = Boolean.TRUE;
                }
                catch (final Exception ex) {}
                return success;
            }
        });
        return b;
    }
    
    private static boolean cleanOldsJDK(final ByteBuffer buffer) {
        final Boolean b = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                Boolean success = Boolean.FALSE;
                try {
                    final Method getCleanerMethod = buffer.getClass().getMethod("cleaner", (Class<?>[])null);
                    if (!getCleanerMethod.isAccessible()) {
                        getCleanerMethod.setAccessible(true);
                    }
                    final Object cleaner = getCleanerMethod.invoke(buffer, (Object[])null);
                    final Method clean = cleaner.getClass().getMethod("clean", (Class<?>[])null);
                    clean.invoke(cleaner, (Object[])null);
                    success = Boolean.TRUE;
                }
                catch (final Exception ex) {}
                return success;
            }
        });
        return b;
    }
}
