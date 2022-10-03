package com.adventnet.mdmconfiguration;

public final class DEPLOYMENTPOLICYSETTINGS
{
    public static final String TABLE = "DeploymentPolicySettings";
    public static final String DEPLOYMENT_TEMPLATE_ID = "DEPLOYMENT_TEMPLATE_ID";
    public static final int DEPLOYMENT_TEMPLATE_ID_IDX = 1;
    public static final String MAX_TARGET_PREFIX = "MAX_TARGET_PREFIX";
    public static final int MAX_TARGET_PREFIX_IDX = 2;
    public static final String REBOOT_AFTER_UPDATE = "REBOOT_AFTER_UPDATE";
    public static final int REBOOT_AFTER_UPDATE_IDX = 3;
    public static final String DOWNLOAD_OVER_WIFI = "DOWNLOAD_OVER_WIFI";
    public static final int DOWNLOAD_OVER_WIFI_IDX = 4;
    public static final String DOWNLOAD_IN_DEP_WINDOW = "DOWNLOAD_IN_DEP_WINDOW";
    public static final int DOWNLOAD_IN_DEP_WINDOW_IDX = 5;
    
    private DEPLOYMENTPOLICYSETTINGS() {
    }
}
