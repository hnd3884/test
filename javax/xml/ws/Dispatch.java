package javax.xml.ws;

import java.util.concurrent.Future;

public interface Dispatch<T> extends BindingProvider
{
    T invoke(final T p0);
    
    Response<T> invokeAsync(final T p0);
    
    Future<?> invokeAsync(final T p0, final AsyncHandler<T> p1);
    
    void invokeOneWay(final T p0);
}
