package com.adventnet.sym.webclient.mdm.doc;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDocumentGroupRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDocumentGroupRetrieverAction() {
        this.logger = DocMgmt.logger;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final Long customerID = MSPWebClientUtil.getCustomerID(request);
        final Long docId = Long.valueOf(request.getParameter("docId"));
        final Criteria docIdCriteria = new Criteria(Column.getColumn("DocumentToDeviceGroup", "DOC_ID"), (Object)docId, 0);
        selectQuery.setCriteria(docIdCriteria);
        selectQuery.addJoin(new Join("DirResRel", "DirObjRegIntVal", new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0).and(new Criteria(Column.getColumn("DirResRel", "RESOURCE_ID"), (Object)Column.getColumn("DirObjRegIntVal", "RESOURCE_ID"), 0)), 1));
        selectQuery.addSelectColumn(Column.getColumn("DirObjRegIntVal", "VALUE", "DIROBJREGINTVAL_STATUS_VALUE"));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)null, 0).or(new Criteria(Column.getColumn("DMDomain", "CUSTOMER_ID"), (Object)customerID, 0))).and(new Criteria(Column.getColumn("Resource", "DB_UPDATED_TIME"), (Object)(-1L), 1)));
        selectQuery.addSortColumn(new SortColumn(Column.getColumn("Resource", "RESOURCE_ID"), true));
        super.setCriteria(selectQuery, viewCtx);
        ProfileAssociateHandler.getInstance().updateGroupProfileSummary();
    }
}
