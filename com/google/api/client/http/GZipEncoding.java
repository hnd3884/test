package com.google.api.client.http;

import java.util.zip.GZIPOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import com.google.api.client.util.StreamingContent;

public class GZipEncoding implements HttpEncoding
{
    @Override
    public String getName() {
        return "gzip";
    }
    
    @Override
    public void encode(final StreamingContent content, final OutputStream out) throws IOException {
        final OutputStream out2 = new BufferedOutputStream(out) {
            @Override
            public void close() throws IOException {
                try {
                    this.flush();
                }
                catch (final IOException ex) {}
            }
        };
        final GZIPOutputStream zipper = new GZIPOutputStream(out2);
        content.writeTo(zipper);
        zipper.close();
    }
}
