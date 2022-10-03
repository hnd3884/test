package com.me.mdm.framework.syncml.core.data;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;

@SyncMLElement(xmlElementName = "Meta")
public class Meta
{
    private String format;
    private String type;
    private String mark;
    private String size;
    private String version;
    private String nextNonce;
    private String maxMsgSize;
    private String maxObjSize;
    
    @SyncMLElement(xmlElementName = "Format")
    public String getFormat() {
        return this.format;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    
    @SyncMLElement(xmlElementName = "Type")
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    @SyncMLElement(xmlElementName = "Mark")
    public String getMark() {
        return this.mark;
    }
    
    public void setMark(final String mark) {
        this.mark = mark;
    }
    
    @SyncMLElement(xmlElementName = "Size")
    public String getSize() {
        return this.size;
    }
    
    public void setSize(final String size) {
        this.size = size;
    }
    
    @SyncMLElement(xmlElementName = "Version")
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    @SyncMLElement(xmlElementName = "NextNonce")
    public String getNextNonce() {
        return this.nextNonce;
    }
    
    public void setNextNonce(final String nextNonce) {
        this.nextNonce = nextNonce;
    }
    
    @SyncMLElement(xmlElementName = "MaxMsgSize")
    public String getMaxMsgSize() {
        return this.maxMsgSize;
    }
    
    public void setMaxMsgSize(final String maxMsgSize) {
        this.maxMsgSize = maxMsgSize;
    }
    
    @SyncMLElement(xmlElementName = "MaxObjSize")
    public String getMaxObjSize() {
        return this.maxObjSize;
    }
    
    public void setMaxObjSize(final String maxObjSize) {
        this.maxObjSize = maxObjSize;
    }
}
