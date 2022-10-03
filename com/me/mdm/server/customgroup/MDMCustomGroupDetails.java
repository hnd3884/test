package com.me.mdm.server.customgroup;

import org.json.JSONObject;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupDetails;

public class MDMCustomGroupDetails extends CustomGroupDetails
{
    public static final int CUSTOM_GROUP_TYPE_MDM = 3;
    public static final int IOS_PLATFORM_TYPE = 1;
    public static final int ANDROID_PLATFORM_TYPE = 2;
    public static final int IOS_GROUP_TYPE = 3;
    public static final int ANDROID_GROUP_TYPE = 4;
    public static final int SAFE_GROUP_TYPE = 5;
    public static final int IOS_CORPORATE_GROUP_IDENTIFIER = 1;
    public static final int IOS_PERSONAL_GROUP_IDENTIFIER = 2;
    public static final int ANDROID_CORPORATE_GROUP_IDENTIFIER = 3;
    public static final int ANDROID_PERSONAL_GROUP_IDENTIFIER = 4;
    public static final String MDM_DOMAIN_NAME = "MDM";
    public static final String ANDROID = "Android";
    public static final String IOS = "iOS";
    public static final String SAFE = "Safe";
    public static final String ALL_IOS_GROUP = "All_iOS";
    public static final String IOS_CORPORATE_GROUP = "Default_iOS_Corporate";
    public static final String IOS_PERSONAL_GROUP = "Default_iOS_Personal";
    public static final String ALL_ANDROID_GROUP = "All_Android";
    public static final String ANDROID_CORPORATE_GROUP = "Default_Android_Corporate";
    public static final String ANDROID_PERSONAL_GROUP = "Default_Android_Personal";
    public static final String WINDOWS_CORPORATE_GROUP = "Default_Windows_Corporate";
    public static final String WINDOWS_PERSONAL_GROUP = "Default_Windows_Personal";
    public static final int WINDOWS_CORPORATE_GROUP_IDENTIFIER = 5;
    public static final int WINDOWS_PERSONAL_GROUP_IDENTIFIER = 6;
    public static final String WINDOWS = "Windows";
    public static final int WINDOWS_GROUP_TYPE = 5;
    public String groupTypeStr;
    public int groupCategory;
    public int groupOwnedBy;
    public int groupIdentifier;
    public Long userId;
    public Long createdTime;
    public Long lastUpdatedTime;
    public JSONObject integrationJson;
    
    public MDMCustomGroupDetails() {
        this.groupTypeStr = null;
        this.groupCategory = 1;
        this.groupOwnedBy = -1;
        this.groupIdentifier = -1;
        this.userId = null;
        this.createdTime = null;
        this.lastUpdatedTime = null;
        this.integrationJson = null;
    }
}
