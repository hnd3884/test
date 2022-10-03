package com.zoho.security.agent;

import java.io.File;

public class AppSenseConstants
{
    public static final String APPSENSE_DOMAIN = "http://appsense";
    public static final String APPSENSE_REGISTRATION_URI = "/zsecagent/v1/register";
    public static final String APPSENSE_RULESLOADER_URI = "/zsecagent/v1/appfirewallrule";
    public static final String APPSENSE_SECURITYXML_URI = "/zsecagent/v1/xmlfile";
    public static final String APPSENSE_CONFIGNOTIFY_URI = "/zsecagent/v1/alter";
    public static final String APPSENSE_ERROR_NOTIFICATION_URI = "/zsecagent/v1/error";
    public static final String HOME_DIR;
    public static final String FWRULES_FILE;
    public static final String FWRULES_TMPFILE;
    public static final String PROPERTY_FILE_NAME = "security-wafproperties.xml";
    public static final String PROPERTY_FILE;
    public static final String PROPERTY_TMPFILE;
    public static final String INVENTORY_FILE;
    public static final String INVENTORY_TMPFILE;
    public static final String WAF_ATTACK_DISCOVERY_INFO_FILE;
    public static final String WAF_ATTACK_DISCOVERY_INFO_TMPFILE;
    public static final String OLD_PROPERTY_FILE;
    public static final String OLD_PROPERTY_TMPFILE;
    public static final String PROPERTY = "PROPERTY";
    public static final String STATUS = "STATUS";
    public static final String REPORT_URI = "REPORT_URI";
    public static final String ZSEC_MATCHED_APPFIREWALL_RULE = "ZSEC_MATCHED_APPFIREWALL_RULE";
    public static final String MILESTONE = "milestone";
    private static String notificationURL;
    private static String errornotificationURL;
    
    public static String getNotificationURL() {
        return AppSenseConstants.notificationURL;
    }
    
    public static String getErrorNotificationURL() {
        return AppSenseConstants.errornotificationURL;
    }
    
    static {
        HOME_DIR = System.getProperty("user.home") + File.separator + "appsense";
        FWRULES_FILE = AppSenseConstants.HOME_DIR + File.separator + "appfirewallrules.json";
        FWRULES_TMPFILE = AppSenseConstants.HOME_DIR + File.separator + "appfirewallrules.tmp";
        PROPERTY_FILE = AppSenseConstants.HOME_DIR + File.separator + "security-wafproperties.xml";
        PROPERTY_TMPFILE = AppSenseConstants.HOME_DIR + File.separator + "security-wafproperties.tmp";
        INVENTORY_FILE = AppSenseConstants.HOME_DIR + File.separator + "inventory.json";
        INVENTORY_TMPFILE = AppSenseConstants.HOME_DIR + File.separator + "inventory.tmp";
        WAF_ATTACK_DISCOVERY_INFO_FILE = AppSenseConstants.HOME_DIR + File.separator + "wafattackdiscoveryinfo.json";
        WAF_ATTACK_DISCOVERY_INFO_TMPFILE = AppSenseConstants.HOME_DIR + File.separator + "wafattackdiscoveryinfo.tmp";
        OLD_PROPERTY_FILE = AppSenseConstants.HOME_DIR + File.separator + "property.json";
        OLD_PROPERTY_TMPFILE = AppSenseConstants.HOME_DIR + File.separator + "property.tmp";
        AppSenseConstants.notificationURL = "http://appsense/zsecagent/v1/alter";
        AppSenseConstants.errornotificationURL = "http://appsense/zsecagent/v1/error";
    }
}
