package io.netty.util;

import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCounted implements ReferenceCounted
{
    private static final long REFCNT_FIELD_OFFSET;
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCounted> AIF_UPDATER;
    private static final ReferenceCountUpdater<AbstractReferenceCounted> updater;
    private volatile int refCnt;
    
    public AbstractReferenceCounted() {
        this.refCnt = AbstractReferenceCounted.updater.initialValue();
    }
    
    @Override
    public int refCnt() {
        return AbstractReferenceCounted.updater.refCnt(this);
    }
    
    protected final void setRefCnt(final int refCnt) {
        AbstractReferenceCounted.updater.setRefCnt(this, refCnt);
    }
    
    @Override
    public ReferenceCounted retain() {
        return AbstractReferenceCounted.updater.retain(this);
    }
    
    @Override
    public ReferenceCounted retain(final int increment) {
        return AbstractReferenceCounted.updater.retain(this, increment);
    }
    
    @Override
    public ReferenceCounted touch() {
        return this.touch(null);
    }
    
    @Override
    public boolean release() {
        return this.handleRelease(AbstractReferenceCounted.updater.release(this));
    }
    
    @Override
    public boolean release(final int decrement) {
        return this.handleRelease(AbstractReferenceCounted.updater.release(this, decrement));
    }
    
    private boolean handleRelease(final boolean result) {
        if (result) {
            this.deallocate();
        }
        return result;
    }
    
    protected abstract void deallocate();
    
    static {
        REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCounted.class, "refCnt");
        AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCounted.class, "refCnt");
        updater = new ReferenceCountUpdater<AbstractReferenceCounted>() {
            @Override
            protected AtomicIntegerFieldUpdater<AbstractReferenceCounted> updater() {
                return AbstractReferenceCounted.AIF_UPDATER;
            }
            
            @Override
            protected long unsafeOffset() {
                return AbstractReferenceCounted.REFCNT_FIELD_OFFSET;
            }
        };
    }
}
