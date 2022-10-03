package com.adventnet.sym.server.mdm.group;

import org.json.JSONObject;

public class GroupEvent
{
    public Long groupID;
    public Long customerID;
    public Long[] memberIds;
    public int groupCategory;
    public int platformType;
    public JSONObject integrationJson;
    
    public GroupEvent(final Long groupID) {
        this.groupID = null;
        this.customerID = null;
        this.memberIds = null;
        this.groupCategory = 1;
        this.integrationJson = null;
        this.groupID = groupID;
    }
    
    public GroupEvent(final Long groupID, final Long customerID) {
        this.groupID = null;
        this.customerID = null;
        this.memberIds = null;
        this.groupCategory = 1;
        this.integrationJson = null;
        this.groupID = groupID;
        this.customerID = customerID;
    }
    
    public GroupEvent(final Long groupID, final Long customerID, final Long[] memberIds) {
        this.groupID = null;
        this.customerID = null;
        this.memberIds = null;
        this.groupCategory = 1;
        this.integrationJson = null;
        this.groupID = groupID;
        this.customerID = customerID;
        this.memberIds = memberIds;
    }
    
    public GroupEvent(final Long groupID, final Long customerID, final Long[] memberIds, final JSONObject integrationJson) {
        this.groupID = null;
        this.customerID = null;
        this.memberIds = null;
        this.groupCategory = 1;
        this.integrationJson = null;
        this.groupID = groupID;
        this.customerID = customerID;
        this.memberIds = memberIds;
        this.integrationJson = integrationJson;
    }
    
    @Override
    public String toString() {
        return this.groupID.toString();
    }
}
