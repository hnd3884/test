package mdm.integrations.certificateauthority.digicert.v1;

public class DigicertConstants
{
    public static final class Proxy
    {
        public static final String PROXY_HOST = "PROXY_HOST";
        public static final String PROXY_PORT = "PROXY_PORT";
        public static final String PROXY_USER_NAME = "PROXY_USER_NAME";
        public static final String PROXY_PASSWORD = "PROXY_PASSWORD";
    }
    
    public static final class Directory
    {
        public static final String KEYSTORE_LOCATION = "KEYSTORE_LOCATION";
        public static final String KEYSTORE_PASSWORD = "KEYSTORE_PASSWORD";
        public static final String TRUSTSTORE_LOCATION = "TRUSTSTORE_LOCATION";
        public static final String TRUSTSTORE_PASSWORD = "TRUSTSTORE_PASSWORD";
    }
    
    public static final class Templates
    {
        public static final String CERTIFICATE_OID = "CERTIFICATE_OID";
    }
    
    public static final class General
    {
        public static final String FIRST_NAME = "FIRST_NAME";
        public static final String LAST_NAME = "LAST_NAME";
        public static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
        public static final String PASSCODE = "PASSCODE";
        public static final String STATUS = "STATUS";
    }
    
    public static final class Status
    {
        public static final String SUCCESS = "0";
        public static final String FAILURE = "0";
    }
}
