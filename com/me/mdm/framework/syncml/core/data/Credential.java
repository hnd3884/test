package com.me.mdm.framework.syncml.core.data;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;

public class Credential
{
    private Meta meta;
    private Object data;
    
    @SyncMLElement(xmlElementName = "Meta")
    public Meta getMeta() {
        return this.meta;
    }
    
    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
    
    @SyncMLElement(xmlElementName = "Data")
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object data) {
        this.data = data;
    }
}
