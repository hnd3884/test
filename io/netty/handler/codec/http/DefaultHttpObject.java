package io.netty.handler.codec.http;

import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.DecoderResult;

public class DefaultHttpObject implements HttpObject
{
    private static final int HASH_CODE_PRIME = 31;
    private DecoderResult decoderResult;
    
    protected DefaultHttpObject() {
        this.decoderResult = DecoderResult.SUCCESS;
    }
    
    @Override
    public DecoderResult decoderResult() {
        return this.decoderResult;
    }
    
    @Deprecated
    @Override
    public DecoderResult getDecoderResult() {
        return this.decoderResult();
    }
    
    @Override
    public void setDecoderResult(final DecoderResult decoderResult) {
        this.decoderResult = ObjectUtil.checkNotNull(decoderResult, "decoderResult");
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.decoderResult.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DefaultHttpObject)) {
            return false;
        }
        final DefaultHttpObject other = (DefaultHttpObject)o;
        return this.decoderResult().equals(other.decoderResult());
    }
}
