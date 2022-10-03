package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

public class WindowsLockDownPayload extends WindowsPayload
{
    public String keyPrefix;
    
    public WindowsLockDownPayload() {
        this.keyPrefix = null;
        this.keyPrefix = "./Vendor/MSFT/EnterpriseAssignedAccess/";
    }
    
    public void setLockDownXml(final String lockDownXML) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElemetWithoutMeta(this.keyPrefix + "AssignedAccess/AssignedAccessXml", lockDownXML));
    }
    
    public void setRemovePayload() {
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix + "AssignedAccess/AssignedAccessXml"));
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
