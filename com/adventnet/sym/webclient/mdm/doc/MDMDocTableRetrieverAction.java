package com.adventnet.sym.webclient.mdm.doc;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.List;
import com.me.mdm.server.doc.DocSummaryHandler;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDocTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDocTableRetrieverAction() {
        this.logger = DocMgmt.logger;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String tagId = request.getParameter("tagId");
            final String timeCri = request.getParameter("timeFilter");
            final String docTypeId = request.getParameter("docTypeId");
            String docTypeName = request.getParameter("docTypeName");
            final HashMap docDetails = new HashMap();
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            Criteria cri = new Criteria(new Column("DocumentDetails", "REPOSITORY_TYPE"), (Object)1, 0);
            cri = cri.and(new Criteria(new Column("DocumentDetails", "CUSTOMER_ID"), (Object)customerID, 0));
            if (tagId != null && !tagId.equals("all") && !tagId.equals("")) {
                selectQuery.addJoin(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                final Criteria filtercri = new Criteria(Column.getColumn("DocumentTagRel", "TAG_ID"), (Object)tagId, 0).and(new Criteria(Column.getColumn("DocumentDetails", "UPDATED_TIME"), (Object)Column.getColumn("DocumentTagRel", "MODIFIED_TIME"), 6));
                cri = cri.and(filtercri);
                docDetails.put("tagId", tagId);
            }
            if (docTypeId != null && !docTypeId.equals("undefined") && !docTypeId.equals("") && !docTypeId.equals("all")) {
                cri = cri.and(new Criteria(new Column("DocumentDetails", "DOC_TYPE"), (Object)Integer.valueOf(docTypeId), 0));
                docTypeName = DocMgmtDataHandler.getInstance().getDocExtention(Integer.valueOf(docTypeId)).substring(1).toUpperCase();
                docDetails.put("docTypeId", docTypeId);
            }
            if (SyMUtil.isStringValid(timeCri)) {
                cri = cri.and(new Criteria(Column.getColumn("DocumentDetails", "ADDED_TIME"), (Object)Long.valueOf(timeCri), 4));
            }
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
            docDetails.put("docTypeName", docTypeName);
            final int documentCount = DocMgmtDataHandler.getInstance().getDocumentCount(customerID);
            docDetails.put("docCount", documentCount);
            request.setAttribute("docDetails", (Object)docDetails);
            DocSummaryHandler.getInstance().reviseDocSummary(null);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMDocTableRetrieverAction...", e);
        }
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        final HashMap transformData = new HashMap();
        final HashMap<Long, List> hashMap = new HashMap<Long, List>();
        transformData.put("ASSOCIATED_GROUP_NAMES", hashMap);
        viewCtx.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)transformData);
    }
}
