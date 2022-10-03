package com.me.idps.core.crud;

import java.util.Properties;

public class DomainEvent
{
    public Integer clientID;
    public Long domainID;
    public Long customerID;
    public Properties domainProperties;
    
    public DomainEvent(final Long domainID, final Long customerID, final Integer clientID) {
        this.clientID = null;
        this.domainID = null;
        this.customerID = null;
        this.domainProperties = null;
        this.domainID = domainID;
        this.clientID = clientID;
        this.customerID = customerID;
    }
}
