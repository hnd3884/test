package com.adventnet.sym.server.mdm.encryption.windows.bitlocker;

public class BitlockerConstants
{
    public static final String REQUIRE_STORAGE_CARD_ENCRYPTION = "require_storage_card_encryption";
    public static final String ENFORCE_BITLOCKER = "enforce_bitlocker";
    public static final String MIN_PIN_LENGTH = "min_pin_length";
    public static final String FIXED_DRIVE_READ_ONLY = "fixed_drive_read_only";
    public static final String REMOVABLE_DRIVE_READ_ONLY = "removable_drive_read_only";
    public static final String REMOVABLE_DRIVE_CROSS_ORIGIN = "removable_drive_cross_origin";
    public static final String SILENT_ENCRYPT_AZURE_AD_DEVICES = "silent_encrypt_azure_ad_devices";
    public static final String ALLOW_STANDARD_USER_ENCRYPTION = "allow_standard_user_encryption";
    public static final String RECOVERY_PASS_ROTATION = "recovery_pass_rotation";
    public static final String BITLOCKER_POLICY_ID = "bitlocker_policy_id";
    public static final String BITLOCKER_ADMX_POLICIES = "BITLOCKER_ADMX_POLICIES";
    public static final int DISALLOWED = 0;
    public static final int REQUIRED = 1;
    public static final int OPTIONAL = 2;
    public static final int NO_MESSAGE = 0;
    public static final int DEFAULT_MESSAGE = 1;
    public static final int RECOVERY_MESSAGE = 2;
    public static final int RECOVERY_URL = 3;
    
    public static final class EncryptionMethod
    {
        public static final String ENCRYPTION_METHOD = "encryption_method";
        public static final String OS_DRIVE = "os_drive";
        public static final String REMOVABLE_DRIVE = "removable_drive";
        public static final String FIXED_DRIVE = "fixed_drive";
    }
    
    public static final class AdditionalStartupAuthentication
    {
        public static final String ADDITIONAL_STARTUP_AUTHENTICATION = "additional_startup_authentication";
        public static final String ALLOW_NON_TPM_DEVICES = "allow_non_tpm_devices";
        public static final String TPM = "tpm";
        public static final String TPM_PIN = "tpm_pin";
        public static final String TPM_KEY = "tpm_key";
        public static final String TPM_KEY_PIN = "tpm_key_pin";
    }
    
    public static final class RecoveryMessage
    {
        public static final String RECOVERY_MESSAGE = "recovery_message";
        public static final String TYPE = "type";
        public static final String RECOVERY_URL = "recovery_url";
    }
    
    public static final class DriveRecoveryOptions
    {
        public static final String OS_DRIVE_RECOVERY_OPTIONS = "os_drive_recovery_options";
        public static final String FIXED_DRIVE_RECOVERY_OPTIONS = "fixed_drive_recovery_options";
        public static final String ALLOW_DRA = "allow_dra";
        public static final String RECOVERY_PASSWORD = "recovery_password";
        public static final String RECOVERY_KEY = "recovery_key";
        public static final String HIDE_RECOVERY_OPTIONS = "hide_recovery_options";
        public static final String STORE_RECOVERY_INFO_IN_AD_DS = "store_recovery_info_in_ad_ds";
        public static final String STORE_KEY_PACKAGES_IN_AD_DS = "store_key_packages_in_ad_ds";
        public static final String WAIT_FOR_RECOVERY_INFO_BACKUP_IN_AD_DS = "wait_for_recovery_info_backup_in_ad_ds";
    }
}
