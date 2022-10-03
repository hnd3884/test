package com.me.devicemanagement.framework.server.admin;

public interface DomainListener
{
    void domainsAdded(final SoMEvent[] p0);
    
    void domainsDeleted(final SoMEvent[] p0);
    
    void domainsUpdated(final SoMEvent[] p0);
    
    void domainsManaged(final SoMEvent[] p0);
    
    void domainsNotManaged(final SoMEvent[] p0);
}
