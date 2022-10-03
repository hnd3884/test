package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import java.util.List;
import com.me.mdm.framework.syncml.core.data.Item;
import org.json.JSONObject;

public class WindowsAppPayload extends WinMobileAppPayload
{
    @Override
    public void initializePayload(final JSONObject jsonObject, final String task) {
        try {
            this.enterpriseID = String.valueOf(jsonObject.get("enterpriseID"));
            this.productID = String.valueOf(jsonObject.get("productID"));
            if (task.equalsIgnoreCase("install") || task.equalsIgnoreCase("update")) {
                this.keyPrefix = "./Vendor/MSFT/EnterpriseAppManagement/" + this.enterpriseID + "/EnterpriseApps/Download/" + this.productID;
            }
            else if (task.equalsIgnoreCase("remove")) {
                this.keyPrefix = "./Vendor/MSFT/EnterpriseAppManagement/" + this.enterpriseID + "/EnterpriseApps/Inventory/" + this.productID;
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public void setName(final String name) {
        final String keyName = this.keyPrefix + "/Name";
        final Item item = this.createCommandItemTagElement(keyName, name, "chr");
        this.getAddPayloadCommand().addRequestItem(item);
        this.getReplacePayloadCommand().addRequestItem(item);
    }
    
    public void setVersion(final String version) {
        final String keyName = this.keyPrefix + "/Version";
        final Item item = this.createCommandItemTagElement(keyName, version, "chr");
        this.getAddPayloadCommand().addRequestItem(item);
        this.getReplacePayloadCommand().addRequestItem(item);
    }
    
    @Override
    public void setURL(final String url, final List dependency) {
        final String keyName = this.keyPrefix + "/URL";
        final Item item = this.createCommandItemTagElement(keyName, url, "chr");
        this.getAddPayloadCommand().addRequestItem(item);
        this.getReplacePayloadCommand().addRequestItem(item);
    }
    
    public void assignToAppCatalog() {
        final String keyName = this.keyPrefix + "/DownloadInstall";
        this.getAddPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, "1", "int"));
    }
    
    @Override
    public void enableSilentInstall() {
        final String keyName = this.keyPrefix + "/DownloadInstall";
        this.getExecPayloadCommand().addRequestItem(this.createCommandItemTagElement(keyName, "0", "int"));
    }
    
    @Override
    public void deleteApp() {
        this.getDeletePayloadCommand().addRequestItem(this.createTargetItemTagElement(this.keyPrefix));
    }
    
    @Override
    public WindowsConfigurationPayload getOSSpecificUpdatePayload(final WindowsConfigurationPayload winConfigPayload) {
        winConfigPayload.setPayloadContent(this.getReplacePayloadCommand());
        winConfigPayload.setPayloadContent(this.getExecPayloadCommand());
        return winConfigPayload;
    }
}
