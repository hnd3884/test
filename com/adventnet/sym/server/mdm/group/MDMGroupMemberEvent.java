package com.adventnet.sym.server.mdm.group;

import java.util.Properties;

public class MDMGroupMemberEvent
{
    public Long customerId;
    public Long groupID;
    public Long[] memberIds;
    public Properties groupProp;
    public Integer groupType;
    public Long userId;
    public Boolean isMove;
    
    public MDMGroupMemberEvent(final Long groupID, final Long[] memberIds) {
        this.customerId = null;
        this.groupID = null;
        this.memberIds = null;
        this.groupProp = null;
        this.groupType = null;
        this.userId = null;
        this.isMove = null;
        this.groupID = groupID;
        this.memberIds = memberIds;
    }
    
    @Override
    public String toString() {
        return this.groupID.toString();
    }
}
