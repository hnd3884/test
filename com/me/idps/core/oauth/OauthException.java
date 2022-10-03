package com.me.idps.core.oauth;

public class OauthException extends Exception
{
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String ACCESS_DENIED = "access_denied";
    public static final String INVALID_GRANT = "invalid_grant";
    public static final String INVALID_CLIENT = "invalid_client";
    public static final String UNAVAILABLE = "unavailable";
    
    public OauthException(final String msg) {
        super(msg);
    }
}
