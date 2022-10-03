package com.google.api.client.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public class TokenErrorResponse extends GenericJson
{
    @Key
    private String error;
    @Key("error_description")
    private String errorDescription;
    @Key("error_uri")
    private String errorUri;
    
    public final String getError() {
        return this.error;
    }
    
    public TokenErrorResponse setError(final String error) {
        this.error = (String)Preconditions.checkNotNull((Object)error);
        return this;
    }
    
    public final String getErrorDescription() {
        return this.errorDescription;
    }
    
    public TokenErrorResponse setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }
    
    public final String getErrorUri() {
        return this.errorUri;
    }
    
    public TokenErrorResponse setErrorUri(final String errorUri) {
        this.errorUri = errorUri;
        return this;
    }
    
    public TokenErrorResponse set(final String fieldName, final Object value) {
        return (TokenErrorResponse)super.set(fieldName, value);
    }
    
    public TokenErrorResponse clone() {
        return (TokenErrorResponse)super.clone();
    }
}
