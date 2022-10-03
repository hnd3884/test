package com.google.api.client.googleapis;

import java.io.IOException;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpExecuteInterceptor;

public final class MethodOverride implements HttpExecuteInterceptor, HttpRequestInitializer
{
    public static final String HEADER = "X-HTTP-Method-Override";
    static final int MAX_URL_LENGTH = 2048;
    private final boolean overrideAllMethods;
    
    public MethodOverride() {
        this(false);
    }
    
    MethodOverride(final boolean overrideAllMethods) {
        this.overrideAllMethods = overrideAllMethods;
    }
    
    public void initialize(final HttpRequest request) {
        request.setInterceptor((HttpExecuteInterceptor)this);
    }
    
    public void intercept(final HttpRequest request) throws IOException {
        if (this.overrideThisMethod(request)) {
            final String requestMethod = request.getRequestMethod();
            request.setRequestMethod("POST");
            request.getHeaders().set("X-HTTP-Method-Override", (Object)requestMethod);
            if (requestMethod.equals("GET")) {
                request.setContent((HttpContent)new UrlEncodedContent((Object)request.getUrl().clone()));
                request.getUrl().clear();
            }
            else if (request.getContent() == null) {
                request.setContent((HttpContent)new EmptyContent());
            }
        }
    }
    
    private boolean overrideThisMethod(final HttpRequest request) throws IOException {
        final String requestMethod = request.getRequestMethod();
        if (requestMethod.equals("POST")) {
            return false;
        }
        if (requestMethod.equals("GET")) {
            if (request.getUrl().build().length() <= 2048) {
                return !request.getTransport().supportsMethod(requestMethod);
            }
        }
        else if (!this.overrideAllMethods) {
            return !request.getTransport().supportsMethod(requestMethod);
        }
        return true;
    }
    
    public static final class Builder
    {
        private boolean overrideAllMethods;
        
        public MethodOverride build() {
            return new MethodOverride(this.overrideAllMethods);
        }
        
        public boolean getOverrideAllMethods() {
            return this.overrideAllMethods;
        }
        
        public Builder setOverrideAllMethods(final boolean overrideAllMethods) {
            this.overrideAllMethods = overrideAllMethods;
            return this;
        }
    }
}
