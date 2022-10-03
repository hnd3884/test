package io.netty.util.internal;

import io.netty.util.IllegalReferenceCountException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import io.netty.util.ReferenceCounted;

public abstract class ReferenceCountUpdater<T extends ReferenceCounted>
{
    protected ReferenceCountUpdater() {
    }
    
    public static long getUnsafeOffset(final Class<? extends ReferenceCounted> clz, final String fieldName) {
        try {
            if (PlatformDependent.hasUnsafe()) {
                return PlatformDependent.objectFieldOffset(clz.getDeclaredField(fieldName));
            }
        }
        catch (final Throwable t) {}
        return -1L;
    }
    
    protected abstract AtomicIntegerFieldUpdater<T> updater();
    
    protected abstract long unsafeOffset();
    
    public final int initialValue() {
        return 2;
    }
    
    private static int realRefCnt(final int rawCnt) {
        return (rawCnt != 2 && rawCnt != 4 && (rawCnt & 0x1) != 0x0) ? 0 : (rawCnt >>> 1);
    }
    
    private static int toLiveRealRefCnt(final int rawCnt, final int decrement) {
        if (rawCnt == 2 || rawCnt == 4 || (rawCnt & 0x1) == 0x0) {
            return rawCnt >>> 1;
        }
        throw new IllegalReferenceCountException(0, -decrement);
    }
    
    private int nonVolatileRawCnt(final T instance) {
        final long offset = this.unsafeOffset();
        return (offset != -1L) ? PlatformDependent.getInt(instance, offset) : this.updater().get(instance);
    }
    
    public final int refCnt(final T instance) {
        return realRefCnt(this.updater().get(instance));
    }
    
    public final boolean isLiveNonVolatile(final T instance) {
        final long offset = this.unsafeOffset();
        final int rawCnt = (offset != -1L) ? PlatformDependent.getInt(instance, offset) : this.updater().get(instance);
        return rawCnt == 2 || rawCnt == 4 || rawCnt == 6 || rawCnt == 8 || (rawCnt & 0x1) == 0x0;
    }
    
    public final void setRefCnt(final T instance, final int refCnt) {
        this.updater().set(instance, (refCnt > 0) ? (refCnt << 1) : 1);
    }
    
    public final void resetRefCnt(final T instance) {
        this.updater().set(instance, this.initialValue());
    }
    
    public final T retain(final T instance) {
        return this.retain0(instance, 1, 2);
    }
    
    public final T retain(final T instance, final int increment) {
        final int rawIncrement = ObjectUtil.checkPositive(increment, "increment") << 1;
        return this.retain0(instance, increment, rawIncrement);
    }
    
    private T retain0(final T instance, final int increment, final int rawIncrement) {
        final int oldRef = this.updater().getAndAdd(instance, rawIncrement);
        if (oldRef != 2 && oldRef != 4 && (oldRef & 0x1) != 0x0) {
            throw new IllegalReferenceCountException(0, increment);
        }
        if ((oldRef <= 0 && oldRef + rawIncrement >= 0) || (oldRef >= 0 && oldRef + rawIncrement < oldRef)) {
            this.updater().getAndAdd(instance, -rawIncrement);
            throw new IllegalReferenceCountException(realRefCnt(oldRef), increment);
        }
        return instance;
    }
    
    public final boolean release(final T instance) {
        final int rawCnt = this.nonVolatileRawCnt(instance);
        return (rawCnt == 2) ? (this.tryFinalRelease0(instance, 2) || this.retryRelease0(instance, 1)) : this.nonFinalRelease0(instance, 1, rawCnt, toLiveRealRefCnt(rawCnt, 1));
    }
    
    public final boolean release(final T instance, final int decrement) {
        final int rawCnt = this.nonVolatileRawCnt(instance);
        final int realCnt = toLiveRealRefCnt(rawCnt, ObjectUtil.checkPositive(decrement, "decrement"));
        return (decrement == realCnt) ? (this.tryFinalRelease0(instance, rawCnt) || this.retryRelease0(instance, decrement)) : this.nonFinalRelease0(instance, decrement, rawCnt, realCnt);
    }
    
    private boolean tryFinalRelease0(final T instance, final int expectRawCnt) {
        return this.updater().compareAndSet(instance, expectRawCnt, 1);
    }
    
    private boolean nonFinalRelease0(final T instance, final int decrement, final int rawCnt, final int realCnt) {
        return (decrement >= realCnt || !this.updater().compareAndSet(instance, rawCnt, rawCnt - (decrement << 1))) && this.retryRelease0(instance, decrement);
    }
    
    private boolean retryRelease0(final T instance, final int decrement) {
        while (true) {
            final int rawCnt = this.updater().get(instance);
            final int realCnt = toLiveRealRefCnt(rawCnt, decrement);
            if (decrement == realCnt) {
                if (this.tryFinalRelease0(instance, rawCnt)) {
                    return true;
                }
            }
            else {
                if (decrement >= realCnt) {
                    throw new IllegalReferenceCountException(realCnt, -decrement);
                }
                if (this.updater().compareAndSet(instance, rawCnt, rawCnt - (decrement << 1))) {
                    return false;
                }
            }
            Thread.yield();
        }
    }
}
