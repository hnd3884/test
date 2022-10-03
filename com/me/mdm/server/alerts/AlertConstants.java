package com.me.mdm.server.alerts;

public class AlertConstants
{
    public static final String USER_ID = "userId";
    
    public static final class LicenseAlertConstant
    {
        public static final Long LESS_THAN;
        public static final Long GREATER_THAN;
        public static final Long LICENSE_PERCENT_EXCEEDED;
        public static final Long LICENSE_PERCENT_BELOW_MINIMUM_LIMIT;
        
        static {
            LESS_THAN = 1L;
            GREATER_THAN = 2L;
            LICENSE_PERCENT_EXCEEDED = 11107L;
            LICENSE_PERCENT_BELOW_MINIMUM_LIMIT = 11106L;
        }
    }
}
