package com.adventnet.sym.server.mdm.certificates.scepserver.digicert;

public class DigicertServerMapping
{
    private long serverId;
    private long raCertificateId;
    private long templateId;
    
    public DigicertServerMapping(final long serverId, final long raCertificateId, final long templateId) {
        this.serverId = serverId;
        this.raCertificateId = raCertificateId;
        this.templateId = templateId;
    }
    
    public long getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final long serverId) {
        this.serverId = serverId;
    }
    
    public long getRaCertificateId() {
        return this.raCertificateId;
    }
    
    public void setRaCertificateId(final long raCertificateId) {
        this.raCertificateId = raCertificateId;
    }
    
    public long getTemplateId() {
        return this.templateId;
    }
    
    public void setTemplateId(final long templateId) {
        this.templateId = templateId;
    }
}
