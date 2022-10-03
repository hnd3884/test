package io.netty.resolver;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.IdentityHashMap;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.EventExecutor;
import java.util.Map;
import io.netty.util.internal.logging.InternalLogger;
import java.io.Closeable;
import java.net.SocketAddress;

public abstract class AddressResolverGroup<T extends SocketAddress> implements Closeable
{
    private static final InternalLogger logger;
    private final Map<EventExecutor, AddressResolver<T>> resolvers;
    private final Map<EventExecutor, GenericFutureListener<Future<Object>>> executorTerminationListeners;
    
    protected AddressResolverGroup() {
        this.resolvers = new IdentityHashMap<EventExecutor, AddressResolver<T>>();
        this.executorTerminationListeners = new IdentityHashMap<EventExecutor, GenericFutureListener<Future<Object>>>();
    }
    
    public AddressResolver<T> getResolver(final EventExecutor executor) {
        ObjectUtil.checkNotNull(executor, "executor");
        if (executor.isShuttingDown()) {
            throw new IllegalStateException("executor not accepting a task");
        }
        AddressResolver<T> r;
        synchronized (this.resolvers) {
            r = this.resolvers.get(executor);
            if (r == null) {
                AddressResolver<T> newResolver;
                try {
                    newResolver = this.newResolver(executor);
                }
                catch (final Exception e) {
                    throw new IllegalStateException("failed to create a new resolver", e);
                }
                this.resolvers.put(executor, newResolver);
                final FutureListener<Object> terminationListener = new FutureListener<Object>() {
                    @Override
                    public void operationComplete(final Future<Object> future) {
                        synchronized (AddressResolverGroup.this.resolvers) {
                            AddressResolverGroup.this.resolvers.remove(executor);
                            AddressResolverGroup.this.executorTerminationListeners.remove(executor);
                        }
                        newResolver.close();
                    }
                };
                this.executorTerminationListeners.put(executor, terminationListener);
                executor.terminationFuture().addListener(terminationListener);
                r = newResolver;
            }
        }
        return r;
    }
    
    protected abstract AddressResolver<T> newResolver(final EventExecutor p0) throws Exception;
    
    @Override
    public void close() {
        final AddressResolver<T>[] rArray;
        final Map.Entry<EventExecutor, GenericFutureListener<Future<Object>>>[] listeners;
        synchronized (this.resolvers) {
            rArray = this.resolvers.values().toArray(new AddressResolver[0]);
            this.resolvers.clear();
            listeners = this.executorTerminationListeners.entrySet().toArray(new Map.Entry[0]);
            this.executorTerminationListeners.clear();
        }
        for (final Map.Entry<EventExecutor, GenericFutureListener<Future<Object>>> entry : listeners) {
            entry.getKey().terminationFuture().removeListener(entry.getValue());
        }
        for (final AddressResolver<T> r : rArray) {
            try {
                r.close();
            }
            catch (final Throwable t) {
                AddressResolverGroup.logger.warn("Failed to close a resolver:", t);
            }
        }
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(AddressResolverGroup.class);
    }
}
