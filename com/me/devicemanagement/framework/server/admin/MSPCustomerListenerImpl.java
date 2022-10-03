package com.me.devicemanagement.framework.server.admin;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Criteria;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.customer.CustomerEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.customer.CustomerListener;

public class MSPCustomerListenerImpl implements CustomerListener
{
    private static Logger logger;
    
    @Override
    public void customerAdded(final CustomerEvent customerEvent) {
        this.removeuserCustomerCacheForCustomer(customerEvent);
    }
    
    @Override
    public void customerDeleted(final CustomerEvent customerEvent) {
        this.removeuserCustomerCacheForCustomer(customerEvent);
    }
    
    @Override
    public void customerUpdated(final CustomerEvent customerEvent) {
        this.removeuserCustomerCacheForCustomer(customerEvent);
    }
    
    @Override
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
        this.removeuserCustomerCacheForCustomer(customerEvent);
    }
    
    private void removeuserCustomerCacheForCustomer(final CustomerEvent customerEvent) {
        final Long customerId = customerEvent.customerID;
        try {
            final List<Long> userIds = DMUserHandler.getAvailableUserIDs(null);
            for (final Long userID : userIds) {
                final String DC_LGOIN_TO_CUSTOMER_CACHE_KEY = "DC_LGOIN_TO_CUSTOMER_CACHE_KEY_" + userID;
                ApiFactoryProvider.getCacheAccessAPI().removeCache(DC_LGOIN_TO_CUSTOMER_CACHE_KEY, 2);
            }
        }
        catch (final Exception e) {
            MSPCustomerListenerImpl.logger.log(Level.SEVERE, "Exception in remove cache ", e);
        }
    }
    
    static {
        MSPCustomerListenerImpl.logger = Logger.getLogger(MSPCustomerListenerImpl.class.getName());
    }
}
