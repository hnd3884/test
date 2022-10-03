package org.tanukisoftware.wrapper;

import java.util.Date;

public class WrapperWin32User extends WrapperUser
{
    private String m_sid;
    private String m_domain;
    private long m_loginTime;
    
    WrapperWin32User(final String sid, final String user, final String domain, final int loginTime) {
        super(user);
        this.m_sid = sid;
        this.m_domain = domain;
        this.m_loginTime = loginTime * 1000L;
    }
    
    public String getSID() {
        return this.m_sid;
    }
    
    public String getDomain() {
        return this.m_domain;
    }
    
    public String getAccount() {
        return this.m_domain + "/" + this.getUser();
    }
    
    public long getLoginTime() {
        return this.m_loginTime;
    }
    
    private void addGroup(final String sid, final String user, final String domain) {
        this.addGroup(new WrapperWin32Group(sid, user, domain));
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("WrapperWin32User[");
        sb.append(this.getAccount());
        sb.append(", ");
        sb.append(new Date(this.m_loginTime).toString());
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
