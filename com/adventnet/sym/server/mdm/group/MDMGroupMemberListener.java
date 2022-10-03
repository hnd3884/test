package com.adventnet.sym.server.mdm.group;

public interface MDMGroupMemberListener
{
    void groupMemberAdded(final MDMGroupMemberEvent p0);
    
    void groupMemberRemoved(final MDMGroupMemberEvent p0);
}
