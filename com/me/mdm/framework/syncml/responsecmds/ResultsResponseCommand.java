package com.me.mdm.framework.syncml.responsecmds;

import java.util.List;
import com.me.mdm.framework.syncml.core.data.Item;
import java.util.HashMap;
import com.me.mdm.framework.syncml.annotations.SyncMLElement;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;

@SyncMLElement(xmlElementName = "Results")
public class ResultsResponseCommand extends SyncMLResponseCommand
{
    public HashMap transform() {
        final HashMap hashMap = new HashMap();
        final List responseItems = this.getResponseItems();
        for (int j = 0; j < responseItems.size(); ++j) {
            final Item responseItem = responseItems.get(j);
            hashMap.put(responseItem.getSource().getLocUri(), responseItem.getData());
        }
        return hashMap;
    }
    
    @Override
    public String getSyncMLCommandName() {
        return this.getClass().getAnnotation(SyncMLElement.class).xmlElementName();
    }
}
