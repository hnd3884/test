package com.google.api.client.googleapis.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.GenericJson;
import java.io.IOException;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Beta;
import com.google.api.client.util.Key;
import com.google.api.client.auth.oauth2.TokenResponse;

public class GoogleTokenResponse extends TokenResponse
{
    @Key("id_token")
    private String idToken;
    
    public GoogleTokenResponse setAccessToken(final String accessToken) {
        return (GoogleTokenResponse)super.setAccessToken(accessToken);
    }
    
    public GoogleTokenResponse setTokenType(final String tokenType) {
        return (GoogleTokenResponse)super.setTokenType(tokenType);
    }
    
    public GoogleTokenResponse setExpiresInSeconds(final Long expiresIn) {
        return (GoogleTokenResponse)super.setExpiresInSeconds(expiresIn);
    }
    
    public GoogleTokenResponse setRefreshToken(final String refreshToken) {
        return (GoogleTokenResponse)super.setRefreshToken(refreshToken);
    }
    
    public GoogleTokenResponse setScope(final String scope) {
        return (GoogleTokenResponse)super.setScope(scope);
    }
    
    @Beta
    public final String getIdToken() {
        return this.idToken;
    }
    
    @Beta
    public GoogleTokenResponse setIdToken(final String idToken) {
        this.idToken = (String)Preconditions.checkNotNull((Object)idToken);
        return this;
    }
    
    @Beta
    public GoogleIdToken parseIdToken() throws IOException {
        return GoogleIdToken.parse(this.getFactory(), this.getIdToken());
    }
    
    public GoogleTokenResponse set(final String fieldName, final Object value) {
        return (GoogleTokenResponse)super.set(fieldName, value);
    }
    
    public GoogleTokenResponse clone() {
        return (GoogleTokenResponse)super.clone();
    }
}
