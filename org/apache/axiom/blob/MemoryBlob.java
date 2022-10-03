package org.apache.axiom.blob;

import java.io.OutputStream;
import java.io.InputStream;

public interface MemoryBlob extends WritableBlob
{
    InputStream getInputStream();
    
    OutputStream getOutputStream();
    
    long getSize();
    
    void release();
    
    InputStream readOnce();
}
