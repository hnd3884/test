package com.me.mdm.server.security.mac.recoverylock;

public enum RecoveryLock
{
    PRE_SECURITY("MacRecoveryLockPreSecurityInfo"), 
    VERIFY_PASSWORD("MacRecoveryLockVerifyPassword"), 
    SET_PASSWORD("MacRecoveryLockSetPasscode"), 
    CLEAR_PASSWORD("MacRecoveryLockClearPasscode"), 
    POST_SECURITY("MacRecoveryLockPostSecurityInfo");
    
    public final String command;
    
    private RecoveryLock(final String command) {
        this.command = command;
    }
}
