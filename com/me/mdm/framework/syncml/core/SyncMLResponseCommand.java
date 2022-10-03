package com.me.mdm.framework.syncml.core;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.ArrayList;
import com.me.mdm.framework.syncml.core.data.Meta;

public abstract class SyncMLResponseCommand
{
    private String cmdId;
    private String msgRef;
    private String cmdRef;
    private Object data;
    private Meta meta;
    private String targetRef;
    private String sourceRef;
    private ArrayList<Item> items;
    private String cmd;
    
    @SyncMLElement(xmlElementName = "CmdID", isMandatory = true)
    public String getCmdId() {
        return this.cmdId;
    }
    
    public void setCmdId(final String cmdId) {
        this.cmdId = cmdId;
    }
    
    @SyncMLElement(xmlElementName = "MsgRef")
    public String getMsgRef() {
        return this.msgRef;
    }
    
    public void setMsgRef(final String msgRef) {
        this.msgRef = msgRef;
    }
    
    @SyncMLElement(xmlElementName = "CmdRef")
    public String getCmdRef() {
        return this.cmdRef;
    }
    
    @SyncMLElement(xmlElementName = "Data")
    public Object getData() {
        return this.data;
    }
    
    public void setCmdRef(final String cmdRef) {
        this.cmdRef = cmdRef;
    }
    
    @SyncMLElement(xmlElementName = "Meta")
    public Meta getMeta() {
        return this.meta;
    }
    
    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
    
    @SyncMLElement(xmlElementName = "TargetRef")
    public String getTargetRef() {
        return this.targetRef;
    }
    
    public void setTargetRef(final String targetRef) {
        this.targetRef = targetRef;
    }
    
    @SyncMLElement(xmlElementName = "SourceRef")
    public String getSourceRef() {
        return this.sourceRef;
    }
    
    public void setSourceRef(final String sourceRef) {
        this.sourceRef = sourceRef;
    }
    
    @SyncMLElement(xmlElementName = "Item")
    public ArrayList<Item> getResponseItems() {
        return this.items;
    }
    
    public void setResponseItems(final ArrayList<Item> items) {
        this.items = items;
    }
    
    public void addResponseItem(final Item item) {
        if (this.items == null) {
            this.items = new ArrayList<Item>();
        }
        this.items.add(item);
    }
    
    @SyncMLElement(xmlElementName = "Cmd")
    public String getCmd() {
        return this.cmd;
    }
    
    public void setCmd(final String cmd) {
        this.cmd = cmd;
    }
    
    public abstract String getSyncMLCommandName();
}
