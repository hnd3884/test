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

public class WebContentFilterUrlImportErrorDetailsTRAction extends MDMTableRetrieverAction
{
    private Logger logger;
    
    public WebContentFilterUrlImportErrorDetailsTRAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            if (customerID != null) {
                final Criteria criteria = new Criteria(new Column("URLDetailsImportInfo", "CUSTOMER_ID"), (Object)customerID, 0);
                query.setCriteria(criteria);
            }
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception while setting view criteria: {0}", ex);
        }
    }
}
