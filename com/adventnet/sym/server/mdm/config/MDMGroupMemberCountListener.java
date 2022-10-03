package com.adventnet.sym.server.mdm.config;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberEvent;
import com.adventnet.sym.server.mdm.group.MDMGroupMemberListener;

public class MDMGroupMemberCountListener implements MDMGroupMemberListener
{
    private void updateGroupCount(final MDMGroupMemberEvent groupEvent) {
        Boolean noGroupCountUpdate = Boolean.FALSE;
        if (groupEvent.groupProp != null) {
            final Properties props = groupEvent.groupProp;
            if (props.containsKey("noGroupCountUpdate")) {
                noGroupCountUpdate = Boolean.valueOf(String.valueOf(((Hashtable<K, Object>)props).get("noGroupCountUpdate")));
            }
        }
        if (!noGroupCountUpdate) {
            ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
        }
    }
    
    @Override
    public void groupMemberAdded(final MDMGroupMemberEvent groupEvent) {
        this.updateGroupCount(groupEvent);
    }
    
    @Override
    public void groupMemberRemoved(final MDMGroupMemberEvent groupEvent) {
        this.updateGroupCount(groupEvent);
    }
}
