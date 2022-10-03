package sun.java2d;

import java.lang.ref.Reference;

public abstract class ReentrantContextProviderTL<K extends ReentrantContext> extends ReentrantContextProvider<K>
{
    private final ThreadLocal<Reference<K>> ctxTL;
    private final ReentrantContextProviderCLQ<K> ctxProviderCLQ;
    
    public ReentrantContextProviderTL(final int n) {
        this(n, 2);
    }
    
    public ReentrantContextProviderTL(final int n, final int n2) {
        super(n);
        this.ctxTL = new ThreadLocal<Reference<K>>();
        this.ctxProviderCLQ = new ReentrantContextProviderCLQ<K>(n2) {
            @Override
            protected K newContext() {
                return ReentrantContextProviderTL.this.newContext();
            }
        };
    }
    
    @Override
    public final K acquire() {
        ReentrantContext reentrantContext = null;
        final Reference reference = this.ctxTL.get();
        if (reference != null) {
            reentrantContext = (K)reference.get();
        }
        if (reentrantContext == null) {
            reentrantContext = this.newContext();
            this.ctxTL.set(this.getOrCreateReference((K)reentrantContext));
        }
        if (reentrantContext.usage == 0) {
            reentrantContext.usage = 1;
        }
        else {
            reentrantContext = this.ctxProviderCLQ.acquire();
        }
        return (K)reentrantContext;
    }
    
    @Override
    public final void release(final K k) {
        if (k.usage == 1) {
            k.usage = 0;
        }
        else {
            this.ctxProviderCLQ.release(k);
        }
    }
}
