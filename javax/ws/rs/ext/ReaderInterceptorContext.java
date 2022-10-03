package javax.ws.rs.ext;

import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;

public interface ReaderInterceptorContext extends InterceptorContext
{
    Object proceed() throws IOException, WebApplicationException;
    
    InputStream getInputStream();
    
    void setInputStream(final InputStream p0);
    
    MultivaluedMap<String, String> getHeaders();
}
