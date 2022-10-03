package com.me.mdm.server.agent;

import java.io.File;

public class MDMAgentConstants
{
    public static final String AGENTFOLDER = "agent";
    public static final String MDM_ANDROID_AGENT = "MDMAndroidAgent.apk";
    public static final String MDM_ANDROID_AGENT_23 = "MDMAndroidAgent23.apk";
    public static final String ANDROID_AGENT_BUNDLE_IDENTIFIER = "com.manageengine.mdm.android";
    public static final String ANDROID_KNOX_AGENT_BUNDLE_IDENTIFIER = "com.manageengine.mdm.samsung.knox";
    public static final String MDM_ANDROID_AGENT_DOWNLOAD_URL = "/agent/MDMAndroidAgent.apk";
    public static final String MDM_ANDROID_AGENT_VERSION = "androidagentversion";
    public static final String MDM_ANDROID_AGENT_VERSION_CODE = "androidagentversioncode";
    public static final String MDM_KNOX_AGENT_VERSION = "knoxagentversion";
    public static final String MDM_KNOX_AGENT_VERSION_CODE = "knoxagentversioncode";
    public static final String MDM_KNOX_AGENT = "MDMKnoxAgent.apk";
    public static final String MDM_KNOX_AGENT_DOWNLOAD_URL = "/agent/MDMKnoxAgent.apk";
    public static final String MDM_ANDROID_ADMIN_AGENT = "MDMAndroidAdmin.apk";
    public static final String MDM_ANDROID_ADMIN_AGENT_DOWNLOAD_URL = "/agent/mdm/admin/MDMAndroidAdmin.apk";
    public static final String MDM_ANDROID_ADMIN_CLOUD_AGENT_DOWNLOAD_URL = "/agent/MDMAdminApp.apk";
    public static final String MDM_FOLDER = "mdm";
    public static final String SIGNED_FOLDER = "signed";
    public static final String UNSIGNED_FOLDER = "unsigned";
    public static final String MDM_WP_AGENT = "mdmwindowsagent.xap";
    public static final String MDM_WINDOWS_AGENT_VERSION = "windowsagentversion";
    public static final String MDM_WINDOWS_AGENT_VERSION_CODE = "windowsagentversioncode";
    public static final String MDM_WINDOWS_MANDATORY_AGENT_VERSION_CODE = "windowsagentmandatoryversioncode";
    public static final String MDM_WP_UNSIGNED_AGENT_DOWNLOAD_URL;
    public static final String MDM_WINDOWS_SIGNED_FOLDER;
    public static final String MDM_ANDROID_AUTH_ENROLLMENT_ID = "device_erid";
    public static final String MDM_ANDROID_AUTH_CUSTOMER_ID = "device_customerid";
    public static final String MDM_ANDROID_AUTH_USER_NAME = "device_username";
    public static final String MDM_ANDROID_AUTH_USER_EMAIL = "device_useremail";
    public static final String MDM_ANDROID_AUTH_SERVER_NAME = "serverName";
    public static final String MDM_ANDROID_AUTH_SERVER_PORT = "portNumber";
    public static final Integer PASSCODE_TYPE_PATTERN;
    public static final Integer PASSCODE_TYPE_NUMBERS;
    public static final Integer PASSCODE_TYPE_ALPHABETS;
    public static final Integer PASSCODE_TYPE_ALPHA_NUMERIC;
    public static final Integer PASSCODE_TYPE_COMPLEX;
    public static final String PASSCODE_COMPLEXITY_LOW = "1";
    public static final String PASSCODE_COMPLEXITY_MEDIUM = "2";
    public static final String PASSCODE_COMPLEXITY_HIGH = "3";
    
    static {
        MDM_WP_UNSIGNED_AGENT_DOWNLOAD_URL = "agent" + File.separator + "mdm" + File.separator + "unsigned" + File.separator + "mdmwindowsagent.xap";
        MDM_WINDOWS_SIGNED_FOLDER = "agent" + File.separator + "mdm" + File.separator + "signed";
        PASSCODE_TYPE_PATTERN = 1;
        PASSCODE_TYPE_NUMBERS = 2;
        PASSCODE_TYPE_ALPHABETS = 3;
        PASSCODE_TYPE_ALPHA_NUMERIC = 4;
        PASSCODE_TYPE_COMPLEX = 5;
    }
}
