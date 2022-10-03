package com.me.mdm.framework.syncml.requestcmds;

import java.util.ArrayList;
import java.util.List;
import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

@SyncMLElement(xmlElementName = "Atomic")
public class AtomicRequestCommand extends SyncMLRequestCommand
{
    private List<SyncMLRequestCommand> cmds;
    
    @SyncMLElement
    public List<SyncMLRequestCommand> getRequestCmds() {
        return this.cmds;
    }
    
    public void setRequestCmds(final List<SyncMLRequestCommand> cmds) {
        this.cmds = cmds;
    }
    
    public void addRequestCmd(final SyncMLRequestCommand cmd) {
        if (this.cmds == null) {
            this.cmds = new ArrayList<SyncMLRequestCommand>();
        }
        if (cmd.getRequestCmdId() == null) {
            cmd.setRequestCmdId(String.valueOf(System.currentTimeMillis()));
        }
        this.cmds.add(cmd);
    }
    
    @Override
    public String getSyncMLCommandName() {
        return this.getClass().getAnnotation(SyncMLElement.class).xmlElementName();
    }
}
