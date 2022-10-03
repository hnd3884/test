package com.adventnet.sym.server.mdm.ios.payload.mac;

import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;

public class MacLoginWindowPayload extends IOSPayload
{
    public MacLoginWindowPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.loginwindow", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setLoginWindowText(final String text) {
        if (text != null) {
            this.getPayloadDict().put("LoginwindowText", (Object)text);
        }
        else {
            this.getPayloadDict().put("LoginwindowText", (Object)"");
        }
    }
    
    public void setShowFullName(final boolean showFullName) {
        this.getPayloadDict().put("SHOWFULLNAME", (Object)showFullName);
    }
    
    public void setHideLocalUsers(final boolean hideLocalUsers) {
        this.getPayloadDict().put("HideLocalUsers", (Object)!hideLocalUsers);
    }
    
    public void setHideMobileAccounts(final boolean bool) {
        this.getPayloadDict().put("HideMobileAccounts", (Object)!bool);
    }
    
    public void setIncludeNetworkUser(final boolean bool) {
        this.getPayloadDict().put("IncludeNetworkUser", (Object)bool);
    }
    
    public void setHideAdminUsers(final boolean bool) {
        this.getPayloadDict().put("HideAdminUsers", (Object)!bool);
    }
    
    public void setShowOther(final boolean bool) {
        this.getPayloadDict().put("SHOWOTHERUSERS_MANAGED", (Object)bool);
    }
    
    public void setShutdownButtonStatus(final boolean isDisabled) {
        this.getPayloadDict().put("ShutDownDisabled", (Object)!isDisabled);
    }
    
    public void setRestartButtonStatus(final boolean isDisabled) {
        this.getPayloadDict().put("RestartDisabled", (Object)!isDisabled);
    }
    
    public void setSleepButtonStatus(final boolean isDisabled) {
        this.getPayloadDict().put("SleepDisabled", (Object)!isDisabled);
    }
    
    public void setAutoLoginStatus(final boolean isAutoLoginDisabled) {
        if (isAutoLoginDisabled) {
            this.getPayloadDict().put("com.apple.login.mcx.DisableAutoLoginClient", (Object)isAutoLoginDisabled);
        }
    }
    
    public void setConsoleAccessStatus(final boolean isConsoleAccessDisabled) {
        this.getPayloadDict().put("DisableConsoleAccess", (Object)!isConsoleAccessDisabled);
    }
    
    public void setAdminMayDisableMCX(final boolean bool) {
        if (bool) {
            this.getPayloadDict().put("AdminMayDisableMCX", (Object)bool);
        }
    }
    
    public void setComputerNameToRecordName(final boolean bool) {
        this.getPayloadDict().put("UseComputerNameForComputerRecordName", (Object)bool);
    }
    
    public void setExternalAccountStatus(final boolean isEnabled) {
        this.getPayloadDict().put("EnableExternalAccounts", (Object)isEnabled);
    }
    
    public void setPasswordHintsAfter(final Integer count) {
        this.getPayloadDict().put("RetriesUntilHint", (Object)count);
    }
    
    public void setRestartDisableOnLogin(final boolean isDisabled) {
        this.getPayloadDict().put("RestartDisabledWhileLoggedIn", (Object)isDisabled);
    }
    
    public void setLogoutDisableOnLogin(final boolean isDisabled) {
        this.getPayloadDict().put("LogOutDisabledWhileLoggedIn", (Object)isDisabled);
    }
    
    public void setShutdownDisableOnLogin(final boolean isDisabled) {
        this.getPayloadDict().put("ShutDownDisabledWhileLoggedIn", (Object)isDisabled);
    }
    
    public void setShowInputMenu(final boolean isAllowed) {
        this.getPayloadDict().put("showInputMenu", (Object)isAllowed);
    }
    
    public void setDisableFDELogin(final boolean isDisabled) {
        this.getPayloadDict().put("DisableFDEAutoLogin", (Object)isDisabled);
    }
    
    public void setDisableImmediateLockOnLogin(final boolean isDisabled) {
        this.getPayloadDict().put("DisableScreenLockImmediate", (Object)isDisabled);
    }
}
