package com.me.mdm.server.apps.provisioningprofiles;

public class ProvisioningProfileConstants
{
    public static final String DEFAULT_PROFILE_NAME = "App Provisioning Profile";
    public static final Integer SOURCE_MANAGED;
    public static final Integer SOURCE_UNKNOWN;
    
    static {
        SOURCE_MANAGED = 1;
        SOURCE_UNKNOWN = 0;
    }
}
