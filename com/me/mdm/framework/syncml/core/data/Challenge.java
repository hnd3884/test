package com.me.mdm.framework.syncml.core.data;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;

@SyncMLElement(xmlElementName = "Chal")
public class Challenge
{
    private Meta meta;
    
    @SyncMLElement(xmlElementName = "Meta")
    public Meta getMeta() {
        return this.meta;
    }
    
    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
}
