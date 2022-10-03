package org.apache.lucene.store;

import java.io.IOException;

public final class NoLockFactory extends LockFactory
{
    public static final NoLockFactory INSTANCE;
    static final NoLock SINGLETON_LOCK;
    
    private NoLockFactory() {
    }
    
    @Override
    public Lock obtainLock(final Directory dir, final String lockName) {
        return NoLockFactory.SINGLETON_LOCK;
    }
    
    static {
        INSTANCE = new NoLockFactory();
        SINGLETON_LOCK = new NoLock();
    }
    
    private static class NoLock extends Lock
    {
        @Override
        public void close() {
        }
        
        @Override
        public void ensureValid() throws IOException {
        }
        
        @Override
        public String toString() {
            return "NoLock";
        }
    }
}
