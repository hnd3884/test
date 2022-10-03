package com.me.mdm.server.windows.profile.payload;

import java.util.List;
import java.util.HashMap;

public class WindowsVPNpayload extends WindowsPayload
{
    private static final int CUSTOM_SSL = 7;
    private static final int PER_APP_VPN_PAYLOAD = 2;
    public static final String PER_APP_TRIGGER_LIST = "perAppTriggerList";
    String baseURI;
    private WindowsPerAppVPNPayload windowsPerAppVPNPayload;
    
    public WindowsVPNpayload(final String connectionName, final int connectionType) {
        this.baseURI = null;
        this.windowsPerAppVPNPayload = null;
        this.baseURI = "./Device/Vendor/MSFT/VPNv2/" + connectionName;
        if (connectionType == 2) {
            this.windowsPerAppVPNPayload = new WindowsPerAppVPNPayload(this.baseURI);
        }
    }
    
    public void setVPNDeletePayload(final Boolean isAtomic) {
        if (!isAtomic) {
            this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.baseURI));
        }
        else {
            this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.baseURI));
        }
    }
    
    public void setCertificateDeletePayload(final String trustedRootCAThumbrpint) {
        final String locUri = "./Vendor/MSFT/CertificateStore/Root/System/" + trustedRootCAThumbrpint;
        this.getNonAtomicDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(locUri));
    }
    
    public void setRememberCredentials(final Boolean rememberCredentials) {
        final String locUri = this.baseURI + "/RememberCredentials";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, rememberCredentials.toString(), "bool"));
    }
    
    public void setManualProxy(final String serverURL) {
        final String locUri = this.baseURI + "/Proxy/Manual/Server";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, serverURL, "chr"));
    }
    
    public void setAutoConfigProxy(final String autoconfigURL) {
        final String locUri = this.baseURI + "/Proxy/AutoConfigUrl";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, autoconfigURL, "chr"));
    }
    
    public void setCertificatePayload(final String trusterRootCACertificate, final String trustedRootCAThumbrpint) {
        final String locUri = "./Vendor/MSFT/CertificateStore/Root/System/" + trustedRootCAThumbrpint + "/EncodedCertificate";
        final String format = "b64";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(locUri, trusterRootCACertificate, format));
    }
    
    public static WindowsVPNpayload getVPNClassFromType(final int connectionType, final String connectionName, final int vpnType) {
        WindowsVPNpayload windowsPayload = null;
        if (connectionType != 7) {
            windowsPayload = new WindowsNativeVPNPayload(connectionName, vpnType);
        }
        else {
            windowsPayload = new WindowsPluginVPNPayload(connectionName, vpnType);
        }
        return windowsPayload;
    }
    
    public void setTriggers(final HashMap triggers) {
        if (this.windowsPerAppVPNPayload != null) {
            final List appList = triggers.get("perAppTriggerList");
            this.windowsPerAppVPNPayload.setAppTriggers(appList, this);
        }
    }
}
