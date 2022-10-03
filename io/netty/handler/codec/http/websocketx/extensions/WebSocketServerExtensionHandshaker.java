package io.netty.handler.codec.http.websocketx.extensions;

public interface WebSocketServerExtensionHandshaker
{
    WebSocketServerExtension handshakeExtension(final WebSocketExtensionData p0);
}
