package com.me.mdm.server.windows.profile.payload;

public class WindowsPluginVPNPayload extends WindowsVPNpayload
{
    String pluginURI;
    
    public WindowsPluginVPNPayload(final String profileIdentifier, final int vpnType) {
        super(profileIdentifier, vpnType);
        this.pluginURI = this.baseURI + "/PluginProfile";
    }
    
    public void addVPNServers(final String serverURL) {
        final String keyName = this.pluginURI + "/ServerUrlList";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, serverURL, "chr"));
    }
    
    public void addCustomXML(final String customXML) {
        final String keyName = this.pluginURI + "/CustomConfiguration";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, customXML, "chr"));
    }
    
    public void setPFN(final String pfn) {
        final String keyName = this.pluginURI + "/PluginPackageFamilyName";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, pfn, "chr"));
    }
}
