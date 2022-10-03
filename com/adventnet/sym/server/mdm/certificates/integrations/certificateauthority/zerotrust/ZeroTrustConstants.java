package com.adventnet.sym.server.mdm.certificates.integrations.certificateauthority.zerotrust;

public class ZeroTrustConstants
{
    public static class Api
    {
        public static final String ZEROTRUST_URL = "zerotrust_url";
        public static final String AUTHTOKEN_KEY = "authtoken_key";
        public static final String AUTHTOKEN_VALUE = "authtoken_value";
    }
    
    public static class Request
    {
        public static class Device
        {
            public static final String DEVICE = "device";
            public static final String DEVICE_USER_EMAIL = "emailId";
            public static final String DEVICE_NAME = "device_name";
            public static final String DEVICE_MODEL = "device_model";
            public static final String DEVICE_MANUFACTURER = "device_manufacturer";
            public static final String DEVICE_UUID = "device_uuid";
            public static final String LAPTOP = "laptop";
            public static final String MOBILE = "mobile";
        }
    }
    
    public static class Response
    {
        public static final String HTTP_RESPONSE_CODE = "http_response_code";
        public static final String SAN = "san";
        public static final String PASSWORD = "password";
        public static final String ZEROTRUST_SAN = "ZEROTRUST_SAN";
        public static final String ZEROTRUST_PASSWORD = "ZEROTRUST_PASSWORD";
    }
}
