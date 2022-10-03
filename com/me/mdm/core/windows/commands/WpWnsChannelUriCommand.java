package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpWnsChannelUriCommand
{
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject deviceDetails) {
        final GetRequestCommand devChannelURI = new GetRequestCommand();
        devChannelURI.setRequestCmdId("GetChannelUri");
        final Item channelURIItem = this.createCommandItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/Push/ChannelURI", null);
        final Item statusItem = this.createCommandItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/Push/Status", null);
        devChannelURI.addRequestItem(channelURIItem);
        devChannelURI.addRequestItem(statusItem);
        responseSyncML.getSyncBody().addRequestCmd(devChannelURI);
    }
    
    private Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        commandItem.setData(sValue);
        return commandItem;
    }
}
