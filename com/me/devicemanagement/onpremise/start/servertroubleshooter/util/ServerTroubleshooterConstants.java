package com.me.devicemanagement.onpremise.start.servertroubleshooter.util;

import java.io.File;

public class ServerTroubleshooterConstants
{
    public static final String SERVICES_RUNNING_DIFFERENT_LOCATION = "service_running_diff_location";
    public static final String DISK_SPACE_NOT_AVAILABLE = "disk_space_low";
    public static final String PRODUCT_ARCH_INCOMPATIBLE = "arch_incompatible";
    public static final String DB_PORT_IN_USE = "db_port_in_use";
    public static final String SERVICE_RUNNING_BY_USER_ACCOUNT = "service_running_by_user_account";
    public static final String PGSQL_UNKNOWN_CASES = "pgsql_startup_failure_unknown_case";
    public static final String SQL_SERVER_INVALID_CREDENTIAL = "mssql_invalid_login_credentials";
    public static final String SQL_SERVER_DISK_SIZE_AND_TRANSACTION_LOG_SIZE = "mssql_transaction_log_size_exceed";
    public static final String SQL_CONNECTION_LOST = "mssql_connection_lost";
    public static final String SQL_CONNECTION_RETRY = "mssql_connection_retry";
    public static final String PPM_LOCK = "ppm_lock";
    public static final String BUILD_NUMBER_INCOMPATIBLE = "build_number_incompatible";
    public static final String REVERT_LOCK = "revert_lock";
    public static final String MIGRATION_LOCK = "migration_lock";
    public static final String FILE_DB_MISMATCH = "file_db_mismatch";
    public static final String WEBSERVER_PORT_IN_USE = "webserver_port_in_use";
    public static final String SOFTWARE_OR_PATCH_STORE_NOT_REACHABLE = "software_or_patch_store_not_reachable";
    public static final String OSD_REPO_NOT_REACHABLE = "osd_repo_not_reachable";
    public static final String SOFTWARE_NOT_REACHABLE = "software_not_reachable";
    public static final String PATCH_NOT_REACHABLE = "patch_not_reachable";
    public static final String SSL_CERTIFICATE_MISSING = "ssl_certificate_missing";
    public static final String SSL_CERTIFICATE_KEY_MISMATCH = "ssl_certificate_key_mismatch";
    public static final String SSL_CERTIFICATE_ENCRYPTED_KEY_ERROR = "ssl_certificate_key_encrypted";
    public static final String SSL_CERTIFICATE_CHAIN_ISSUE = "ssl_certificate_chain_issue";
    public static final String PGSQL_IN_RECOVERY = "pgsql_in_recovery_mode";
    public static final String PGSQL_FAILS_IN_RECOVERY = "pgsql_fails_in_recovery_mode";
    public static final String REMOTE_PGSQL_NOT_COMPATIBLE = "remote_pg_not_compatible";
    public static final long MINIMUM_HARD_DISK_SPACE_REQUIRED = 524288000L;
    public static final String STARTUP_INFO_CONF_FILE_PATH;
    public static final String FAILURE_METRACK_POST_BAT_FILE_PATH;
    
    static {
        STARTUP_INFO_CONF_FILE_PATH = "conf" + File.separator + "METracking" + File.separator + "startupinfo.conf";
        FAILURE_METRACK_POST_BAT_FILE_PATH = "bin" + File.separator + "ServerStatusUpdaterMetrack.bat";
    }
}
