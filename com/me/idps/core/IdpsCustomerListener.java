package com.me.idps.core;

import com.me.idps.core.upgrade.AzureOAuth210902;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import com.me.devicemanagement.framework.server.customer.CustomerListener;

public class IdpsCustomerListener implements CustomerListener
{
    public void customerAdded(final CustomerEvent customerEvent) {
        final Long customerId = customerEvent.customerID;
        new AzureOAuth210902().handleUpgrade(customerId);
    }
    
    public void customerDeleted(final CustomerEvent customerEvent) {
    }
    
    public void customerUpdated(final CustomerEvent customerEvent) {
    }
    
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
    }
}
