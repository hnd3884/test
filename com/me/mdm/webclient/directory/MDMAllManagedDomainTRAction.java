package com.me.mdm.webclient.directory;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.idps.core.util.DirectoryUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.components.table.CSRTableController;

public class MDMAllManagedDomainTRAction extends CSRTableController
{
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerId = MSPWebClientUtil.getCustomerID(request);
            Criteria criteria;
            if (DirectoryUtil.getInstance().isZDexplicit((long)customerId)) {
                criteria = new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)1, 1);
            }
            else {
                criteria = new Criteria(Column.getColumn("DMDomain", "CLIENT_ID"), (Object)new Integer[] { 1, 201 }, 9);
            }
            Criteria queryCriteria = selectQuery.getCriteria();
            if (queryCriteria != null) {
                queryCriteria = queryCriteria.and(criteria);
            }
            else {
                queryCriteria = criteria;
            }
            queryCriteria = queryCriteria.and(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerId, 0));
            selectQuery.setCriteria(queryCriteria);
        }
        catch (final Exception ex) {
            IDPSlogger.ERR.log(Level.WARNING, "Exception occured while setting criteria for All Managed Domain report  ", ex);
        }
        super.setCriteria(selectQuery, viewCtx);
    }
}
