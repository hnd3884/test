package com.me.mdm.framework.syncml.core;

import java.util.ArrayList;
import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import java.util.List;

public class SyncBodyMessage
{
    private List<SyncMLRequestCommand> requestCmds;
    private List<SyncMLResponseCommand> responseCmds;
    private Boolean finalMessage;
    
    @SyncMLElement(xmlElementName = "responseCmds")
    public List<SyncMLResponseCommand> getResponseCmds() {
        return this.responseCmds;
    }
    
    public void setResponseCmds(final List<SyncMLResponseCommand> cmds) {
        this.responseCmds = cmds;
    }
    
    public void addResponseCmd(final SyncMLResponseCommand cmd) {
        if (this.responseCmds == null) {
            this.responseCmds = new ArrayList<SyncMLResponseCommand>();
        }
        if (cmd.getCmdId() == null) {
            cmd.setCmdId(String.valueOf(this.responseCmds.size() + 1));
        }
        this.responseCmds.add(cmd);
    }
    
    @SyncMLElement(xmlElementName = "requestCmds")
    public List<SyncMLRequestCommand> getRequestCmds() {
        return this.requestCmds;
    }
    
    public void setRequestCmds(final List<SyncMLRequestCommand> cmds) {
        this.requestCmds = cmds;
    }
    
    public void addRequestCmd(final SyncMLRequestCommand cmd) {
        if (this.requestCmds == null) {
            this.requestCmds = new ArrayList<SyncMLRequestCommand>();
        }
        if (cmd.getRequestCmdId() == null) {
            cmd.setRequestCmdId(String.valueOf(this.requestCmds.size() + 1));
        }
        this.requestCmds.add(cmd);
    }
    
    @SyncMLElement(xmlElementName = "Final")
    public Boolean getFinalMessage() {
        return this.finalMessage;
    }
    
    public void setFinalMessage(final Boolean finalMessage) {
        this.finalMessage = finalMessage;
    }
}
