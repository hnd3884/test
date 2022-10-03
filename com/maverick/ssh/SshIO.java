package com.maverick.ssh;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface SshIO
{
    InputStream getInputStream() throws IOException;
    
    OutputStream getOutputStream() throws IOException;
    
    void close() throws IOException;
}
