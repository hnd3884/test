package io.netty.util.internal;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

public final class PendingWrite
{
    private static final ObjectPool<PendingWrite> RECYCLER;
    private final ObjectPool.Handle<PendingWrite> handle;
    private Object msg;
    private Promise<Void> promise;
    
    public static PendingWrite newInstance(final Object msg, final Promise<Void> promise) {
        final PendingWrite pending = PendingWrite.RECYCLER.get();
        pending.msg = msg;
        pending.promise = promise;
        return pending;
    }
    
    private PendingWrite(final ObjectPool.Handle<PendingWrite> handle) {
        this.handle = handle;
    }
    
    public boolean recycle() {
        this.msg = null;
        this.promise = null;
        this.handle.recycle(this);
        return true;
    }
    
    public boolean failAndRecycle(final Throwable cause) {
        ReferenceCountUtil.release(this.msg);
        if (this.promise != null) {
            this.promise.setFailure(cause);
        }
        return this.recycle();
    }
    
    public boolean successAndRecycle() {
        if (this.promise != null) {
            this.promise.setSuccess(null);
        }
        return this.recycle();
    }
    
    public Object msg() {
        return this.msg;
    }
    
    public Promise<Void> promise() {
        return this.promise;
    }
    
    public Promise<Void> recycleAndGet() {
        final Promise<Void> promise = this.promise;
        this.recycle();
        return promise;
    }
    
    static {
        RECYCLER = ObjectPool.newPool((ObjectPool.ObjectCreator<PendingWrite>)new ObjectPool.ObjectCreator<PendingWrite>() {
            @Override
            public PendingWrite newObject(final ObjectPool.Handle<PendingWrite> handle) {
                return new PendingWrite(handle, null);
            }
        });
    }
}
