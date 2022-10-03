package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;

public class WindowsPasscodePayload extends WindowsPayload
{
    String keyPrefix;
    
    public WindowsPasscodePayload() {
        this.keyPrefix = "./Vendor/MSFT/DeviceLock/Provider/MEMDM";
    }
    
    public WindowsPasscodePayload(final String commandUUID) {
        this.keyPrefix = "./Vendor/MSFT/DeviceLock/Provider/MEMDM";
    }
    
    public void setDevicePasswordEnabled(final Integer intDevicePasscodeEnabled) {
        final String keyName = this.keyPrefix + "/DevicePasswordEnabled";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intDevicePasscodeEnabled.toString(), "int"));
    }
    
    public void setAllowSimpleDevicePassword(final Integer intAllowSimpleDevicePassword) {
        final String keyName = this.keyPrefix + "/AllowSimpleDevicePassword";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intAllowSimpleDevicePassword.toString(), "int"));
    }
    
    public void setMinDevicePasswordLength(final Integer intMinDevicePasswordLength) {
        final String keyName = this.keyPrefix + "/MinDevicePasswordLength";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intMinDevicePasswordLength.toString(), "int"));
    }
    
    public void setMinDevicePasswordComplexCharacters(final Integer intMinPasswordComplexChar) {
        final String keyName = this.keyPrefix + "/MinDevicePasswordComplexCharacters";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intMinPasswordComplexChar.toString(), "int"));
    }
    
    public void setAlphanumericDevicePasswordRequired(final Integer intAlphanumericDevicePasswordRequired) {
        final String keyName = this.keyPrefix + "/AlphanumericDevicePasswordRequired";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intAlphanumericDevicePasswordRequired.toString(), "int"));
    }
    
    public void setDevicePasswordExpiration(final Integer intDevicePasswordExpiration) {
        final String keyName = this.keyPrefix + "/DevicePasswordExpiration";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intDevicePasswordExpiration.toString(), "int"));
    }
    
    public void setDevicePasswordHistory(final Integer intPasswordHistory) {
        final String keyName = this.keyPrefix + "/DevicePasswordHistory";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intPasswordHistory.toString(), "int"));
    }
    
    public void setMaxDevicePasswordFailedAttempts(final Integer intMaxDevicePasswordFailedAttempts) {
        final String keyName = this.keyPrefix + "/MaxDevicePasswordFailedAttempts";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intMaxDevicePasswordFailedAttempts.toString(), "int"));
    }
    
    public void setMaxInactivityTimeDeviceLock(final Integer intMaxInactivityTimeDeviceLock) {
        final String keyName = this.keyPrefix + "/MaxInactivityTimeDeviceLock";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intMaxInactivityTimeDeviceLock.toString(), "int"));
    }
    
    public void setMinimumPasswordAge(final Integer intMinimumPasswordAge) {
        final String keyName = this.keyPrefix + "/MinimumPasswordAge";
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, intMinimumPasswordAge.toString(), "int"));
    }
    
    public void setRemoveProfilePayload() {
        final String keyName = this.keyPrefix;
        final Item item = this.createTargetItemTagElement(keyName);
        this.getDeletePayloadCommand().addRequestItem(item);
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getReplacePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
}
