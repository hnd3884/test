package javax.ws.rs.ext;

public interface ContextResolver<T>
{
    T getContext(final Class<?> p0);
}
