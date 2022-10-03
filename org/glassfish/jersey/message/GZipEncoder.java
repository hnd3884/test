package org.glassfish.jersey.message;

import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import javax.annotation.Priority;
import org.glassfish.jersey.spi.ContentEncoder;

@Priority(4000)
public class GZipEncoder extends ContentEncoder
{
    public GZipEncoder() {
        super(new String[] { "gzip", "x-gzip" });
    }
    
    @Override
    public InputStream decode(final String contentEncoding, final InputStream encodedStream) throws IOException {
        return new GZIPInputStream(encodedStream);
    }
    
    @Override
    public OutputStream encode(final String contentEncoding, final OutputStream entityStream) throws IOException {
        return new GZIPOutputStream(entityStream);
    }
}
