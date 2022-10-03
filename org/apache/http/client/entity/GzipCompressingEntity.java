package org.apache.http.client.entity;

import java.util.zip.GZIPOutputStream;
import org.apache.http.util.Args;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.message.BasicHeader;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

public class GzipCompressingEntity extends HttpEntityWrapper
{
    private static final String GZIP_CODEC = "gzip";
    
    public GzipCompressingEntity(final HttpEntity entity) {
        super(entity);
    }
    
    public Header getContentEncoding() {
        return (Header)new BasicHeader("Content-Encoding", "gzip");
    }
    
    public long getContentLength() {
        return -1L;
    }
    
    public boolean isChunked() {
        return true;
    }
    
    public InputStream getContent() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    public void writeTo(final OutputStream outStream) throws IOException {
        Args.notNull((Object)outStream, "Output stream");
        final GZIPOutputStream gzip = new GZIPOutputStream(outStream);
        this.wrappedEntity.writeTo((OutputStream)gzip);
        gzip.close();
    }
}
