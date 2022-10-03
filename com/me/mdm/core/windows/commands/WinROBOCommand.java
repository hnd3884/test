package com.me.mdm.core.windows.commands;

import com.me.mdm.framework.syncml.core.data.Location;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ExecRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;

public class WinROBOCommand
{
    public void processRequest(final SyncMLMessage response, final JSONObject jsonObject) {
        final AtomicRequestCommand atomicCommand = new AtomicRequestCommand();
        atomicCommand.setRequestCmdId("TriggerROBO");
        final String locUri = "./Vendor/MSFT/CertificateStore/My/WSTEP/Renew/RenewNow";
        final ExecRequestCommand execCommand = new ExecRequestCommand();
        execCommand.setRequestCmdId("TriggerROBO");
        execCommand.addRequestItem(this.createTargetItemTagElement(locUri));
        atomicCommand.addRequestCmd(execCommand);
        response.getSyncBody().addRequestCmd(atomicCommand);
        response.getSyncBody().setFinalMessage(Boolean.TRUE);
    }
    
    Item createTargetItemTagElement(final String locationUri) {
        final Item item = new Item();
        final Location location = new Location(locationUri);
        item.setTarget(location);
        return item;
    }
}
