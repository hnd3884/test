package com.google.api.client.http;

import java.io.IOException;
import java.io.OutputStream;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.StreamingContent;

public final class HttpEncodingStreamingContent implements StreamingContent
{
    private final StreamingContent content;
    private final HttpEncoding encoding;
    
    public HttpEncodingStreamingContent(final StreamingContent content, final HttpEncoding encoding) {
        this.content = Preconditions.checkNotNull(content);
        this.encoding = Preconditions.checkNotNull(encoding);
    }
    
    @Override
    public void writeTo(final OutputStream out) throws IOException {
        this.encoding.encode(this.content, out);
    }
    
    public StreamingContent getContent() {
        return this.content;
    }
    
    public HttpEncoding getEncoding() {
        return this.encoding;
    }
}
