package com.me.devicemanagement.framework.server.authorization;

import java.util.logging.Logger;

public class RoleEvent
{
    private Logger logger;
    public Long roleID;
    
    public RoleEvent(final Long roleID) {
        this.logger = Logger.getLogger(RoleEvent.class.getName());
        this.roleID = null;
        this.roleID = roleID;
    }
    
    @Override
    public String toString() {
        return this.roleID.toString();
    }
}
