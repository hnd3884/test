package com.sun.jndi.ldap.pool;

public final class PoolCleaner implements Runnable
{
    private final Pool[] pools;
    private final long period;
    
    public PoolCleaner(final long period, final Pool[] array) {
        this.period = period;
        this.pools = array.clone();
    }
    
    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait(this.period);
                }
                catch (final InterruptedException ex) {}
                final long n = System.currentTimeMillis() - this.period;
                for (int i = 0; i < this.pools.length; ++i) {
                    if (this.pools[i] != null) {
                        this.pools[i].expire(n);
                    }
                }
            }
        }
    }
}
