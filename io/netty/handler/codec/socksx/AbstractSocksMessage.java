package io.netty.handler.codec.socksx;

import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.DecoderResult;

public abstract class AbstractSocksMessage implements SocksMessage
{
    private DecoderResult decoderResult;
    
    public AbstractSocksMessage() {
        this.decoderResult = DecoderResult.SUCCESS;
    }
    
    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }
    
    @Override
    public void setDecoderResult(final DecoderResult decoderResult) {
        this.decoderResult = ObjectUtil.checkNotNull(decoderResult, "decoderResult");
    }
}
