package javax.ws.rs.container;

import java.io.IOException;

public interface ContainerResponseFilter
{
    void filter(final ContainerRequestContext p0, final ContainerResponseContext p1) throws IOException;
}
