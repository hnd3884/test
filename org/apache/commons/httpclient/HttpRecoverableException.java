package org.apache.commons.httpclient;

public class HttpRecoverableException extends HttpException
{
    public HttpRecoverableException() {
    }
    
    public HttpRecoverableException(final String message) {
        super(message);
    }
}
