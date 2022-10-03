package com.me.devicemanagement.onpremise.server.alerts;

public class AlertConstants
{
    public static final Long UNKNOWN_USER_ALERT;
    public static final Long USER_INCORRECT_PASSWORD_ALERT;
    public static final Long USER_INCORRECT_OTP_ALERT;
    public static final Long USER_ACCOUNT_LOCKED_ALERT;
    public static final Long USER_ACCOUNT_ACTIVATION_ALERT;
    public static final Long USER_RESET_PASSWORD_INITIATED_ALERT;
    public static final Long USER_RESET_PASSWORD_SUCCESSFULLY_ALERT;
    
    static {
        UNKNOWN_USER_ALERT = 50001L;
        USER_INCORRECT_PASSWORD_ALERT = 50002L;
        USER_INCORRECT_OTP_ALERT = 50003L;
        USER_ACCOUNT_LOCKED_ALERT = 50004L;
        USER_ACCOUNT_ACTIVATION_ALERT = 50005L;
        USER_RESET_PASSWORD_INITIATED_ALERT = 50006L;
        USER_RESET_PASSWORD_SUCCESSFULLY_ALERT = 50007L;
    }
}
