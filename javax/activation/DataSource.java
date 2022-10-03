package javax.activation;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface DataSource
{
    String getContentType();
    
    InputStream getInputStream() throws IOException;
    
    String getName();
    
    OutputStream getOutputStream() throws IOException;
}
