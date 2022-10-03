package com.google.api.client.googleapis.services.json;

import java.io.IOException;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;

public class CommonGoogleJsonClientRequestInitializer extends CommonGoogleClientRequestInitializer
{
    @Deprecated
    public CommonGoogleJsonClientRequestInitializer() {
    }
    
    @Deprecated
    public CommonGoogleJsonClientRequestInitializer(final String key) {
        super(key);
    }
    
    @Deprecated
    public CommonGoogleJsonClientRequestInitializer(final String key, final String userIp) {
        super(key, userIp);
    }
    
    @Override
    public final void initialize(final AbstractGoogleClientRequest<?> request) throws IOException {
        super.initialize(request);
        this.initializeJsonRequest((AbstractGoogleJsonClientRequest)request);
    }
    
    protected void initializeJsonRequest(final AbstractGoogleJsonClientRequest<?> request) throws IOException {
    }
    
    public static class Builder extends CommonGoogleClientRequestInitializer.Builder
    {
        @Override
        protected Builder self() {
            return this;
        }
    }
}
