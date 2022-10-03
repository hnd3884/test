package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public final class WebSocketServerHandshakeException extends WebSocketHandshakeException
{
    private static final long serialVersionUID = 1L;
    private final HttpRequest request;
    
    public WebSocketServerHandshakeException(final String message) {
        this(message, (HttpRequest)null);
    }
    
    public WebSocketServerHandshakeException(final String message, final HttpRequest httpRequest) {
        super(message);
        if (httpRequest != null) {
            this.request = new DefaultHttpRequest(httpRequest.protocolVersion(), httpRequest.method(), httpRequest.uri(), httpRequest.headers());
        }
        else {
            this.request = null;
        }
    }
    
    public HttpRequest request() {
        return this.request;
    }
}
