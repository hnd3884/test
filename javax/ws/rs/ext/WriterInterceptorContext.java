package javax.ws.rs.ext;

import javax.ws.rs.core.MultivaluedMap;
import java.io.OutputStream;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public interface WriterInterceptorContext extends InterceptorContext
{
    void proceed() throws IOException, WebApplicationException;
    
    Object getEntity();
    
    void setEntity(final Object p0);
    
    OutputStream getOutputStream();
    
    void setOutputStream(final OutputStream p0);
    
    MultivaluedMap<String, Object> getHeaders();
}
