package org.apache.commons.httpclient;

import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.io.InterruptedIOException;
import java.io.IOException;

public class DefaultHttpMethodRetryHandler implements HttpMethodRetryHandler
{
    private static Class SSL_HANDSHAKE_EXCEPTION;
    private int retryCount;
    private boolean requestSentRetryEnabled;
    
    public DefaultHttpMethodRetryHandler(final int retryCount, final boolean requestSentRetryEnabled) {
        this.retryCount = retryCount;
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }
    
    public DefaultHttpMethodRetryHandler() {
        this(3, false);
    }
    
    public boolean retryMethod(final HttpMethod method, final IOException exception, final int executionCount) {
        if (method == null) {
            throw new IllegalArgumentException("HTTP method may not be null");
        }
        if (exception == null) {
            throw new IllegalArgumentException("Exception parameter may not be null");
        }
        return (!(method instanceof HttpMethodBase) || !((HttpMethodBase)method).isAborted()) && executionCount <= this.retryCount && (exception instanceof NoHttpResponseException || (!(exception instanceof InterruptedIOException) && !(exception instanceof UnknownHostException) && !(exception instanceof NoRouteToHostException) && (DefaultHttpMethodRetryHandler.SSL_HANDSHAKE_EXCEPTION == null || !DefaultHttpMethodRetryHandler.SSL_HANDSHAKE_EXCEPTION.isInstance(exception)) && (!method.isRequestSent() || this.requestSentRetryEnabled)));
    }
    
    public boolean isRequestSentRetryEnabled() {
        return this.requestSentRetryEnabled;
    }
    
    public int getRetryCount() {
        return this.retryCount;
    }
    
    static {
        DefaultHttpMethodRetryHandler.SSL_HANDSHAKE_EXCEPTION = null;
        try {
            DefaultHttpMethodRetryHandler.SSL_HANDSHAKE_EXCEPTION = Class.forName("javax.net.ssl.SSLHandshakeException");
        }
        catch (final ClassNotFoundException ex) {}
    }
}
