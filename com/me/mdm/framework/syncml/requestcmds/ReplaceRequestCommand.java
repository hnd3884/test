package com.me.mdm.framework.syncml.requestcmds;

import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;

@SyncMLElement(xmlElementName = "Replace")
public class ReplaceRequestCommand extends SyncMLRequestCommand
{
    @Override
    public String getSyncMLCommandName() {
        return this.getClass().getAnnotation(SyncMLElement.class).xmlElementName();
    }
}
