package org.bouncycastle.est;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Source<T>
{
    InputStream getInputStream() throws IOException;
    
    OutputStream getOutputStream() throws IOException;
    
    T getSession();
    
    void close() throws IOException;
}
