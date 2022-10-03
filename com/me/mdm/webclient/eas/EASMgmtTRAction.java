package com.me.mdm.webclient.eas;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.sym.webclient.mdm.MDMTableRetrieverAction;

public class EASMgmtTRAction extends MDMTableRetrieverAction
{
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final Long customerID = MSPWebClientUtil.getCustomerID(request);
        final String crit = request.getParameter("crit");
        Criteria cri = null;
        if (crit != null) {
            final int critVal = Integer.valueOf(crit);
            final Criteria customerCri = cri = new Criteria(Column.getColumn("EASServerDetails", "CUSTOMER_ID"), (Object)customerID, 0);
            if (critVal == 2) {
                final Criteria managedCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 1);
                final Criteria enrollCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
                cri = cri.and(managedCri.and(enrollCri));
            }
            if (critVal == 3) {
                final Criteria unmanagedCri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
                final Criteria unenrollCri = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 1);
                cri = cri.and(unmanagedCri.or(unenrollCri));
            }
        }
        query.setCriteria(cri);
        super.setCriteria(query, viewCtx);
    }
}
