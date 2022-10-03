package com.adventnet.sym.webclient.mdm.enroll.adep;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoThreadLocal;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.components.table.CSRTableController;

public class AppleAccountsDEPTRAction extends CSRTableController
{
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            CustomerInfoThreadLocal.setSkipCustomerFilter("true");
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            Criteria criteria = null;
            if (customerID != null) {
                criteria = new Criteria(new Column("DEPTokenDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            }
            if (criteria != null) {
                query.setCriteria(criteria);
            }
            super.setCriteria(query, viewCtx);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
