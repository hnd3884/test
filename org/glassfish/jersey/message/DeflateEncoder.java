package org.glassfish.jersey.message;

import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.OutputStream;
import java.io.IOException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.inject.Inject;
import javax.ws.rs.core.Configuration;
import javax.annotation.Priority;
import org.glassfish.jersey.spi.ContentEncoder;

@Priority(4000)
public class DeflateEncoder extends ContentEncoder
{
    private final Configuration config;
    
    @Inject
    public DeflateEncoder(final Configuration config) {
        super(new String[] { "deflate" });
        this.config = config;
    }
    
    @Override
    public InputStream decode(final String contentEncoding, final InputStream encodedStream) throws IOException {
        final InputStream markSupportingStream = encodedStream.markSupported() ? encodedStream : new BufferedInputStream(encodedStream);
        markSupportingStream.mark(1);
        final int firstByte = markSupportingStream.read();
        markSupportingStream.reset();
        if ((firstByte & 0xF) == 0x8) {
            return new InflaterInputStream(markSupportingStream);
        }
        return new InflaterInputStream(markSupportingStream, new Inflater(true));
    }
    
    @Override
    public OutputStream encode(final String contentEncoding, final OutputStream entityStream) throws IOException {
        final Object value = this.config.getProperty("jersey.config.deflate.nozlib");
        boolean deflateWithoutZLib;
        if (value instanceof String) {
            deflateWithoutZLib = Boolean.valueOf((String)value);
        }
        else {
            deflateWithoutZLib = (value instanceof Boolean && (boolean)value);
        }
        return deflateWithoutZLib ? new DeflaterOutputStream(entityStream, new Deflater(-1, true)) : new DeflaterOutputStream(entityStream);
    }
}
