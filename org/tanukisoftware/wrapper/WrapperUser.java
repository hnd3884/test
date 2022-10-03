package org.tanukisoftware.wrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class WrapperUser
{
    private String m_user;
    private List m_groups;
    
    WrapperUser(final String user) {
        this.m_groups = new ArrayList();
        this.m_user = user;
    }
    
    public String getUser() {
        return this.m_user;
    }
    
    void addGroup(final WrapperGroup group) {
        this.m_groups.add(group);
    }
    
    public WrapperGroup[] getGroups() {
        final WrapperGroup[] groups = new WrapperGroup[this.m_groups.size()];
        this.m_groups.toArray(groups);
        return groups;
    }
}
