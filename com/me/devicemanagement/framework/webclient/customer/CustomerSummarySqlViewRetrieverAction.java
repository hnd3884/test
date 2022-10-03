package com.me.devicemanagement.framework.webclient.customer;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DMSqlViewRetrieverAction;

public class CustomerSummarySqlViewRetrieverAction extends DMSqlViewRetrieverAction
{
    private Logger logger;
    
    public CustomerSummarySqlViewRetrieverAction() {
        this.logger = Logger.getLogger(CustomerSummarySqlViewController.class.getName());
    }
    
    @Override
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String scrit = super.getVariableValue(viewCtx, variableName);
        if (variableName.equals("SCRITERIA")) {
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final String isClientCall = CustomerInfoThreadLocal.getIsClientCall();
            if (isClientCall != null) {
                String criteria = "";
                final Long[] customers = CustomerInfoUtil.getInstance().getCustomers();
                if (customers != null && customers.length > 0 && (customers.length != 1 || customers[0] != -1L)) {
                    if (customers.length == 1) {
                        criteria = "CustomerInfo.CUSTOMER_ID = '" + customers[0] + "'";
                    }
                    else {
                        final String customerIDs = this.customerIDsInString(customers);
                        criteria = "CustomerInfo.CUSTOMER_ID IN (" + customerIDs + ")";
                    }
                }
                if (!criteria.equals("")) {
                    scrit = scrit + " and " + criteria;
                }
            }
        }
        this.logger.log(Level.FINE, "Criteria  ---- " + scrit);
        return scrit;
    }
    
    private String customerIDsInString(final Long[] customers) {
        String customerIDs = String.valueOf(customers[0]);
        for (int iCustomerIDIndex = 1; iCustomerIDIndex < customers.length; ++iCustomerIDIndex) {
            customerIDs = customerIDs + "," + String.valueOf(customers[iCustomerIDIndex]);
        }
        return customerIDs;
    }
}
