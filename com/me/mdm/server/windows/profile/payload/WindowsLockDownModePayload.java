package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

public class WindowsLockDownModePayload extends WindowsPayload
{
    public String keyPrefix;
    
    public WindowsLockDownModePayload() {
        this.keyPrefix = null;
        this.keyPrefix = "./Device/Vendor/MSFT/AssignedAccess/";
    }
    
    public void setConfigurationXML(final String configurationXML) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.keyPrefix + "Configuration", configurationXML, "chr"));
    }
    
    public void setConfigurationDelete() {
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix + "Configuration"));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setAtomicPayloadContent(this.getReplacePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setAtomicPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
}
