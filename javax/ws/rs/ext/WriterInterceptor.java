package javax.ws.rs.ext;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public interface WriterInterceptor
{
    void aroundWriteTo(final WriterInterceptorContext p0) throws IOException, WebApplicationException;
}
