package com.adventnet.sym.webclient.mdm.doc;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDocPolicyListRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDocPolicyListRetrieverAction() {
        this.logger = DocMgmt.logger;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            Criteria cri = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_TYPE_ID"), (Object)401, 0);
            cri = cri.and(new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)customerID, 0));
            selectQuery.setCriteria(cri);
            selectQuery.addSortColumn(new SortColumn(Column.getColumn("DeploymentPolicy", "DEPLOYMENT_POLICY_ID"), true));
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occured in MDMDocTableRetrieverAction...", e);
        }
    }
}
