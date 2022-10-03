package com.me.devicemanagement.framework.server.customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerHandler
{
    private List customerListenerList;
    private static CustomerHandler customerHandler;
    public static final int CUSTOMER_ADDED = 1000;
    public static final int CUSTOMER_DELETED = 1001;
    public static final int FIRST_CUSTOMER_ADDED = 1002;
    public static final int CUSTOMER_UPDATED = 1003;
    
    private CustomerHandler() {
        this.customerListenerList = null;
        this.customerListenerList = new ArrayList();
    }
    
    public static synchronized CustomerHandler getInstance() {
        if (CustomerHandler.customerHandler == null) {
            CustomerHandler.customerHandler = new CustomerHandler();
        }
        return CustomerHandler.customerHandler;
    }
    
    public void addCustomerListener(final CustomerListener listener) {
        this.customerListenerList.add(listener);
    }
    
    public void removeCustomerListener(final CustomerListener listener) {
        this.customerListenerList.remove(listener);
    }
    
    public void invokeCustomerListeners(final CustomerEvent customerEvent, final int operation) throws Exception {
        final int l = this.customerListenerList.size();
        if (operation == 1000) {
            for (int s = 0; s < l; ++s) {
                final CustomerListener listener = this.customerListenerList.get(s);
                listener.customerAdded(customerEvent);
            }
        }
        else if (operation == 1001) {
            for (int s = 0; s < l; ++s) {
                final CustomerListener listener = this.customerListenerList.get(s);
                listener.customerDeleted(customerEvent);
            }
        }
        else if (operation == 1003) {
            for (int s = 0; s < l; ++s) {
                final CustomerListener listener = this.customerListenerList.get(s);
                listener.customerUpdated(customerEvent);
            }
        }
        else if (operation == 1002) {
            for (int s = 0; s < l; ++s) {
                final CustomerListener listener = this.customerListenerList.get(s);
                listener.firstCustomerAdded(customerEvent);
            }
        }
    }
    
    static {
        CustomerHandler.customerHandler = null;
    }
}
