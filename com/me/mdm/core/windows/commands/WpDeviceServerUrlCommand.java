package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.agent.handlers.windows.WindowsMigrationUtil;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpDeviceServerUrlCommand
{
    private Logger logger;
    
    public WpDeviceServerUrlCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final String commandName = (String)jsonObject.get("commandName");
            final String newServerUrl = WindowsMigrationUtil.getInstance().getMigratedURL(jsonObject);
            final ReplaceRequestCommand urlReplaceCommand = new ReplaceRequestCommand();
            urlReplaceCommand.setRequestCmdId(commandName);
            final String dmClientBaseLocURI = "./Vendor/MSFT/DMClient/Provider/MEMDM/ManagementServiceAddress";
            urlReplaceCommand.addRequestItem(this.createTargetItemTagElement(dmClientBaseLocURI, newServerUrl, null));
            final AtomicRequestCommand urlReplaceAtomicCommand = new AtomicRequestCommand();
            urlReplaceAtomicCommand.setRequestCmdId(commandName);
            urlReplaceAtomicCommand.addRequestCmd(urlReplaceCommand);
            responseSyncML.getSyncBody().addRequestCmd(urlReplaceAtomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in WpDeviceServerUrlCommand.processRequestForDEPToken {0}", ex);
        }
    }
    
    private Item createTargetItemTagElement(final String locationUri, final String itemData, final String sMetaFormat) {
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
