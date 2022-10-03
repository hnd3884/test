package com.me.devicemanagement.framework.server.authorization;

public interface RoleListener
{
    void roleAdded(final RoleEvent p0);
    
    void roleDeleted(final RoleEvent p0);
    
    void roleUpdated(final RoleEvent p0);
}
