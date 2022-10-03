package com.me.mdm.server.windows.profile.payload;

import com.me.mdm.framework.syncml.xml.SyncMLMessage2XMLConverterException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.framework.syncml.xml.SyncMLMessage2XMLConverter;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.core.SyncHeaderMessage;
import com.me.mdm.framework.syncml.core.SyncBodyMessage;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import com.me.mdm.framework.syncml.requestcmds.DeleteRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.AtomicRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;

public class WindowsBaseConfigPayload
{
    private SequenceRequestCommand payloadRoot;
    private AtomicRequestCommand payloadAtomicChild;
    private DeleteRequestCommand payloadPreDeleteChild;
    
    public WindowsBaseConfigPayload() {
        this.payloadRoot = null;
        this.payloadAtomicChild = null;
        this.payloadPreDeleteChild = null;
        this.payloadRoot = new SequenceRequestCommand();
        this.payloadAtomicChild = new AtomicRequestCommand();
        this.payloadPreDeleteChild = new DeleteRequestCommand();
    }
    
    public SequenceRequestCommand getPayloadContent() {
        return this.payloadRoot;
    }
    
    public DeleteRequestCommand getNonAtomicPayloadContent() {
        return this.payloadPreDeleteChild;
    }
    
    public AtomicRequestCommand getAtomicPayloadContent() {
        return this.payloadAtomicChild;
    }
    
    @Override
    public String toString() {
        String toString = null;
        final SyncMLMessage syncMLMessage = new SyncMLMessage();
        final SyncBodyMessage syncBodyMessage = new SyncBodyMessage();
        final SyncHeaderMessage syncHeaderMessage = new SyncHeaderMessage();
        if (this.payloadPreDeleteChild != null && this.payloadPreDeleteChild.getRequestItems() != null && !this.payloadPreDeleteChild.getRequestItems().isEmpty()) {
            this.payloadRoot.addRequestCmd(this.payloadPreDeleteChild);
        }
        if (this.payloadAtomicChild != null && this.payloadAtomicChild.getRequestCmds() != null && !this.payloadAtomicChild.getRequestCmds().isEmpty()) {
            this.payloadRoot.addRequestCmd(this.payloadAtomicChild);
        }
        syncBodyMessage.addRequestCmd(this.payloadRoot);
        syncMLMessage.setSyncBody(syncBodyMessage);
        syncMLMessage.setSyncHeader(syncHeaderMessage);
        final SyncMLMessage2XMLConverter convert = new SyncMLMessage2XMLConverter();
        try {
            toString = convert.transform(syncMLMessage);
        }
        catch (final SyncMLMessage2XMLConverterException ex) {
            Logger.getLogger(WindowsPayload.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toString;
    }
}
