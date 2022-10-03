package io.netty.buffer;

import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf
{
    private static final long REFCNT_FIELD_OFFSET;
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> AIF_UPDATER;
    private static final ReferenceCountUpdater<AbstractReferenceCountedByteBuf> updater;
    private volatile int refCnt;
    
    protected AbstractReferenceCountedByteBuf(final int maxCapacity) {
        super(maxCapacity);
        this.refCnt = AbstractReferenceCountedByteBuf.updater.initialValue();
    }
    
    @Override
    boolean isAccessible() {
        return AbstractReferenceCountedByteBuf.updater.isLiveNonVolatile(this);
    }
    
    @Override
    public int refCnt() {
        return AbstractReferenceCountedByteBuf.updater.refCnt(this);
    }
    
    protected final void setRefCnt(final int refCnt) {
        AbstractReferenceCountedByteBuf.updater.setRefCnt(this, refCnt);
    }
    
    protected final void resetRefCnt() {
        AbstractReferenceCountedByteBuf.updater.resetRefCnt(this);
    }
    
    @Override
    public ByteBuf retain() {
        return AbstractReferenceCountedByteBuf.updater.retain(this);
    }
    
    @Override
    public ByteBuf retain(final int increment) {
        return AbstractReferenceCountedByteBuf.updater.retain(this, increment);
    }
    
    @Override
    public ByteBuf touch() {
        return this;
    }
    
    @Override
    public ByteBuf touch(final Object hint) {
        return this;
    }
    
    @Override
    public boolean release() {
        return this.handleRelease(AbstractReferenceCountedByteBuf.updater.release(this));
    }
    
    @Override
    public boolean release(final int decrement) {
        return this.handleRelease(AbstractReferenceCountedByteBuf.updater.release(this, decrement));
    }
    
    private boolean handleRelease(final boolean result) {
        if (result) {
            this.deallocate();
        }
        return result;
    }
    
    protected abstract void deallocate();
    
    static {
        REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCountedByteBuf.class, "refCnt");
        AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");
        updater = new ReferenceCountUpdater<AbstractReferenceCountedByteBuf>() {
            @Override
            protected AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> updater() {
                return AbstractReferenceCountedByteBuf.AIF_UPDATER;
            }
            
            @Override
            protected long unsafeOffset() {
                return AbstractReferenceCountedByteBuf.REFCNT_FIELD_OFFSET;
            }
        };
    }
}
