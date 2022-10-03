package com.me.idps.core.crud;

public interface DMDomainListener
{
    void domainsAdded(final DomainEvent[] p0);
    
    void domainsPreDelete(final DomainEvent[] p0);
    
    void domainsDeleted(final DomainEvent[] p0);
    
    void domainsUpdated(final DomainEvent[] p0);
}
