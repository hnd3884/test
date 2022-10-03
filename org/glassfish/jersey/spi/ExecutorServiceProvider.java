package org.glassfish.jersey.spi;

import java.util.concurrent.ExecutorService;

@Contract
public interface ExecutorServiceProvider
{
    ExecutorService getExecutorService();
    
    void dispose(final ExecutorService p0);
}
