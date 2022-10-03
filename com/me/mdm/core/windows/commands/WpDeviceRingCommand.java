package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpDeviceRingCommand
{
    public void processRequest(final SyncMLMessage responseSyncML) {
        try {
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("DeviceRing");
            final ExecRequestCommand execCommand = new ExecRequestCommand();
            execCommand.setRequestCmdId("DeviceRing");
            execCommand.addRequestItem(this.createTargetItemTagElement("./Vendor/MSFT/RemoteRing/Ring"));
            atomicCommand.addRequestCmd(execCommand);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
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
