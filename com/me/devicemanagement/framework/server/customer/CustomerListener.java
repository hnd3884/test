package com.me.devicemanagement.framework.server.customer;

public interface CustomerListener
{
    void customerAdded(final CustomerEvent p0);
    
    void customerDeleted(final CustomerEvent p0);
    
    void customerUpdated(final CustomerEvent p0);
    
    void firstCustomerAdded(final CustomerEvent p0);
}
