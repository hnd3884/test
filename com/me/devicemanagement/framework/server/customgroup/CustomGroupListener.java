package com.me.devicemanagement.framework.server.customgroup;

public interface CustomGroupListener
{
    void customGroupAdded(final CustomGroupEvent p0);
    
    void customGroupDeleted(final CustomGroupEvent p0);
    
    void customGroupModified(final CustomGroupEvent p0);
    
    default void cgModifiedForCompDeletedEvent(final CustomGroupEvent customGroupEvent) {
    }
}
