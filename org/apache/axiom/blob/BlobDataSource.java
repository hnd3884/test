package org.apache.axiom.blob;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.axiom.ext.activation.SizeAwareDataSource;

public class BlobDataSource implements SizeAwareDataSource
{
    private final Blob blob;
    private final String contentType;
    
    public BlobDataSource(final Blob blob, final String contentType) {
        this.blob = blob;
        this.contentType = contentType;
    }
    
    public InputStream getInputStream() throws IOException {
        return this.blob.getInputStream();
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public String getName() {
        return null;
    }
    
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public long getSize() {
        return this.blob.getSize();
    }
}
