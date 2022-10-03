package com.me.mdm.server.acp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ProtocolConstants
{
    public static final String UDID = "UDID";
    public static final String DEVICE_PLATFORM = "DevicePlatform";
    public static final String STATUS = "Status";
    public static final String ERROR_CODE = "ErrorCode";
    public static final String ERROR_KEY = "ErrorKey";
    public static final String ERROR_MESSAGE = "ErrorMessage";
    public static final String ACKNOWLEDGED = "Acknowledged";
    public static final String ERROR = "Error";
    public static final String MSG_REQUEST_TYPE = "MsgRequestType";
    public static final String MSG_VERSION = "MsgVersion";
    public static final String MSG_REQUEST = "MsgRequest";
    public static final String MSG_RESPONSE_TYPE = "MsgResponseType";
    public static final String MSG_RESPONSE = "MsgResponse";
    public static final String APP_LIST = "ApplicationList";
    public static final String APP_SUMMARY = "ApplicationSummary";
    public static final String APP_DETAILS = "ApplicationDetails";
    public static final String INSTALL_APP = "InstallApplication";
    public static final String INSTALL_ALL_APP = "InstallAllApplication";
    public static final String UPDATE_APP = "UpdateApplication";
    public static final String UPDATE_ALL_APP = "UpdateAllApplication";
    public static final String INSTALLATION_STATUS_UPDATE = "InstallationStatusUpdate";
    public static final String VPP_REGISTERED_USER = "VppRegisteredUser";
    public static final String USER_AGENT = "userAgent";
    public static final String SCHEDULE_APP_CATALOG_SYNC = "ScheduleAppCatalogSync";
    public static final String APP_CATALOG_SYNC_STATUS = "AppCatalogSyncStatus";
    public static final String APP_LIST_RESPONSE = "ApplicationListResponse";
    public static final String APP_SUMMARY_RESPONSE = "ApplicationSummaryResponse";
    public static final String APP_DETAILS_RESPONSE = "ApplicationDetailsResponse";
    public static final String INSTALL_APP_RESPONSE = "InstallApplicationResponse";
    public static final String UPDATE_APP_RESPONSE = "UpdateApplicationResponse";
    public static final String INSTALLATION_STATUS_UPDATE_RESPONSE = "InstallationStatusUpdateResponse";
    public static final String VPP_REGISTERED_USER_RESPONSE = "VppRegisteredUserResponse";
    public static final String APP_CATALOG_SYNC_STATUS_RESPONSE = "AppCatalogSyncStatusResponse";
    public static final String FILTER_BY_APP_STATUS = "FilterByAppStatus";
    public static final String FILTER_BY_APP_NAME = "FilterByAppName";
    public static final String FILTER_BY_APP_BUNDLE_IDENTIFIER = "FilterByAppBundleIdentifier";
    public static final String REQUIRE_SUMMARY_INFO = "RequireSummaryInfo";
    public static final String SORT_BY_COLUMN = "SortByColumn";
    public static final String SORT_BY_DESCENDING = "SortByDescending";
    public static final String REQUIRE_VPP_REGISTERED_USER = "RequireVppRegisteredUser";
    public static final String EXCLUDE_KEYS_LIST = "ExcludeKeysList";
    public static final String APPLICATION_COUNT = "ApplicationCount";
    public static final String MANAGED_APPS = "ManagedApps";
    public static final String APP_CATALOG_SUMMARY = "AppCatalogSummary";
    public static final String LAST_SYNC_TIME = "LastSyncTime";
    public static final String ENROLLMENT_REQ_ID = "EnrollmentReqId";
    public static final String YET_TO_INSTALL = "YetToInstall";
    public static final String INSTALLING = "Installing";
    public static final String INSTALLED = "Installed";
    public static final String REMOVED = "Removed";
    public static final String REMOVE_CANCELLED = "RemoveCancelled";
    public static final String INSTALLED_NOT_MANAGED = "InstalledNotmanaged";
    public static final String IS_VPP_USER_ASSIGNMENT_NEEDED = "IsVppUserAssignMentNeeded";
    public static final String INVITATION_URL = "InvitationUrl";
    public static final String APP_IMAGE_BACKGROUND = "AppImageBackground";
    public static final Map<String, String> STATUS_CONSTANT_MAP = new HashMap<String, String>() {
        {
            this.put("YetToInstall", "0");
            this.put("Installing", "1");
            this.put("Installed", "2");
            this.put("Removed", "3");
            this.put("RemoveCancelled", "4");
            this.put("InstalledNotmanaged", "5");
            this.put("0", "YetToInstall");
            this.put("1", "Installing");
            this.put("2", "Installed");
            this.put("3", "Removed");
            this.put("4", "RemoveCancelled");
            this.put("5", "InstalledNotmanaged");
            this.put("6", "InstalledNotmanaged");
        }
    };
    public static final Map<String, String> PLATFORM_MAP = new HashMap<String, String>() {
        {
            this.put(String.valueOf(1), "IOS");
            this.put(String.valueOf(3), "WINDOWSPHONE");
            this.put(String.valueOf(2), "ANDROID");
        }
    };
    public static final Map<String, String> KEY_TO_DB_COLUMN_NAME_MAP = new HashMap<String, String>() {
        {
            this.put("AppId", "PUBLISHED_APP_ID");
            this.put("AppName", "APP_NAME");
            this.put("AppVersion", "APP_VERSION");
            this.put("AppStatus", "STATUS");
            this.put("AppIdentifier", "IDENTIFIER");
            this.put("AppCategory", "APP_CATEGORY_NAME");
            this.put("AppCategoryKey", "APP_CATEGORY_LABEL");
            this.put("AppUpdatedTime", "UPDATED_AT");
            this.put("AppCollectionStatus", "CollnToResAlias.STATUS");
        }
    };
    public static final List<String> MANAGED_APPS_KEY_LIST = new ArrayList<String>() {
        {
            this.add("AppId");
            this.add("AppName");
            this.add("AppVersion");
            this.add("AppStatus");
            this.add("AppIdentifier");
            this.add("AppCategory");
            this.add("AppCategoryKey");
            this.add("AppUpdatedTime");
            this.add("AppType");
            this.add("IsMarkedForDelete");
            this.add("AppAction");
            this.add("IsInstalled");
            this.add("AppIconImageUrl");
            this.add("IsVppLicensed");
            this.add("isAppUserAssignable");
            this.add("AppDescription");
            this.add("AppCollectionStatus");
        }
    };
    public static final String APP_NAME = "AppName";
    public static final String APP_VERSION = "AppVersion";
    public static final String APP_STATUS = "AppStatus";
    public static final String APP_COLLECTION_STATUS = "AppCollectionStatus";
    public static final String APP_DESCRIPTION = "AppDescription";
    public static final String APP_ICON_IMAGE_URL = "AppIconImageUrl";
    public static final String APP_FILE_URL = "AppFileUrl";
    public static final String APP_IDENTIFIER = "AppIdentifier";
    public static final String APP_CATEGORY = "AppCategory";
    public static final String APP_CATEGORY_KEY = "AppCategoryKey";
    public static final String APP_TYPE = "AppType";
    public static final String APP_SCOPE = "AppScope";
    public static final String APP_ID = "AppId";
    public static final String APP_ACTION = "AppAction";
    public static final String APP_PACKAGE_NAME = "AppPackageName";
    public static final String APP_VERSION_CODE = "AppVersionCode";
    public static final String APP_UPDATED_TIME = "AppUpdatedTime";
    public static final String IS_PAID_APP = "IsPaidApp";
    public static final String IS_INSTALLED = "IsInstalled";
    public static final String IS_MARKED_FOR_DELETE = "IsMarkedForDelete";
    public static final String IS_VPP_LICENSED = "IsVppLicensed";
    public static final String IS_APP_USER_ASSIGNABLE = "isAppUserAssignable";
    public static final String IS_SCHEDULED_APP_INSTALLATION_NEEDED = "IsScheduledAppInstallationNeeded";
    public static final int FREE_APP_PACKAGE_TYPE = 0;
    public static final int PAID_APP_PACKAGE_TYPE = 1;
    public static final int ENTERPRISE_APP_PACKAGE_TYPE = 2;
    public static final String STORE_APP = "StoreApp";
    public static final String ENTERPRISE_APP = "EnterpriseApp";
    public static final String APP_REMOVAL_ACTION = "Remove";
    public static final String APP_INSTALL_ACTION = "Install";
    public static final String APP_UPDATE_ACTION = "Update";
    public static final int INSTALLED_APP_STATUS = 2;
    public static final String TOTAL = "Total";
    public static final String FREE = "Free";
    public static final String PAID = "Paid";
    public static final String CATEGORY_NAME = "CategoryName";
    public static final String CATEGORY_KEY = "CategoryKey";
    public static final String CATEGORY_APP_COUNT = "CategoryAppCount";
    public static final String IOS = "IOS";
    public static final String ANDROID = "ANDROID";
    public static final String WINDOWSPHONE = "WINDOWSPHONE";
    public static final String CHECKSUM = "Checksum";
    public static final String APP_CATALOG_SYNCTIME = "SyncTime";
    public static final String APP_CATALOG_SYNCSTATUS = "SyncStatus";
}
