package sun.nio.ch;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class FileLockImpl extends FileLock
{
    private volatile boolean valid;
    
    FileLockImpl(final FileChannel fileChannel, final long n, final long n2, final boolean b) {
        super(fileChannel, n, n2, b);
        this.valid = true;
    }
    
    FileLockImpl(final AsynchronousFileChannel asynchronousFileChannel, final long n, final long n2, final boolean b) {
        super(asynchronousFileChannel, n, n2, b);
        this.valid = true;
    }
    
    @Override
    public boolean isValid() {
        return this.valid;
    }
    
    void invalidate() {
        assert Thread.holdsLock(this);
        this.valid = false;
    }
    
    @Override
    public synchronized void release() throws IOException {
        final Channel acquiredBy = this.acquiredBy();
        if (!acquiredBy.isOpen()) {
            throw new ClosedChannelException();
        }
        if (this.valid) {
            if (acquiredBy instanceof FileChannelImpl) {
                ((FileChannelImpl)acquiredBy).release(this);
            }
            else {
                if (!(acquiredBy instanceof AsynchronousFileChannelImpl)) {
                    throw new AssertionError();
                }
                ((AsynchronousFileChannelImpl)acquiredBy).release(this);
            }
            this.valid = false;
        }
    }
}
