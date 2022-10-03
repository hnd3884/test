package com.me.devicemanagement.framework.server.authentication;

public interface UserListener
{
    void userAdded(final UserEvent p0);
    
    void userDeleted(final UserEvent p0);
    
    void userModified(final UserEvent p0);
    
    void userManagedComputerChanged(final ScopeEvent p0);
    
    void adminUserDeleted(final UserEvent p0);
}
