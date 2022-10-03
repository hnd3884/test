package com.me.mdm.server.windows.profile.payload;

import java.util.Iterator;
import java.util.List;

public class WinEDPPayload extends WindowsPayload
{
    String baseURI;
    String configName;
    
    public WinEDPPayload() {
        this.baseURI = "./Device/Vendor/MSFT/EnterpriseDataProtection/Settings/";
        this.configName = "EDPAppLockPolicy";
    }
    
    public void setEnterpriseProtectedDomains(final List<String> domainList) {
        final Iterator iterator = domainList.iterator();
        final StringBuilder domainString = new StringBuilder();
        while (iterator.hasNext()) {
            domainString.append(iterator.next());
            if (iterator.hasNext()) {
                domainString.append("|");
            }
        }
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EnterpriseProtectedDomainNames", domainString.toString(), "chr"));
    }
    
    public void setEDPEnforcementLevel(final int edpLevel) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EDPEnforcementLevel", edpLevel + "", "int"));
    }
    
    public void setAppLockPolicyBlob(final String appBlob, final String type) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement("./Device/Vendor/MSFT/AppLocker/EnterpriseDataProtection/" + this.configName + "/" + type + "/Policy", appBlob, "chr"));
    }
    
    public void setshowEDPIcon(final boolean showEDP) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "EDPShowIcons", (showEDP ? 1 : 0) + "", "int"));
    }
    
    public void setDataRecoveryBlob(final String dataRecoveryBlob) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "DataRecoveryCertificate", dataRecoveryBlob, "chr"));
    }
    
    public void setrevokeOnUnaenroll(final boolean revokeOnUnenroll) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "RevokeOnUnenroll", (revokeOnUnenroll ? 1 : 0) + "", "chr"));
    }
    
    public void setAllowUserDecryption(final boolean allowUserDecryption) {
        this.getReplacePayloadCommand().addRequestItem(this.createCommandItemTagElement(this.baseURI + "RevokeOnUnenroll", (allowUserDecryption ? 1 : 0) + "", "chr"));
    }
}
