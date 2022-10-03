package javax.ws.rs.client;

public interface InvocationCallback<RESPONSE>
{
    void completed(final RESPONSE p0);
    
    void failed(final Throwable p0);
}
