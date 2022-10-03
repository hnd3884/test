package com.me.devicemanagement.framework.server.alerts.sms;

import org.json.JSONObject;
import java.util.Properties;

public interface SMSAPI
{
    public static final int SMS_STATUS_SUCCESS = 0;
    public static final int SMS_STATUS_CREDIT_FAILURE = 10001;
    public static final int SMS_STATUS_HTTP_FAILURE = 10000;
    public static final String SMS_USED_COUNT = "SMS_USED_COUNT";
    public static final int SMS_STATUS_MOBILE_UNREACHABLE = 10002;
    public static final int SMS_STATUS_MESSAGE_BLOCKED = 10003;
    public static final int SMS_STATUS_UNKNOWN_ERROR = 10004;
    
    boolean isSMSSettingsConfigured();
    
    Properties getSMSConfigurationSettings();
    
    JSONObject sendHTTPToSMS(final Properties p0);
    
    int getRemainingCredits();
}
