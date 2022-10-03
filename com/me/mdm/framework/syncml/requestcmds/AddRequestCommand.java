package com.me.mdm.framework.syncml.requestcmds;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

@SyncMLElement(xmlElementName = "Add")
public class AddRequestCommand extends SyncMLRequestCommand
{
    private String lang;
    
    @SyncMLElement(xmlElementName = "Lang")
    public String getLang() {
        return this.lang;
    }
    
    public void setLang(final String lang) {
        this.lang = lang;
    }
    
    @Override
    public String getSyncMLCommandName() {
        return this.getClass().getAnnotation(SyncMLElement.class).xmlElementName();
    }
}
