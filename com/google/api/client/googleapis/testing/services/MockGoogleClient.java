package com.google.api.client.googleapis.testing.services;

import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Beta;
import com.google.api.client.googleapis.services.AbstractGoogleClient;

@Beta
public class MockGoogleClient extends AbstractGoogleClient
{
    public MockGoogleClient(final HttpTransport transport, final String rootUrl, final String servicePath, final ObjectParser objectParser, final HttpRequestInitializer httpRequestInitializer) {
        this(new Builder(transport, rootUrl, servicePath, objectParser, httpRequestInitializer));
    }
    
    protected MockGoogleClient(final Builder builder) {
        super(builder);
    }
    
    @Beta
    public static class Builder extends AbstractGoogleClient.Builder
    {
        public Builder(final HttpTransport transport, final String rootUrl, final String servicePath, final ObjectParser objectParser, final HttpRequestInitializer httpRequestInitializer) {
            super(transport, rootUrl, servicePath, objectParser, httpRequestInitializer);
        }
        
        @Override
        public MockGoogleClient build() {
            return new MockGoogleClient(this);
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
