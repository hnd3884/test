package com.me.mdm.framework.syncml.requestcmds;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

@SyncMLElement(xmlElementName = "Alert")
public class AlertRequestCommand extends SyncMLRequestCommand
{
    private String lang;
    private Object data;
    
    @SyncMLElement(xmlElementName = "Lang")
    public String getLang() {
        return this.lang;
    }
    
    public void setLang(final String lang) {
        this.lang = lang;
    }
    
    @SyncMLElement(xmlElementName = "Data")
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object data) {
        this.data = data;
    }
    
    @Override
    public String getSyncMLCommandName() {
        return this.getClass().getAnnotation(SyncMLElement.class).xmlElementName();
    }
}
