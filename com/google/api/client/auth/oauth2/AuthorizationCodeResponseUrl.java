package com.google.api.client.auth.oauth2;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Key;
import com.google.api.client.http.GenericUrl;

public class AuthorizationCodeResponseUrl extends GenericUrl
{
    @Key
    private String code;
    @Key
    private String state;
    @Key
    private String error;
    @Key("error_description")
    private String errorDescription;
    @Key("error_uri")
    private String errorUri;
    
    public AuthorizationCodeResponseUrl(final String encodedResponseUrl) {
        super(encodedResponseUrl);
        Preconditions.checkArgument(this.code == null != (this.error == null));
    }
    
    public final String getCode() {
        return this.code;
    }
    
    public AuthorizationCodeResponseUrl setCode(final String code) {
        this.code = code;
        return this;
    }
    
    public final String getState() {
        return this.state;
    }
    
    public AuthorizationCodeResponseUrl setState(final String state) {
        this.state = state;
        return this;
    }
    
    public final String getError() {
        return this.error;
    }
    
    public AuthorizationCodeResponseUrl setError(final String error) {
        this.error = error;
        return this;
    }
    
    public final String getErrorDescription() {
        return this.errorDescription;
    }
    
    public AuthorizationCodeResponseUrl setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }
    
    public final String getErrorUri() {
        return this.errorUri;
    }
    
    public AuthorizationCodeResponseUrl setErrorUri(final String errorUri) {
        this.errorUri = errorUri;
        return this;
    }
    
    public AuthorizationCodeResponseUrl set(final String fieldName, final Object value) {
        return (AuthorizationCodeResponseUrl)super.set(fieldName, value);
    }
    
    public AuthorizationCodeResponseUrl clone() {
        return (AuthorizationCodeResponseUrl)super.clone();
    }
}
