package javax.ws.rs.container;

import java.io.IOException;

public interface ContainerRequestFilter
{
    void filter(final ContainerRequestContext p0) throws IOException;
}
