package org.apache.lucene.store;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public final class VerifyingLockFactory extends LockFactory
{
    final LockFactory lf;
    final InputStream in;
    final OutputStream out;
    
    public VerifyingLockFactory(final LockFactory lf, final InputStream in, final OutputStream out) throws IOException {
        this.lf = lf;
        this.in = in;
        this.out = out;
    }
    
    @Override
    public Lock obtainLock(final Directory dir, final String lockName) throws IOException {
        return new CheckedLock(this.lf.obtainLock(dir, lockName));
    }
    
    private class CheckedLock extends Lock
    {
        private final Lock lock;
        
        public CheckedLock(final Lock lock) throws IOException {
            this.lock = lock;
            this.verify((byte)1);
        }
        
        @Override
        public void ensureValid() throws IOException {
            this.lock.ensureValid();
        }
        
        @Override
        public void close() throws IOException {
            try (final Lock l = this.lock) {
                l.ensureValid();
                this.verify((byte)0);
            }
        }
        
        private void verify(final byte message) throws IOException {
            VerifyingLockFactory.this.out.write(message);
            VerifyingLockFactory.this.out.flush();
            final int ret = VerifyingLockFactory.this.in.read();
            if (ret < 0) {
                throw new IllegalStateException("Lock server died because of locking error.");
            }
            if (ret != message) {
                throw new IOException("Protocol violation.");
            }
        }
    }
}
