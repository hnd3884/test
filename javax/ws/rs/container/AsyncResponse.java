package javax.ws.rs.container;

import java.util.Map;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.Date;

public interface AsyncResponse
{
    public static final long NO_TIMEOUT = 0L;
    
    boolean resume(final Object p0);
    
    boolean resume(final Throwable p0);
    
    boolean cancel();
    
    boolean cancel(final int p0);
    
    boolean cancel(final Date p0);
    
    boolean isSuspended();
    
    boolean isCancelled();
    
    boolean isDone();
    
    boolean setTimeout(final long p0, final TimeUnit p1);
    
    void setTimeoutHandler(final TimeoutHandler p0);
    
    Collection<Class<?>> register(final Class<?> p0);
    
    Map<Class<?>, Collection<Class<?>>> register(final Class<?> p0, final Class<?>... p1);
    
    Collection<Class<?>> register(final Object p0);
    
    Map<Class<?>, Collection<Class<?>>> register(final Object p0, final Object... p1);
}
