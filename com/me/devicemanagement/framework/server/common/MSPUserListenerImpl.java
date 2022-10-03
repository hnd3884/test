package com.me.devicemanagement.framework.server.common;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.authentication.UserEvent;
import com.me.devicemanagement.framework.server.authentication.AbstractUserListener;

public class MSPUserListenerImpl extends AbstractUserListener
{
    @Override
    public void userAdded(final UserEvent userEvent) {
        this.removeUserCustomerCacheForUser(userEvent);
    }
    
    @Override
    public void userDeleted(final UserEvent userEvent) {
        this.removeUserCustomerCacheForUser(userEvent);
    }
    
    @Override
    public void userModified(final UserEvent userEvent) {
        this.removeUserCustomerCacheForUser(userEvent);
    }
    
    private void removeUserCustomerCacheForUser(final UserEvent userEvent) {
        final Long userID = userEvent.userID;
        final String DC_LGOIN_TO_CUSTOMER_CACHE_KEY = "DC_LGOIN_TO_CUSTOMER_CACHE_KEY_" + userID;
        ApiFactoryProvider.getCacheAccessAPI().removeCache(DC_LGOIN_TO_CUSTOMER_CACHE_KEY, 2);
    }
}
