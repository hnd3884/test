package com.adventnet.sym.server.mdm.ios.payload.mac;

import com.me.mdm.server.security.mac.recoverylock.RecoveryLock;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.ios.payload.IOSCommandPayload;
import com.adventnet.sym.server.mdm.ios.payload.PayloadHandler;

public class MacPayloadHandler extends PayloadHandler
{
    private static MacPayloadHandler pHandler;
    
    public static MacPayloadHandler getInstance() {
        if (MacPayloadHandler.pHandler == null) {
            MacPayloadHandler.pHandler = new MacPayloadHandler();
        }
        return MacPayloadHandler.pHandler;
    }
    
    public IOSCommandPayload createFirmwarePreSecurityInfoCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("SecurityInfo");
        commandPayload.getPayloadDict().put("CommandUUID", (Object)"MacFirmwarePreSecurityInfo");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "MacFirmwarePreSecurityInfo");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "MacFirmwarePreSecurityInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createFirmwarePostSecurityInfoCommand() {
        final IOSCommandPayload commandPayload = this.createCommandPayload("SecurityInfo");
        commandPayload.getPayloadDict().put("CommandUUID", (Object)"MacFirmwarePostSecurityInfo");
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "MacFirmwarePostSecurityInfo");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "MacFirmwarePostSecurityInfo", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createSetFirmwarePasswordCommand(final String newPassword, final String currentPwd, final String commandUUID) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("SetFirmwarePassword");
        commandPayload.getPayloadDict().put("CommandUUID", (Object)commandUUID);
        commandPayload.getCommandDict().put("NewPassword", (Object)newPassword);
        if (currentPwd != null) {
            commandPayload.getCommandDict().put("CurrentPassword", (Object)currentPwd);
        }
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "MacFirmwareSetPasscode");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "MacFirmwareSetPasscode", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createClearFirmwarePasswordCommand(final String curentPwd) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("SetFirmwarePassword");
        commandPayload.getPayloadDict().put("CommandUUID", (Object)"MacFirmwareClearPasscode");
        commandPayload.getCommandDict().put("NewPassword", (Object)"");
        commandPayload.getCommandDict().put("CurrentPassword", (Object)curentPwd);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "MacFirmwareClearPasscode");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "MacFirmwareClearPasscode", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createVerifyFirmwarePasswordCommand(final String verifyPassword) {
        final IOSCommandPayload commandPayload = this.createCommandPayload("VerifyFirmwarePassword");
        commandPayload.getPayloadDict().put("CommandUUID", (Object)"MacFirmwareVerifyPassword");
        commandPayload.getCommandDict().put("Password", (Object)verifyPassword);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "MacFirmwareVerifyPassword");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "MacFirmwareVerifyPassword", commandPayload.toString() });
        return commandPayload;
    }
    
    public IOSCommandPayload createRecoveryLockPreSecurityInfoCommand() {
        return this.createSecurityInfoCommand(RecoveryLock.PRE_SECURITY);
    }
    
    public IOSCommandPayload createRecoveryLockPostSecurityInfoCommand() {
        return this.createSecurityInfoCommand(RecoveryLock.POST_SECURITY);
    }
    
    private IOSCommandPayload createSecurityInfoCommand(final RecoveryLock recoveryLock) {
        final IOSCommandPayload securityInfoCommand = this.createCommandPayload("SecurityInfo");
        securityInfoCommand.getPayloadDict().put("CommandUUID", (Object)recoveryLock.command);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", recoveryLock.command);
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { recoveryLock.command, securityInfoCommand.toString() });
        return securityInfoCommand;
    }
    
    public IOSCommandPayload createSetRecoveryLockPasswordCommand(final String newPassword, final String currentPassword, final String commandUUID) {
        final IOSCommandPayload setPasswordCmd = this.createCommandPayload("SetRecoveryLock");
        setPasswordCmd.getPayloadDict().put("CommandUUID", (Object)commandUUID);
        setPasswordCmd.getCommandDict().put("NewPassword", (Object)newPassword);
        if (currentPassword != null) {
            setPasswordCmd.getCommandDict().put("CurrentPassword", (Object)currentPassword);
        }
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "SetRecoveryLock");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "SetRecoveryLock", setPasswordCmd.toString() });
        return setPasswordCmd;
    }
    
    public IOSCommandPayload createClearRecoveryLockPasswordCommand(final String currentPassword) {
        final IOSCommandPayload clearPasswordCmd = this.createCommandPayload("SetRecoveryLock");
        clearPasswordCmd.getPayloadDict().put("CommandUUID", (Object)RecoveryLock.CLEAR_PASSWORD.command);
        clearPasswordCmd.getCommandDict().put("NewPassword", (Object)"");
        clearPasswordCmd.getCommandDict().put("CurrentPassword", (Object)currentPassword);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", RecoveryLock.CLEAR_PASSWORD.command);
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { RecoveryLock.CLEAR_PASSWORD.command, clearPasswordCmd.toString() });
        return clearPasswordCmd;
    }
    
    public IOSCommandPayload createVerifyRecoveryLockPasswordCommand(final String verifyPassword) {
        final IOSCommandPayload verifyPasswordCmd = this.createCommandPayload("VerifyRecoveryLock");
        verifyPasswordCmd.getPayloadDict().put("CommandUUID", (Object)RecoveryLock.VERIFY_PASSWORD.command);
        verifyPasswordCmd.getCommandDict().put("Password", (Object)verifyPassword);
        this.mdmLogger.log(Level.INFO, "{0} Command has been created successfully", "VerifyRecoveryLock");
        this.mdmLogger.log(Level.FINE, "{0} Command Payload Data : {1}", new Object[] { "VerifyRecoveryLock", verifyPasswordCmd.toString() });
        return verifyPasswordCmd;
    }
    
    static {
        MacPayloadHandler.pHandler = null;
    }
}
