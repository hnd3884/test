package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.util.HashMap;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpWnsRegistrationCommand
{
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject deviceDetails) {
        try {
            final String sPFN = (String)MDMApiFactoryProvider.getSecureKeyProviderAPI().getWindowsWakeUpCredentials().get(1).get("PFN");
            final ReplaceRequestCommand devPFNSet = new ReplaceRequestCommand();
            devPFNSet.setRequestCmdId("DeviceCommunicationPush");
            final Item pfnItem = this.createCommandItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/Push/PFN", sPFN);
            devPFNSet.addRequestItem(pfnItem);
            final GetRequestCommand devChannelURI = new GetRequestCommand();
            devChannelURI.setRequestCmdId("DeviceCommunicationPush");
            final Item channelURIItem = this.createCommandItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/Push/ChannelURI", null);
            devChannelURI.addRequestItem(channelURIItem);
            final Item statusItem = this.createCommandItemTagElement("./Vendor/MSFT/DMClient/Provider/MEMDM/Push/Status", null);
            devChannelURI.addRequestItem(statusItem);
            final SequenceRequestCommand sequence = new SequenceRequestCommand();
            sequence.setRequestCmdId("DeviceCommunicationPush");
            sequence.addRequestCmd(devPFNSet);
            sequence.addRequestCmd(devChannelURI);
            responseSyncML.getSyncBody().addRequestCmd(sequence);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        commandItem.setData(sValue);
        return commandItem;
    }
}
