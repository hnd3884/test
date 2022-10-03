package org.tanukisoftware.wrapper;

public abstract class WrapperGroup
{
    private String m_group;
    
    WrapperGroup(final String group) {
        this.m_group = group;
    }
    
    public String getGroup() {
        return this.m_group;
    }
}
