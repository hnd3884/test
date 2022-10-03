package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;

public final class HttpMessageDecoderResult extends DecoderResult
{
    private final int initialLineLength;
    private final int headerSize;
    
    HttpMessageDecoderResult(final int initialLineLength, final int headerSize) {
        super(HttpMessageDecoderResult.SIGNAL_SUCCESS);
        this.initialLineLength = initialLineLength;
        this.headerSize = headerSize;
    }
    
    public int initialLineLength() {
        return this.initialLineLength;
    }
    
    public int headerSize() {
        return this.headerSize;
    }
    
    public int totalSize() {
        return this.initialLineLength + this.headerSize;
    }
}
