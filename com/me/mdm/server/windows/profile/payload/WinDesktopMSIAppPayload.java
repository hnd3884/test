package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import java.util.List;
import org.json.JSONObject;

public class WinDesktopMSIAppPayload extends WinDesktopAppPayload
{
    String msiInstallJob;
    
    public WinDesktopMSIAppPayload() {
        this.msiInstallJob = "";
    }
    
    @Override
    public void initializePayload(final JSONObject jsonObject, final String task) {
        super.initializePayload(jsonObject, task);
        this.keyPrefix = "./Device/Vendor/MSFT/EnterpriseDesktopAppManagement/MSI/" + this.productID;
        this.msiInstallJob = jsonObject.optString("msiInstallJob", "");
    }
    
    @Override
    public void setURL(final String url, final List dependency) {
        final String keyName = this.keyPrefix + "/DownloadInstall";
        this.getAddPayloadCommand().addRequestItem(this.createTargetItemTagElement(keyName));
    }
    
    @Override
    public void enableSilentInstall() {
        final String keyName = this.keyPrefix + "/DownloadInstall";
        this.getExecPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, this.msiInstallJob, "xml"));
    }
    
    @Override
    public void deleteApp() {
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificRemovePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getDeletePayloadCommand());
        return winConfigPayload;
    }
}
