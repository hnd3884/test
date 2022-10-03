package javax.ws.rs.container;

public interface ResourceContext
{
     <T> T getResource(final Class<T> p0);
    
     <T> T initResource(final T p0);
}
