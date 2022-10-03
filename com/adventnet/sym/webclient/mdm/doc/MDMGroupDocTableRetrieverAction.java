package com.adventnet.sym.webclient.mdm.doc;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import java.util.Properties;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.me.mdm.server.doc.DocMgmt;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMGroupDocTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMGroupDocTableRetrieverAction() {
        this.logger = DocMgmt.logger;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String groupId = request.getParameter("groupId");
            final String deviceId = request.getParameter("deviceId");
            final String deviceDocStatus = request.getParameter("deviceDocStatus");
            final String groupDocStatus = request.getParameter("groupDocStatus");
            final HashMap groupDocViewDetails = new HashMap();
            final String tagId = request.getParameter("tagId");
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Boolean isMDMAdmin = DMUserHandler.isUserInRole(loginId, "All_Managed_Mobile_Devices");
            Criteria cri = new Criteria(new Column("DocumentDetails", "REPOSITORY_TYPE"), (Object)1, 0);
            if (groupId != null && !groupId.equals("all")) {
                final Criteria groupCri = new Criteria(new Column("DocumentToDeviceGroup", "CUSTOMGROUP_ID"), (Object)groupId, 0);
                cri = cri.and(groupCri);
                final Boolean isAllManagedGroup = RBDAUtil.getInstance().hasRBDAGroupCheck(loginId, true);
                if (!isMDMAdmin && !isAllManagedGroup) {
                    selectQuery.addJoin(RBDAUtil.getInstance().getUserCustomGroupMappingJoin("DocumentToDeviceGroup", "CUSTOMGROUP_ID"));
                    final Criteria userCustomGroupCriteria = RBDAUtil.getInstance().getUserCustomGroupCriteria(loginId);
                    cri = cri.and(userCustomGroupCriteria);
                }
                groupDocViewDetails.put("groupId", groupId);
                final HashMap groupSummaryMap = (HashMap)ProfileAssociateHandler.getInstance().getGroupSummary(new ArrayList(Arrays.asList(Long.valueOf(groupId))));
                final Properties groupSummary = groupSummaryMap.get(Long.valueOf(groupId));
                final int docCount = ((Hashtable<K, Integer>)groupSummary).get("DOC_COUNT");
                groupDocViewDetails.put("DOC_DISTRIBUTED_COUNT", docCount);
            }
            if (deviceId != null && !deviceId.equals("all")) {
                final Criteria groupCri = new Criteria(new Column("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"), (Object)deviceId, 0);
                cri = cri.and(groupCri);
                final Criteria associateCri = new Criteria(new Column("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)true, 0);
                cri = cri.and(associateCri);
                if (!isMDMAdmin) {
                    selectQuery.addJoin(RBDAUtil.getInstance().getUserDeviceMappingJoin("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"));
                    final Criteria userDeviceCriteria = RBDAUtil.getInstance().getUserDeviceMappingCriteria(loginId);
                    cri = cri.and(userDeviceCriteria);
                }
                groupDocViewDetails.put("deviceId", deviceId);
                final HashMap deviceSummaryMap = (HashMap)ProfileAssociateHandler.getInstance().getDeviceSummary(new ArrayList(Arrays.asList(Long.valueOf(deviceId))));
                final Properties deviceSummary = deviceSummaryMap.get(Long.valueOf(deviceId));
                final int docDistributedCount = ((Hashtable<K, Integer>)deviceSummary).get("DOC_COUNT");
                groupDocViewDetails.put("DEVICE_DOC_DISTRIBUTED_COUNT", docDistributedCount);
            }
            if (tagId != null && !tagId.equals("all") && !tagId.equals("")) {
                selectQuery.addJoin(new Join("DocumentDetails", "DocumentTagRel", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
                final Criteria filtercri = new Criteria(new Column("DocumentTagRel", "TAG_ID"), (Object)tagId, 0);
                cri = cri.and(filtercri);
                groupDocViewDetails.put("tagId", tagId);
            }
            if (deviceDocStatus != null && !deviceDocStatus.equals("all")) {
                final Criteria statusCri = new Criteria(new Column("DocumentManagedDeviceInfo", "STATUS_ID"), (Object)deviceDocStatus, 0);
                cri = cri.and(statusCri);
                groupDocViewDetails.put("deviceDocStatus", deviceDocStatus);
            }
            if (groupDocStatus != null && !groupDocStatus.equals("all")) {
                final Criteria statusCri = new Criteria(new Column("DocumentToDeviceGroup", "STATUS_ID"), (Object)groupDocStatus, 0);
                cri = cri.and(statusCri);
                groupDocViewDetails.put("groupDocStatus", groupDocStatus);
            }
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
            request.setAttribute("groupDocViewDetails", (Object)groupDocViewDetails);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMDocTableRetrieverAction...", e);
        }
    }
}
