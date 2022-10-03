package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

public class WindowsClientCertificatePayload extends WindowsPayload
{
    String keyPrefix;
    
    public WindowsClientCertificatePayload() {
        this.keyPrefix = "./Vendor/MSFT/ClientCertificateInstall/PFXCertInstall/";
    }
    
    public void setKeyLocationPayload(final String thumbprint) {
        final String keyName = this.keyPrefix + thumbprint + "/KeyLocation";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, "3", "int"));
    }
    
    public void setPFXCertPasswordPayload(final String thumbprint, final String password) {
        final String keyName = this.keyPrefix + thumbprint + "/PFXCertPassword";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, password, "chr"));
    }
    
    public void setPFXKeyExportablePayload(final String thumbprint) {
        final String keyName = this.keyPrefix + thumbprint + "/PFXKeyExportable";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, "true", "bool"));
    }
    
    public void setPFXCertBlobPayload(final String thumbprint, final String Contents) {
        final String keyName = this.keyPrefix + thumbprint + "/PFXCertBlob";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, Contents, "chr"));
    }
    
    public void setClientCertificateNonAtomicDelete(final String thumbprint) {
        final String keyName = this.keyPrefix + thumbprint;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    public void setClientCertificateDelete(final String thumbprint) {
        final String keyName = this.keyPrefix + thumbprint;
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificInstallPayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getAddPayloadCommand());
        winConfigPayload.setNonAtomicPayloadContent(this.getNonAtomicDeletePayloadCommand());
        return winConfigPayload;
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setAtomicPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
}
