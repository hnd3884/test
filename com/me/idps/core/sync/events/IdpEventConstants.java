package com.me.idps.core.sync.events;

import org.json.simple.JSONArray;
import com.me.idps.core.sync.product.DirProdImplRequest;

public enum IdpEventConstants
{
    PRODUCT_OPS(1, "handleProductOps", "PRODUCT_OPS"), 
    MEMBER_ADDED_EVENT(2, "memberAdded", "ADDED_EVENT", true, false), 
    MEMBER_REMOVED_EVENT(3, "memberRemoved", "REMOVED_EVENT", true, false), 
    MODIFIED_EVENT(4, (String)null, "MODIFIED_EVENT"), 
    USER_MODIFIED_EVENT(5, "userModified", "USER_MODIFIED_EVENT", true, false), 
    GROUP_MODIFIED_EVENT(6, "groupModified", "GROUP_MODIFIED_EVENT", true, false), 
    STATUS_CHANGE_EVENT(7, (String)null, "STATUS_CHANGE_EVENT"), 
    USER_DELETED_EVENT(8, "userDeleted", "USER_DELETED_EVENT", true, false), 
    USER_ACTIVATED_EVENT(9, "userActivated", "USER_ACTIVATED_EVENT", true, false), 
    USER_DIR_DISABLED_EVENT(10, "userDirDisabled", "USER_DIR_DISABLED_EVENT", true, false), 
    USER_SYNC_DISABLED_EVENT(11, "userSyncDisabled", "USER_SYNC_DISABLED_EVENT", true, false), 
    GROUP_DELETED_EVENT(12, "groupDeleted", "GROUP_DELETED_EVENT", true, false), 
    GROUP_ACTIVATED_EVENT(13, "groupActivated", "GROUP_ACTIVATED_EVENT", true, false), 
    GROUP_DIR_DISABLED_EVENT(14, "groupDirDisabled", "GROUP_DIR_DISABLED_EVENT", true, false), 
    GROUP_SYNC_DISABLED_EVENT(15, "groupSyncDisabled", "GROUP_SYNC_DISABLED_EVENT", true, false), 
    APPROVE_DOMAIN_DELETION(17, "approveDomainDeletion", "APPROVE_DOMAIN_DELETION"), 
    CUSTOM_OPS(18, "customHandling", "CUSTOM_HANDLING"), 
    POST_SYNC_OPS(19, "postSyncOPS", "POST_SYNC_OPS"), 
    RESOLVE_RESOURCE_DUPLICATES(20, "resolveResourceDuplicates", "RESOLVE_RES_DUPL"), 
    BIND_RES(21, "bindRes", "BIND_RES"), 
    UNBIND_DELETED_RES(22, "unBindDeletedRes", "UNBIND_DEL_RES"), 
    GET_PROD_SPECIFIC_ME_TRACKING_KEYS(23, "getProdSpecificMeTrackingKeys", "GET_ME_TRACKING_KEYS", false, true), 
    DO_PROD_SPECIFIC_ME_TRACKING(24, "doProdSpecificMEtracking", "DO_PROD_SPECIFIC_ME_TRACKING", false, true), 
    GET_DEFAULT_CUST_PHONE_NUMBER(25, "getDefulatCustPhoneNumber", "GET_DEFAULT_CUST_PHONE_NUM"), 
    PROCESS_USER_IDF_DETAILS(26, "getUserIDFdetails", "GET_USR_IDF_DETAILS", false, true), 
    FEATURE_PARAMS(27, "featureOps", "FEATURE_OPS", false, true), 
    ERR_IDP_CONF(28, "throwExcepForErrResp", "ERR_IDP_CONF"), 
    GET_AUTO_VA_DISABLED_TABLES(29, "getAutoVAdisabledTables", "GET_AUTO_VA_DISABLED_TABLES", false, true), 
    SCHEDULER_SPREAD_ADJUST(30, "adjustSchedulerSpread", "SCHEDULER_SPREAD_ADJUST"), 
    AD_INTEG_CONSENT(31, "setADIntegConsent", "AD_INTEG_CONSENT"), 
    HANDLE_UPGRADE(32, "handleUpgrade", "HANDLE_UPGRADE");
    
    private int eventType;
    private int implControl;
    private String methodName;
    private String displayName;
    private boolean skipForTest;
    private boolean coreHandlingRequired;
    
    private IdpEventConstants(final int eventType, final String methodName, final String displayName) {
        this(eventType, methodName, displayName, false, false);
    }
    
    private IdpEventConstants(final int eventType, final String methodName, final String displayName, final boolean skipForTest, final boolean coreHandlingRequired) {
        this(eventType, methodName, displayName, skipForTest, coreHandlingRequired, -1);
    }
    
    private IdpEventConstants(final int eventType, final String methodName, final String displayName, final boolean skipForTest, final boolean coreHandlingRequired, final int implControl) {
        this.eventType = eventType;
        this.methodName = methodName;
        this.implControl = implControl;
        this.displayName = displayName;
        this.skipForTest = skipForTest;
        this.coreHandlingRequired = coreHandlingRequired;
    }
    
    public static String getEventType(final int eventType) {
        for (final IdpEventConstants cur : values()) {
            if (cur.eventType == eventType) {
                return cur.displayName;
            }
        }
        return "";
    }
    
    public static IdpEventConstants getEvent(final int eventType) {
        for (final IdpEventConstants cur : values()) {
            if (cur.eventType == eventType) {
                return cur;
            }
        }
        return null;
    }
    
    public int getEventType() {
        return this.eventType;
    }
    
    public String getInitializationMethod() {
        return this.methodName;
    }
    
    @Override
    public String toString() {
        return this.displayName;
    }
    
    public boolean isTestSkippable() {
        return this.skipForTest;
    }
    
    public int getImplControl() {
        return this.implControl;
    }
    
    public boolean isCoreHandlingRequired() {
        return this.coreHandlingRequired;
    }
    
    public String getEventHint(final DirProdImplRequest dirProdImplRequest) {
        switch (this) {
            case MEMBER_ADDED_EVENT:
            case MEMBER_REMOVED_EVENT: {
                final Long[] membersResIDs = (Long[])dirProdImplRequest.args[1];
                return String.valueOf(membersResIDs.length);
            }
            case USER_DELETED_EVENT:
            case USER_ACTIVATED_EVENT:
            case USER_DIR_DISABLED_EVENT:
            case USER_SYNC_DISABLED_EVENT:
            case GROUP_DELETED_EVENT:
            case GROUP_ACTIVATED_EVENT:
            case GROUP_DIR_DISABLED_EVENT:
            case GROUP_SYNC_DISABLED_EVENT: {
                return String.valueOf(((JSONArray)dirProdImplRequest.args[0]).size());
            }
            default: {
                return null;
            }
        }
    }
}
