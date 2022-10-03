package javax.ws.rs.ext;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public interface ReaderInterceptor
{
    Object aroundReadFrom(final ReaderInterceptorContext p0) throws IOException, WebApplicationException;
}
