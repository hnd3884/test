package com.me.devicemanagement.framework.webclient.message;

public interface DMMessageAuditAPI
{
    void updateClickCountAudit(final Long p0, final Long p1, final boolean p2, final boolean p3, final boolean p4);
    
    void addOrUpdateMsgCountAudit(final Long p0, final Long p1, final int p2, final boolean p3);
    
    void updateMsgTabClickCountAudit(final Long p0, final Long p1, final int p2);
}
