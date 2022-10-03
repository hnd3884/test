package com.me.mdm.framework.syncml.requestcmds;

import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Credential;
import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

@SyncMLElement(xmlElementName = "Get")
public class GetRequestCommand extends SyncMLRequestCommand
{
    private String lang;
    private Credential cred;
    private Meta meta;
    
    @SyncMLElement(xmlElementName = "Lang")
    public String getLang() {
        return this.lang;
    }
    
    public void setLang(final String lang) {
        this.lang = lang;
    }
    
    @SyncMLElement(xmlElementName = "Cred")
    @Override
    public Credential getRequestCredential() {
        return this.cred;
    }
    
    @Override
    public void setRequestCredential(final Credential cred) {
        this.cred = cred;
    }
    
    @SyncMLElement(xmlElementName = "Meta")
    @Override
    public Meta getMeta() {
        return this.meta;
    }
    
    @Override
    public void setMeta(final Meta meta) {
        this.meta = meta;
    }
    
    @Override
    public String getSyncMLCommandName() {
        return this.getClass().getAnnotation(SyncMLElement.class).xmlElementName();
    }
}
