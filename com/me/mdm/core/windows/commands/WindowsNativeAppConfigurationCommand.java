package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WindowsNativeAppConfigurationCommand
{
    public Logger logger;
    
    public WindowsNativeAppConfigurationCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("WindowsNativeAppConfig");
            final ReplaceRequestCommand replaceRequestCommand = new ReplaceRequestCommand();
            replaceRequestCommand.setRequestCmdId("WindowsNativeAppConfig");
            replaceRequestCommand.addRequestItem(this.createTargetItemTagElement("./User/Vendor/MSFT/EnterpriseModernAppManagement/AppManagement/nonStore/ZohoCorp.ManageEngineMDM_hfrrf6a1akhx2/AppSettingPolicy/" + jsonObject.get("key"), jsonObject.get("value").toString()));
            atomicCommand.addRequestCmd(replaceRequestCommand);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception while creating commadn for native App.", exp);
        }
    }
    
    Item createTargetItemTagElement(final String locationUri, final String data) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        item.setData(data);
        return item;
    }
}
