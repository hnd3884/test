package org.glassfish.jersey.client;

import javax.inject.Inject;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.collection.Value;
import javax.inject.Named;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import java.util.logging.Logger;
import org.glassfish.jersey.spi.ThreadPoolExecutorProvider;

@ClientAsyncExecutor
class DefaultClientAsyncExecutorProvider extends ThreadPoolExecutorProvider
{
    private static final Logger LOGGER;
    private final LazyValue<Integer> asyncThreadPoolSize;
    
    @Inject
    public DefaultClientAsyncExecutorProvider(@Named("ClientAsyncThreadPoolSize") final int poolSize) {
        super("jersey-client-async-executor");
        this.asyncThreadPoolSize = (LazyValue<Integer>)Values.lazy((Value)new Value<Integer>() {
            public Integer get() {
                if (poolSize <= 0) {
                    DefaultClientAsyncExecutorProvider.LOGGER.config(LocalizationMessages.IGNORED_ASYNC_THREADPOOL_SIZE(poolSize));
                    return Integer.MAX_VALUE;
                }
                DefaultClientAsyncExecutorProvider.LOGGER.config(LocalizationMessages.USING_FIXED_ASYNC_THREADPOOL(poolSize));
                return poolSize;
            }
        });
    }
    
    protected int getMaximumPoolSize() {
        return (int)this.asyncThreadPoolSize.get();
    }
    
    protected int getCorePoolSize() {
        final Integer maximumPoolSize = this.getMaximumPoolSize();
        if (maximumPoolSize != Integer.MAX_VALUE) {
            return maximumPoolSize;
        }
        return 0;
    }
    
    static {
        LOGGER = Logger.getLogger(DefaultClientAsyncExecutorProvider.class.getName());
    }
}
