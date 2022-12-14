package io.netty.util.concurrent;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ObjectUtil;

public final class FailedFuture<V> extends CompleteFuture<V>
{
    private final Throwable cause;
    
    public FailedFuture(final EventExecutor executor, final Throwable cause) {
        super(executor);
        this.cause = ObjectUtil.checkNotNull(cause, "cause");
    }
    
    @Override
    public Throwable cause() {
        return this.cause;
    }
    
    @Override
    public boolean isSuccess() {
        return false;
    }
    
    @Override
    public Future<V> sync() {
        PlatformDependent.throwException(this.cause);
        return this;
    }
    
    @Override
    public Future<V> syncUninterruptibly() {
        PlatformDependent.throwException(this.cause);
        return this;
    }
    
    @Override
    public V getNow() {
        return null;
    }
}
