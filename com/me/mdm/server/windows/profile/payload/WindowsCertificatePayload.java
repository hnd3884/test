package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

public class WindowsCertificatePayload extends WindowsPayload
{
    String ServerkeyPrefix;
    
    public WindowsCertificatePayload() {
        this.ServerkeyPrefix = "./Vendor/MSFT/RootCATrustedCertificates/";
    }
    
    public void setDeleteForRootCertificate(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "Root/" + thumbprint;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    public void setDeleteForCACertificate(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "CA/" + thumbprint;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    public void setEncodedRootCertificateContent(final String contents, final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "Root/" + thumbprint + "/EncodedCertificate";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, contents, "b64"));
    }
    
    public void setEncodedCACertificateContent(final String contents, final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "CA/" + thumbprint + "/EncodedCertificate";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, contents, "b64"));
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
    
    public void setRemoveProfileRootPayload(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "Root/" + thumbprint;
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    public void setRemoveProfileCAPayload(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "CA/" + thumbprint;
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
}
