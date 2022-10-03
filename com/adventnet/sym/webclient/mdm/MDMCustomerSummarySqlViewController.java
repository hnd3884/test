package com.adventnet.sym.webclient.mdm;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import org.apache.commons.lang.StringUtils;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.table.web.DMSqlViewRetriever;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.customer.CustomerSummarySqlViewController;

public class MDMCustomerSummarySqlViewController extends CustomerSummarySqlViewController
{
    private Logger logger;
    
    public MDMCustomerSummarySqlViewController() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String associatedValue;
        if (variableName.equals("SCRITERIA") && (CustomerInfoThreadLocal.getSummaryPage() == null || CustomerInfoThreadLocal.getSummaryPage().equalsIgnoreCase("false"))) {
            CustomerInfoThreadLocal.setSummaryPage("true");
            associatedValue = super.getVariableValue(viewCtx, variableName);
            CustomerInfoThreadLocal.setSummaryPage("false");
        }
        else {
            associatedValue = super.getVariableValue(viewCtx, variableName);
        }
        if (variableName.trim().startsWith("DBRANGECRITERIA")) {
            associatedValue = DMSqlViewRetriever.checkAndGetDBRangeCriteriaAssociatedValue(variableName);
        }
        if (variableName.equals("SCRITERIA")) {
            associatedValue = "(1 = 1)";
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
                        final String customerIDs = StringUtils.join((Object[])customers, ',');
                        criteria = "CustomerInfo.CUSTOMER_ID IN (" + customerIDs + ")";
                    }
                }
                if (!criteria.equals("")) {
                    associatedValue = associatedValue + " and " + criteria;
                }
                try {
                    String searchCriteria = "(1=1)";
                    final String[] searchcol = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
                    if (searchcol != null && searchcol.length != 0) {
                        final String[] searchval = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
                        for (int i = 0; i < searchcol.length; ++i) {
                            searchCriteria = searchCriteria + " and " + searchcol[i] + " like '%" + DMIAMEncoder.encodeSQLForNonPatternContext(searchval[i]) + "%'";
                        }
                        associatedValue = associatedValue + "and " + searchCriteria;
                    }
                }
                catch (final Exception exp) {
                    this.logger.log(Level.SEVERE, "Exception while validate search text!", exp);
                }
            }
        }
        this.logger.log(Level.FINE, "Criteria  ---- {0}", associatedValue);
        return associatedValue;
    }
}
