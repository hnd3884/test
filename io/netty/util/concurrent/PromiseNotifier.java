package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;

public class PromiseNotifier<V, F extends Future<V>> implements GenericFutureListener<F>
{
    private static final InternalLogger logger;
    private final Promise<? super V>[] promises;
    private final boolean logNotifyFailure;
    
    @SafeVarargs
    public PromiseNotifier(final Promise<? super V>... promises) {
        this(true, (Promise[])promises);
    }
    
    @SafeVarargs
    public PromiseNotifier(final boolean logNotifyFailure, final Promise<? super V>... promises) {
        ObjectUtil.checkNotNull(promises, "promises");
        for (final Promise<? super V> promise : promises) {
            ObjectUtil.checkNotNullWithIAE(promise, "promise");
        }
        this.promises = promises.clone();
        this.logNotifyFailure = logNotifyFailure;
    }
    
    public static <V, F extends Future<V>> F cascade(final F future, final Promise<? super V> promise) {
        return cascade(true, future, (Promise<? super Object>)promise);
    }
    
    public static <V, F extends Future<V>> F cascade(final boolean logNotifyFailure, final F future, final Promise<? super V> promise) {
        promise.addListener((GenericFutureListener<? extends Future<? super V>>)new FutureListener() {
            @Override
            public void operationComplete(final Future f) {
                if (f.isCancelled()) {
                    future.cancel(false);
                }
            }
        });
        future.addListener(new PromiseNotifier(logNotifyFailure, new Promise[] { promise }) {
            @Override
            public void operationComplete(final Future f) throws Exception {
                if (promise.isCancelled() && f.isCancelled()) {
                    return;
                }
                super.operationComplete(future);
            }
        });
        return future;
    }
    
    @Override
    public void operationComplete(final F future) throws Exception {
        final InternalLogger internalLogger = this.logNotifyFailure ? PromiseNotifier.logger : null;
        if (future.isSuccess()) {
            final V result = future.get();
            for (final Promise<? super V> p : this.promises) {
                PromiseNotificationUtil.trySuccess(p, result, internalLogger);
            }
        }
        else if (future.isCancelled()) {
            for (final Promise<? super V> p2 : this.promises) {
                PromiseNotificationUtil.tryCancel(p2, internalLogger);
            }
        }
        else {
            final Throwable cause = future.cause();
            for (final Promise<? super V> p : this.promises) {
                PromiseNotificationUtil.tryFailure(p, cause, internalLogger);
            }
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(PromiseNotifier.class);
    }
}
