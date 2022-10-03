package com.adventnet.sym.webclient.mdm.config;

import java.util.logging.Level;
import com.me.mdm.server.doc.DocMgmt;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.ArrayList;

public class DocMgmtAction
{
    public ArrayList<Long> getRelevantCustomerIDs() {
        final ArrayList<Long> customerIDs = new ArrayList<Long>();
        try {
            final Long userID = MDMUtil.getInstance().getCurrentlyLoggedOnUserID();
            final ArrayList<HashMap> customerIDlist = CustomerInfoUtil.getInstance().getCustomerDetailsForUser(userID);
            if (customerIDlist != null && !customerIDlist.isEmpty()) {
                for (int i = 0; i < customerIDlist.size(); ++i) {
                    final HashMap customerMap = customerIDlist.get(i);
                    final Long customerID = customerMap.get("CUSTOMER_ID");
                    if (customerID != null) {
                        customerIDs.add(customerID);
                    }
                }
            }
        }
        catch (final Exception ex) {
            DocMgmt.logger.log(Level.SEVERE, "exception in getting customer scope for a user", ex);
        }
        return customerIDs;
    }
}
