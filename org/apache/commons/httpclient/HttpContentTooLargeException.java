package org.apache.commons.httpclient;

public class HttpContentTooLargeException extends HttpException
{
    private int maxlen;
    
    public HttpContentTooLargeException(final String message, final int maxlen) {
        super(message);
        this.maxlen = maxlen;
    }
    
    public int getMaxLength() {
        return this.maxlen;
    }
}
