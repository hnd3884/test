package org.apache.axiom.attachments;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.axiom.ext.activation.SizeAwareDataSource;

public class ByteArrayDataSource implements SizeAwareDataSource
{
    private byte[] data;
    private String type;
    
    public ByteArrayDataSource(final byte[] data, final String type) {
        this.data = data;
        this.type = type;
    }
    
    public ByteArrayDataSource(final byte[] data) {
        this.data = data;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getContentType() {
        if (this.type == null) {
            return "application/octet-stream";
        }
        return this.type;
    }
    
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream((this.data == null) ? new byte[0] : this.data);
    }
    
    public String getName() {
        return "ByteArrayDataSource";
    }
    
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("Not Supported");
    }
    
    public long getSize() {
        return (this.data == null) ? 0L : this.data.length;
    }
}
