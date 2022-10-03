package org.glassfish.jersey.server.spi;

import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.ContainerException;
import java.io.OutputStream;
import org.glassfish.jersey.server.ContainerResponse;

public interface ContainerResponseWriter
{
    OutputStream writeResponseStatusAndHeaders(final long p0, final ContainerResponse p1) throws ContainerException;
    
    boolean suspend(final long p0, final TimeUnit p1, final TimeoutHandler p2);
    
    void setSuspendTimeout(final long p0, final TimeUnit p1) throws IllegalStateException;
    
    void commit();
    
    void failure(final Throwable p0);
    
    boolean enableResponseBuffering();
    
    public interface TimeoutHandler
    {
        void onTimeout(final ContainerResponseWriter p0);
    }
}
