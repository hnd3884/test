package org.apache.lucene.store;

import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import org.apache.lucene.util.SuppressForbidden;
import java.lang.reflect.Method;
import java.util.Objects;
import java.security.PrivilegedAction;
import java.util.Locale;
import org.apache.lucene.util.Constants;
import java.nio.MappedByteBuffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.io.IOException;
import java.nio.file.Path;

public class MMapDirectory extends FSDirectory
{
    private boolean useUnmapHack;
    private boolean preload;
    public static final int DEFAULT_MAX_CHUNK_SIZE;
    final int chunkSizePower;
    public static final boolean UNMAP_SUPPORTED;
    private static final ByteBufferIndexInput.BufferCleaner CLEANER;
    
    public MMapDirectory(final Path path, final LockFactory lockFactory) throws IOException {
        this(path, lockFactory, MMapDirectory.DEFAULT_MAX_CHUNK_SIZE);
    }
    
    public MMapDirectory(final Path path) throws IOException {
        this(path, FSLockFactory.getDefault());
    }
    
    public MMapDirectory(final Path path, final int maxChunkSize) throws IOException {
        this(path, FSLockFactory.getDefault(), maxChunkSize);
    }
    
    public MMapDirectory(final Path path, final LockFactory lockFactory, final int maxChunkSize) throws IOException {
        super(path, lockFactory);
        this.useUnmapHack = MMapDirectory.UNMAP_SUPPORTED;
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("Maximum chunk size for mmap must be >0");
        }
        this.chunkSizePower = 31 - Integer.numberOfLeadingZeros(maxChunkSize);
        assert this.chunkSizePower >= 0 && this.chunkSizePower <= 30;
    }
    
    public void setUseUnmap(final boolean useUnmapHack) {
        if (useUnmapHack && !MMapDirectory.UNMAP_SUPPORTED) {
            throw new IllegalArgumentException("Unmap hack not supported on this platform!");
        }
        this.useUnmapHack = useUnmapHack;
    }
    
    public boolean getUseUnmap() {
        return this.useUnmapHack;
    }
    
    public void setPreload(final boolean preload) {
        this.preload = preload;
    }
    
    public boolean getPreload() {
        return this.preload;
    }
    
    public final int getMaxChunkSize() {
        return 1 << this.chunkSizePower;
    }
    
    @Override
    public IndexInput openInput(final String name, final IOContext context) throws IOException {
        this.ensureOpen();
        final Path path = this.directory.resolve(name);
        try (final FileChannel c = FileChannel.open(path, StandardOpenOption.READ)) {
            final String resourceDescription = "MMapIndexInput(path=\"" + path.toString() + "\")";
            final boolean useUnmap = this.getUseUnmap();
            return ByteBufferIndexInput.newInstance(resourceDescription, this.map(resourceDescription, c, 0L, c.size()), c.size(), this.chunkSizePower, useUnmap ? MMapDirectory.CLEANER : null, useUnmap);
        }
    }
    
    final ByteBuffer[] map(final String resourceDescription, final FileChannel fc, final long offset, final long length) throws IOException {
        if (length >>> this.chunkSizePower >= 2147483647L) {
            throw new IllegalArgumentException("RandomAccessFile too big for chunk size: " + resourceDescription);
        }
        final long chunkSize = 1L << this.chunkSizePower;
        final int nrBuffers = (int)(length >>> this.chunkSizePower) + 1;
        final ByteBuffer[] buffers = new ByteBuffer[nrBuffers];
        long bufferStart = 0L;
        for (int bufNr = 0; bufNr < nrBuffers; ++bufNr) {
            final int bufSize = (int)((length > bufferStart + chunkSize) ? chunkSize : (length - bufferStart));
            MappedByteBuffer buffer;
            try {
                buffer = fc.map(FileChannel.MapMode.READ_ONLY, offset + bufferStart, bufSize);
            }
            catch (final IOException ioe) {
                throw this.convertMapFailedIOException(ioe, resourceDescription, bufSize);
            }
            if (this.preload) {
                buffer.load();
            }
            buffers[bufNr] = buffer;
            bufferStart += bufSize;
        }
        return buffers;
    }
    
    private IOException convertMapFailedIOException(final IOException ioe, final String resourceDescription, final int bufSize) {
        String originalMessage;
        Throwable originalCause;
        if (ioe.getCause() instanceof OutOfMemoryError) {
            originalMessage = "Map failed";
            originalCause = null;
        }
        else {
            originalMessage = ioe.getMessage();
            originalCause = ioe.getCause();
        }
        String moreInfo;
        if (!Constants.JRE_IS_64BIT) {
            moreInfo = "MMapDirectory should only be used on 64bit platforms, because the address space on 32bit operating systems is too small. ";
        }
        else if (Constants.WINDOWS) {
            moreInfo = "Windows is unfortunately very limited on virtual address space. If your index size is several hundred Gigabytes, consider changing to Linux. ";
        }
        else if (Constants.LINUX) {
            moreInfo = "Please review 'ulimit -v', 'ulimit -m' (both should return 'unlimited'), and 'sysctl vm.max_map_count'. ";
        }
        else {
            moreInfo = "Please review 'ulimit -v', 'ulimit -m' (both should return 'unlimited'). ";
        }
        final IOException newIoe = new IOException(String.format(Locale.ENGLISH, "%s: %s [this may be caused by lack of enough unfragmented virtual address space or too restrictive virtual memory limits enforced by the operating system, preventing us to map a chunk of %d bytes. %sMore information: http://blog.thetaphi.de/2012/07/use-lucenes-mmapdirectory-on-64bit.html]", originalMessage, resourceDescription, bufSize, moreInfo), originalCause);
        newIoe.setStackTrace(ioe.getStackTrace());
        return newIoe;
    }
    
    static {
        DEFAULT_MAX_CHUNK_SIZE = (Constants.JRE_IS_64BIT ? 1073741824 : 268435456);
        UNMAP_SUPPORTED = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @SuppressForbidden(reason = "Needs access to private APIs in DirectBuffer and sun.misc.Cleaner to enable hack")
            @Override
            public Boolean run() {
                try {
                    final Class<?> dbClazz = Class.forName("java.nio.DirectByteBuffer");
                    final Method cleanerMethod = dbClazz.getMethod("cleaner", (Class<?>[])new Class[0]);
                    cleanerMethod.setAccessible(true);
                    final Class<?> cleanerClazz = Class.forName("sun.misc.Cleaner");
                    cleanerClazz.getMethod("clean", (Class<?>[])new Class[0]);
                    return Objects.equals(cleanerMethod.getReturnType(), cleanerClazz);
                }
                catch (final Exception e) {
                    return false;
                }
            }
        });
        CLEANER = new ByteBufferIndexInput.BufferCleaner() {
            @Override
            public void freeBuffer(final ByteBufferIndexInput parent, final ByteBuffer buffer) throws IOException {
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                        @SuppressForbidden(reason = "Needs access to private APIs in DirectBuffer and sun.misc.Cleaner to enable hack")
                        @Override
                        public Void run() throws Exception {
                            final Method getCleanerMethod = buffer.getClass().getMethod("cleaner", (Class<?>[])new Class[0]);
                            getCleanerMethod.setAccessible(true);
                            final Object cleaner = getCleanerMethod.invoke(buffer, new Object[0]);
                            if (cleaner != null) {
                                cleaner.getClass().getMethod("clean", (Class<?>[])new Class[0]).invoke(cleaner, new Object[0]);
                            }
                            return null;
                        }
                    });
                }
                catch (final PrivilegedActionException e) {
                    throw new IOException("Unable to unmap the mapped buffer: " + parent.toString(), e.getCause());
                }
            }
        };
    }
}
