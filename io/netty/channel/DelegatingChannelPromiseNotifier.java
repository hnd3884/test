package io.netty.channel;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;

public final class DelegatingChannelPromiseNotifier implements ChannelPromise, ChannelFutureListener
{
    private static final InternalLogger logger;
    private final ChannelPromise delegate;
    private final boolean logNotifyFailure;
    
    public DelegatingChannelPromiseNotifier(final ChannelPromise delegate) {
        this(delegate, !(delegate instanceof VoidChannelPromise));
    }
    
    public DelegatingChannelPromiseNotifier(final ChannelPromise delegate, final boolean logNotifyFailure) {
        this.delegate = ObjectUtil.checkNotNull(delegate, "delegate");
        this.logNotifyFailure = logNotifyFailure;
    }
    
    @Override
    public void operationComplete(final ChannelFuture future) throws Exception {
        final InternalLogger internalLogger = this.logNotifyFailure ? DelegatingChannelPromiseNotifier.logger : null;
        if (future.isSuccess()) {
            final Void result = future.get();
            PromiseNotificationUtil.trySuccess(this.delegate, result, internalLogger);
        }
        else if (future.isCancelled()) {
            PromiseNotificationUtil.tryCancel(this.delegate, internalLogger);
        }
        else {
            final Throwable cause = future.cause();
            PromiseNotificationUtil.tryFailure(this.delegate, cause, internalLogger);
        }
    }
    
    @Override
    public Channel channel() {
        return this.delegate.channel();
    }
    
    @Override
    public ChannelPromise setSuccess(final Void result) {
        this.delegate.setSuccess(result);
        return this;
    }
    
    @Override
    public ChannelPromise setSuccess() {
        this.delegate.setSuccess();
        return this;
    }
    
    @Override
    public boolean trySuccess() {
        return this.delegate.trySuccess();
    }
    
    @Override
    public boolean trySuccess(final Void result) {
        return this.delegate.trySuccess(result);
    }
    
    @Override
    public ChannelPromise setFailure(final Throwable cause) {
        this.delegate.setFailure(cause);
        return this;
    }
    
    @Override
    public ChannelPromise addListener(final GenericFutureListener<? extends Future<? super Void>> listener) {
        this.delegate.addListener(listener);
        return this;
    }
    
    @Override
    public ChannelPromise addListeners(final GenericFutureListener<? extends Future<? super Void>>... listeners) {
        this.delegate.addListeners(listeners);
        return this;
    }
    
    @Override
    public ChannelPromise removeListener(final GenericFutureListener<? extends Future<? super Void>> listener) {
        this.delegate.removeListener(listener);
        return this;
    }
    
    @Override
    public ChannelPromise removeListeners(final GenericFutureListener<? extends Future<? super Void>>... listeners) {
        this.delegate.removeListeners(listeners);
        return this;
    }
    
    @Override
    public boolean tryFailure(final Throwable cause) {
        return this.delegate.tryFailure(cause);
    }
    
    @Override
    public boolean setUncancellable() {
        return this.delegate.setUncancellable();
    }
    
    @Override
    public ChannelPromise await() throws InterruptedException {
        this.delegate.await();
        return this;
    }
    
    @Override
    public ChannelPromise awaitUninterruptibly() {
        this.delegate.awaitUninterruptibly();
        return this;
    }
    
    @Override
    public boolean isVoid() {
        return this.delegate.isVoid();
    }
    
    @Override
    public ChannelPromise unvoid() {
        return this.isVoid() ? new DelegatingChannelPromiseNotifier(this.delegate.unvoid()) : this;
    }
    
    @Override
    public boolean await(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.delegate.await(timeout, unit);
    }
    
    @Override
    public boolean await(final long timeoutMillis) throws InterruptedException {
        return this.delegate.await(timeoutMillis);
    }
    
    @Override
    public boolean awaitUninterruptibly(final long timeout, final TimeUnit unit) {
        return this.delegate.awaitUninterruptibly(timeout, unit);
    }
    
    @Override
    public boolean awaitUninterruptibly(final long timeoutMillis) {
        return this.delegate.awaitUninterruptibly(timeoutMillis);
    }
    
    @Override
    public Void getNow() {
        return this.delegate.getNow();
    }
    
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return this.delegate.cancel(mayInterruptIfRunning);
    }
    
    @Override
    public boolean isCancelled() {
        return this.delegate.isCancelled();
    }
    
    @Override
    public boolean isDone() {
        return this.delegate.isDone();
    }
    
    @Override
    public Void get() throws InterruptedException, ExecutionException {
        return this.delegate.get();
    }
    
    @Override
    public Void get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.delegate.get(timeout, unit);
    }
    
    @Override
    public ChannelPromise sync() throws InterruptedException {
        this.delegate.sync();
        return this;
    }
    
    @Override
    public ChannelPromise syncUninterruptibly() {
        this.delegate.syncUninterruptibly();
        return this;
    }
    
    @Override
    public boolean isSuccess() {
        return this.delegate.isSuccess();
    }
    
    @Override
    public boolean isCancellable() {
        return this.delegate.isCancellable();
    }
    
    @Override
    public Throwable cause() {
        return this.delegate.cause();
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(DelegatingChannelPromiseNotifier.class);
    }
}
