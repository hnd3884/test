package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.logging.Logger;

public class WpSideLoadCommand
{
    Logger logger;
    public static WpSideLoadCommand wpSideLoadCommand;
    
    public WpSideLoadCommand() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject cmdParams) {
        try {
            final String cmd = String.valueOf(cmdParams.get("CommandUUID"));
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId(cmd);
            final ReplaceRequestCommand enableSideLoad = new ReplaceRequestCommand();
            enableSideLoad.setRequestCmdId(cmd);
            final Item item = this.getSideLoadItemForCmd(cmd);
            final ArrayList items = new ArrayList();
            items.add(item);
            enableSideLoad.setRequestItems(items);
            atomicCommand.addRequestCmd(enableSideLoad);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception in WpSideLoadCommand : {0}", exp);
        }
    }
    
    private Item getSideLoadItemForCmd(final String cmd) {
        Item item = null;
        if (cmd.equals("EnableSideloadApps")) {
            item = this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/ApplicationManagement/AllowAllTrustedApps", "1");
        }
        else if (cmd.equals("DisableSideloadApps")) {
            item = this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/ApplicationManagement/AllowAllTrustedApps", "0");
        }
        else if (cmd.equals("SideloadNotConfigured")) {
            item = this.createTargetItemTagElement("./Vendor/MSFT/Policy/Config/ApplicationManagement/AllowAllTrustedApps", "65535");
        }
        return item;
    }
    
    private Item createTargetItemTagElement(final String locationUri, final String data) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        final Meta meta = new Meta();
        meta.setFormat("int");
        item.setMeta(meta);
        item.setData(data);
        return item;
    }
    
    static {
        WpSideLoadCommand.wpSideLoadCommand = null;
    }
}
