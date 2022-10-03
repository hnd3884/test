package com.adventnet.mdmconfiguration;

public final class BITLOCKERPOLICY
{
    public static final String TABLE = "BitlockerPolicy";
    public static final String BITLOCKER_POLICY_ID = "BITLOCKER_POLICY_ID";
    public static final int BITLOCKER_POLICY_ID_IDX = 1;
    public static final String REQUIRE_STORAGE_CARD_ENCRYPTION = "REQUIRE_STORAGE_CARD_ENCRYPTION";
    public static final int REQUIRE_STORAGE_CARD_ENCRYPTION_IDX = 2;
    public static final String REQUIRE_DEVICE_ENCRYPTION = "REQUIRE_DEVICE_ENCRYPTION";
    public static final int REQUIRE_DEVICE_ENCRYPTION_IDX = 3;
    public static final String ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION = "ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION";
    public static final int ALLOW_WARNING_FOR_OTHER_DISK_ENCRYPTION_IDX = 4;
    public static final String ALLOW_STANDARD_USER_ENCRYPTION = "ALLOW_STANDARD_USER_ENCRYPTION";
    public static final int ALLOW_STANDARD_USER_ENCRYPTION_IDX = 5;
    public static final String CONFIGURE_RECOVERY_PASSWORD_ROTATION = "CONFIGURE_RECOVERY_PASSWORD_ROTATION";
    public static final int CONFIGURE_RECOVERY_PASSWORD_ROTATION_IDX = 6;
    public static final String ADMX_BACKED_POLICY_GROUP_ID = "ADMX_BACKED_POLICY_GROUP_ID";
    public static final int ADMX_BACKED_POLICY_GROUP_ID_IDX = 7;
    
    private BITLOCKERPOLICY() {
    }
}
