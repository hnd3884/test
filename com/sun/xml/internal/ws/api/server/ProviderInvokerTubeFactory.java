package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.server.provider.SyncProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.AsyncProviderInvokerTube;
import java.util.Iterator;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.provider.ProviderArgumentsBuilder;
import com.sun.istack.internal.NotNull;
import java.util.logging.Logger;

public abstract class ProviderInvokerTubeFactory<T>
{
    private static final ProviderInvokerTubeFactory DEFAULT;
    private static final Logger logger;
    
    protected abstract ProviderInvokerTube<T> doCreate(@NotNull final Class<T> p0, @NotNull final Invoker p1, @NotNull final ProviderArgumentsBuilder<?> p2, final boolean p3);
    
    public static <T> ProviderInvokerTube<T> create(@Nullable final ClassLoader classLoader, @NotNull final Container container, @NotNull final Class<T> implType, @NotNull final Invoker invoker, @NotNull final ProviderArgumentsBuilder<?> argsBuilder, final boolean isAsync) {
        for (final ProviderInvokerTubeFactory factory : ServiceFinder.find(ProviderInvokerTubeFactory.class, classLoader, container)) {
            final ProviderInvokerTube<T> tube = factory.doCreate(implType, invoker, argsBuilder, isAsync);
            if (tube != null) {
                if (ProviderInvokerTubeFactory.logger.isLoggable(Level.FINE)) {
                    ProviderInvokerTubeFactory.logger.log(Level.FINE, "{0} successfully created {1}", new Object[] { factory.getClass(), tube });
                }
                return tube;
            }
        }
        return ProviderInvokerTubeFactory.DEFAULT.createDefault(implType, invoker, argsBuilder, isAsync);
    }
    
    protected ProviderInvokerTube<T> createDefault(@NotNull final Class<T> implType, @NotNull final Invoker invoker, @NotNull final ProviderArgumentsBuilder<?> argsBuilder, final boolean isAsync) {
        return (ProviderInvokerTube<T>)(isAsync ? new AsyncProviderInvokerTube<Object>(invoker, argsBuilder) : new SyncProviderInvokerTube<Object>(invoker, argsBuilder));
    }
    
    static {
        DEFAULT = new DefaultProviderInvokerTubeFactory();
        logger = Logger.getLogger(ProviderInvokerTubeFactory.class.getName());
    }
    
    private static class DefaultProviderInvokerTubeFactory<T> extends ProviderInvokerTubeFactory<T>
    {
        public ProviderInvokerTube<T> doCreate(@NotNull final Class<T> implType, @NotNull final Invoker invoker, @NotNull final ProviderArgumentsBuilder<?> argsBuilder, final boolean isAsync) {
            return this.createDefault(implType, invoker, argsBuilder, isAsync);
        }
    }
}
