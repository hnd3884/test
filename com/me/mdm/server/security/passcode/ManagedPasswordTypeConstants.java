package com.me.mdm.server.security.passcode;

public class ManagedPasswordTypeConstants
{
    public static final class PasswordType
    {
        public static final int FIRMWARE = 1;
        public static final int RECOVERY_LOCK = 2;
    }
    
    public static final class RequestAPI
    {
        public static final String MANAGED_PASSWORD = "managed_password";
    }
    
    public static final class ResponseAPI
    {
        public static final String MANAGED_PASSWORD_ID = "managed_password_id";
    }
}
