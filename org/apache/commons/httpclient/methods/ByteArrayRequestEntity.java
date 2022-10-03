package org.apache.commons.httpclient.methods;

import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayRequestEntity implements RequestEntity
{
    private byte[] content;
    private String contentType;
    
    public ByteArrayRequestEntity(final byte[] content) {
        this(content, null);
    }
    
    public ByteArrayRequestEntity(final byte[] content, final String contentType) {
        if (content == null) {
            throw new IllegalArgumentException("The content cannot be null");
        }
        this.content = content;
        this.contentType = contentType;
    }
    
    public boolean isRepeatable() {
        return true;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public void writeRequest(final OutputStream out) throws IOException {
        out.write(this.content);
    }
    
    public long getContentLength() {
        return this.content.length;
    }
    
    public byte[] getContent() {
        return this.content;
    }
}
