package com.adventnet.sym.server.mdm.inv;

public class MDMInvdetails
{
    public Long resourceId;
    public String strData;
    public Integer scope;
    
    public MDMInvdetails(final Long resourceId, final String strData) {
        this.resourceId = resourceId;
        this.strData = strData;
    }
    
    public MDMInvdetails(final Long resourceId, final String strData, final Integer scope) {
        this.resourceId = resourceId;
        this.strData = strData;
        this.scope = scope;
    }
}
