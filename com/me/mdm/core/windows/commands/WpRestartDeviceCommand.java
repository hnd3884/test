package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpRestartDeviceCommand
{
    public void processRequest(final SyncMLMessage responseSyncML) {
        try {
            final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
            atomicCommand.setRequestCmdId("RestartDevice");
            final ExecRequestCommand execCommand = new ExecRequestCommand();
            execCommand.addRequestItem(this.createCommandItemTagElement("./Vendor/MSFT/Reboot/RebootNow", null));
            atomicCommand.addRequestCmd(execCommand);
            responseSyncML.getSyncBody().addRequestCmd(atomicCommand);
            responseSyncML.getSyncBody().setFinalMessage(Boolean.TRUE);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    private Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        if (sValue != null) {
            commandItem.setData(sValue);
        }
        return commandItem;
    }
}
