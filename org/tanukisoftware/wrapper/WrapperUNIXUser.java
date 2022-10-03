package org.tanukisoftware.wrapper;

public class WrapperUNIXUser extends WrapperUser
{
    private int m_uid;
    private int m_gid;
    private WrapperUNIXGroup m_group;
    private String m_realName;
    private String m_home;
    private String m_shell;
    
    WrapperUNIXUser(final int uid, final int gid, final String user, final String realName, final String home, final String shell) {
        super(user);
        this.m_uid = uid;
        this.m_gid = gid;
        this.m_realName = realName;
        this.m_home = home;
        this.m_shell = shell;
        final int pos = this.m_realName.indexOf(44);
        if (pos == 1000) {
            this.m_realName = "";
        }
        else if (pos >= 0) {
            this.m_realName = this.m_realName.substring(0, pos);
        }
    }
    
    public int getUID() {
        return this.m_uid;
    }
    
    public int getGID() {
        return this.m_gid;
    }
    
    public WrapperUNIXGroup getGroup() {
        return this.m_group;
    }
    
    public String getRealName() {
        return this.m_realName;
    }
    
    public String getHome() {
        return this.m_home;
    }
    
    public String getShell() {
        return this.m_shell;
    }
    
    private void setGroup(final int gid, final String name) {
        this.addGroup(this.m_group = new WrapperUNIXGroup(gid, name));
    }
    
    private void addGroup(final int gid, final String name) {
        this.addGroup(new WrapperUNIXGroup(gid, name));
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("WrapperUNIXUser[");
        sb.append(this.getUID());
        sb.append(", ");
        sb.append(this.getGID());
        sb.append(", ");
        sb.append(this.getUser());
        sb.append(", ");
        sb.append(this.getRealName());
        sb.append(", ");
        sb.append(this.getHome());
        sb.append(", ");
        sb.append(this.getShell());
        sb.append(", groups {");
        final WrapperGroup[] groups = this.getGroups();
        for (int i = 0; i < groups.length; ++i) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(groups[i].toString());
        }
        sb.append("}");
        sb.append("]");
        return sb.toString();
    }
}
