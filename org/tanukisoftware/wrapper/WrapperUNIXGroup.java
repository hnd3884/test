package org.tanukisoftware.wrapper;

public class WrapperUNIXGroup extends WrapperGroup
{
    private int m_gid;
    
    WrapperUNIXGroup(final int gid, final String name) {
        super(name);
        this.m_gid = gid;
    }
    
    public int getGID() {
        return this.m_gid;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("WrapperUNIXGroup[");
        sb.append(this.getGID());
        sb.append(this.getGroup());
        sb.append("]");
        return sb.toString();
    }
}
