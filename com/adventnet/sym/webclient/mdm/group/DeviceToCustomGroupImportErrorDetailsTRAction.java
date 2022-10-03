package com.adventnet.sym.webclient.mdm.group;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DCTableRetrieverAction;

public class DeviceToCustomGroupImportErrorDetailsTRAction extends DCTableRetrieverAction
{
    private static Logger logger;
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            if (customerID != null) {
                final Criteria criteria = new Criteria(new Column("DeviceToCustomGroupImportInfo", "CUSTOMER_ID"), (Object)customerID, 0);
                query.setCriteria(criteria);
            }
            DeviceToCustomGroupImportErrorDetailsTRAction.logger.log(Level.INFO, "After setting criteria, query :{0}", query);
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            DeviceToCustomGroupImportErrorDetailsTRAction.logger.log(Level.SEVERE, "Exception while setting criteria: {0}", ex);
        }
    }
    
    static {
        DeviceToCustomGroupImportErrorDetailsTRAction.logger = Logger.getLogger("MDMEnrollment");
    }
}
