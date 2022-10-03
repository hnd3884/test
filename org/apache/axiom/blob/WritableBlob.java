package org.apache.axiom.blob;

import org.apache.axiom.ext.io.StreamCopyException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface WritableBlob extends Blob
{
    OutputStream getOutputStream() throws IOException;
    
    long readFrom(final InputStream p0) throws StreamCopyException;
    
    void release() throws IOException;
}
