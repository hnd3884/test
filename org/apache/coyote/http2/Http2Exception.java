package org.apache.coyote.http2;

abstract class Http2Exception extends Exception
{
    private static final long serialVersionUID = 1L;
    private final Http2Error error;
    
    Http2Exception(final String msg, final Http2Error error) {
        super(msg);
        this.error = error;
    }
    
    Http2Exception(final String msg, final Http2Error error, final Throwable cause) {
        super(msg, cause);
        this.error = error;
    }
    
    Http2Error getError() {
        return this.error;
    }
}
