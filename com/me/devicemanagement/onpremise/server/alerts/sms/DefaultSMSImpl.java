package com.me.devicemanagement.onpremise.server.alerts.sms;

import org.json.JSONObject;
import java.util.Properties;
import com.me.devicemanagement.framework.server.alerts.sms.SMSAPI;

public class DefaultSMSImpl implements SMSAPI
{
    public boolean isSMSSettingsConfigured() {
        return false;
    }
    
    public Properties getSMSConfigurationSettings() {
        throw new UnsupportedOperationException("Operation sendHTTPToSMS not supported");
    }
    
    public JSONObject sendHTTPToSMS(final Properties smsProperties) {
        throw new UnsupportedOperationException("Operation sendHTTPToSMS not supported");
    }
    
    public int getRemainingCredits() {
        return -1;
    }
}
