package javax.servlet.http;

import java.util.Collection;
import java.io.IOException;
import java.io.InputStream;

public interface Part
{
    InputStream getInputStream() throws IOException;
    
    String getContentType();
    
    String getName();
    
    String getSubmittedFileName();
    
    long getSize();
    
    void write(final String p0) throws IOException;
    
    void delete() throws IOException;
    
    String getHeader(final String p0);
    
    Collection<String> getHeaders(final String p0);
    
    Collection<String> getHeaderNames();
}
