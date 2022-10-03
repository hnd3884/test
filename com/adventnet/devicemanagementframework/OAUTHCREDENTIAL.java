package com.adventnet.devicemanagementframework;

public final class OAUTHCREDENTIAL
{
    public static final String TABLE = "OauthCredential";
    public static final String CREDENTIAL_ID = "CREDENTIAL_ID";
    public static final int CREDENTIAL_ID_IDX = 1;
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final int CLIENT_ID_IDX = 2;
    public static final String CLIENT_SECRET = "CLIENT_SECRET";
    public static final int CLIENT_SECRET_IDX = 3;
    public static final String AUTH_URL = "AUTH_URL";
    public static final int AUTH_URL_IDX = 4;
    public static final String TOKEN_URL = "TOKEN_URL";
    public static final int TOKEN_URL_IDX = 5;
    public static final String SCOPE = "SCOPE";
    public static final int SCOPE_IDX = 6;
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final int ACCESS_TOKEN_IDX = 7;
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final int REFRESH_TOKEN_IDX = 8;
    public static final String EXPIRES_AT = "EXPIRES_AT";
    public static final int EXPIRES_AT_IDX = 9;
    
    private OAUTHCREDENTIAL() {
    }
}
