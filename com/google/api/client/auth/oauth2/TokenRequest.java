package com.google.api.client.auth.oauth2;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.UrlEncodedContent;
import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Joiner;
import java.util.Collection;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Key;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.util.GenericData;

public class TokenRequest extends GenericData
{
    HttpRequestInitializer requestInitializer;
    HttpExecuteInterceptor clientAuthentication;
    private final HttpTransport transport;
    private final JsonFactory jsonFactory;
    private GenericUrl tokenServerUrl;
    @Key("scope")
    private String scopes;
    @Key("grant_type")
    private String grantType;
    protected Class<? extends TokenResponse> responseClass;
    
    public TokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final GenericUrl tokenServerUrl, final String grantType) {
        this(transport, jsonFactory, tokenServerUrl, grantType, TokenResponse.class);
    }
    
    public TokenRequest(final HttpTransport transport, final JsonFactory jsonFactory, final GenericUrl tokenServerUrl, final String grantType, final Class<? extends TokenResponse> responseClass) {
        this.transport = (HttpTransport)Preconditions.checkNotNull((Object)transport);
        this.jsonFactory = (JsonFactory)Preconditions.checkNotNull((Object)jsonFactory);
        this.setTokenServerUrl(tokenServerUrl);
        this.setGrantType(grantType);
        this.setResponseClass(responseClass);
    }
    
    public final HttpTransport getTransport() {
        return this.transport;
    }
    
    public final JsonFactory getJsonFactory() {
        return this.jsonFactory;
    }
    
    public final HttpRequestInitializer getRequestInitializer() {
        return this.requestInitializer;
    }
    
    public TokenRequest setRequestInitializer(final HttpRequestInitializer requestInitializer) {
        this.requestInitializer = requestInitializer;
        return this;
    }
    
    public final HttpExecuteInterceptor getClientAuthentication() {
        return this.clientAuthentication;
    }
    
    public TokenRequest setClientAuthentication(final HttpExecuteInterceptor clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
        return this;
    }
    
    public final GenericUrl getTokenServerUrl() {
        return this.tokenServerUrl;
    }
    
    public TokenRequest setTokenServerUrl(final GenericUrl tokenServerUrl) {
        this.tokenServerUrl = tokenServerUrl;
        Preconditions.checkArgument(tokenServerUrl.getFragment() == null);
        return this;
    }
    
    public final String getScopes() {
        return this.scopes;
    }
    
    public TokenRequest setScopes(final Collection<String> scopes) {
        this.scopes = ((scopes == null) ? null : Joiner.on(' ').join((Iterable)scopes));
        return this;
    }
    
    public final String getGrantType() {
        return this.grantType;
    }
    
    public TokenRequest setGrantType(final String grantType) {
        this.grantType = (String)Preconditions.checkNotNull((Object)grantType);
        return this;
    }
    
    public final Class<? extends TokenResponse> getResponseClass() {
        return this.responseClass;
    }
    
    public TokenRequest setResponseClass(final Class<? extends TokenResponse> responseClass) {
        this.responseClass = responseClass;
        return this;
    }
    
    public final HttpResponse executeUnparsed() throws IOException {
        final HttpRequestFactory requestFactory = this.transport.createRequestFactory((HttpRequestInitializer)new HttpRequestInitializer() {
            public void initialize(final HttpRequest request) throws IOException {
                if (TokenRequest.this.requestInitializer != null) {
                    TokenRequest.this.requestInitializer.initialize(request);
                }
                final HttpExecuteInterceptor interceptor = request.getInterceptor();
                request.setInterceptor((HttpExecuteInterceptor)new HttpExecuteInterceptor() {
                    public void intercept(final HttpRequest request) throws IOException {
                        if (interceptor != null) {
                            interceptor.intercept(request);
                        }
                        if (TokenRequest.this.clientAuthentication != null) {
                            TokenRequest.this.clientAuthentication.intercept(request);
                        }
                    }
                });
            }
        });
        final HttpRequest request = requestFactory.buildPostRequest(this.tokenServerUrl, (HttpContent)new UrlEncodedContent((Object)this));
        request.setParser((ObjectParser)new JsonObjectParser(this.jsonFactory));
        request.setThrowExceptionOnExecuteError(false);
        final HttpResponse response = request.execute();
        if (response.isSuccessStatusCode()) {
            return response;
        }
        throw TokenResponseException.from(this.jsonFactory, response);
    }
    
    public TokenResponse execute() throws IOException {
        return (TokenResponse)this.executeUnparsed().parseAs((Class)this.responseClass);
    }
    
    public TokenRequest set(final String fieldName, final Object value) {
        return (TokenRequest)super.set(fieldName, value);
    }
}
