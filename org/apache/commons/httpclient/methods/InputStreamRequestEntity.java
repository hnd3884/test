package org.apache.commons.httpclient.methods;

import org.apache.commons.logging.LogFactory;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.commons.logging.Log;

public class InputStreamRequestEntity implements RequestEntity
{
    public static final int CONTENT_LENGTH_AUTO = -2;
    private static final Log LOG;
    private long contentLength;
    private InputStream content;
    private byte[] buffer;
    private String contentType;
    
    public InputStreamRequestEntity(final InputStream content) {
        this(content, null);
    }
    
    public InputStreamRequestEntity(final InputStream content, final String contentType) {
        this(content, -2L, contentType);
    }
    
    public InputStreamRequestEntity(final InputStream content, final long contentLength) {
        this(content, contentLength, null);
    }
    
    public InputStreamRequestEntity(final InputStream content, final long contentLength, final String contentType) {
        this.buffer = null;
        if (content == null) {
            throw new IllegalArgumentException("The content cannot be null");
        }
        this.content = content;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    private void bufferContent() {
        if (this.buffer != null) {
            return;
        }
        if (this.content != null) {
            try {
                final ByteArrayOutputStream tmp = new ByteArrayOutputStream();
                final byte[] data = new byte[4096];
                int l = 0;
                while ((l = this.content.read(data)) >= 0) {
                    tmp.write(data, 0, l);
                }
                this.buffer = tmp.toByteArray();
                this.content = null;
                this.contentLength = this.buffer.length;
            }
            catch (final IOException e) {
                InputStreamRequestEntity.LOG.error((Object)e.getMessage(), (Throwable)e);
                this.buffer = null;
                this.content = null;
                this.contentLength = 0L;
            }
        }
    }
    
    public boolean isRepeatable() {
        return this.buffer != null;
    }
    
    public void writeRequest(final OutputStream out) throws IOException {
        if (this.content != null) {
            final byte[] tmp = new byte[4096];
            int total = 0;
            int i = 0;
            while ((i = this.content.read(tmp)) >= 0) {
                out.write(tmp, 0, i);
                total += i;
            }
        }
        else {
            if (this.buffer == null) {
                throw new IllegalStateException("Content must be set before entity is written");
            }
            out.write(this.buffer);
        }
    }
    
    public long getContentLength() {
        if (this.contentLength == -2L && this.buffer == null) {
            this.bufferContent();
        }
        return this.contentLength;
    }
    
    public InputStream getContent() {
        return this.content;
    }
    
    static {
        LOG = LogFactory.getLog(InputStreamRequestEntity.class);
    }
}
