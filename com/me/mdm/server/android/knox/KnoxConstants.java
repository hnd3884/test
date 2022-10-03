package com.me.mdm.server.android.knox;

public class KnoxConstants
{
    public static final String KNOX_VERSION = "KnoxVersion";
    public static final String KNOX_APP_PREFIX = "sec_container_1.";
    public static final int KNOX_VERSION_1_0 = 1;
    public static final int KNOX_VERSION_2_0 = 2;
    public static final String KNOX_COMMAND_CREATE_CONTAINER = "CreateContainer";
    public static final String KNOX_COMMAND_REMOVE_CONTAINER = "RemoveContainer";
    public static final String KNOX_COMMAND_LOCK_CONTAINER = "ContainerLock";
    public static final String KNOX_COMMAND_UNLOCK_CONTAINER = "ContainerUnlock";
    public static final String KNOX_COMMAND_CLEAR_CONTAINER_PASSWORD = "ClearContainerPasscode";
    public static final String KNOX_COMMAND_APPLY_LICENSE = "ActivateKnoxLicense";
    public static final String KNOX_COMMAND_REVOKE_LICENSE = "DeactivateKnoxLicense";
    public static final String KNOX_COMMAND_MIGRATE_APP_TO_CONTAINER = "MigrateAppToContainer";
    public static final String KNOX_COMMAND_ACTIVATE_KNOX = "ActivateKnox";
    public static final String KNOX_COMMAND_DEACTIVATE_KNOX = "DeactivateKnox";
    public static final String COMMAND_GET_KNOX_AVAILABILITY_UPGRADE = "GetKnoxAvailabilityUpgrade";
    public static final String COMMAND_GET_KNOX_AVAILABILITY_ENROLLMENT = "GetKnoxAvailabilityEnrollment";
    public static final String COMMAND_GET_KNOX_AVAILABILITY = "GetKnoxAvailability";
    public static final int KNOX_COMMAND_CREATE_CONTAINER_ID = 11;
    public static final int KNOX_COMMAND_REMOVE_CONTAINER_ID = 12;
    public static final int KNOX_COMMAND_LOCK_CONTAINER_ID = 13;
    public static final int KNOX_COMMAND_UNLOCK_CONTAINER_ID = 14;
    public static final int KNOX_COMMAND_CLEAR_CONTAINER_PASSWORD_ID = 15;
    public static final int KNOX_COMMAND_APPLY_LICENSE_ID = 16;
    public static final int KNOX_COMMAND_REVOKE_LICENSE_ID = 17;
    public static final int KNOX_COMMAND_ACTIVATE_KNOX_ID = 18;
    public static final int KNOX_COMMAND_DEACTIVATE_KNOX_ID = 19;
    public static final int DC_MDM_ANDROID_KNOX_LICENSE_STATUS_DISTRIBUTED = 1;
    public static final int DC_MDM_ANDROID_KNOX_LICENSE_STATUS_YET_TO_DISTRIBUTED = 2;
    public static final int DC_MDM_ANDROID_KNOX_LICENSE_STATUS_ASSOCIATED = 3;
    public static final int DC_MDM_ANDROID_KNOX_LICENSE_STATUS_FAILED = 4;
    public static final int DC_MDM_ANDROID_KNOX_LICENSE_STATUS_REVOKED = 5;
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_DISTRIBUTED = "dc.mdm.android.knox.license.remarks.distributed";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_YET_TO_DISTRIBUTED = "dc.mdm.android.knox.license.remarks.yetToDistributed";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_SUCCESS = "dc.common.SUCCESS";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_CURRENT_DATE = "dc.mdm.android.knox.license.remarks.failed.currentDate";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_INTERNAL = "dc.mdm.android.knox.license.remarks.failed.internal";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_INTERNAL_SERVER = "dc.mdm.android.knox.license.remarks.failed.internalServer";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_INVALID_LICESNE = "dc.mdm.android.knox.license.remarks.failed.invalidLicense";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_EXPIRED = "dc.common.LICENSE_EXPIRED";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_QUAILTY_EXHAUST = "dc.mdm.android.knox.license.remarks.failed.qualityExhaust";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_TERMINATED = "dc.mdm.android.knox.license.remarks.failed.terminated";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_NETWORK_DISCONNECTED = "dc.mdm.android.knox.license.remarks.failed.networkDisconnected";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_NETWORK_GENERAL = "dc.mdm.android.knox.license.remarks.failed.networkGeneral";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_NULL_PARAM = "dc.mdm.android.knox.license.remarks.failed.nullParam";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_UNKNOWN = "dc.mdm.android.knox.license.remarks.failed.unknown";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_USER_DISAGREED = "dc.mdm.android.knox.license.remarks.failed.userDisagreed";
    public static final String DC_MDM_ANDROID_KNOX_LICENSE_REMARKS_FAILED_LICENSE_DEACTIVATED = "dc.mdm.android.knox.license.remarks.failed.licenseDeactivated";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_ACTIVE = "dc.mdm.android.knox.container.active";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_INPROGRESS = "dc.mdm.android.knox.container.inprogress";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_LOCKED = "dc.mdm.android.knox.container.locked";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_NOT_AVAILABLE = "dc.mdm.android.knox.container.notAvailable";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_CREATION_FAILED = "dc.mdm.android.knox.container.creationFailed";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_CREATION_FAILED_DEFAULT_EXIST = "dc.mdm.android.knox.container.creationFailed.defaultExist";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_CREATION_FAILED_CANCELED = "dc.mdm.android.knox.container.creationFailed.canceled";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_REMOVED = "dc.mdm.android.knox.container.removed";
    public static final String DC_MDM_ANDROID_KNOX_CONTAINER_REMARKS_REMOVED_BY_USER = "dc.mdm.android.knox.container.removed.byUser";
    public static final int KLMS_RESULT_TYPE_ACTIVATION = 9101;
    public static final int KLMS_RESULT_TYPE_DEACTIVATION = 9102;
    public static final int KLMS_RESULT_TYPE_VALIDATION = 9103;
    public static final int KNOX_CONTAINER_STATE_NOT_AVAILABLE = 0;
    public static final int KNOX_CONTAINER_STATE_ACTIVE = 1;
    public static final int KNOX_CONTAINER_STATE_LOCKED = 2;
    public static final int KNOX_CONTAINER_STATE_INPROGRESS = 3;
    public static final int KNOX_ACTIVATION_STATUS_INITIATED = 20000;
    public static final int KNOX_ACTIVATION_STATUS_ACTIVATED = 20001;
    public static final int KNOX_ACTIVATION_STATUS_DEACTIVATED = 20002;
    public static final int KNOX_ACTIVATION_STATUS_FAILED = 20003;
    public static final int KNOX_ACTIVATION_STATUS_NOTAVAILABLE = 20004;
    public static final int KNOX_DISTRIBUTION_SETTING_AUTOMATIC = 1;
    public static final int KNOX_DISTRIBUTION_SETTING_MANUAL = 2;
    public static final String KNOX_API_LEVEL = "KnoxAPILevel";
}