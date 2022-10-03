package com.me.mdm.server.windows.profile.payload;

public class WinMobileCertificatePayload extends WindowsCertificatePayload
{
    public WinMobileCertificatePayload() {
        this.ServerkeyPrefix = "./Vendor/MSFT/CertificateStore/";
    }
    
    @Override
    public void setDeleteForRootCertificate(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "Root/System/" + thumbprint;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    @Override
    public void setDeleteForCACertificate(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "CA/System/" + thumbprint;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    @Override
    public void setEncodedRootCertificateContent(final String contents, final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "Root/System/" + thumbprint + "/EncodedCertificate";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, contents, "b64"));
    }
    
    @Override
    public void setEncodedCACertificateContent(final String contents, final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "CA/System/" + thumbprint + "/EncodedCertificate";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, contents, "b64"));
    }
    
    @Override
    public void setRemoveProfileRootPayload(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "Root/System/" + thumbprint;
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    @Override
    public void setRemoveProfileCAPayload(final String thumbprint) {
        final String keyName = this.ServerkeyPrefix + "CA/System/" + thumbprint;
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
}
