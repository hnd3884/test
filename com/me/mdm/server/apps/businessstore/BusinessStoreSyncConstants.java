package com.me.mdm.server.apps.businessstore;

public class BusinessStoreSyncConstants
{
    public static final Integer BS_SERVICE_VPP;
    public static final Integer BS_SERVICE_AFW;
    public static final Integer BS_SERVICE_WBS;
    public static final int BUSINESS_STORE_NO_STATUS = 0;
    public static final int BUSINESS_STORE_DOWNLOAD_DETAILS = 1;
    public static final int BUSINESS_STORE_SYNC_IN_PROGRESS = 2;
    public static final int BUSINESS_STORE_SYNC_COMPLETED = 3;
    public static final int BUSINESS_STORE_SYNC_FAILED = 4;
    public static final int BUSINESS_STORE_SYNC_USERS = 5;
    public static final int BUSINESS_STORE_BUILDING_LAYOUT = 6;
    public static final int BUSINESS_STORE_SYNC_DEVICES = 7;
    public static final int CONTACT_SUPPORT_CHAT_BOX = 1000;
    public static final int NON_PROD_RELEASE_LABEL_AVAILABLE = 2000;
    public static final int ENTERPRISE_PRIORITYMODE_SET = 2001;
    public static final int MANDATORY_APP_DATA_MISSING = 2002;
    public static final int ERROR_ADDING_TO_APPREPO = 2003;
    public static final int APP_NOT_AVAILABLE_FOR_ENTERPRISE = 2004;
    
    static {
        BS_SERVICE_VPP = 101;
        BS_SERVICE_AFW = 201;
        BS_SERVICE_WBS = 301;
    }
}
