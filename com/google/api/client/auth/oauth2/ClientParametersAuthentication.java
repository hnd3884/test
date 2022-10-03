package com.google.api.client.auth.oauth2;

import java.util.Map;
import com.google.api.client.util.Data;
import com.google.api.client.http.UrlEncodedContent;
import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.Preconditions;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequestInitializer;

public class ClientParametersAuthentication implements HttpRequestInitializer, HttpExecuteInterceptor
{
    private final String clientId;
    private final String clientSecret;
    
    public ClientParametersAuthentication(final String clientId, final String clientSecret) {
        this.clientId = (String)Preconditions.checkNotNull((Object)clientId);
        this.clientSecret = clientSecret;
    }
    
    public void initialize(final HttpRequest request) throws IOException {
        request.setInterceptor((HttpExecuteInterceptor)this);
    }
    
    public void intercept(final HttpRequest request) throws IOException {
        final Map<String, Object> data = Data.mapOf(UrlEncodedContent.getContent(request).getData());
        data.put("client_id", this.clientId);
        if (this.clientSecret != null) {
            data.put("client_secret", this.clientSecret);
        }
    }
    
    public final String getClientId() {
        return this.clientId;
    }
    
    public final String getClientSecret() {
        return this.clientSecret;
    }
}
