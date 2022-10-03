package com.me.mdm.server.common;

public class I18NKeyConstant
{
    public static class profileRemarkCodes
    {
        public static final String IOS_WALLPAPER_ERROR_IMAGE = "mdm.profile.wallpaper.error.invalidResponseImage";
        public static final String IOS_WALLPAPER_ERROR_NOTSUPERVISED = "mdm.profile.wallpaper.error.notSupervised";
        public static final String IOS_PASSCODE_DISABLE_ERROR = "mdm.profile.passcode.error.disabled";
        public static final String IOS_PASSCODE_DISABLE_CLEAR_PASSCODE_ERROR = "mdm.profile.passcode.error.clearFailed";
        public static final String IOS_PER_APP_VPN_APP_NOT_MANAGED = "mdm.profile.perappVPN.error";
        public static final String IOS_NO_USER_ACCOUNT_ERROR = "mdm.ios.user.account.not.exist";
        public static final String IOS_HOME_SCREEN_FAILURE = "mdm.profile.ios.kiosk.homescreen_multiple_failure";
        public static final String IOS_WEB_SHORTCUT_RETRY = "mdm.profiles.ios.kiosk.single_web_app_retry";
        public static final String IOS_WEB_SHORTCUT_FAILURE = "mdm.profiles.ios.kiosk.single_web_app_error";
        public static final String IOS_APP_NOTIFICATION_SETTINGS_NOT_APPLICABLE = "mdm.profile.ios.appnotificationpolicy.not_applicable";
        public static final String IOS_PROFILE_INSTALLATION_ERROR = "mdm.profile.ios.profile_installation_payload";
        public static final String GENERAL_ERROR_COMMANDFORMAT = "dc.db.mdm.scanStaus.command_format_wrong";
        public static final String GENERAL_SUCCESS_MSG = "dc.db.mdm.collection.Successfully_applied_policy";
        public static final String GENERAL_WAITINGFORDEVICE = "mdm.profile.distribution.waitingfordeviceinfo";
        public static final String GENERAL_SUCCESS_REMOVE_PROFILE = "dc.db.mdm.collection.Successfully_removed_the_policy";
        public static final String GENERAL_WAITING_FOR_REMOVAL = "mdm.collection.waiting_for_device_unlock_remove";
        public static final String PASSCODE_DISABLE_PRIVACY_ERROR = "mdm.profile.passcode.error.privacy_error";
        public static final String IOS_KIOSK_APP_FAILED = "mdm.profile.ios.kiosk.appFailed";
        public static final String IOS_KIOSK_USER_BASED = "mdm.profile.ios.kiosk.userBasedApps";
        public static final String IOS_KIOSK_AUTOMATE_APP = "mdm.profile.ios.kiosk.automateApp";
        public static final String IOS_KIOSK_REDISTRIBUTE_PROFILE = "mdm.profile.ios.kiosk.redistributeProfile";
        public static final String IOS_KIOSK_APP_NOT_AVAILBALE = "mdm.profile.ios.kiosk.appnotAvailable";
        public static final String IOS_KIOSK_APP_NOT_USER_BASED = "mdm.profile.ios.kiosk.noappUserBased";
        public static final String IOS_KIOSK_APP_DISASSOCIATE = "dc.mdm.kiosk.conflicting.kiosk.payload";
        public static final String IOS_MULTIPLE_KIOSK_PAYLOAD = "dc.mdm.kiosk.error.msg.multiple.kiosk.payloads";
        public static final String IOS_IDENTICAL_PROFILE_ALREADY_EXIST = "dc.mdm.identical_profile_already_exist";
        public static final String IOS_APN_ALREADY_EXIST = "mdm.profile.apn_already_exist_error_msg";
        public static final String IOS_SUPERVISED_DEVICE_ONLY = "mdm.profile.ios.supervised.only";
        public static final String MAC_FILEVAULT_LICENSE_NOT_APPLICABLE = "mdm.profile.filevault_not_applicable_edition";
        public static final String IOS_LOCK_WALL_FAILED_UNSUPERVISED = "mdm.profile.lockscreen.wallunsupervised";
        public static final String IOS_LOCK_SCREEN_FAILED = "mdm.profile.lockscreen.failed";
        public static final String IOS_LOCK_SCREEN_IMAGE_FAILED = "mdm.profile.lockscreen.imagefailed";
        public static final String IOS_LOCK_SCREEN_IMAGE_PAYLOAD_PUBLISH_FAILED = "mdm.profile.lockscreen.publish.failed";
        public static final String MAC_AD_BIND_FAILED = "mdm.profile.directory.binding.failed";
    }
    
