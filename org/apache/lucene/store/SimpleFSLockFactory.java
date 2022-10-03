package org.apache.lucene.store;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.nio.file.FileSystemException;
import java.nio.file.Path;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

public final class SimpleFSLockFactory extends FSLockFactory
{
    public static final SimpleFSLockFactory INSTANCE;
    
    private SimpleFSLockFactory() {
    }
    
    @Override
    protected Lock obtainFSLock(final FSDirectory dir, final String lockName) throws IOException {
        final Path lockDir = dir.getDirectory();
        Files.createDirectories(lockDir, (FileAttribute<?>[])new FileAttribute[0]);
        final Path lockFile = lockDir.resolve(lockName);
        try {
            Files.createFile(lockFile, (FileAttribute<?>[])new FileAttribute[0]);
        }
        catch (final FileAlreadyExistsException | AccessDeniedException e) {
            throw new LockObtainFailedException("Lock held elsewhere: " + lockFile, e);
        }
        final FileTime creationTime = Files.readAttributes(lockFile, BasicFileAttributes.class, new LinkOption[0]).creationTime();
        return new SimpleFSLock(lockFile, creationTime);
    }
    
    static {
        INSTANCE = new SimpleFSLockFactory();
    }
    
    static final class SimpleFSLock extends Lock
    {
        private final Path path;
        private final FileTime creationTime;
        private volatile boolean closed;
        
        SimpleFSLock(final Path path, final FileTime creationTime) throws IOException {
            this.path = path;
            this.creationTime = creationTime;
        }
        
        @Override
        public void ensureValid() throws IOException {
            if (this.closed) {
                throw new AlreadyClosedException("Lock instance already released: " + this);
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
            try {
                try {
                    this.ensureValid();
                }
                catch (final Throwable exc) {
                    throw new LockReleaseFailedException("Lock file cannot be safely removed. Manual intervention is recommended.", exc);
                }
                try {
                    Files.delete(this.path);
                }
                catch (final Throwable exc) {
                    throw new LockReleaseFailedException("Unable to remove lock file. Manual intervention is recommended", exc);
                }
            }
            finally {
                this.closed = true;
            }
        }
        
        @Override
        public String toString() {
            return "SimpleFSLock(path=" + this.path + ",ctime=" + this.creationTime + ")";
        }
    }
}
