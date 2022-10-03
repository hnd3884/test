package org.apache.lucene.store;

import java.io.IOException;

public abstract class BaseDirectory extends Directory
{
    protected volatile boolean isOpen;
    protected final LockFactory lockFactory;
    
    protected BaseDirectory(final LockFactory lockFactory) {
        this.isOpen = true;
        if (lockFactory == null) {
            throw new NullPointerException("LockFactory cannot be null, use an explicit instance!");
        }
        this.lockFactory = lockFactory;
    }
    
    @Override
    public final Lock obtainLock(final String name) throws IOException {
        return this.lockFactory.obtainLock(this, name);
    }
    
    @Override
    protected final void ensureOpen() throws AlreadyClosedException {
        if (!this.isOpen) {
            throw new AlreadyClosedException("this Directory is closed");
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + " lockFactory=" + this.lockFactory;
    }
}
