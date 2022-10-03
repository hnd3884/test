package com.adventnet.sym.server.mdm.group;

public interface MDMCustomGroupListner
{
    void customGroupAdded(final GroupEvent p0);
    
    void customGroupDeleted(final GroupEvent p0);
    
    void customGroupModified(final GroupEvent p0);
    
    void customGroupPreDelete(final GroupEvent p0);
}