    public static class appsRemarkCodes
    {
        public static final String APPS_NOT_SUPPORTED_TAB = "dc.mdm.device_mgmt.app_not_supported_for_tablets";
        public static final String APPS_NOT_SUPPORTED_PHONE = "dc.mdm.device_mgmt.app_not_supported_for_smartphone";
        public static final String APPS_NOT_SUPPORTED_DEVICE = "mdm.windows.app.no_compatible_package";
        public static final String ADHOC_APP_NOT_SUPPORTED_DEVICE = "dc.mdm.devicemgmt.device_not_registered_for_adhoc";
        public static final String APP_EXPIRED = "dc.mdm.devicemgmt.app_expired_distribution_error";
        public static final String APPS_AUTOMATIC_INSTALL = "dc.db.mdm.apps.status.Installing";
        public static final String KIOSK_APP_NOT_INSTALL = "mdm.apps.ios.kiosk.installApp";
        public static final String KIOSK_APP_UPDATE_FAILURE = "mdm.apps.ios.kiosk.updateAppFailure";
        public static final String KIOSK_APP_UPDATE_PROFILE_FAILURE = "mdm.apps.ios.kiosk.updateProfileFailure";
        public static final String KIOSK_APP_ALREADY_INSTALLED = "mdm.apps.ios.kioskUpdateNote";
        public static final String KIOSK_APP_REMOVE_NOTALLOWED = "mdm.apps.ios.kioskRemove_notAllowed";
        public static final String KIOSK_APP_NOTAPPLICABLE = "mdm.apps.ios.kiosk_silent_notApplicable";
        public static final String APP_INSTALL_SUCCESS = "dc.db.mdm.collection.Successfully_installed_the_app";
        public static final String APP_ALREADY_INSTALLED_SUCCESS = "dc.db.mdm.collection.App_already_installed";
        public static final String KIOSK_APP_UPDATING_REMOTELOCK_NOTE = "mdm.apps.ios.kioskApp_updating_remoteLock";
        public static final String APPS_INSTALL_NOTIFICATION_SENT = "dc.db.mdm.apps.status.automatic_install";
        public static final String APPS_UPDATE_NOTIFICATION_SENT = "dc.db.mdm.apps.status.automatic_update";
    }
    
    public static class profileKeys
    {
        public static final String RESTRICTION = "mdm.profile.ios.restriction";
        public static final String WALLPAPER = "dc.conf.dispConf.wallpaper";
        public static final String KIOSK = "dc.mdm.profile.android.kiosk";
        public static final String LOCKSCREEN = "mdm.profile.assetTagging";
        public static final String PASSCODE = "dc.mdm.enroll.passcode";
        public static final String IOS_ACCESSIBILITY_SETTINGS = "mdm.profile.ios.accessibility_settings";
    }
    
    public static class multiVersionKeys
    {
        public static final String PRODUCTION_APP = "mdm.db.appmgmt.release_label_prod";
        public static final String BETA_APP = "mdm.db.appmgmt.release_label_beta";
        public static final String APP_ALREADY_DISTRIBUTED_TO_TARGET = "mdm.appmgmt.app_already_distributed";
        public static final String APP_MARKED_AS_STABLE = "mdm.evt.appmgmt.app_merged";
        public static final String STABLE_APP_AUTO_DISTRIBUTED = "mdm.evt.appmgmt.merged_app_auto_distributed";
        public static final String NEW_CHANNEL_CREATED = "mdm.evt.appmgmt.channel_created";
    }
    
    public static class osupdate
    {
        public static final String OSUPDATE_TAKING_MORE_TIME = "mdm.db.osupdate.download_take_more_time";
        public static final String OSUPDATE_DOWNLOADING = "mdm.db.osupdate.update_downloading";
        public static final String OSUPDATE_INSTALLING = "mdm.db.osupdate.update_installing";
        public static final String OSUPDATE_UNSUPERVISED_DEVICE = "mdm.db.osupdate.unsupervised_device";
        public static final String OSUPDATE_ASSOCIATED = "mdm.db.osupdate.notification_sent";
        public static final String OSUPDATE_INITIATING_NEXT_VERSION = "mdm.db.osupdate.updating_next_version";
    }
}
