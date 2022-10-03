package com.google.api.client.http.apache.v2;

import java.net.URI;
import com.google.api.client.util.Preconditions;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

final class HttpExtensionMethod extends HttpEntityEnclosingRequestBase
{
    private final String methodName;
    
    public HttpExtensionMethod(final String methodName, final String uri) {
        this.methodName = (String)Preconditions.checkNotNull((Object)methodName);
        this.setURI(URI.create(uri));
    }
    
    public String getMethod() {
        return this.methodName;
    }
}
