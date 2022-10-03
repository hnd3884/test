package com.me.devicemanagement.onpremise.server.metrack;

import java.io.File;
import java.util.Properties;

public interface MEDMTracker
{
    public static final String LOCAL_DNS_LIST = "csez.zohocorpin.com,tsi.zohocorpin.com,ru.zohocorpin.com";
    public static final String MAIL_SERVER_CONFIGURED = "isMailServerConfigured";
    public static final String IS_TLS_ENABLED = "isTLSEnabled";
    public static final String IS_SMTPS_ENABLED = "isSMTPSEnabled";
    public static final String IS_MAIL_AUTH_ENABLED = "isAuthEnabled";
    public static final String MAIL_SERVER_DETAILS = "MailServer_Details";
    public static final String MAIL_SERVER_NAME = "MailServer_Name";
    public static final String AUTH_TYPE = "authType";
    public static final String BASIC_AUTHENTICATION = "Basic";
    public static final String OAUTH_AUTHENTICATION = "OAuth";
    public static final String LOG = "METrackLog";
    public static final String BASE_OS = "OS";
    public static final String BASE_RAM = "Ram";
    public static final String BASE_DID = "did";
    public static final String BASE_DID_STATUS = "DID_Status";
    public static final String BASE_DC_SERVER_DETAILS = "DCServer_Details";
    public static final String GENERAL_MACHINE = "general";
    public static final String AMAZON_MACHINE = "amazon_virtual";
    public static final String AZURE_MACHINE = "azure_virtual";
    public static final String WORKGROUP = "Workgroup";
    public static final String LANGUAGE = "Language";
    public static final String LOGIN = "Login_Count";
    public static final String LAST_LOGIN = "Last_Login";
    public static final String BUILD_NO = "Build_Number";
    public static final String INSTALLATION_TIME = "Installation_Time";
    public static final String INSTALLATION_TIMESTAMP = "Installation_Timestamp";
    public static final String OS_NAME = "OS_Name";
    public static final String SP_VERSION = "SP_Version";
    public static final String OS_VERSION = "OS_Version";
    public static final String OS_ARCH = "OSArch";
    public static final String JRE_VERSION = "JRE_Version";
    public static final String ANTI_VIRUS_NAMES = "AV";
    public static final String IS_SPECIFIC_IP_ENABLED = "IsSpecificIPEnabled";
    public static final String JRE_ARCH = "JREArch";
    public static final String DC_ARCH_MIGRATION = "64bitArchMigration";
    public static final String APACHE_VERSION = "Apache_Version";
    public static final String INSTALL_BUILD_NUM = "Install_Build_Num";
    public static final String IS_ADDON = "Is_Addon";
    public static final String DB_DETAILS = "DataBase_Details";
    public static final String MAINTENANCE_DETAILS = "Maintenance_Details";
    public static final String DB_SIZE = "DBSizeInMB";
    public static final String DB_VERSION = "DBVersion";
    public static final String DBMIGRATIONDTLS = "DBMigrationDtls";
    public static final String DB_BACKGROUND_DATA_DELETION = "Db_Background_Data_Deletion";
    public static final int HPROF_FILES = 1;
    public static final int PID_FILES = 2;
    public static final int DBLOCK_FILES = 3;
    public static final String DEBUG_DETAILS = "Debug_Details";
    public static final String IS_DEBUG_LOG_UPLOAD_ENABLED = "IsDebugLogUploadEnabled";
    public static final String LATEST_DEBUG_LOG_UPLOAD_STATUS = "LatestDebugLogUploadStatus";
    public static final String FSM_CORRUPTED_TABLE_DETAILS = "FSMCorruptedTableDetails";
    public static final String FSM_CORRUPTED_TABLE_UNIQUECOUNT = "FSMCorruptedTableUniqueCount";
    public static final String FSM_CORRUPTED_TABLE_COUNT = "FSMCorruptedTableCount";
    public static final String FSM_CORRUPTED_DETAILS = "FSMCorruptionDetails";
    public static final int FSM_CORRUPTED_TABLE_UPLOAD_RANGE = 5;
    public static final String NUMBER_OF_LIVE_CONNECTIONS = "NumberOfLiveConnections";
    public static final String IS_REMOTE_POSTGRES = "isRemoteDatabase";
    public static final String MSSQL_CONNECTION_TYPE = "MssqlConnectionType";
    public static final String PRODUCT_CONNECTION_TYPE = "ProductConnectionType";
    public static final String DB_ARCH = "DBArch";
    public static final String PGSQL_MAX_MEMORY = "PgSQLMaxMem";
    public static final String PGSQL_MAX_MEM_LAST_MODIFIED_TIME = "PgSQLMemModTime";
    public static final String PGSQL_DB_TUNING_ENABLED = "PgSQLDbTuned";
    public static final String TOTAL_DBLOCK = "TotalDBLock";
    public static final String DBLOCK_IN_LASTMONTH = "DBLockInLastMonth";
    public static final String DBLOCK_LAST_DETECTED_TIME = "DBLockLastDetectedTime";
    public static final String MAX_DBLOCKED_TIME = "MaxDBLockedTimeinms";
    public static final String MAX_DBLOCKED_QUERY_COUNT = "MaxDBLockedQueryCount";
    public static final String MAX_THREADS_BLOCKED = "MaxThreadsBlockedCount";
    public static final String DB_LOCKS_BUILDWISE = "DBLocksBuildwise";
    public static final String UPDMGR_TRACKER_JSONFILE = System.getProperty("server.home") + File.separator + "Patch" + File.separator + "UpdMgrTracker.json";
    public static final String PPM_DETAILS = "PPM_Details";
    public static final String AMS_EXPIRY_STATUS_FOR_PPM = "AMSExpiryStatusForPPM";
    public static final String AMS_EXPIRED_PPM_COUNT = "AMSExpiredPPMCount";
    public static final String QPM_TRACING = "qpmTracing";
    public static final String IS_FOS_ENABLED = "FosStatus";
    public static final String IS_FOS_TRIALED = "IsFosTrialed";
    public static final String FOS_TRAIL_VALIDITY = "FosTrialValidity";
    public static final String FOS_TAKEOVER = "TakeOverCount";
    public static final String TAKEOVER_TIME_BY_SLAVE = "takeOverTimeBySlave";
    public static final String IS_FOS_CONFIGURED_BEFORE = "isFOSConfiguredBefore";
    public static final String FOS_CONFIGURED_COUNT = "fosConfiguredBeforeCount";
    public static final String FOS_LAST_TAKEOVER_TIME = "LatestFosTakeOverTime";
    public static final String FOS_DETAILS = "FOS_Details";
    public static final String REPLICATION_ERRORS = "FOS_Replication_Errors";
    public static final String IS_DB_IN_SAME_NETWORK = "IsDBInSameNetwork";
    public static final String IS_FOS_IN_SAME_NETWORK = "IsFOSInSameNetwork";
    public static final String CURRENTLY_SERVING_NODE = "CurrentServingNode";
    public static final String IS_FOS_CONFIGURED = "IsFosConfigured";
    public static final String SERVER_DETAILS = "Server_Details";
    public static final String SERVER_RAM_MEMORY = "ServerRAMMem";
    public static final String PPM_INSTALLED_DATE_IN_LONG = "PPM_Installed_Date_In_Long";
    public static final String PPM_INSTALLATION_DETAILS = "PPM_Installation_Details";
    public static final String HPROF_COUNT = "Hprof_Count";
    public static final String HPROF_LAST_MODIFIED_TIME = "Hprof_Last_Modified_Time";
    public static final String HPROF_COUNT_BUILDWISE = "HprofCountBuildWise";
    public static final String PID_COUNT = "PID_Count";
    public static final String PID_LAST_MODIFIED_TIME = "PID_Last_Modified_Time";
    public static final String THIRDPARTY_SSL_ENABLED = "Thirdparty_SSL_Enabled";
    public static final String USING_INTERMEDIATE_CERT = "Using_Intermediate_Cert";
    public static final String PROXY_DETAILS = "Proxy_Details";
    public static final String SELF_SIGNED_CA_ENABLED = "Self_Signed_CA_Enabled";
    public static final String SSL_PROVIDER = "SSL_Provider";
    public static final String LOGIN_COUNT = "TotUserLoginCount";
    public static final String LAST_LOGIN_AT = "LstLoginAt";
    public static final String TWO_FACTOR_AUTH = "TwoFactorAuth";
    public static final String LOCALIZATION_DETAILS = "Localization_Details";
    public static final String NON_ENGLISH_USER = "NonEngUser";
    public static final String ENGLISH_USER = "English_User";
    public static final String CHINESE_USER = "Chinese_User";
    public static final String JAPANESE_USER = "Japanese_User";
    public static final String GERMAN_USER = "German_User";
    public static final String FRENCH_USER = "French_User";
    public static final String PRODUCT_MODE = "Product_Mode";
    public static final String FLASH_UPDS_LAST_CHECKED_AT = "FlashUpdsLastChkdAt";
    public static final String SPICEWORKS_ACCESS_COUNT = "SPW_ACCESS_COUNT";
    public static final String CHECK_FOR_UPDATES_INFO = "CheckforUpdatesInfo";
    public static final String BUILD_NUMBER_CLICKS = "noOfBuildNoClicks";
    public static final String USER_CLICKS = "noOfUserClicks";
    public static final String MSG_DISPLAYED = "msgDisplayedtoUser";
    public static final String GDPR_DASHBOARD_SUMMARY = "GDPR_Dashboard_Summary";
    public static final String FS_DETAILS = "FsDetails";
    public static final String FS_BUILD_NUMBER = "FsBuildNumber";
    public static final String FS_TCP_ENABLED = "FsTcpEnabled";
    public static final String FS_WSS_ENABLED = "FsWssEnabled";
    public static final String FS_FTP_ENABLED = "FsFtpEnabled";
    public static final String FS_IS_PUBLIC_IP = "FsIsPublicIP";
    public static final String FS_REACHABLE = "FsReachable";
    public static final String FS_CONFIGURED = "FsConfigured";
    public static final String FS_TRIALED = "isFsTrialed";
    public static final String FS_TRIAL_VALIDITY = "FsTrialValidity";
    public static final String FS_TRIAL_EMAIL = "FsTrialUserEmail";
    public static final String STSTOOL_DETAILS = "STSTool_details";
    public static final String SILENT_UPDATE_TRACK_DETAILS = "SilentUpdateDetails";
    public static final String SU_AUTO_APPROVE_ENABLED = "SUAutoApproveEnabled";
    public static final String SU_EXPORT_FAILED_REQUESTS = "SUExportFailReq";
    public static final String SU_QPPM_DOWNLOAD_FAILED_STATUS = "QPPMDownldFailDtls";
    public static final String SU_QPPM_DOWNLOAD_FAILED_STATUS_TOTLCUNT = "QPPMDownldFailDtlsTotlCunt";
    public static final String SU_DYNAMIC_CHECKER_DOWNLOAD_FAILED_STATUS = "DynamicCheckerDownldFailDtls";
    public static final String SU_DYNAMIC_CHECKER_DOWNLOAD_FAILED_STATUS_TOTLCUNT = "DynamicCheckerDownldFailDtlsTotlCunt";
    public static final String SU_REMINDME_LATTER_QPPMS = "SUReminderMELatter";
    public static final String SU_REMINDME_LATTER_QPPMS_TOTLCUNT = "SUReminderMELatterTotlCunt";
    public static final String SU_IGNORE_THE_QPPMS = "SUIgnoreTheQPPMs";
    public static final String SU_IGNORE_THE_QPPMS_TOTLCUNT = "SUIgnoreTheQPPMsTotlCunt";
    public static final String SU_DISMISS_QPPMS = "SUDismissQPPMs";
    public static final String SU_DISMISS_QPPMS_TOTLCUNT = "SUDismissQPPMsTotlCunt";
    public static final String IS_VALID_CUSTOMER = "IS_Valid_Customer";
    public static final String IS_VALID_REMARKS = "IS_Valid_Remarks";
    public static final String USE_OLD_IS_VALID_METHOD = "UseOldIsValidMethod";
    public static final String IS_VALID_METHOD = "IsValidMethod";
    public static final String OS_COUNTRY = "OS_Country";
    public static final String COUNTRY_BASED_ON_USER_PERSONALIZATION = "Usr_Personalize_Country";
    
    Properties getTrackerProperties();
}