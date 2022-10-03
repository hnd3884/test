package com.adventnet.sym.webclient.mdm.doc;

import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.server.doc.DocMgmtDataHandler;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import org.json.simple.JSONObject;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDocDeviceTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDocDeviceTableRetrieverAction() {
        this.logger = DocMgmt.logger;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final Long docId = Long.valueOf(request.getParameter("docId"));
            final String statusId = request.getParameter("statusId");
            final String groupId = request.getParameter("groupId");
            final String policyId = request.getParameter("policyId");
            final JSONObject docDeviceDetails = new JSONObject();
            JSONObject documentDetails = new JSONObject();
            Criteria cri = new Criteria(new Column("DocumentManagedDeviceRel", "DOC_ID"), (Object)docId, 0);
            final Criteria associateCri = new Criteria(new Column("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)true, 0);
            cri = cri.and(associateCri);
            if (statusId != null && !statusId.equals("all")) {
                final Criteria statusCri = new Criteria(new Column("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)statusId, 0);
                cri = cri.and(statusCri);
                docDeviceDetails.put((Object)"statusId", (Object)statusId);
            }
            if (groupId != null && !groupId.equals("all")) {
                selectQuery.addJoin(new Join("DocumentManagedDeviceRel", "CustomGroupMemberRel", new String[] { "MANAGEDDEVICE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                final Criteria groupCri = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                cri = cri.and(groupCri);
            }
            if (policyId != null && !policyId.equals("all")) {
                selectQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentPolicyResourceRel", new String[] { "MANAGEDDEVICE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                final Criteria policyCri = new Criteria(new Column("DocumentPolicyResourceRel", "DEPLOYMENT_POLICY_ID"), (Object)Long.valueOf(policyId), 0);
                final Criteria docPolicyCri = new Criteria(new Column("DocumentPolicyResourceRel", "DOC_ID"), (Object)docId, 0);
                cri = cri.and(policyCri.and(docPolicyCri));
            }
            final Long[] customerIDs = { MSPWebClientUtil.getCustomerID(request) };
            documentDetails = DocMgmtDataHandler.getInstance().getDocDetails(customerIDs, docId);
            docDeviceDetails.put((Object)"groupId", (Object)groupId);
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
            request.setAttribute("docDeviceDetails", (Object)JSONUtil.getInstance().convertLongToString(docDeviceDetails));
            request.setAttribute("documentDetails", (Object)documentDetails);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMDocTableRetrieverAction...", e);
        }
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        try {
            final HashMap transformData = new HashMap();
            final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
            final ArrayList<Long> list = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "ManagedDevice.RESOURCE_ID");
            final Long loginId = DMUserHandler.getLoginId();
            if (!viewCtx.getRequest().isUserInRole("All_Managed_Mobile_Devices")) {
                final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("UserDeviceMapping"));
                query.addSelectColumn(Column.getColumn("UserDeviceMapping", "RESOURCE_ID"));
                query.addSelectColumn(Column.getColumn("UserDeviceMapping", "LOGIN_ID"));
                Criteria cRes = new Criteria(new Column("UserDeviceMapping", "RESOURCE_ID"), (Object)list.toArray(), 8);
                final Criteria userDeviceMappingCriteria = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginId);
                cRes = cRes.and(userDeviceMappingCriteria);
                query.setCriteria(cRes);
                final long stime = System.currentTimeMillis();
                final DataObject dao = MDMUtil.getPersistenceLite().get(query);
                this.logger.log(Level.INFO, "postModelFetch(): Query Execution Time - {0}", System.currentTimeMillis() - stime);
                final Iterator userDeviceMapRows = dao.getRows("UserDeviceMapping");
                final List adminAccessResourceIds = DBUtil.getColumnValuesAsList(userDeviceMapRows, "RESOURCE_ID");
                transformData.put("USER_DEVICE_RESOURCE", adminAccessResourceIds);
                viewCtx.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)transformData);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while process pre rendering..", e);
        }
    }
}
