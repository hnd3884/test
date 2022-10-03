package org.apache.http.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.util.Args;
import org.apache.http.entity.ContentType;

public class ByteArrayBody extends AbstractContentBody
{
    private final byte[] data;
    private final String filename;
    
    @Deprecated
    public ByteArrayBody(final byte[] data, final String mimeType, final String filename) {
        this(data, ContentType.create(mimeType), filename);
    }
    
    public ByteArrayBody(final byte[] data, final ContentType contentType, final String filename) {
        super(contentType);
        Args.notNull((Object)data, "byte[]");
        this.data = data;
        this.filename = filename;
    }
    
    public ByteArrayBody(final byte[] data, final String filename) {
        this(data, "application/octet-stream", filename);
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        out.write(this.data);
    }
    
    @Override
    public String getCharset() {
        return null;
    }
    
    public String getTransferEncoding() {
        return "binary";
    }
    
    public long getContentLength() {
        return this.data.length;
    }
}
