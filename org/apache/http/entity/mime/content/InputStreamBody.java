package org.apache.http.entity.mime.content;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.util.Args;
import org.apache.http.entity.ContentType;
import java.io.InputStream;

public class InputStreamBody extends AbstractContentBody
{
    private final InputStream in;
    private final String filename;
    
    @Deprecated
    public InputStreamBody(final InputStream in, final String mimeType, final String filename) {
        this(in, ContentType.create(mimeType), filename);
    }
    
    public InputStreamBody(final InputStream in, final String filename) {
        this(in, ContentType.DEFAULT_BINARY, filename);
    }
    
    public InputStreamBody(final InputStream in, final ContentType contentType, final String filename) {
        super(contentType);
        Args.notNull((Object)in, "Input stream");
        this.in = in;
        this.filename = filename;
    }
    
    public InputStreamBody(final InputStream in, final ContentType contentType) {
        this(in, contentType, null);
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        Args.notNull((Object)out, "Output stream");
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = this.in.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            out.flush();
        }
        finally {
            this.in.close();
        }
    }
    
    public String getTransferEncoding() {
        return "binary";
    }
    
    public long getContentLength() {
        return -1L;
    }
    
    public String getFilename() {
        return this.filename;
    }
}
