package com.me.mdm.framework.syncml.core;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;

@SyncMLElement(xmlElementName = "SyncML")
public class SyncMLMessage
{
    private SyncHeaderMessage syncHeader;
    private SyncBodyMessage syncBody;
    
    @SyncMLElement(xmlElementName = "SyncHdr", isMandatory = true)
    public SyncHeaderMessage getSyncHeader() {
        return this.syncHeader;
    }
    
    public void setSyncHeader(final SyncHeaderMessage header) {
        this.syncHeader = header;
    }
    
    @SyncMLElement(xmlElementName = "SyncBody", isMandatory = true)
    public SyncBodyMessage getSyncBody() {
        return this.syncBody;
    }
    
    public void setSyncBody(final SyncBodyMessage body) {
        this.syncBody = body;
    }
}
