package com.me.devicemanagement.framework.server.customer;

import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.Properties;
import java.util.logging.Logger;

public class CustomerEvent
{
    private Logger logger;
    public Long customerID;
    public Properties customerProperties;
    
    public CustomerEvent(final Long customerID, final Properties customerProperties) {
        this.logger = SyMLogger.getCustomerLogger();
        this.customerID = null;
        this.customerProperties = null;
        this.customerID = customerID;
        this.customerProperties = customerProperties;
    }
    
    @Override
    public String toString() {
        return this.customerID.toString();
    }
}
