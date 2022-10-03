package sun.java2d;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class ReentrantContextProviderCLQ<K extends ReentrantContext> extends ReentrantContextProvider<K>
{
    private final ConcurrentLinkedQueue<Reference<K>> ctxQueue;
    
    public ReentrantContextProviderCLQ(final int n) {
        super(n);
        this.ctxQueue = new ConcurrentLinkedQueue<Reference<K>>();
    }
    
    @Override
    public final K acquire() {
        ReentrantContext context;
        Reference reference;
        for (context = null; context == null && (reference = this.ctxQueue.poll()) != null; context = (K)reference.get()) {}
        if (context == null) {
            context = this.newContext();
            context.usage = 2;
        }
        return (K)context;
    }
    
    @Override
    public final void release(final K k) {
        if (k.usage == 2) {
            this.ctxQueue.offer(this.getOrCreateReference(k));
        }
    }
}
