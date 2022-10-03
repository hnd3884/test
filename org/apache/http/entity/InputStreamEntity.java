package org.apache.http.entity;

import java.io.OutputStream;
import java.io.IOException;
import org.apache.http.util.Args;
import java.io.InputStream;

public class InputStreamEntity extends AbstractHttpEntity
{
    private final InputStream content;
    private final long length;
    
    public InputStreamEntity(final InputStream inStream) {
        this(inStream, -1L);
    }
    
    public InputStreamEntity(final InputStream inStream, final long length) {
        this(inStream, length, null);
    }
    
    public InputStreamEntity(final InputStream inStream, final ContentType contentType) {
        this(inStream, -1L, contentType);
    }
    
    public InputStreamEntity(final InputStream inStream, final long length, final ContentType contentType) {
        this.content = Args.notNull(inStream, "Source input stream");
        this.length = length;
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    @Override
    public boolean isRepeatable() {
        return false;
    }
    
    @Override
    public long getContentLength() {
        return this.length;
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return this.content;
    }
    
    @Override
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull(outStream, "Output stream");
        final InputStream inStream = this.content;
        try {
            final byte[] buffer = new byte[4096];
            if (this.length < 0L) {
                int readLen;
                while ((readLen = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, readLen);
                }
            }
            else {
                int readLen;
                for (long remaining = this.length; remaining > 0L; remaining -= readLen) {
                    readLen = inStream.read(buffer, 0, (int)Math.min(4096L, remaining));
                    if (readLen == -1) {
                        break;
                    }
                    outStream.write(buffer, 0, readLen);
                }
            }
        }
        finally {
            inStream.close();
        }
    }
    
    @Override
    public boolean isStreaming() {
        return true;
    }
}
