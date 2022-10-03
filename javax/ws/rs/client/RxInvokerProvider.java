package javax.ws.rs.client;

import java.util.concurrent.ExecutorService;

public interface RxInvokerProvider<T extends RxInvoker>
{
    boolean isProviderFor(final Class<?> p0);
    
    T getRxInvoker(final SyncInvoker p0, final ExecutorService p1);
}
