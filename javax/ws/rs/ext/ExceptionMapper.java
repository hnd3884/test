package javax.ws.rs.ext;

import javax.ws.rs.core.Response;

public interface ExceptionMapper<E extends Throwable>
{
    Response toResponse(final E p0);
}
