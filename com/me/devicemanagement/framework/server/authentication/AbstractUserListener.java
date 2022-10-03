package com.me.devicemanagement.framework.server.authentication;

public class AbstractUserListener implements UserListener
{
    @Override
    public void userAdded(final UserEvent userEvent) {
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
    }
    
    @Override
    public void userModified(final UserEvent userEvent) {
    }
    
    @Override
    public void userManagedComputerChanged(final ScopeEvent scopeEvent) {
    }
    
    @Override
    public void adminUserDeleted(final UserEvent userEvent) {
    }
}
