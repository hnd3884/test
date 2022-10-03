package org.apache.lucene.store;

import java.util.Collections;
import java.util.HashSet;
import java.nio.channels.FileLock;
import java.nio.file.attribute.FileTime;
import java.nio.file.Path;
import org.apache.lucene.util.IOUtils;
import java.io.Closeable;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.LinkOption;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

public final class NativeFSLockFactory extends FSLockFactory
{
    public static final NativeFSLockFactory INSTANCE;
    private static final Set<String> LOCK_HELD;
    
    private NativeFSLockFactory() {
    }
    
    @Override
    protected Lock obtainFSLock(final FSDirectory dir, final String lockName) throws IOException {
        final Path lockDir = dir.getDirectory();
        Files.createDirectories(lockDir, (FileAttribute<?>[])new FileAttribute[0]);
        final Path lockFile = lockDir.resolve(lockName);
        try {
            Files.createFile(lockFile, (FileAttribute<?>[])new FileAttribute[0]);
        }
        catch (final IOException ex) {}
        final Path realPath = lockFile.toRealPath(new LinkOption[0]);
        final FileTime creationTime = Files.readAttributes(realPath, BasicFileAttributes.class, new LinkOption[0]).creationTime();
        if (NativeFSLockFactory.LOCK_HELD.add(realPath.toString())) {
            FileChannel channel = null;
            FileLock lock = null;
            try {
                channel = FileChannel.open(realPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                lock = channel.tryLock();
                if (lock != null) {
                    return new NativeFSLock(lock, channel, realPath, creationTime);
                }
                throw new LockObtainFailedException("Lock held by another program: " + realPath);
            }
            finally {
                if (lock == null) {
                    IOUtils.closeWhileHandlingException(channel);
                    clearLockHeld(realPath);
                }
            }
        }
        throw new LockObtainFailedException("Lock held by this virtual machine: " + realPath);
    }
    
    private static final void clearLockHeld(final Path path) throws IOException {
        final boolean remove = NativeFSLockFactory.LOCK_HELD.remove(path.toString());
        if (!remove) {
            throw new AlreadyClosedException("Lock path was cleared but never marked as held: " + path);
        }
    }
    
    static {
        INSTANCE = new NativeFSLockFactory();
        LOCK_HELD = Collections.synchronizedSet(new HashSet<String>());
    }
    
    static final class NativeFSLock extends Lock
    {
        final FileLock lock;
        final FileChannel channel;
        final Path path;
        final FileTime creationTime;
        volatile boolean closed;
        
        NativeFSLock(final FileLock lock, final FileChannel channel, final Path path, final FileTime creationTime) {
            this.lock = lock;
            this.channel = channel;
            this.path = path;
            this.creationTime = creationTime;
        }
        
        @Override
        public void ensureValid() throws IOException {
            if (this.closed) {
                throw new AlreadyClosedException("Lock instance already released: " + this);
            }
            if (!NativeFSLockFactory.LOCK_HELD.contains(this.path.toString())) {
                throw new AlreadyClosedException("Lock path unexpectedly cleared from map: " + this);
            }
            if (!this.lock.isValid()) {
                throw new AlreadyClosedException("FileLock invalidated by an external force: " + this);
            }
            final long size = this.channel.size();
            if (size != 0L) {
                throw new AlreadyClosedException("Unexpected lock file size: " + size + ", (lock=" + this + ")");
            }
            final FileTime ctime = Files.readAttributes(this.path, BasicFileAttributes.class, new LinkOption[0]).creationTime();
            if (!this.creationTime.equals(ctime)) {
                throw new AlreadyClosedException("Underlying file changed by an external force at " + this.creationTime + ", (lock=" + this + ")");
            }
        }
        
        @Override
        public synchronized void close() throws IOException {
            if (this.closed) {
                return;
            }
            try (final FileChannel channel = this.channel;
                 final FileLock lock = this.lock) {
                assert lock != null;
                assert channel != null;
            }
            finally {
                this.closed = true;
                clearLockHeld(this.path);
            }
        }
        
        @Override
        public String toString() {
            return "NativeFSLock(path=" + this.path + ",impl=" + this.lock + ",ctime=" + this.creationTime + ")";
        }
    }
}
