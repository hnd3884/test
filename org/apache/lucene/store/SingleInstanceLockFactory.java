package org.apache.lucene.store;

import java.io.IOException;
import java.util.HashSet;

public final class SingleInstanceLockFactory extends LockFactory
{
    final HashSet<String> locks;
    
    public SingleInstanceLockFactory() {
        this.locks = new HashSet<String>();
    }
    
    @Override
    public Lock obtainLock(final Directory dir, final String lockName) throws IOException {
        synchronized (this.locks) {
            if (this.locks.add(lockName)) {
                return new SingleInstanceLock(lockName);
            }
            throw new LockObtainFailedException("lock instance already obtained: (dir=" + dir + ", lockName=" + lockName + ")");
        }
    }
    
    private class SingleInstanceLock extends Lock
    {
        private final String lockName;
        private volatile boolean closed;
        
        public SingleInstanceLock(final String lockName) {
            this.lockName = lockName;
        }
        
        @Override
        public void ensureValid() throws IOException {
            if (this.closed) {
                throw new AlreadyClosedException("Lock instance already released: " + this);
            }
            synchronized (SingleInstanceLockFactory.this.locks) {
                if (!SingleInstanceLockFactory.this.locks.contains(this.lockName)) {
                    throw new AlreadyClosedException("Lock instance was invalidated from map: " + this);
                }
            }
        }
        
        @Override
        public synchronized void close() throws IOException {
            if (this.closed) {
                return;
            }
            try {
                synchronized (SingleInstanceLockFactory.this.locks) {
                    if (!SingleInstanceLockFactory.this.locks.remove(this.lockName)) {
                        throw new AlreadyClosedException("Lock was already released: " + this);
                    }
                }
            }
            finally {
                this.closed = true;
            }
        }
        
        @Override
        public String toString() {
            return super.toString() + ": " + this.lockName;
        }
    }
}
