package com.google.api.client.googleapis.services.json;

import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.googleapis.services.AbstractGoogleClient;

public abstract class AbstractGoogleJsonClient extends AbstractGoogleClient
{
    protected AbstractGoogleJsonClient(final Builder builder) {
        super(builder);
    }
    
    public JsonObjectParser getObjectParser() {
        return (JsonObjectParser)super.getObjectParser();
    }
    
    public final JsonFactory getJsonFactory() {
        return this.getObjectParser().getJsonFactory();
    }
    
    public abstract static class Builder extends AbstractGoogleClient.Builder
    {
        protected Builder(final HttpTransport transport, final JsonFactory jsonFactory, final String rootUrl, final String servicePath, final HttpRequestInitializer httpRequestInitializer, final boolean legacyDataWrapper) {
            super(transport, rootUrl, servicePath, (ObjectParser)new JsonObjectParser.Builder(jsonFactory).setWrapperKeys((Collection)(legacyDataWrapper ? Arrays.asList("data", "error") : Collections.emptySet())).build(), httpRequestInitializer);
        }
        
        public final JsonObjectParser getObjectParser() {
            return (JsonObjectParser)super.getObjectParser();
        }
        
        public final JsonFactory getJsonFactory() {
            return this.getObjectParser().getJsonFactory();
        }
        
        @Override
        public abstract AbstractGoogleJsonClient build();
        
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
