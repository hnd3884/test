package org.apache.lucene.store;

import java.io.IOException;

public abstract class FSLockFactory extends LockFactory
{
    public static final FSLockFactory getDefault() {
        return NativeFSLockFactory.INSTANCE;
    }
    
    @Override
    public final Lock obtainLock(final Directory dir, final String lockName) throws IOException {
        if (!(dir instanceof FSDirectory)) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + " can only be used with FSDirectory subclasses, got: " + dir);
        }
        return this.obtainFSLock((FSDirectory)dir, lockName);
    }
    
    protected abstract Lock obtainFSLock(final FSDirectory p0, final String p1) throws IOException;
}
