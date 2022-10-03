package com.adventnet.sym.server.mdm.core;

public interface ManagedUserListener
{
    void userAdded(final UserEvent p0);
    
    void userDeleted(final UserEvent p0);
    
    void userDetailsModified(final UserEvent p0);
    
    void userTrashed(final UserEvent p0);
}
