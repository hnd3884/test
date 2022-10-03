package com.google.api.client.googleapis.testing.auth.oauth2;

import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Clock;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.testing.http.MockHttpTransport;
import com.google.api.client.util.Beta;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

@Beta
public class MockGoogleCredential extends GoogleCredential
{
    public static final String ACCESS_TOKEN = "access_xyz";
    public static final String REFRESH_TOKEN = "refresh123";
    private static final String EXPIRES_IN_SECONDS = "3600";
    private static final String TOKEN_TYPE = "Bearer";
    private static final String TOKEN_RESPONSE = "{\"access_token\": \"%s\", \"expires_in\":  %s, \"refresh_token\": \"%s\", \"token_type\": \"%s\"}";
    private static final String DEFAULT_TOKEN_RESPONSE_JSON;
    
    public MockGoogleCredential(final Builder builder) {
        super(builder);
    }
    
    public static MockHttpTransport newMockHttpTransportWithSampleTokenResponse() {
        final MockLowLevelHttpResponse mockLowLevelHttpResponse = new MockLowLevelHttpResponse().setContentType("application/json; charset=UTF-8").setContent(MockGoogleCredential.DEFAULT_TOKEN_RESPONSE_JSON);
        final MockLowLevelHttpRequest request = new MockLowLevelHttpRequest().setResponse(mockLowLevelHttpResponse);
        return new MockHttpTransport.Builder().setLowLevelHttpRequest(request).build();
    }
    
    static {
        DEFAULT_TOKEN_RESPONSE_JSON = String.format("{\"access_token\": \"%s\", \"expires_in\":  %s, \"refresh_token\": \"%s\", \"token_type\": \"%s\"}", "access_xyz", "3600", "refresh123", "Bearer");
    }
    
    @Beta
    public static class Builder extends GoogleCredential.Builder
    {
        @Override
        public Builder setTransport(final HttpTransport transport) {
            return (Builder)super.setTransport(transport);
        }
        
        @Override
        public Builder setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
            return (Builder)super.setClientAuthentication(clientAuthentication);
        }
        
        @Override
        public Builder setJsonFactory(final JsonFactory jsonFactory) {
            return (Builder)super.setJsonFactory(jsonFactory);
        }
        
        @Override
        public Builder setClock(final Clock clock) {
            return (Builder)super.setClock(clock);
        }
        
        @Override
        public MockGoogleCredential build() {
            if (this.getTransport() == null) {
                this.setTransport((HttpTransport)new MockHttpTransport.Builder().build());
            }
            if (this.getClientAuthentication() == null) {
                this.setClientAuthentication((HttpExecuteInterceptor)new MockClientAuthentication());
            }
            if (this.getJsonFactory() == null) {
                this.setJsonFactory((JsonFactory)new GsonFactory());
            }
            return new MockGoogleCredential(this);
        }
    }
    
    @Beta
    private static class MockClientAuthentication implements HttpExecuteInterceptor
    {
        public void intercept(final HttpRequest request) throws IOException {
        }
    }
}
