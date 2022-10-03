package org.tanukisoftware.wrapper;

public class WrapperWin32Group extends WrapperGroup
{
    private String m_sid;
    private String m_domain;
    
    WrapperWin32Group(final String sid, final String user, final String domain) {
        super(user);
        this.m_sid = sid;
        this.m_domain = domain;
    }
    
    public String getSID() {
        return this.m_sid;
    }
    
    public String getDomain() {
        return this.m_domain;
    }
    
    public String getAccount() {
        return this.m_domain + "/" + this.getGroup();
    }
    
    public String toString() {
        return "WrapperWin32Group[" + this.getAccount() + "]";
    }
}
