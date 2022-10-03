package com.me.devicemanagement.framework.webclient.authentication;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DCSqlViewController;

public class UserAdministrationSqlViewController extends DCSqlViewController
{
    private static Logger logger;
    
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
                    final String isSummaryPage = CustomerInfoThreadLocal.getSummaryPage();
                    if (isSummaryPage != null && isSummaryPage.equals("true")) {
                        criteria = "(" + criteria + " or CustomerInfo.CUSTOMER_ID IS NULL )";
                    }
                }
                if (!criteria.equals("")) {
                    scrit = scrit + " and " + criteria;
                }
            }
        }
        UserAdministrationSqlViewController.logger.log(Level.FINE, "Criteria  ---- " + scrit);
        return scrit;
    }
    
    @Override
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final String sQuery = super.getSQLString(viewCtx);
        UserAdministrationSqlViewController.logger.log(Level.FINE, "Query  ---- " + sQuery);
        return sQuery;
    }
    
    private String customerIDsInString(final Long[] customers) {
        String customerIDs = String.valueOf(customers[0]);
        for (int iCustomerIDIndex = 1; iCustomerIDIndex < customers.length; ++iCustomerIDIndex) {
            customerIDs = customerIDs + "," + String.valueOf(customers[iCustomerIDIndex]);
        }
        return customerIDs;
    }
    
    static {
        UserAdministrationSqlViewController.logger = Logger.getLogger("UserManagementLogger");
    }
}
