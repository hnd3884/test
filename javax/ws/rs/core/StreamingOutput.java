package javax.ws.rs.core;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.OutputStream;

public interface StreamingOutput
{
    void write(final OutputStream p0) throws IOException, WebApplicationException;
}
