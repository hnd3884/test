package javax.xml.ws;

public interface AsyncHandler<T>
{
    void handleResponse(final Response<T> p0);
}
