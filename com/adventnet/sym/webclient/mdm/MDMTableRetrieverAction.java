package com.adventnet.sym.webclient.mdm;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.components.table.web.DCTableRetrieverAction;

public class MDMTableRetrieverAction extends DCTableRetrieverAction
{
    public void setCriteria(SelectQuery selectQuery, final ViewContext viewCtx) {
        final List tableList = selectQuery.getTableList();
        final Iterator tableItr = tableList.iterator();
        while (tableItr.hasNext()) {
            final String tableName = tableItr.next().toString();
            Long customerID = null;
            if (tableName.contains("CustomerInfo")) {
                final HttpServletRequest request = viewCtx.getRequest();
                if (request.getParameter("reportInitByScheduledReport") != null && request.getParameter("reportInitByScheduledReport").equalsIgnoreCase("true")) {
                    customerID = CustomerInfoUtil.getInstance().getCustomerId();
                }
                else {
                    customerID = MSPWebClientUtil.getCustomerID(request);
                }
                Criteria customerCriteria = new Criteria(Column.getColumn("CustomerInfo", "CUSTOMER_ID"), (Object)customerID, 0);
                final Criteria criteria = selectQuery.getCriteria();
                if (criteria != null) {
                    customerCriteria = customerCriteria.and(criteria);
                }
                selectQuery.setCriteria(customerCriteria);
                break;
            }
        }
        super.setCriteria(selectQuery, viewCtx);
        selectQuery = RBDAUtil.getInstance().getRBDAQuery(selectQuery);
    }
}
