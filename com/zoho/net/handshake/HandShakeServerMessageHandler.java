package com.zoho.net.handshake;

public interface HandShakeServerMessageHandler
{
    String getResponseMessage(final HandShakePacket p0);
    
    void responsePacketSent(final HandShakePacket p0);
}
