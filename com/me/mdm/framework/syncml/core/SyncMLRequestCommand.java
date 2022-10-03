package com.me.mdm.framework.syncml.core;

import java.util.ArrayList;
import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.List;
import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Credential;

public abstract class SyncMLRequestCommand
{
    private String requestCmdId;
    private String noResponseForRequest;
    private Credential requestCredential;
    private Meta requestMeta;
    private List<Item> requestItems;
    
    protected SyncMLRequestCommand() {
    }
    
    @SyncMLElement(xmlElementName = "CmdID", isMandatory = true)
    public String getRequestCmdId() {
        return this.requestCmdId;
    }
    
    public void setRequestCmdId(final String cmdId) {
        this.requestCmdId = cmdId;
    }
    
    @SyncMLElement(xmlElementName = "NoResp")
    public String getNoResponseForRequest() {
        return this.noResponseForRequest;
    }
    
    public void setNoResponseForRequest(final String noResp) {
        this.noResponseForRequest = noResp;
    }
    
    public void addRequestItem(final Item item) {
        if (this.requestItems == null) {
            this.requestItems = new ArrayList<Item>();
        }
        this.requestItems.add(item);
    }
    
    @SyncMLElement(xmlElementName = "Item")
    public List<Item> getRequestItems() {
        return this.requestItems;
    }
    
    public void setRequestItems(final List<Item> items) {
        this.requestItems = items;
    }
    
    @SyncMLElement(xmlElementName = "Cred")
    public Credential getRequestCredential() {
        return this.requestCredential;
    }
    
    public void setRequestCredential(final Credential cred) {
        this.requestCredential = cred;
    }
    
    @SyncMLElement(xmlElementName = "Meta")
    public Meta getMeta() {
        return this.requestMeta;
    }
    
    public void setMeta(final Meta meta) {
        this.requestMeta = meta;
    }
    
    public abstract String getSyncMLCommandName();
}
