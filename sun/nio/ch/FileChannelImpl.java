package sun.nio.ch;

import java.util.Collection;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.FileLockInterruptionException;
import java.nio.channels.Channel;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.misc.JavaNioAccess;
import sun.misc.Cleaner;
import java.nio.channels.ReadableByteChannel;
import java.nio.MappedByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.NonReadableChannelException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.io.Closeable;
import java.nio.channels.FileLock;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.io.FileDescriptor;
import java.nio.channels.FileChannel;

public class FileChannelImpl extends FileChannel
{
    private static final long allocationGranularity;
    private final FileDispatcher nd;
    private final FileDescriptor fd;
    private final boolean writable;
    private final boolean readable;
    private final boolean append;
    private final Object parent;
    private final String path;
    private final NativeThreadSet threads;
    private final Object positionLock;
    private static volatile boolean transferSupported;
    private static volatile boolean pipeSupported;
    private static volatile boolean fileSupported;
    private static final long MAPPED_TRANSFER_SIZE = 8388608L;
    private static final int TRANSFER_SIZE = 8192;
    private static final int MAP_RO = 0;
    private static final int MAP_RW = 1;
    private static final int MAP_PV = 2;
    private volatile FileLockTable fileLockTable;
    private static boolean isSharedFileLockTable;
    private static volatile boolean propertyChecked;
    
    private FileChannelImpl(final FileDescriptor fd, final String path, final boolean readable, final boolean writable, final boolean append, final Object parent) {
        this.threads = new NativeThreadSet(2);
        this.positionLock = new Object();
        this.fd = fd;
        this.readable = readable;
        this.writable = writable;
        this.append = append;
        this.parent = parent;
        this.path = path;
        this.nd = new FileDispatcherImpl(append);
    }
    
    public static FileChannel open(final FileDescriptor fileDescriptor, final String s, final boolean b, final boolean b2, final Object o) {
        return new FileChannelImpl(fileDescriptor, s, b, b2, false, o);
    }
    
    public static FileChannel open(final FileDescriptor fileDescriptor, final String s, final boolean b, final boolean b2, final boolean b3, final Object o) {
        return new FileChannelImpl(fileDescriptor, s, b, b2, b3, o);
    }
    
