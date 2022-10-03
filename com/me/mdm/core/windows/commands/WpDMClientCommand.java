package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpDMClientCommand
{
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final Long resourceID = jsonObject.optLong("RESOURCE_ID", (long)new Long("-1"));
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("DeviceClientSettings");
            final ReplaceRequestCommand replaceCmd = new ReplaceRequestCommand();
            replaceCmd.setRequestCmdId("DeviceClientSettings");
            final boolean isAdminEnrolledDevice = jsonObject.optBoolean("isAdminEnrolledDevice", (boolean)Boolean.FALSE);
            final ArrayList items = new ArrayList();
            items.add(this.createTargetItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/EntDMID", resourceID.toString(), null));
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("DoNotPollOnUserLogin")) {
                items.add(this.createTargetItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/Poll/PollOnLogin", "false", "bool"));
            }
            if (!String.valueOf(jsonObject.get("osVersion")).startsWith("8.0.") && jsonObject.getInt("MANAGED_STATUS") == 2) {
                if (!jsonObject.getBoolean("USER_UNENROLL")) {
                    items.add(this.createTargetItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowManualMDMUnenrollment", "0", "int"));
                    if (isAdminEnrolledDevice) {
                        items.add(this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/Security/AllowRemoveProvisioningPackage", "0", "int"));
                    }
                }
                else {
                    items.add(this.createTargetItemTagElement("./Vendor/MSFT/PolicyManager/My/Experience/AllowManualMDMUnenrollment", "1", "int"));
                    if (isAdminEnrolledDevice) {
                        items.add(this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/Security/AllowRemoveProvisioningPackage", "1", "int"));
                    }
                }
            }
            replaceCmd.setRequestItems(items);
            atomicCommand.addRequestCmd(replaceCmd);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public Item createTargetItemTagElement(final String locationUri, final String itemData, final String sMetaFormat) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        item.setData(itemData);
        if (sMetaFormat != null) {
            final Meta meta = new Meta();
            meta.setFormat(sMetaFormat);
            item.setMeta(meta);
        }
        return item;
    }
}
