package com.google.api.client.googleapis.testing.services.json;

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Beta;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;

@Beta
public class MockGoogleJsonClient extends AbstractGoogleJsonClient
{
    protected MockGoogleJsonClient(final Builder builder) {
        super(builder);
    }
    
    public MockGoogleJsonClient(final HttpTransport transport, final JsonFactory jsonFactory, final String rootUrl, final String servicePath, final HttpRequestInitializer httpRequestInitializer, final boolean legacyDataWrapper) {
        this(new Builder(transport, jsonFactory, rootUrl, servicePath, httpRequestInitializer, legacyDataWrapper));
    }
    
    @Beta
    public static class Builder extends AbstractGoogleJsonClient.Builder
    {
        public Builder(final HttpTransport transport, final JsonFactory jsonFactory, final String rootUrl, final String servicePath, final HttpRequestInitializer httpRequestInitializer, final boolean legacyDataWrapper) {
            super(transport, jsonFactory, rootUrl, servicePath, httpRequestInitializer, legacyDataWrapper);
        }
        
        @Override
        public MockGoogleJsonClient build() {
            return new MockGoogleJsonClient(this);
        }
        
        @Override
        public Builder setRootUrl(final String rootUrl) {
            return (Builder)super.setRootUrl(rootUrl);
        }
        
        @Override
        public Builder setServicePath(final String servicePath) {
            return (Builder)super.setServicePath(servicePath);
        }
        
        @Override
        public Builder setGoogleClientRequestInitializer(final GoogleClientRequestInitializer googleClientRequestInitializer) {
            return (Builder)super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
        }
        
        @Override
        public Builder setHttpRequestInitializer(final HttpRequestInitializer httpRequestInitializer) {
            return (Builder)super.setHttpRequestInitializer(httpRequestInitializer);
        }
        
        @Override
        public Builder setApplicationName(final String applicationName) {
            return (Builder)super.setApplicationName(applicationName);
        }
        
        @Override
        public Builder setSuppressPatternChecks(final boolean suppressPatternChecks) {
            return (Builder)super.setSuppressPatternChecks(suppressPatternChecks);
        }
        
        @Override
        public Builder setSuppressRequiredParameterChecks(final boolean suppressRequiredParameterChecks) {
            return (Builder)super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
        }
        
        @Override
        public Builder setSuppressAllChecks(final boolean suppressAllChecks) {
            return (Builder)super.setSuppressAllChecks(suppressAllChecks);
        }
    }
}
