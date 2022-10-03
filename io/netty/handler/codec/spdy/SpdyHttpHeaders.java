package io.netty.handler.codec.spdy;

import io.netty.util.AsciiString;

public final class SpdyHttpHeaders
{
    private SpdyHttpHeaders() {
    }
    
    public static final class Names
    {
        public static final AsciiString STREAM_ID;
        public static final AsciiString ASSOCIATED_TO_STREAM_ID;
        public static final AsciiString PRIORITY;
        public static final AsciiString SCHEME;
        
        private Names() {
        }
        
        static {
            STREAM_ID = AsciiString.cached("x-spdy-stream-id");
            ASSOCIATED_TO_STREAM_ID = AsciiString.cached("x-spdy-associated-to-stream-id");
            PRIORITY = AsciiString.cached("x-spdy-priority");
            SCHEME = AsciiString.cached("x-spdy-scheme");
        }
    }
}
