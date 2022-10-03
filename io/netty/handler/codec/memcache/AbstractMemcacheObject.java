package io.netty.handler.codec.memcache;

import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.DecoderResult;
import io.netty.util.AbstractReferenceCounted;

public abstract class AbstractMemcacheObject extends AbstractReferenceCounted implements MemcacheObject
{
    private DecoderResult decoderResult;
    
    protected AbstractMemcacheObject() {
        this.decoderResult = DecoderResult.SUCCESS;
    }
    
    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }
    
    @Override
    public void setDecoderResult(final DecoderResult result) {
        this.decoderResult = ObjectUtil.checkNotNull(result, "DecoderResult should not be null.");
    }
}
