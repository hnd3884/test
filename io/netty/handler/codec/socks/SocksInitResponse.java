package io.netty.handler.codec.socks;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class SocksInitResponse extends SocksResponse
{
    private final SocksAuthScheme authScheme;
    
    public SocksInitResponse(final SocksAuthScheme authScheme) {
        super(SocksResponseType.INIT);
        this.authScheme = ObjectUtil.checkNotNull(authScheme, "authScheme");
    }
    
    public SocksAuthScheme authScheme() {
        return this.authScheme;
    }
    
    @Override
    public void encodeAsByteBuf(final ByteBuf byteBuf) {
        byteBuf.writeByte(this.protocolVersion().byteValue());
        byteBuf.writeByte(this.authScheme.byteValue());
    }
}