    private void ensureOpen() throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }
    
    @Override
    protected void implCloseChannel() throws IOException {
        if (this.fileLockTable != null) {
            for (final FileLock fileLock : this.fileLockTable.removeAll()) {
                synchronized (fileLock) {
                    if (!fileLock.isValid()) {
                        continue;
                    }
                    this.nd.release(this.fd, fileLock.position(), fileLock.size());
                    ((FileLockImpl)fileLock).invalidate();
                }
            }
        }
        this.threads.signalAndWait();
        if (this.parent != null) {
            ((Closeable)this.parent).close();
        }
        else {
            this.nd.close(this.fd);
        }
    }
    
    @Override
    public int read(final ByteBuffer byteBuffer) throws IOException {
        this.ensureOpen();
        if (!this.readable) {
            throw new NonReadableChannelException();
        }
        synchronized (this.positionLock) {
            int read = 0;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return 0;
                }
                do {
                    read = IOUtil.read(this.fd, byteBuffer, -1L, this.nd);
                } while (read == -3 && this.isOpen());
                return IOStatus.normalize(read);
            }
            finally {
                this.threads.remove(add);
                this.end(read > 0);
                assert IOStatus.check(read);
            }
        }
    }
    
    @Override
    public long read(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IndexOutOfBoundsException();
        }
        this.ensureOpen();
        if (!this.readable) {
            throw new NonReadableChannelException();
        }
        synchronized (this.positionLock) {
            long read = 0L;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return 0L;
                }
                do {
                    read = IOUtil.read(this.fd, array, n, n2, this.nd);
                } while (read == -3L && this.isOpen());
                return IOStatus.normalize(read);
            }
            finally {
                this.threads.remove(add);
                this.end(read > 0L);
                assert IOStatus.check(read);
            }
        }
    }
    
    @Override
    public int write(final ByteBuffer byteBuffer) throws IOException {
        this.ensureOpen();
        if (!this.writable) {
            throw new NonWritableChannelException();
        }
        synchronized (this.positionLock) {
            int write = 0;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return 0;
                }
                do {
                    write = IOUtil.write(this.fd, byteBuffer, -1L, this.nd);
                } while (write == -3 && this.isOpen());
                return IOStatus.normalize(write);
            }
            finally {
                this.threads.remove(add);
                this.end(write > 0);
                assert IOStatus.check(write);
            }
        }
    }
    
    @Override
    public long write(final ByteBuffer[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n > array.length - n2) {
            throw new IndexOutOfBoundsException();
        }
        this.ensureOpen();
        if (!this.writable) {
            throw new NonWritableChannelException();
        }
        synchronized (this.positionLock) {
            long write = 0L;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return 0L;
                }
                do {
                    write = IOUtil.write(this.fd, array, n, n2, this.nd);
                } while (write == -3L && this.isOpen());
                return IOStatus.normalize(write);
            }
            finally {
                this.threads.remove(add);
                this.end(write > 0L);
                assert IOStatus.check(write);
            }
        }
    }
    
    @Override
    public long position() throws IOException {
        this.ensureOpen();
        synchronized (this.positionLock) {
            long n = -1L;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return 0L;
                }
                do {
                    n = (this.append ? this.nd.size(this.fd) : this.nd.seek(this.fd, -1L));
                } while (n == -3L && this.isOpen());
                return IOStatus.normalize(n);
            }
            finally {
                this.threads.remove(add);
                this.end(n > -1L);
                assert IOStatus.check(n);
            }
        }
    }
    
    @Override
    public FileChannel position(final long n) throws IOException {
        this.ensureOpen();
        if (n < 0L) {
            throw new IllegalArgumentException();
        }
        synchronized (this.positionLock) {
            long seek = -1L;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return null;
                }
                do {
                    seek = this.nd.seek(this.fd, n);
                } while (seek == -3L && this.isOpen());
                return this;
            }
            finally {
                this.threads.remove(add);
                this.end(seek > -1L);
                assert IOStatus.check(seek);
            }
        }
    }
    
    @Override
    public long size() throws IOException {
        this.ensureOpen();
        synchronized (this.positionLock) {
            long size = -1L;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return -1L;
                }
                do {
                    size = this.nd.size(this.fd);
                } while (size == -3L && this.isOpen());
                return IOStatus.normalize(size);
            }
            finally {
                this.threads.remove(add);
                this.end(size > -1L);
                assert IOStatus.check(size);
            }
        }
    }
    
    @Override
    public FileChannel truncate(final long n) throws IOException {
        this.ensureOpen();
        if (n < 0L) {
            throw new IllegalArgumentException("Negative size");
        }
        if (!this.writable) {
            throw new NonWritableChannelException();
        }
        synchronized (this.positionLock) {
            int truncate = -1;
            int add = -1;
            try {
                this.begin();
                add = this.threads.add();
                if (!this.isOpen()) {
                    return null;
                }
                long size;
                do {
                    size = this.nd.size(this.fd);
                } while (size == -3L && this.isOpen());
                if (!this.isOpen()) {
                    return null;
                }
                long seek;
                do {
                    seek = this.nd.seek(this.fd, -1L);
                } while (seek == -3L && this.isOpen());
                if (!this.isOpen()) {
                    return null;
                }
                assert seek >= 0L;
                if (n < size) {
                    do {
                        truncate = this.nd.truncate(this.fd, n);
                    } while (truncate == -3 && this.isOpen());
                    if (!this.isOpen()) {
                        return null;
                    }
                }
                if (seek > n) {
                    seek = n;
                }
                while (this.nd.seek(this.fd, seek) == -3L && this.isOpen()) {}
                return this;
            }
            finally {
                this.threads.remove(add);
                this.end(truncate > -1);
                assert IOStatus.check(truncate);
            }
        }
    }
    
    @Override
    public void force(final boolean b) throws IOException {
        this.ensureOpen();
        int force = -1;
        int add = -1;
        try {
            this.begin();
            add = this.threads.add();
            if (!this.isOpen()) {
                return;
            }
            do {
                force = this.nd.force(this.fd, b);
            } while (force == -3 && this.isOpen());
        }
        finally {
            this.threads.remove(add);
            this.end(force > -1);
            assert IOStatus.check(force);
        }
    }
    
    private long transferToDirectlyInternal(final long n, final int n2, final WritableByteChannel writableByteChannel, final FileDescriptor fileDescriptor) throws IOException {
        assert !(!Thread.holdsLock(this.positionLock));
        long transferTo0 = -1L;
        int add = -1;
        try {
            this.begin();
            add = this.threads.add();
            if (!this.isOpen()) {
                return -1L;
            }
            do {
                transferTo0 = this.transferTo0(this.fd, n, n2, fileDescriptor);
            } while (transferTo0 == -3L && this.isOpen());
            if (transferTo0 == -6L) {
                if (writableByteChannel instanceof SinkChannelImpl) {
                    FileChannelImpl.pipeSupported = false;
                }
                if (writableByteChannel instanceof FileChannelImpl) {
                    FileChannelImpl.fileSupported = false;
                }
                return -6L;
            }
            if (transferTo0 == -4L) {
                FileChannelImpl.transferSupported = false;
                return -4L;
            }
            return IOStatus.normalize(transferTo0);
        }
        finally {
            this.threads.remove(add);
            this.end(transferTo0 > -1L);
        }
    }
    
    private long transferToDirectly(final long n, final int n2, final WritableByteChannel writableByteChannel) throws IOException {
        if (!FileChannelImpl.transferSupported) {
            return -4L;
        }
        FileDescriptor fileDescriptor = null;
        if (writableByteChannel instanceof FileChannelImpl) {
            if (!FileChannelImpl.fileSupported) {
                return -6L;
            }
            fileDescriptor = ((FileChannelImpl)writableByteChannel).fd;
        }
        else if (writableByteChannel instanceof SelChImpl) {
            if (writableByteChannel instanceof SinkChannelImpl && !FileChannelImpl.pipeSupported) {
                return -6L;
            }
            if (!this.nd.canTransferToDirectly((SelectableChannel)writableByteChannel)) {
                return -6L;
            }
            fileDescriptor = ((SelChImpl)writableByteChannel).getFD();
        }
        if (fileDescriptor == null) {
            return -4L;
        }
        if (IOUtil.fdVal(this.fd) == IOUtil.fdVal(fileDescriptor)) {
            return -4L;
        }
        if (this.nd.transferToDirectlyNeedsPositionLock()) {
            synchronized (this.positionLock) {
                final long position = this.position();
                try {
                    return this.transferToDirectlyInternal(n, n2, writableByteChannel, fileDescriptor);
                }
                finally {
                    this.position(position);
                }
            }
        }
        return this.transferToDirectlyInternal(n, n2, writableByteChannel, fileDescriptor);
    }
    
    private long transferToTrustedChannel(long n, final long n2, final WritableByteChannel writableByteChannel) throws IOException {
        final boolean b = writableByteChannel instanceof SelChImpl;
        if (!(writableByteChannel instanceof FileChannelImpl) && !b) {
            return -4L;
        }
        long n3 = n2;
        while (n3 > 0L) {
            final long min = Math.min(n3, 8388608L);
            try {
                final MappedByteBuffer map = this.map(MapMode.READ_ONLY, n, min);
                try {
                    final int write = writableByteChannel.write(map);
                    assert write >= 0;
                    n3 -= write;
                    if (b) {
                        break;
                    }
                    assert write > 0;
                    n += write;
                }
                finally {
                    unmap(map);
                }
            }
            catch (final ClosedByInterruptException ex) {
                assert !writableByteChannel.isOpen();
                try {
                    this.close();
                }
                catch (final Throwable t) {
                    ex.addSuppressed(t);
                }
                throw ex;
            }
            catch (final IOException ex2) {
                if (n3 == n2) {
                    throw ex2;
                }
                break;
            }
        }
        return n2 - n3;
    }
    
    private long transferToArbitraryChannel(final long n, final int n2, final WritableByteChannel writableByteChannel) throws IOException {
        final ByteBuffer allocate = ByteBuffer.allocate(Math.min(n2, 8192));
        long n3 = 0L;
        long n4 = n;
        try {
            while (n3 < n2) {
                allocate.limit(Math.min((int)(n2 - n3), 8192));
                final int read = this.read(allocate, n4);
                if (read <= 0) {
                    break;
                }
                allocate.flip();
                final int write = writableByteChannel.write(allocate);
                n3 += write;
                if (write != read) {
                    break;
                }
                n4 += write;
                allocate.clear();
            }
            return n3;
        }
        catch (final IOException ex) {
            if (n3 > 0L) {
                return n3;
            }
            throw ex;
        }
    }
    
    @Override
    public long transferTo(final long n, final long n2, final WritableByteChannel writableByteChannel) throws IOException {
        this.ensureOpen();
        if (!writableByteChannel.isOpen()) {
            throw new ClosedChannelException();
        }
        if (!this.readable) {
            throw new NonReadableChannelException();
        }
        if (writableByteChannel instanceof FileChannelImpl && !((FileChannelImpl)writableByteChannel).writable) {
            throw new NonWritableChannelException();
        }
        if (n < 0L || n2 < 0L) {
            throw new IllegalArgumentException();
        }
        final long size = this.size();
        if (n > size) {
            return 0L;
        }
        int n3 = (int)Math.min(n2, 2147483647L);
        if (size - n < n3) {
            n3 = (int)(size - n);
        }
        final long transferToDirectly;
        if ((transferToDirectly = this.transferToDirectly(n, n3, writableByteChannel)) >= 0L) {
            return transferToDirectly;
        }
        final long transferToTrustedChannel;
        if ((transferToTrustedChannel = this.transferToTrustedChannel(n, n3, writableByteChannel)) >= 0L) {
            return transferToTrustedChannel;
        }
        return this.transferToArbitraryChannel(n, n3, writableByteChannel);
    }
    
    private long transferFromFileChannel(final FileChannelImpl fileChannelImpl, long n, final long n2) throws IOException {
        if (!fileChannelImpl.readable) {
            throw new NonReadableChannelException();
        }
        synchronized (fileChannelImpl.positionLock) {
            final long position = fileChannelImpl.position();
            long min;
            final long n3 = min = Math.min(n2, fileChannelImpl.size() - position);
            long n4 = position;
            while (min > 0L) {
                final MappedByteBuffer map = fileChannelImpl.map(MapMode.READ_ONLY, n4, Math.min(min, 8388608L));
                try {
                    final long n5 = this.write(map, n);
                    assert n5 > 0L;
                    n4 += n5;
                    n += n5;
                    min -= n5;
                }
                catch (final IOException ex) {
                    if (min == n3) {
                        throw ex;
                    }
                    break;
                }
                finally {
                    unmap(map);
                }
            }
            final long n6 = n3 - min;
            fileChannelImpl.position(position + n6);
            return n6;
        }
    }
    
    private long transferFromArbitraryChannel(final ReadableByteChannel readableByteChannel, final long n, final long n2) throws IOException {
        final ByteBuffer allocate = ByteBuffer.allocate((int)Math.min(n2, 8192L));
        long n3 = 0L;
        long n4 = n;
        try {
            while (n3 < n2) {
                allocate.limit((int)Math.min(n2 - n3, 8192L));
                final int read = readableByteChannel.read(allocate);
                if (read <= 0) {
                    break;
                }
                allocate.flip();
                final int write = this.write(allocate, n4);
                n3 += write;
                if (write != read) {
                    break;
                }
                n4 += write;
                allocate.clear();
            }
            return n3;
        }
        catch (final IOException ex) {
            if (n3 > 0L) {
                return n3;
            }
            throw ex;
        }
    }
    
    @Override
    public long transferFrom(final ReadableByteChannel readableByteChannel, final long n, final long n2) throws IOException {
        this.ensureOpen();
        if (!readableByteChannel.isOpen()) {
            throw new ClosedChannelException();
        }
        if (!this.writable) {
            throw new NonWritableChannelException();
        }
        if (n < 0L || n2 < 0L) {
            throw new IllegalArgumentException();
        }
        if (n > this.size()) {
            return 0L;
        }
        if (readableByteChannel instanceof FileChannelImpl) {
            return this.transferFromFileChannel((FileChannelImpl)readableByteChannel, n, n2);
        }
        return this.transferFromArbitraryChannel(readableByteChannel, n, n2);
    }
    
    @Override
    public int read(final ByteBuffer byteBuffer, final long n) throws IOException {
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        if (n < 0L) {
            throw new IllegalArgumentException("Negative position");
        }
        if (!this.readable) {
            throw new NonReadableChannelException();
        }
        this.ensureOpen();
        if (this.nd.needsPositionLock()) {
            synchronized (this.positionLock) {
                return this.readInternal(byteBuffer, n);
            }
        }
        return this.readInternal(byteBuffer, n);
    }
    
    private int readInternal(final ByteBuffer byteBuffer, final long n) throws IOException {
        assert !(!Thread.holdsLock(this.positionLock));
        int read = 0;
        int add = -1;
        try {
            this.begin();
            add = this.threads.add();
            if (!this.isOpen()) {
                return -1;
            }
            do {
                read = IOUtil.read(this.fd, byteBuffer, n, this.nd);
            } while (read == -3 && this.isOpen());
            return IOStatus.normalize(read);
        }
        finally {
            this.threads.remove(add);
            this.end(read > 0);
            assert IOStatus.check(read);
        }
    }
    
    @Override
    public int write(final ByteBuffer byteBuffer, final long n) throws IOException {
        if (byteBuffer == null) {
            throw new NullPointerException();
        }
        if (n < 0L) {
            throw new IllegalArgumentException("Negative position");
        }
        if (!this.writable) {
            throw new NonWritableChannelException();
        }
        this.ensureOpen();
        if (this.nd.needsPositionLock()) {
            synchronized (this.positionLock) {
                return this.writeInternal(byteBuffer, n);
            }
        }
        return this.writeInternal(byteBuffer, n);
    }
    
    private int writeInternal(final ByteBuffer byteBuffer, final long n) throws IOException {
        assert !(!Thread.holdsLock(this.positionLock));
        int write = 0;
        int add = -1;
        try {
            this.begin();
            add = this.threads.add();
            if (!this.isOpen()) {
                return -1;
            }
            do {
                write = IOUtil.write(this.fd, byteBuffer, n, this.nd);
            } while (write == -3 && this.isOpen());
            return IOStatus.normalize(write);
        }
        finally {
            this.threads.remove(add);
            this.end(write > 0);
            assert IOStatus.check(write);
        }
    }
    
    private static void unmap(final MappedByteBuffer mappedByteBuffer) {
        final Cleaner cleaner = ((DirectBuffer)mappedByteBuffer).cleaner();
        if (cleaner != null) {
            cleaner.clean();
        }
    }
    
    @Override
    public MappedByteBuffer map(final MapMode mapMode, final long n, final long n2) throws IOException {
        this.ensureOpen();
        if (mapMode == null) {
            throw new NullPointerException("Mode is null");
        }
        if (n < 0L) {
            throw new IllegalArgumentException("Negative position");
        }
        if (n2 < 0L) {
            throw new IllegalArgumentException("Negative size");
        }
        if (n + n2 < 0L) {
            throw new IllegalArgumentException("Position + size overflow");
        }
        if (n2 > 2147483647L) {
            throw new IllegalArgumentException("Size exceeds Integer.MAX_VALUE");
        }
        int n3 = -1;
        if (mapMode == MapMode.READ_ONLY) {
            n3 = 0;
        }
        else if (mapMode == MapMode.READ_WRITE) {
            n3 = 1;
        }
        else if (mapMode == MapMode.PRIVATE) {
            n3 = 2;
        }
        assert n3 >= 0;
        if (mapMode != MapMode.READ_ONLY && !this.writable) {
            throw new NonWritableChannelException();
        }
        if (!this.readable) {
            throw new NonReadableChannelException();
        }
        long n4 = -1L;
        int add = -1;
        try {
            this.begin();
            add = this.threads.add();
            if (!this.isOpen()) {
                return null;
            }
            int n5;
            long n7;
            synchronized (this.positionLock) {
                long size;
                do {
                    size = this.nd.size(this.fd);
                } while (size == -3L && this.isOpen());
                if (!this.isOpen()) {
                    return null;
                }
                if (size < n + n2) {
                    if (!this.writable) {
                        throw new IOException("Channel not open for writing - cannot extend file to required size");
                    }
                    while (this.nd.truncate(this.fd, n + n2) == -3 && this.isOpen()) {}
                    if (!this.isOpen()) {
                        return null;
                    }
                }
                if (n2 == 0L) {
                    n4 = 0L;
                    final FileDescriptor fileDescriptor = new FileDescriptor();
                    if (!this.writable || n3 == 0) {
                        return Util.newMappedByteBufferR(0, 0L, fileDescriptor, null);
                    }
                    return Util.newMappedByteBuffer(0, 0L, fileDescriptor, null);
                }
                else {
                    n5 = (int)(n % FileChannelImpl.allocationGranularity);
                    final long n6 = n - n5;
                    n7 = n2 + n5;
                    try {
                        n4 = this.map0(n3, n6, n7);
                    }
                    catch (final OutOfMemoryError outOfMemoryError) {
                        System.gc();
                        try {
                            Thread.sleep(100L);
                        }
                        catch (final InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        try {
                            n4 = this.map0(n3, n6, n7);
                        }
                        catch (final OutOfMemoryError outOfMemoryError2) {
                            throw new IOException("Map failed", outOfMemoryError2);
                        }
                    }
                }
            }
            FileDescriptor duplicateForMapping;
            try {
                duplicateForMapping = this.nd.duplicateForMapping(this.fd);
            }
            catch (final IOException ex2) {
                unmap0(n4, n7);
                throw ex2;
            }
            assert IOStatus.checkAll(n4);
            assert n4 % FileChannelImpl.allocationGranularity == 0L;
            final int n8 = (int)n2;
            final Unmapper unmapper = new Unmapper(n4, n7, n8, duplicateForMapping);
            if (!this.writable || n3 == 0) {
                return Util.newMappedByteBufferR(n8, n4 + n5, duplicateForMapping, unmapper);
            }
            return Util.newMappedByteBuffer(n8, n4 + n5, duplicateForMapping, unmapper);
        }
        finally {
            this.threads.remove(add);
            this.end(IOStatus.checkAll(n4));
        }
    }
    
    public static JavaNioAccess.BufferPool getMappedBufferPool() {
        return new JavaNioAccess.BufferPool() {
            @Override
            public String getName() {
                return "mapped";
            }
            
            @Override
            public long getCount() {
                return Unmapper.count;
            }
            
            @Override
            public long getTotalCapacity() {
                return Unmapper.totalCapacity;
            }
            
            @Override
            public long getMemoryUsed() {
                return Unmapper.totalSize;
            }
        };
    }
    
    private static boolean isSharedFileLockTable() {
        if (!FileChannelImpl.propertyChecked) {
            synchronized (FileChannelImpl.class) {
                if (!FileChannelImpl.propertyChecked) {
                    final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.nio.ch.disableSystemWideOverlappingFileLockCheck"));
                    FileChannelImpl.isSharedFileLockTable = (s == null || s.equals("false"));
                    FileChannelImpl.propertyChecked = true;
                }
            }
        }
        return FileChannelImpl.isSharedFileLockTable;
    }
    
    private FileLockTable fileLockTable() throws IOException {
        if (this.fileLockTable == null) {
            synchronized (this) {
                if (this.fileLockTable == null) {
                    if (isSharedFileLockTable()) {
                        final int add = this.threads.add();
                        try {
                            this.ensureOpen();
                            this.fileLockTable = FileLockTable.newSharedFileLockTable(this, this.fd);
                        }
                        finally {
                            this.threads.remove(add);
                        }
                    }
                    else {
                        this.fileLockTable = new SimpleFileLockTable();
                    }
                }
            }
        }
        return this.fileLockTable;
    }
    
    @Override
    public FileLock lock(final long n, final long n2, final boolean b) throws IOException {
        this.ensureOpen();
        if (b && !this.readable) {
            throw new NonReadableChannelException();
        }
        if (!b && !this.writable) {
            throw new NonWritableChannelException();
        }
        FileLockImpl fileLockImpl = new FileLockImpl(this, n, n2, b);
        final FileLockTable fileLockTable = this.fileLockTable();
        fileLockTable.add(fileLockImpl);
        boolean b2 = false;
        int add = -1;
        try {
            this.begin();
            add = this.threads.add();
            if (!this.isOpen()) {
                return null;
            }
            int lock;
            do {
                lock = this.nd.lock(this.fd, true, n, n2, b);
            } while (lock == 2 && this.isOpen());
            if (this.isOpen()) {
                if (lock == 1) {
                    assert b;
                    final FileLockImpl fileLockImpl2 = new FileLockImpl(this, n, n2, false);
                    fileLockTable.replace(fileLockImpl, fileLockImpl2);
                    fileLockImpl = fileLockImpl2;
                }
                b2 = true;
            }
        }
        finally {
            if (!b2) {
                fileLockTable.remove(fileLockImpl);
            }
            this.threads.remove(add);
            try {
                this.end(b2);
            }
            catch (final ClosedByInterruptException ex) {
                throw new FileLockInterruptionException();
            }
        }
        return fileLockImpl;
    }
    
    @Override
    public FileLock tryLock(final long n, final long n2, final boolean b) throws IOException {
        this.ensureOpen();
        if (b && !this.readable) {
            throw new NonReadableChannelException();
        }
        if (!b && !this.writable) {
            throw new NonWritableChannelException();
        }
        final FileLockImpl fileLockImpl = new FileLockImpl(this, n, n2, b);
        final FileLockTable fileLockTable = this.fileLockTable();
        fileLockTable.add(fileLockImpl);
        final int add = this.threads.add();
        try {
            int lock;
            try {
                this.ensureOpen();
                lock = this.nd.lock(this.fd, false, n, n2, b);
            }
            catch (final IOException ex) {
                fileLockTable.remove(fileLockImpl);
                throw ex;
            }
            if (lock == -1) {
                fileLockTable.remove(fileLockImpl);
                return null;
            }
            if (lock != 1) {
                return fileLockImpl;
            }
            assert b;
            final FileLockImpl fileLockImpl2 = new FileLockImpl(this, n, n2, false);
            fileLockTable.replace(fileLockImpl, fileLockImpl2);
            return fileLockImpl2;
        }
        finally {
            this.threads.remove(add);
        }
    }
    
    void release(final FileLockImpl fileLockImpl) throws IOException {
        final int add = this.threads.add();
        try {
            this.ensureOpen();
            this.nd.release(this.fd, fileLockImpl.position(), fileLockImpl.size());
        }
        finally {
            this.threads.remove(add);
        }
        assert this.fileLockTable != null;
        this.fileLockTable.remove(fileLockImpl);
    }
    
    private native long map0(final int p0, final long p1, final long p2) throws IOException;
    
    private static native int unmap0(final long p0, final long p1);
    
    private native long transferTo0(final FileDescriptor p0, final long p1, final long p2, final FileDescriptor p3);
    
    private static native long initIDs();
    
    static {
        FileChannelImpl.transferSupported = true;
        FileChannelImpl.pipeSupported = true;
        FileChannelImpl.fileSupported = true;
        IOUtil.load();
        allocationGranularity = initIDs();
    }
    
    private static class Unmapper implements Runnable
    {
        private static final NativeDispatcher nd;
        static volatile int count;
        static volatile long totalSize;
        static volatile long totalCapacity;
        private volatile long address;
        private final long size;
        private final int cap;
        private final FileDescriptor fd;
        
        private Unmapper(final long address, final long size, final int cap, final FileDescriptor fd) {
            assert address != 0L;
            this.address = address;
            this.size = size;
            this.cap = cap;
            this.fd = fd;
            synchronized (Unmapper.class) {
                ++Unmapper.count;
                Unmapper.totalSize += size;
                Unmapper.totalCapacity += cap;
            }
        }
        
        @Override
        public void run() {
            if (this.address == 0L) {
                return;
            }
            unmap0(this.address, this.size);
            this.address = 0L;
            if (this.fd.valid()) {
                try {
                    Unmapper.nd.close(this.fd);
                }
                catch (final IOException ex) {}
            }
            synchronized (Unmapper.class) {
                --Unmapper.count;
                Unmapper.totalSize -= this.size;
                Unmapper.totalCapacity -= this.cap;
            }
        }
        
        static {
            nd = new FileDispatcherImpl();
        }
    }
    
    private static class SimpleFileLockTable extends FileLockTable
    {
        private final List<FileLock> lockList;
        
        public SimpleFileLockTable() {
            this.lockList = new ArrayList<FileLock>(2);
        }
        
        private void checkList(final long n, final long n2) throws OverlappingFileLockException {
            assert Thread.holdsLock(this.lockList);
            final Iterator<FileLock> iterator = this.lockList.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().overlaps(n, n2)) {
                    throw new OverlappingFileLockException();
                }
            }
        }
        
        @Override
        public void add(final FileLock fileLock) throws OverlappingFileLockException {
            synchronized (this.lockList) {
                this.checkList(fileLock.position(), fileLock.size());
                this.lockList.add(fileLock);
            }
        }
        
        @Override
        public void remove(final FileLock fileLock) {
            synchronized (this.lockList) {
                this.lockList.remove(fileLock);
            }
        }
        
        @Override
        public List<FileLock> removeAll() {
            synchronized (this.lockList) {
                final ArrayList list = new ArrayList((Collection<? extends E>)this.lockList);
                this.lockList.clear();
                return list;
            }
        }
        
        @Override
        public void replace(final FileLock fileLock, final FileLock fileLock2) {
            synchronized (this.lockList) {
                this.lockList.remove(fileLock);
                this.lockList.add(fileLock2);
            }
        }
    }
}
