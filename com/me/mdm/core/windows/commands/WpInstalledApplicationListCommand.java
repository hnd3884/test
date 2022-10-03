package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import org.json.JSONObject;

public class WpInstalledApplicationListCommand
{
    public static WpInstalledApplicationListCommand getInstance(final JSONObject jsonObject) {
        final String osVersion = jsonObject.optString("OS_VERSION");
        final Boolean isPrivacy = jsonObject.optBoolean("isPrivacy", (boolean)Boolean.FALSE);
        WpInstalledApplicationListCommand wpInstalledApplCmd = null;
        if (osVersion != null && ManagedDeviceHandler.getInstance().isOsVersionGreaterThan(osVersion, 10.0f)) {
            final int modelType = jsonObject.optInt("MODEL_TYPE", 1);
            if (modelType == 3 || modelType == 4 || modelType == 2) {
                if (!isPrivacy) {
                    wpInstalledApplCmd = new WinDesktopInstalledAppListCommand();
                }
                else {
                    wpInstalledApplCmd = new WindowsInstallAppPrivacyCommand();
                }
            }
            else if (!isPrivacy) {
                wpInstalledApplCmd = new WinMobileInstalledAppListCommand();
            }
            else {
                wpInstalledApplCmd = new WindowsInstallAppPrivacyCommand();
            }
        }
        else {
            wpInstalledApplCmd = new WpInstalledApplicationListCommand();
        }
        return wpInstalledApplCmd;
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final GetRequestCommand installedApplicationList = new GetRequestCommand();
            installedApplicationList.setRequestCmdId("InstalledApplicationList");
            final String enterpriseID = String.valueOf(jsonObject.get("ENTERPRISE_ID"));
            final String locationUri = "./Vendor/MSFT/EnterpriseAppManagement/" + enterpriseID + "/EnterpriseApps/Inventory?list=StructData";
            installedApplicationList.addRequestItem(this.createTargetItemTagElement(locationUri));
            final String storeAppQueryUri = "./Vendor/MSFT/EnterpriseAppManagement/" + enterpriseID + "/EnterpriseApps/Inventory/%7B" + "551ab9a7-413b-4b79-8142-74550af0c72e" + "%7D";
            installedApplicationList.addRequestItem(this.createTargetItemTagElement(storeAppQueryUri));
            installedApplicationList.addRequestItem(this.createTargetItemTagElement(storeAppQueryUri + "/Version"));
            installedApplicationList.addRequestItem(this.createTargetItemTagElement(storeAppQueryUri + "/Title"));
            installedApplicationList.addRequestItem(this.createTargetItemTagElement(storeAppQueryUri + "/Publisher"));
            installedApplicationList.addRequestItem(this.createTargetItemTagElement(storeAppQueryUri + "/InstallDate"));
            responseSyncML.getSyncBody().addRequestCmd(installedApplicationList);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
}
