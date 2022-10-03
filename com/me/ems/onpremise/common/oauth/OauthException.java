package com.me.ems.onpremise.common.oauth;

public class OauthException extends Exception
{
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String INVALID_GRANT = "INVALID_GRANT";
    public static final String INVALID_CLIENT = "INVALID_CLIENT";
    public static final String UNAVAILABLE = "UNAVAILABLE";
    
    public OauthException(final String msg) {
        super(msg);
    }
}
