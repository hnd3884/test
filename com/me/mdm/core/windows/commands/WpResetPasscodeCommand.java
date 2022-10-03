package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WpResetPasscodeCommand
{
    public void processRequest(final SyncMLMessage responseSyncML) {
        try {
            final SequenceRequestCommand sequenceCommand = new SequenceRequestCommand();
            sequenceCommand.setRequestCmdId("ResetPasscode");
            final ExecRequestCommand execCommand = new ExecRequestCommand();
            execCommand.setRequestCmdId("ResetPasscode");
            execCommand.addRequestItem(this.createTargetItemTagElement("./Vendor/MSFT/RemoteLock/LockAndResetPIN"));
            final GetRequestCommand getCommand = new GetRequestCommand();
            getCommand.setRequestCmdId("ResetPasscode");
            getCommand.addRequestItem(this.createTargetItemTagElement("./Vendor/MSFT/RemoteLock/NewPINValue"));
            sequenceCommand.addRequestCmd(execCommand);
            sequenceCommand.addRequestCmd(getCommand);
            responseSyncML.getSyncBody().addRequestCmd(sequenceCommand);
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
