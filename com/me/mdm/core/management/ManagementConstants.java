package com.me.mdm.core.management;

public class ManagementConstants
{
    public static class Types
    {
        public static final Integer MODERN_MGMT;
        public static final Integer MOBILE_MGMT;
        
        static {
            MODERN_MGMT = 2;
            MOBILE_MGMT = 1;
        }
    }
    
    public static class Entities
    {
        public static final int PROFILES = 1;
        public static final int RESOURCES = 2;
    }
    
    public static class Keys
    {
        public static final String MANAGEMENT_TYPE = "management_type";
    }
}
