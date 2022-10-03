package com.me.devicemanagement.framework.server.admin;

import java.util.Properties;
import java.util.logging.Logger;

public class SoMEvent
{
    private Logger logger;
    public Long resourceID;
    public Long customerID;
    public Properties resourceProperties;
    
    public SoMEvent(final Long resourceID) {
        this.logger = Logger.getLogger(SoMEvent.class.getName());
        this.resourceID = null;
        this.customerID = null;
        this.resourceProperties = null;
        this.resourceID = resourceID;
    }
    
    public SoMEvent(final Long resourceID, final Long customerID) {
        this.logger = Logger.getLogger(SoMEvent.class.getName());
        this.resourceID = null;
        this.customerID = null;
        this.resourceProperties = null;
        this.resourceID = resourceID;
        this.customerID = customerID;
    }
    
    @Override
    public String toString() {
        return this.resourceID.toString();
    }
}
