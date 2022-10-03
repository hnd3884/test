package io.netty.handler.codec;

public interface DecoderResultProvider
{
    DecoderResult decoderResult();
    
    void setDecoderResult(final DecoderResult p0);
}
