package com.google.api.client.http;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public abstract class HttpTransport
{
    static final Logger LOGGER;
    private static final String[] SUPPORTED_METHODS;
    
    public final HttpRequestFactory createRequestFactory() {
        return this.createRequestFactory(null);
    }
    
    public final HttpRequestFactory createRequestFactory(final HttpRequestInitializer initializer) {
        return new HttpRequestFactory(this, initializer);
    }
    
    HttpRequest buildRequest() {
        return new HttpRequest(this, null);
    }
    
    public boolean supportsMethod(final String method) throws IOException {
        return Arrays.binarySearch(HttpTransport.SUPPORTED_METHODS, method) >= 0;
    }
    
    public boolean isMtls() {
        return false;
    }
    
    protected abstract LowLevelHttpRequest buildRequest(final String p0, final String p1) throws IOException;
    
    public void shutdown() throws IOException {
    }
    
    static {
        LOGGER = Logger.getLogger(HttpTransport.class.getName());
        Arrays.sort(SUPPORTED_METHODS = new String[] { "DELETE", "GET", "POST", "PUT" });
    }
}
