package com.adventnet.sym.webclient.mdm.enroll.adminenroll;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class AssignUserErrorDetailsTRAction extends MDMTableRetrieverAction
{
    private static Logger logger;
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            Criteria criteria = null;
            final String templateTypeStr = viewCtx.getRequest().getParameter("enrollmentTemplate");
            if (templateTypeStr != null && !templateTypeStr.isEmpty() && !templateTypeStr.equalsIgnoreCase(String.valueOf(-1))) {
                final Criteria templateTypeCriteria = new Criteria(new Column("AssignUserImportInfo", "TEMPLATE_TYPE"), (Object)Integer.parseInt(templateTypeStr), 0);
                if (criteria == null) {
                    criteria = templateTypeCriteria;
                }
                else {
                    criteria = criteria.and(templateTypeCriteria);
                }
            }
            if (customerID != null) {
                final Criteria customerCriteria = new Criteria(new Column("AssignUserImportInfo", "CUSTOMER_ID"), (Object)customerID, 0);
                if (criteria == null) {
                    criteria = customerCriteria;
                }
                else {
                    criteria = criteria.and(customerCriteria);
                }
            }
            query.setCriteria(criteria);
            AssignUserErrorDetailsTRAction.logger.log(Level.INFO, "After setting criteria, query :{0}", query);
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            AssignUserErrorDetailsTRAction.logger.log(Level.SEVERE, "Exception while setting criteria: {0}", ex);
        }
    }
    
    static {
        AssignUserErrorDetailsTRAction.logger = Logger.getLogger("MDMEnrollment");
    }
}
