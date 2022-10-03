package com.me.mdm.webclient.eas;

import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.server.csv.CustomerParamsHandler;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class EasPolicyBulkUserAssignErrorDetailsTRAction extends MDMTableRetrieverAction
{
    public static Logger logger;
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            if (customerID != null) {
                final Criteria customerCriteria = new Criteria(new Column("EASCsvTableAlias", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria errorRemarks = new Criteria(new Column("EASCsvTableAlias", "ERROR_REMARKS"), (Object)null, 1);
                query.setCriteria(customerCriteria.and(errorRemarks));
            }
            EasPolicyBulkUserAssignErrorDetailsTRAction.logger.log(Level.INFO, "After setting criteria, query :{0}", query);
            final Boolean isEmailAddress = Boolean.parseBoolean(CustomerParamsHandler.getInstance().getParameterValue("IsEmailInCSV", (long)customerID));
            request.setAttribute("IsEmailInCSV", (Object)isEmailAddress);
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            EasPolicyBulkUserAssignErrorDetailsTRAction.logger.log(Level.SEVERE, "Exception while setting criteria: {0}", ex);
        }
    }
    
    static {
        EasPolicyBulkUserAssignErrorDetailsTRAction.logger = Logger.getLogger("EASMgmtLogger");
    }
}
