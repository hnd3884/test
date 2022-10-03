package com.adventnet.sym.webclient.mdm.doc;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMPolicyDocumentListRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMPolicyDocumentListRetrieverAction() {
        this.logger = DocMgmt.logger;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long policyId = Long.valueOf(request.getParameter("policyId"));
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            selectQuery.addJoin(new Join("DocumentDetails", "DocumentPolicyResourceRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
            selectQuery.addJoin(new Join("DocumentPolicyResourceRel", "CMDeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2));
            selectQuery.addJoin(new Join("CMDeploymentPolicy", "DeploymentPolicy", new String[] { "DEPLOYMENT_POLICY_ID" }, new String[] { "DEPLOYMENT_POLICY_ID" }, 2));
            selectQuery.addJoin(new Join("DeploymentPolicy", "DeploymentConfig", new String[] { "DEPLOYMENT_CONFIG_ID" }, new String[] { "DEPLOYMENT_CONFIG_ID" }, 2));
            final Criteria cri = new Criteria(new Column("DeploymentPolicy", "DEPLOYMENT_POLICY_TYPE_ID"), (Object)401, 0);
            final Criteria docIdCriteria = new Criteria(new Column("DocumentPolicyResourceRel", "DEPLOYMENT_POLICY_ID"), (Object)policyId, 0);
            final Criteria custCriteria = new Criteria(new Column("DeploymentConfig", "CUSTOMER_ID"), (Object)customerID, 0);
            final GroupByClause groupByClause = new GroupByClause(selectQuery.getSelectColumns());
            selectQuery.setGroupByClause(groupByClause);
            selectQuery.setCriteria(custCriteria.and(cri.and(docIdCriteria)));
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMDocTableRetrieverAction...", e);
        }
    }
}
