package com.google.api.client.http;

import com.google.api.client.util.StreamingContent;
import com.google.api.client.util.IOUtils;
import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.io.IOException;

public abstract class AbstractHttpContent implements HttpContent
{
    private HttpMediaType mediaType;
    private long computedLength;
    
    protected AbstractHttpContent(final String mediaType) {
        this((mediaType == null) ? null : new HttpMediaType(mediaType));
    }
    
    protected AbstractHttpContent(final HttpMediaType mediaType) {
        this.computedLength = -1L;
        this.mediaType = mediaType;
    }
    
    @Override
    public long getLength() throws IOException {
        if (this.computedLength == -1L) {
            this.computedLength = this.computeLength();
        }
        return this.computedLength;
    }
    
    public final HttpMediaType getMediaType() {
        return this.mediaType;
    }
    
    public AbstractHttpContent setMediaType(final HttpMediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }
    
    protected final Charset getCharset() {
        return (this.mediaType == null || this.mediaType.getCharsetParameter() == null) ? StandardCharsets.ISO_8859_1 : this.mediaType.getCharsetParameter();
    }
    
    @Override
    public String getType() {
        return (this.mediaType == null) ? null : this.mediaType.build();
    }
    
    protected long computeLength() throws IOException {
        return computeLength(this);
    }
    
    @Override
    public boolean retrySupported() {
        return true;
    }
    
    public static long computeLength(final HttpContent content) throws IOException {
        if (!content.retrySupported()) {
            return -1L;
        }
        return IOUtils.computeLength(content);
    }
}
