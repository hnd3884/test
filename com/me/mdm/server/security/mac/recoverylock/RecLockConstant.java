package com.me.mdm.server.security.mac.recoverylock;

public class RecLockConstant
{
    public static final String CURRENT_PASSWORD = "CurrentPassword";
    public static final String NEW_PASSWORD = "NewPassword";
    public static final String VERIFY_PASSWORD = "Password";
    public static final String RESOURCE_ID = "RESOURCE_ID";
    public static final String SEQ_CMD_ID = "SEQ_CMD_ID";
    public static final String IS_RECOVERY_LOCK_ENABLED = "IsRecoveryLockEnabled";
    public static final String NEW_PASSWORD_ID = "newPasswordID";
    public static final String EXISTING_PASSWORD_ID = "existingPasswordID";
    public static final String IS_EXISTING_PASSWORD_VERIFIED = "isExistingPasswordVerified";
    public static final String IS_CLEAR_PASSWORD = "isClearPassword";
    public static final String COLLECTION_ID = "CollectionID";
    public static final String PASSWORD_VERIFIED = "PasswordVerified";
    public static final String IS_PASSWORD_CLEARED = "isPasswordCleared";
    
    public static class ReqType
    {
        public static final String SET_RECOVERY_LOCK = "SetRecoveryLock";
        public static final String VERIFY_RECOVERY_LOCK = "VerifyRecoveryLock";
    }
}
