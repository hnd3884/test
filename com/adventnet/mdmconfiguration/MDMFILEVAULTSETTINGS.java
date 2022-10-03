package com.adventnet.mdmconfiguration;

public final class MDMFILEVAULTSETTINGS
{
    public static final String TABLE = "MDMFileVaultSettings";
    public static final String ENCRYPTION_SETTINGS_ID = "ENCRYPTION_SETTINGS_ID";
    public static final int ENCRYPTION_SETTINGS_ID_IDX = 1;
    public static final String RECOVERY_KEY_TYPE = "RECOVERY_KEY_TYPE";
    public static final int RECOVERY_KEY_TYPE_IDX = 2;
    public static final String COPY_RECOVERY_KEY_TO_MDM = "COPY_RECOVERY_KEY_TO_MDM";
    public static final int COPY_RECOVERY_KEY_TO_MDM_IDX = 3;
    public static final String FORCE_ENCRYPTION_UNTIL_LOGOUT = "FORCE_ENCRYPTION_UNTIL_LOGOUT";
    public static final int FORCE_ENCRYPTION_UNTIL_LOGOUT_IDX = 4;
    public static final String MAXIMUM_ATTEMPTS_TO_FORCE = "MAXIMUM_ATTEMPTS_TO_FORCE";
    public static final int MAXIMUM_ATTEMPTS_TO_FORCE_IDX = 5;
    public static final String ASK_ENABLE_DURING_LOGOUT = "ASK_ENABLE_DURING_LOGOUT";
    public static final int ASK_ENABLE_DURING_LOGOUT_IDX = 6;
    public static final String MESSAGE_TO_USER = "MESSAGE_TO_USER";
    public static final int MESSAGE_TO_USER_IDX = 7;
    
    private MDMFILEVAULTSETTINGS() {
    }
}
