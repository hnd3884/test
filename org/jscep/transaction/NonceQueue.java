package org.jscep.transaction;

import java.util.WeakHashMap;
import java.util.Map;

public final class NonceQueue
{
    private static final int DEFAULT_QUEUE_SIZE = 20;
    private final Map<Nonce, Boolean> backingQueue;
    
    public NonceQueue() {
        this.backingQueue = new WeakHashMap<Nonce, Boolean>(20);
    }
    
    public synchronized void add(final Nonce nonce) {
        this.backingQueue.put(nonce, Boolean.FALSE);
    }
    
    public synchronized boolean contains(final Nonce nonce) {
        return this.backingQueue.containsKey(nonce);
    }
}
