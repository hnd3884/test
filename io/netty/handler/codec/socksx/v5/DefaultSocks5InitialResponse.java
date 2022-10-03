package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;

public class DefaultSocks5InitialResponse extends AbstractSocks5Message implements Socks5InitialResponse
{
    private final Socks5AuthMethod authMethod;
    
    public DefaultSocks5InitialResponse(final Socks5AuthMethod authMethod) {
        this.authMethod = ObjectUtil.checkNotNull(authMethod, "authMethod");
    }
    
    @Override
    public Socks5AuthMethod authMethod() {
        return this.authMethod;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder(StringUtil.simpleClassName(this));
        final DecoderResult decoderResult = this.decoderResult();
        if (!decoderResult.isSuccess()) {
            buf.append("(decoderResult: ");
            buf.append(decoderResult);
            buf.append(", authMethod: ");
        }
        else {
            buf.append("(authMethod: ");
        }
        buf.append(this.authMethod());
        buf.append(')');
        return buf.toString();
    }
}
