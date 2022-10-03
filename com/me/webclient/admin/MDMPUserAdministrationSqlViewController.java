package com.me.webclient.admin;

import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.onpremise.server.authentication.MDMPUserHandler;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberSqlViewController;

public class MDMPUserAdministrationSqlViewController extends MDMEmberSqlViewController
{
    private static Logger logger;
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            request.setAttribute("UserLastLogon", (Object)MDMPUserHandler.getInstance().userLastLogonDetails());
        }
        catch (final Exception e) {
            MDMPUserAdministrationSqlViewController.logger.log(Level.WARNING, "Exception in MDMPUserAdministrationSqlViewController {0}", e);
        }
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        final HttpServletRequest request = viewCtx.getRequest();
        String scrit = super.getVariableValue(viewCtx, variableName);
        final String skipCustomerFilter = CustomerInfoThreadLocal.getSkipCustomerFilter();
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
        CustomerInfoThreadLocal.setSkipCustomerFilter(skipCustomerFilter);
        try {
            request.setAttribute("UserLastLogon", (Object)MDMPUserHandler.getInstance().userLastLogonDetails());
        }
        catch (final Exception e) {
            MDMPUserAdministrationSqlViewController.logger.log(Level.INFO, "Exception in getting login details {0}", e);
        }
        MDMPUserAdministrationSqlViewController.logger.log(Level.FINE, "Criteria  ---- {0}", scrit);
        return scrit;
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final String sQuery = super.getSQLString(viewCtx);
        MDMPUserAdministrationSqlViewController.logger.log(Level.FINE, "Query  ---- {0}", sQuery);
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
        MDMPUserAdministrationSqlViewController.logger = Logger.getLogger("UserManagementLogger");
    }
}
