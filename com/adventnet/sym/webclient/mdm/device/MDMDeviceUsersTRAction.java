package com.adventnet.sym.webclient.mdm.device;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDeviceUsersTRAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDeviceUsersTRAction() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final Long resourceId = Long.valueOf(request.getParameter("resourceId"));
            final Criteria resourceCriteria = new Criteria(new Column("MdDeviceRecentUsersInfo", "RESOURCE_ID"), (Object)resourceId, 0);
            final Criteria customerCriteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
            selectQuery.setCriteria(resourceCriteria.and(customerCriteria));
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception occoured in MDMDeviceUsersTRAction...", e);
        }
    }
}
