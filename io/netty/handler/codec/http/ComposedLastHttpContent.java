package io.netty.handler.codec.http;

import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

final class ComposedLastHttpContent implements LastHttpContent
{
    private final HttpHeaders trailingHeaders;
    private DecoderResult result;
    
    ComposedLastHttpContent(final HttpHeaders trailingHeaders) {
        this.trailingHeaders = trailingHeaders;
    }
    
    ComposedLastHttpContent(final HttpHeaders trailingHeaders, final DecoderResult result) {
        this(trailingHeaders);
        this.result = result;
    }
    
    @Override
    public HttpHeaders trailingHeaders() {
        return this.trailingHeaders;
    }
    
    @Override
    public LastHttpContent copy() {
        final LastHttpContent content = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
        content.trailingHeaders().set(this.trailingHeaders());
        return content;
    }
    
    @Override
    public LastHttpContent duplicate() {
        return this.copy();
    }
    
    @Override
    public LastHttpContent retainedDuplicate() {
        return this.copy();
    }
    
    @Override
    public LastHttpContent replace(final ByteBuf content) {
        final LastHttpContent dup = new DefaultLastHttpContent(content);
        dup.trailingHeaders().setAll(this.trailingHeaders());
        return dup;
    }
    
    @Override
    public LastHttpContent retain(final int increment) {
        return this;
    }
    
    @Override
    public LastHttpContent retain() {
        return this;
    }
    
    @Override
    public LastHttpContent touch() {
        return this;
    }
    
    @Override
    public LastHttpContent touch(final Object hint) {
        return this;
    }
    
    @Override
    public ByteBuf content() {
        return Unpooled.EMPTY_BUFFER;
    }
    
    @Override
    public DecoderResult decoderResult() {
        return this.result;
    }
    
    @Override
    public DecoderResult getDecoderResult() {
        return this.decoderResult();
    }
    
    @Override
    public void setDecoderResult(final DecoderResult result) {
        this.result = result;
    }
    
    @Override
    public int refCnt() {
        return 1;
    }
    
    @Override
    public boolean release() {
        return false;
    }
    
    @Override
    public boolean release(final int decrement) {
        return false;
    }
}
