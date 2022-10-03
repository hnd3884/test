package com.me.mdm.framework.syncml.responsecmds;

import com.me.mdm.framework.syncml.core.data.Challenge;
import com.me.mdm.framework.syncml.core.data.Credential;
import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;

@SyncMLElement(xmlElementName = "Status")
public class StatusResponseCommand extends SyncMLResponseCommand
{
    private String cmd;
    private Credential cred;
    private Challenge chal;
    private Object data;
    
    @SyncMLElement(xmlElementName = "Cred")
    public Credential getCred() {
        return this.cred;
    }
    
    public void setCred(final Credential cred) {
        this.cred = cred;
    }
    
    @SyncMLElement(xmlElementName = "Chal")
    public Challenge getChal() {
        return this.chal;
    }
    
    public void setChal(final Challenge chal) {
        this.chal = chal;
    }
    
    @SyncMLElement(xmlElementName = "Data")
    @Override
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
