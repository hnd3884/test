package com.adventnet.devicemanagementframework;

public final class SMTPCONFIGURATION
{
    public static final String TABLE = "SmtpConfiguration";
    public static final String SERVERNAME = "SERVERNAME";
    public static final int SERVERNAME_IDX = 1;
    public static final String PORT = "PORT";
    public static final int PORT_IDX = 2;
    public static final String USERNAME = "USERNAME";
    public static final int USERNAME_IDX = 3;
    public static final String PASSWORD = "PASSWORD";
    public static final int PASSWORD_IDX = 4;
    public static final String SENDER_NAME = "SENDER_NAME";
    public static final int SENDER_NAME_IDX = 5;
    public static final String SENDER_ADDRESS = "SENDER_ADDRESS";
    public static final int SENDER_ADDRESS_IDX = 6;
    public static final String IS_TLS_ENABLED = "IS_TLS_ENABLED";
    public static final int IS_TLS_ENABLED_IDX = 7;
    public static final String IS_SMTPS_ENABLED = "IS_SMTPS_ENABLED";
    public static final int IS_SMTPS_ENABLED_IDX = 8;
    public static final String PREVIOUS_ERROR_CODE = "PREVIOUS_ERROR_CODE";
    public static final int PREVIOUS_ERROR_CODE_IDX = 9;
    public static final String AUTH_TYPE = "AUTH_TYPE";
    public static final int AUTH_TYPE_IDX = 10;
    public static final String USE_PROXY = "USE_PROXY";
    public static final int USE_PROXY_IDX = 11;
    public static final String CREDENTIAL_ID = "CREDENTIAL_ID";
    public static final int CREDENTIAL_ID_IDX = 12;
    
    private SMTPCONFIGURATION() {
    }
}
