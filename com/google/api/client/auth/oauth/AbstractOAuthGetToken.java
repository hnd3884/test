package com.google.api.client.auth.oauth;

import java.io.IOException;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.UrlEncodedParser;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Beta;
import com.google.api.client.http.GenericUrl;

@Beta
public abstract class AbstractOAuthGetToken extends GenericUrl
{
    public HttpTransport transport;
    public String consumerKey;
    public OAuthSigner signer;
    protected boolean usePost;
    
    protected AbstractOAuthGetToken(final String authorizationServerUrl) {
        super(authorizationServerUrl);
    }
    
    public final OAuthCredentialsResponse execute() throws IOException {
        final HttpRequestFactory requestFactory = this.transport.createRequestFactory();
        final HttpRequest request = requestFactory.buildRequest(this.usePost ? "POST" : "GET", (GenericUrl)this, (HttpContent)null);
        this.createParameters().intercept(request);
        final HttpResponse response = request.execute();
        response.setContentLoggingLimit(0);
        final OAuthCredentialsResponse oauthResponse = new OAuthCredentialsResponse();
        UrlEncodedParser.parse(response.parseAsString(), (Object)oauthResponse);
        return oauthResponse;
    }
    
    public OAuthParameters createParameters() {
        final OAuthParameters result = new OAuthParameters();
        result.consumerKey = this.consumerKey;
        result.signer = this.signer;
        return result;
    }
}
