package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketExtensionFilter
{
    public static final WebSocketExtensionFilter NEVER_SKIP = new WebSocketExtensionFilter() {
        @Override
        public boolean mustSkip(final WebSocketFrame frame) {
            return false;
        }
    };
    public static final WebSocketExtensionFilter ALWAYS_SKIP = new WebSocketExtensionFilter() {
        @Override
        public boolean mustSkip(final WebSocketFrame frame) {
            return true;
        }
    };
    
    boolean mustSkip(final WebSocketFrame p0);
}
