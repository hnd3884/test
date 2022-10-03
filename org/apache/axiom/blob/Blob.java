package org.apache.axiom.blob;

import org.apache.axiom.ext.io.StreamCopyException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Blob
{
    InputStream getInputStream() throws IOException;
    
    void writeTo(final OutputStream p0) throws StreamCopyException;
    
    long getSize();
}
