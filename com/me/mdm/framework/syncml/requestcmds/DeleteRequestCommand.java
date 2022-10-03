package com.me.mdm.framework.syncml.requestcmds;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

@SyncMLElement(xmlElementName = "Delete")
public class DeleteRequestCommand extends SyncMLRequestCommand
{
    private Boolean archive;
    private Boolean sftDel;
    
    @SyncMLElement(xmlElementName = "Archive")
    public Boolean getArchive() {
        return this.archive;
    }
    
    public void setArchive(final Boolean archive) {
        this.archive = archive;
    }
    
    @SyncMLElement(xmlElementName = "SftDel")
    public Boolean getSftDel() {
        return this.sftDel;
    }
    
    public void setSftDel(final Boolean sftDel) {
        this.sftDel = sftDel;
    }
    
    @Override
    public String getSyncMLCommandName() {
        return this.getClass().getAnnotation(SyncMLElement.class).xmlElementName();
    }
}
