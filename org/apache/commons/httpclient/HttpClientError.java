package org.apache.commons.httpclient;

public class HttpClientError extends Error
{
    public HttpClientError() {
    }
    
    public HttpClientError(final String message) {
        super(message);
    }
}
