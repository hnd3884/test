package com.me.mdm.server.apps;

public class AppCatalogConstants
{
    public static final int YET_TO_INSTALL_APP_STATUS = 0;
    public static final int INSTALLING_APP_STATUS = 1;
    public static final int INSTALLED_APP_STATUS = 2;
    public static final int REMOVED_APP_STATUS = 3;
    public static final int REMOVE_CANCELLED_APP_STATUS = 4;
    public static final int INSTALLED_NOT_MANAGED = 5;
    public static final int MANAGEMENT_FAILED = 6;
    public static final int FAILED = 7;
    public static final String INSTALLING_APP_REMARK = "dc.db.mdm.apps.status.Installing";
    public static final String INSTALLED_APP_REMARK = "dc.db.mdm.collection.Successfully_installed_the_app";
    public static final String APP_ALREADY_INSTALLED_REMARK = "dc.db.mdm.collection.App_already_installed";
    public static final String APP_REMOVED_REMARK = "dc.db.mdm.collection.Successfully_removed_the_app";
    public static final String MANAGED_APP_UNINSTALLED_REMARK = "dc.db.mdm.apps.status.ManagedButUninstalled";
    public static final String USER_CANCELLED_REMOVE_REMARKS = "dc.db.mdm.collection.Successfully_user_remove_cancell_the_app";
    public static final String APP_DOWNLOAD_SCHEDULED_FOR_RETRY = "mdm.app.scheduled_for_retry";
    public static final String APP_INSTALLATION_IN_AGENT_QUEUE = "mdm.app.Installation_in_agent_queue";
    public static final String APP_DOWNLOAD_FAILED = "mdm.agent.download.failed.unknownerror";
    public static final String APP_INSTALLATION_FAILED = "mdm.agent.App_installation_failed_failure";
    public static final String SPLIT_APK_INSTALLED_REMARKS = "mdm.app.installed.applicable_version";
    public static final String APP_TO_BE_UPDATED_REMARKS = "dc.db.mdm.apps.status.UpgradeApp";
    public static final String APP_UPDATE_INCOMPATIBLE = "dc.db.mdm.apps.status.NotUpgradable";
    public static final String APP_UPDATE_INCOMPATIBLE_GENERIC = "dc.db.mdm.apps.status.NotUpgradable_Generic";
    public static final String APP_INSTALLATION_FAILED_ABORTED = "mdm.agent.App_installation_failed_aborted";
    public static final String APP_INSTALLATION_FAILED_CONFLICT = "mdm.agent.App_installation_failed_conflict";
    public static final String APP_INSTALLATION_FAILED_INCOMPATIBLE = "mdm.agent.App_installation_failed_incompatible";
    public static final String APP_INSTALLATION_FAILED_INVALID = "mdm.agent.App_installation_failed_invalid";
    public static final String APP_INSTALLATION_FAILED_STORAGE = "mdm.agent.App_installation_failed_storage";
    public static final String APP_INSTALLATION_FAILED_BLOCKED_BY_PLAYPROTECT = "mdm.agent.App_installation_blocked_by_playprotect";
    public static final String APP_INSTALLATION_FAILED_VERSION_CODE_MISMATCH = "mdm.agent.installation_failed_versioncode_mismatch";
    public static final String APP_INSTALLATION_FAILED_MIUI_OPTIMIZATION = "mdm.agent.installation_failed_miui_optimization";
    public static final String APP_DOWNLOAD_FAILED_NETWORK_UNREACHABLE = "mdm.agent.download.failed.networkunreachable";
    public static final String APP_DOWNLOAD_FAILED_SOCKET_TIMEOUT = "mdm.agent.download.failed.sockettimeout";
    public static final String APP_DOWNLOAD_FAILED_UNKNOWN_HOST = "mdm.agent.download.failed.unknownhost";
    public static final String APP_DOWNLOAD_FAILED_MALFORMED_URL = "mdm.agent.download.failed.malformedurl";
    public static final String APP_DOWNLOAD_FAILED_SSL_HANDSHAKE = "mdm.agent.download.failed.sslhandshake";
    public static final String APP_DOWNLOAD_FAILED_FILE_NOT_FOUND_EXCEPTION = "mdm.agent.download.failed.filenotfoundexception";
    public static final String APP_DOWNLOAD_FAILED_FILE_NOT_FOUND = "mdm.agent.download.failed.filenotfound";
    public static final String APP_DOWNLOAD_FAILED_INVALID_FILE_FORMAT = "mdm.agent.download.failed.invalidfile";
}
