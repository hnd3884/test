package com.me.mdm.framework.syncml.core;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Credential;
import com.me.mdm.framework.syncml.core.data.Location;

public class SyncHeaderMessage
{
    private String dtdVersion;
    private String protocolVersion;
    private String sessionID;
    private String msgID;
    private Location target;
    private Location source;
    private String responseURI;
    private Boolean noResp;
    private Credential credential;
    private Meta meta;
    
    @SyncMLElement(xmlElementName = "VerDTD")
    public String getVerDTD() {
        return this.dtdVersion;
    }
    
    public void setVerDTD(final String verDTD) {
        this.dtdVersion = verDTD;
    }
    
    @SyncMLElement(xmlElementName = "VerProto")
    public String getVerProto() {
        return this.protocolVersion;
    }
    
    public void setVerProto(final String verProto) {
        this.protocolVersion = verProto;
    }
    
    @SyncMLElement(xmlElementName = "SessionID")
    public String getSessionID() {
        return this.sessionID;
    }
    
    public void setSessionID(final String sessionID) {
        this.sessionID = sessionID;
    }
    
    @SyncMLElement(xmlElementName = "MsgID")
    public String getMsgID() {
        return this.msgID;
    }
    
    public void setMsgID(final String msgID) {
        this.msgID = msgID;
    }
    
    @SyncMLElement(xmlElementName = "Target")
    public Location getTarget() {
        return this.target;
    }
    
    public void setTarget(final Location target) {
        this.target = target;
    }
    
    @SyncMLElement(xmlElementName = "Source")
    public Location getSource() {
        return this.source;
    }
    
    public void setSource(final Location source) {
        this.source = source;
    }
    
    @SyncMLElement(xmlElementName = "Meta")
    public Meta getMeta() {
        return this.meta;
    }
    
    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
    
    @SyncMLElement(xmlElementName = "RespURI")
    public String getRespURI() {
        return this.responseURI;
    }
    
    public void setRespURI(final String respURI) {
        this.responseURI = respURI;
    }
    
    @SyncMLElement(xmlElementName = "NoResp")
    public Boolean getNoResp() {
        return this.noResp;
    }
    
    public void setNoResp(final Boolean noResp) {
        this.noResp = noResp;
    }
    
    @SyncMLElement(xmlElementName = "Cred")
    public Credential getCredential() {
        return this.credential;
    }
    
    public void setCredential(final Credential credential) {
        this.credential = credential;
    }
}
