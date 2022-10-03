package org.apache.commons.httpclient;

public class DefaultMethodRetryHandler implements MethodRetryHandler
{
    private int retryCount;
    private boolean requestSentRetryEnabled;
    
    public DefaultMethodRetryHandler() {
        this.retryCount = 3;
        this.requestSentRetryEnabled = false;
    }
    
    public boolean retryMethod(final HttpMethod method, final HttpConnection connection, final HttpRecoverableException recoverableException, final int executionCount, final boolean requestSent) {
        return (!requestSent || this.requestSentRetryEnabled) && executionCount <= this.retryCount;
    }
    
    public boolean isRequestSentRetryEnabled() {
        return this.requestSentRetryEnabled;
    }
    
    public int getRetryCount() {
        return this.retryCount;
    }
    
    public void setRequestSentRetryEnabled(final boolean requestSentRetryEnabled) {
        this.requestSentRetryEnabled = requestSentRetryEnabled;
    }
    
    public void setRetryCount(final int retryCount) {
        this.retryCount = retryCount;
    }
}
