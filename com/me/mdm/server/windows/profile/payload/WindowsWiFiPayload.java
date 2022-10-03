package com.me.mdm.server.windows.profile.payload;

public class WindowsWiFiPayload extends WindowsPayload
{
    String locUriBase;
    
    public WindowsWiFiPayload() {
        this.locUriBase = "./Vendor/MSFT/WiFi/Profile/";
    }
    
    public void setWlanXml(final String ssidName, final String data) {
        final String locUri = this.locUriBase + ssidName + "/WlanXml";
        final String format = "chr";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, data, format));
    }
    
    public void setProxy(final String ssidName, final String url, final String port) {
        final String locUri = this.locUriBase + ssidName + "/Proxy";
        final String format = "chr";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, url + ":" + port, format));
    }
    
    public void setDeleteOnInstallItem(final String ssidName) {
        final String locUri = this.locUriBase + ssidName;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(locUri));
    }
    
    public void setRemoveProfile(final String ssidName) {
        final String locUri = this.locUriBase + ssidName + "/WlanXml";
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(locUri));
    }
    
    public void setCertificatePayload(final String b64EncodedCert, final String certThumbPrint) {
        final String locUri = "./Vendor/MSFT/CertificateStore/Root/System/" + certThumbPrint + "/EncodedCertificate";
        final String format = "b64";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, b64EncodedCert, format));
    }
    
    public void setCertificateDeletePayload(final String certThumbPrint) {
        final String locUri = "./Vendor/MSFT/CertificateStore/Root/System/" + certThumbPrint;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(locUri));
    }
}
