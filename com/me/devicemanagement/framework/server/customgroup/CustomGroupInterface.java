package com.me.devicemanagement.framework.server.customgroup;

import com.adventnet.persistence.DataObject;

public interface CustomGroupInterface
{
    void addGroup(final CustomGroupDetails p0);
    
    void updateGroup(final CustomGroupDetails p0);
    
    void deleteGroup(final Long[] p0);
    
    Long getGroupResourceId(final String p0, final Long p1, final String p2);
    
    void addorUpdateGroup(final Long p0, final int p1, final int p2, final boolean p3, final String p4);
    
    void addGroupMemberRel(final DataObject p0, final Long p1, final Long[] p2);
    
    boolean deleteGroupResource(final Long p0, final Long p1);
    
    boolean removeMemberfromGroup(final Long p0, final Long[] p1);
    
    boolean addMembertoGroup(final Long p0, final Long[] p1);
    
    boolean removeMemberfromGroups(final Long p0, final Long[] p1);
    
    boolean addMembertoGroups(final Long p0, final Long[] p1);
}
