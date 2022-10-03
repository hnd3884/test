package sun.rmi.transport;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Connection
{
    InputStream getInputStream() throws IOException;
    
    void releaseInputStream() throws IOException;
    
    OutputStream getOutputStream() throws IOException;
    
    void releaseOutputStream() throws IOException;
    
    boolean isReusable();
    
    void close() throws IOException;
    
    Channel getChannel();
}
