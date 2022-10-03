package com.google.api.client.auth.openidconnect;

import com.google.api.client.util.GenericData;
import com.google.api.client.json.GenericJson;
import com.google.api.client.auth.oauth2.TokenRequest;
import java.io.IOException;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Key;
import com.google.api.client.util.Beta;
import com.google.api.client.auth.oauth2.TokenResponse;

@Beta
public class IdTokenResponse extends TokenResponse
{
    @Key("id_token")
    private String idToken;
    
    public final String getIdToken() {
        return this.idToken;
    }
    
    public IdTokenResponse setIdToken(final String idToken) {
        this.idToken = (String)Preconditions.checkNotNull((Object)idToken);
        return this;
    }
    
    @Override
    public IdTokenResponse setAccessToken(final String accessToken) {
        super.setAccessToken(accessToken);
        return this;
    }
    
    @Override
    public IdTokenResponse setTokenType(final String tokenType) {
        super.setTokenType(tokenType);
        return this;
    }
    
    @Override
    public IdTokenResponse setExpiresInSeconds(final Long expiresIn) {
        super.setExpiresInSeconds(expiresIn);
        return this;
    }
    
    @Override
    public IdTokenResponse setRefreshToken(final String refreshToken) {
        super.setRefreshToken(refreshToken);
        return this;
    }
    
    @Override
    public IdTokenResponse setScope(final String scope) {
        super.setScope(scope);
        return this;
    }
    
    public IdToken parseIdToken() throws IOException {
        return IdToken.parse(this.getFactory(), this.idToken);
    }
    
    public static IdTokenResponse execute(final TokenRequest tokenRequest) throws IOException {
        return (IdTokenResponse)tokenRequest.executeUnparsed().parseAs((Class)IdTokenResponse.class);
    }
    
    @Override
    public IdTokenResponse set(final String fieldName, final Object value) {
        return (IdTokenResponse)super.set(fieldName, value);
    }
    
    @Override
    public IdTokenResponse clone() {
        return (IdTokenResponse)super.clone();
    }
}
