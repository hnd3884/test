package com.me.mdm.chrome.agent.enrollment;

import org.json.JSONException;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.Context;

public abstract class ChromeDeviceEnrollmentProcessor
{
    public abstract boolean processEnrollment(final Context p0, final JSONObject p1) throws JSONException;
    
    class EnrollmentConstants
    {
        public static final String USER_NAME = "UserName";
        public static final String DOMAIN_NAME = "DomainName";
        public static final String DOMAIN_NAME_LIST = "DomainNameList";
        public static final String EMAIL_ID = "EmailAddress";
        public static final String SERVER_NAME = "ServerName";
        public static final String SERVER_PORT = "ServerPort";
        public static final String AD_PASSWORD = "ADPassword";
        public static final String OTP_PASSWORD = "OTPPassword";
        public static final String OWNED_BY = "OwnedBy";
        public static final String IS_ONPREMISE = "IsOnPremise";
        public static final String DEVICE_NAME = "DeviceName";
        public static final String REGISTRAION_ID = "RegistrationID";
        public static final String REGISTRATION_TYPE = "RegistrationType";
        public static final String DEVICE_UDID = "UDID";
        public static final String ENROLLMENT_REQUEST_ID = "EnrollmentReqID";
        public static final String CUSTOMER_ID = "CustomerID";
        public static final String AGENT_VERSION = "AgentVersion";
        public static final String AGENT_VERSION_CODE = "AgentVersionCode";
        public static final String KEY_DEVICE_INFO = "DeviceInfo";
    }
}
