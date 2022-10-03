package com.me.devicemanagement.framework.server.websockets;

public class WSAsyncSocket extends WSSocketImpl implements AsyncSocket
{
    public WSAsyncSocket(final Object wsSocketSession, final Long clientId) {
        super(wsSocketSession, clientId);
    }
}
