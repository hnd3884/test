package com.me.devicemanagement.framework.server.customer;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Hashtable;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;

public class GeneralCustomerListenerBaseImpl implements CustomerListener
{
    @Override
    public void customerAdded(final CustomerEvent customerEvent) {
        MessageProvider.getInstance().hideMessage("CUSTOMER_NOT_ADDED");
        final Long customerId = customerEvent.customerID;
        final ArrayList userList = DMUserHandler.getDefaultAdministratorRoleUserList();
        final Iterator itr = userList.iterator();
        while (itr.hasNext()) {
            try {
                final Hashtable userHashTable = itr.next();
                final Long userID = userHashTable.get("USER_ID");
                CustomerInfoUtil.getInstance().addCustomerToUserMapping(userID, customerId);
            }
            catch (final Exception ex) {
                Logger.getLogger(GeneralCustomerListenerBaseImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void customerDeleted(final CustomerEvent customerEvent) {
        try {
            if (CustomerInfoUtil.getInstance().getCreatedCustomerInfoList().isEmpty()) {
                MessageProvider.getInstance().unhideMessage("CUSTOMER_NOT_ADDED");
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void customerUpdated(final CustomerEvent customerEvent) {
    }
    
    @Override
    public void firstCustomerAdded(final CustomerEvent customerEvent) {
    }
}
