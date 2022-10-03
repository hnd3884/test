package javax.ws.rs.client;

import java.io.IOException;

public interface ClientResponseFilter
{
    void filter(final ClientRequestContext p0, final ClientResponseContext p1) throws IOException;
}
