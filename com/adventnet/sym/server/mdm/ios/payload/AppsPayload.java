package com.adventnet.sym.server.mdm.ios.payload;

import com.me.devicemanagement.framework.server.util.DMSecurityUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;

public class AppsPayload extends IOSCommandPayload
{
    public void setManifestURL(final String manifestUrl) {
        this.getCommandDict().put("ManifestURL", (Object)manifestUrl);
    }
    
    public void setiTunesStoreID(final int iTunesStoreID) {
        this.getCommandDict().put("iTunesStoreID", (Object)iTunesStoreID);
    }
    
    public void setManagementFlags(final int managementFlags) {
        this.getCommandDict().put("ManagementFlags", (Object)managementFlags);
    }
    
    public void setChangeManagementState(final String managementState) {
        this.getCommandDict().put("ChangeManagementState", (Object)managementState);
    }
    
    public void setPinningRevocationCheckRequired(final Boolean value) {
        this.getCommandDict().put("PinningRevocationCheckRequired", (Object)value);
    }
    
    public void setVPPPurchaseMethod() {
        final NSDictionary commandDict = new NSDictionary();
        commandDict.put("PurchaseMethod", (Object)1);
        this.getCommandDict().put("Options", (NSObject)commandDict);
    }
    
    public void setInstallAsManaged(final Boolean value) {
        this.getCommandDict().put("InstallAsManaged", (Object)value);
    }
    
    public void setConfiguration(final NSDictionary configurationDict) {
        this.getCommandDict().put("Configuration", (NSObject)configurationDict);
    }
    
    public void setManifest(final String fileLocation) throws Exception {
        final NSDictionary rootDict = (NSDictionary)DMSecurityUtil.parsePropertyList(ApiFactoryProvider.getFileAccessAPI().readFile(fileLocation));
        this.getCommandDict().put("Manifest", (NSObject)rootDict);
    }
}
