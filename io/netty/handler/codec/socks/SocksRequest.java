package io.netty.handler.codec.socks;

import io.netty.util.internal.ObjectUtil;

public abstract class SocksRequest extends SocksMessage
{
    private final SocksRequestType requestType;
    
    protected SocksRequest(final SocksRequestType requestType) {
        super(SocksMessageType.REQUEST);
        this.requestType = ObjectUtil.checkNotNull(requestType, "requestType");
    }
    
    public SocksRequestType requestType() {
        return this.requestType;
    }
}
