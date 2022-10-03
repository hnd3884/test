package com.me.devicemanagement.framework.server.customgroup;

import java.util.Properties;

public class CustomGroupEvent
{
    public Long customGroupID;
    public Long customerID;
    public Properties cgProperties;
    
    public CustomGroupEvent(final Long customGroupID) {
        this.customGroupID = null;
        this.customerID = null;
        this.cgProperties = null;
        this.customGroupID = customGroupID;
    }
    
    public CustomGroupEvent(final Long customGroupID, final Long customerID) {
        this.customGroupID = null;
        this.customerID = null;
        this.cgProperties = null;
        this.customGroupID = customGroupID;
        this.customerID = customerID;
    }
    
    public CustomGroupEvent(final Long customGroupID, final Long customerID, final Properties cgProperties) {
        this.customGroupID = null;
        this.customerID = null;
        this.cgProperties = null;
        this.customGroupID = customGroupID;
        this.customerID = customerID;
        this.cgProperties = cgProperties;
    }
    
    @Override
    public String toString() {
        return this.customGroupID.toString();
    }
}
