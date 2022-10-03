package com.google.api.client.googleapis.compute;

import java.util.Collection;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.util.Clock;
import com.google.api.client.util.Preconditions;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.googleapis.auth.oauth2.OAuth2Utils;
import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Beta;
import com.google.api.client.auth.oauth2.Credential;

@Beta
public class ComputeCredential extends Credential
{
    public static final String TOKEN_SERVER_ENCODED_URL;
    
    public ComputeCredential(final HttpTransport transport, final JsonFactory jsonFactory) {
        this(new Builder(transport, jsonFactory));
    }
    
    protected ComputeCredential(final Builder builder) {
        super((Credential.Builder)builder);
    }
    
    protected TokenResponse executeRefreshToken() throws IOException {
        final GenericUrl tokenUrl = new GenericUrl(this.getTokenServerEncodedUrl());
        final HttpRequest request = this.getTransport().createRequestFactory().buildGetRequest(tokenUrl);
        request.setParser((ObjectParser)new JsonObjectParser(this.getJsonFactory()));
        request.getHeaders().set("Metadata-Flavor", (Object)"Google");
        return (TokenResponse)request.execute().parseAs((Class)TokenResponse.class);
    }
    
    static {
        TOKEN_SERVER_ENCODED_URL = OAuth2Utils.getMetadataServerUrl() + "/computeMetadata/v1/instance/service-accounts/default/token";
    }
    
    @Beta
    public static class Builder extends Credential.Builder
    {
        public Builder(final HttpTransport transport, final JsonFactory jsonFactory) {
            super(BearerToken.authorizationHeaderAccessMethod());
            this.setTransport(transport);
            this.setJsonFactory(jsonFactory);
            this.setTokenServerEncodedUrl(ComputeCredential.TOKEN_SERVER_ENCODED_URL);
        }
        
        public ComputeCredential build() {
            return new ComputeCredential(this);
        }
        
        public Builder setTransport(final HttpTransport transport) {
            return (Builder)super.setTransport((HttpTransport)Preconditions.checkNotNull((Object)transport));
        }
        
        public Builder setClock(final Clock clock) {
            return (Builder)super.setClock(clock);
        }
        
        public Builder setJsonFactory(final JsonFactory jsonFactory) {
            return (Builder)super.setJsonFactory((JsonFactory)Preconditions.checkNotNull((Object)jsonFactory));
        }
        
        public Builder setTokenServerUrl(final GenericUrl tokenServerUrl) {
            return (Builder)super.setTokenServerUrl((GenericUrl)Preconditions.checkNotNull((Object)tokenServerUrl));
        }
        
        public Builder setTokenServerEncodedUrl(final String tokenServerEncodedUrl) {
            return (Builder)super.setTokenServerEncodedUrl((String)Preconditions.checkNotNull((Object)tokenServerEncodedUrl));
        }
        
        public Builder setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
            Preconditions.checkArgument(clientAuthentication == null);
            return this;
        }
        
        public Builder setRequestInitializer(final HttpRequestInitializer requestInitializer) {
            return (Builder)super.setRequestInitializer(requestInitializer);
        }
        
        public Builder addRefreshListener(final CredentialRefreshListener refreshListener) {
            return (Builder)super.addRefreshListener(refreshListener);
        }
        
        public Builder setRefreshListeners(final Collection<CredentialRefreshListener> refreshListeners) {
            return (Builder)super.setRefreshListeners((Collection)refreshListeners);
        }
    }
}
