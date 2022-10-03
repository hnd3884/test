package org.apache.tika.concurrent;

import java.util.concurrent.ExecutorService;

public interface ConfigurableThreadPoolExecutor extends ExecutorService
{
    void setMaximumPoolSize(final int p0);
    
    void setCorePoolSize(final int p0);
}
