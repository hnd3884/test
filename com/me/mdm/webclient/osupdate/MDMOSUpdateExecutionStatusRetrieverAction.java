package com.me.mdm.webclient.osupdate;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.ArrayList;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Collection;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.config.ProfileUtil;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMOSUpdateExecutionStatusRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMOSUpdateExecutionStatusRetrieverAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final String statusId = request.getParameter("statusId");
            final String groupId = request.getParameter("groupId");
            final String collectionId = request.getParameter("collectionId");
            Criteria groupCri = null;
            int platformType = -1;
            final Long profileId = (Long)DBUtil.getValueFromDB("RecentProfileToColln", "COLLECTION_ID", (Object)Long.parseLong(collectionId), "PROFILE_ID");
            List mdmGpList;
            if (profileId != null) {
                platformType = new ProfileUtil().getPlatformType(profileId);
                mdmGpList = MDMGroupHandler.getCustomGroups(platformType + 2);
            }
            else {
                mdmGpList = MDMGroupHandler.getCustomGroups(4);
                mdmGpList.addAll(MDMGroupHandler.getCustomGroups(3));
            }
            if (mdmGpList != null) {
                request.setAttribute("mdmGroupList", (Object)mdmGpList);
            }
            if (groupId != null) {
                request.setAttribute("mdmGroupId", (Object)groupId);
            }
            final Criteria collectionCri = new Criteria(new Column("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria platformCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
            if (groupId != null && !groupId.equals("all")) {
                selectQuery.addJoin(new Join("ManagedDevice", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                groupCri = new Criteria(new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
            }
            final Criteria managedDeviceCriteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            Criteria finalCriteria = collectionCri.and(platformCri).and(managedDeviceCriteria);
            if (groupCri != null) {
                finalCriteria = finalCriteria.and(groupCri);
            }
            selectQuery.setCriteria(finalCriteria);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMOSUpdateExecutionStatusRetrieverAction... {0}", e);
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
                final List adminAccessLoginId = DBUtil.getColumnValuesAsList(userDeviceMapRows, "RESOURCE_ID");
                transformData.put("USER_DEVICE_RESOURCE", adminAccessLoginId);
                viewCtx.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)transformData);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while process pre rendering..", e);
        }
    }
}
