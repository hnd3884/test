package com.adventnet.sym.webclient.mdm.enroll.user;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class ManagedUserDetailsTRAction extends MDMTableRetrieverAction
{
    private static Logger logger;
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            if (customerID != null) {
                final Criteria criteria = new Criteria(new Column("Resource", "CUSTOMER_ID"), (Object)customerID, 0);
                query.setCriteria(criteria);
            }
            super.setCriteria(query, viewCtx);
            final String sql = RelationalAPI.getInstance().getSelectSQL((Query)query);
            ManagedUserDetailsTRAction.logger.info(sql);
        }
        catch (final Exception ex) {
            ManagedUserDetailsTRAction.logger.log(Level.SEVERE, "Exception while setting criteria: {0}", ex);
        }
    }
    
    static {
        ManagedUserDetailsTRAction.logger = Logger.getLogger("MDMEnrollment");
    }
}
