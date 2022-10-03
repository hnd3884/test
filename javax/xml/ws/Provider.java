package javax.xml.ws;

public interface Provider<T>
{
    T invoke(final T p0);
}
