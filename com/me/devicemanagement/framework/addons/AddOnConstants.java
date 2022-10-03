package com.me.devicemanagement.framework.addons;

public class AddOnConstants
{
    public static String meta_FILE;
    public static final String ADDON_STATUS_RUNNING = "ADDON_STATUS_RUNNING";
    public static final int ENABLED_FOR_SPECIFIC_RESOURCE = 2;
    public static final int ENABLED = 1;
    public static final int DISABLED = 0;
    public static final int SECURITY_ADDON = 0;
    public static final int SECURITY_MODULES = 1;
    
    static {
        AddOnConstants.meta_FILE = "addons-status.json";
    }
}
