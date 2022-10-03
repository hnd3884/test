package org.glassfish.jersey.server.spi;

import javax.ws.rs.core.Response;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ResponseErrorMapper
{
    Response toResponse(final Throwable p0);
}
