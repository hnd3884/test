package com.me.mdm.webclient.config;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class WebContentFilterUrlDetailsTRAction extends MDMTableRetrieverAction
{
    private Logger logger;
    
    public WebContentFilterUrlDetailsTRAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String configDataItemIDString = request.getParameter("config_data_item_id");
            Long configDataItemID = null;
            if (configDataItemIDString != null) {
                configDataItemID = Long.parseLong(request.getParameter("config_data_item_id"));
            }
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            if (customerID != null) {
                final Criteria configDataItemIdCri = new Criteria(new Column("URLRestrictionDetails", "CONFIG_DATA_ITEM_ID"), (Object)configDataItemID, 0);
                final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
                query.setCriteria(customerCriteria.and(configDataItemIdCri));
            }
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while setting csv view criteria: {0}", ex);
        }
    }
}
