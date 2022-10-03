package javax.ws.rs.client;

import java.io.IOException;

public interface ClientRequestFilter
{
    void filter(final ClientRequestContext p0) throws IOException;
}
