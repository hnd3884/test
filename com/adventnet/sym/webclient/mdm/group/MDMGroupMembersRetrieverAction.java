package com.adventnet.sym.webclient.mdm.group;

import com.adventnet.sym.webclient.mdm.encryption.ios.MDMDeviceRecentUserViewHandler;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.mdm.server.customgroup.MDMCustomGroupUtil;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.role.RBDAUtil;
import com.me.mdm.webclient.transformer.TransformerUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.adventnet.ds.query.Join;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMGroupMembersRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMGroupMembersRetrieverAction() {
        this.logger = Logger.getLogger(MDMGroupMembersRetrieverAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            int modelType = -1;
            final String groupIdStr = request.getParameter("groupId");
            final String parentViewName = request.getParameter("viewName");
            final String platform = request.getParameter("platform");
            Criteria cri = null;
            final HashMap deviceTypeMap = MDMUtil.getInstance().getMDMDeviceTypeMap();
            final HashMap groupMemberViewDetails = new HashMap();
            Long groupId = null;
            groupMemberViewDetails.put("deviceTypeMap", deviceTypeMap);
            if (groupIdStr != null && !groupIdStr.equals("") && !groupIdStr.equals("-1") && !groupIdStr.equals("[]")) {
                if (groupIdStr.contains("[")) {
                    groupId = MDMGroupHandler.getInstance().decodeGroupMemberIds(groupIdStr)[0];
                }
                else {
                    groupId = Long.valueOf(groupIdStr);
                }
                groupMemberViewDetails.put("groupId", groupId);
                final boolean isStaticUniqueGrp = MDMGroupHandler.isGroupOfCategory(groupId, 5);
                final HashMap mdmGroupDetails = MDMGroupHandler.getInstance().getGroupDetails(groupId);
                if (!this.containsCGRelJoin(selectQuery)) {
                    selectQuery.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                }
                final Long createdByUserId = mdmGroupDetails.get("CREATED_BY");
                final Long loggedInUserId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final Long loggedInLoginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                final Long createdByLoginId = DMUserHandler.getLoginIdForUserId(createdByUserId);
                final String domainName = mdmGroupDetails.get("DOMAIN_NETBIOS_NAME");
                final Boolean groupWrite = request.isUserInRole("MDM_GroupMgmt_Write") || request.isUserInRole("ModernMgmt_MDMGroupMgmt_Write");
                final Boolean groupAdmin = request.isUserInRole("MDM_GroupMgmt_Admin") || request.isUserInRole("ModernMgmt_MDMGroupMgmt_Admin");
                if (groupAdmin && (((createdByUserId == null || createdByLoginId == null) && TransformerUtil.hasUserAllDeviceScopeGroup(viewCtx, true)) || loggedInUserId.equals(createdByUserId) || (RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(createdByLoginId, true) && TransformerUtil.hasUserAllDeviceScopeGroup(viewCtx, true)))) {
                    request.setAttribute("isEditable", (Object)true);
                }
                else if (groupWrite && (((createdByUserId == null || createdByLoginId == null) && TransformerUtil.hasUserAllDeviceScopeGroup(viewCtx, true)) || loggedInUserId.equals(createdByUserId))) {
                    request.setAttribute("isEditable", (Object)true);
                }
                if (!loggedInUserId.equals(createdByUserId) && createdByLoginId != null && !RBDAUtil.getInstance().hasUserAllDeviceScopeGroup(createdByLoginId, true)) {
                    request.setAttribute("techStaticGroup", (Object)true);
                }
                cri = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)groupId, 0);
                final Integer groupType = mdmGroupDetails.get("GROUP_TYPE");
                if (groupType != 7) {
                    cri = cri.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
                }
                else {
                    final String s = domainName;
                    MDMUtil.getInstance();
                    if (!s.equalsIgnoreCase("MDM")) {
                        request.setAttribute("adGroup", (Object)true);
                    }
                }
                request.setAttribute("isStaticUniqueGrp", (Object)isStaticUniqueGrp);
            }
            else if (parentViewName != null && parentViewName.equals("addGroup")) {
                final Criteria deviceCri = cri = new Criteria(Column.getColumn("ManagedDevice", "RESOURCE_ID"), (Object)null, 0);
                request.setAttribute("isEditable", (Object)true);
            }
            else {
                final Long userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final SelectQuery groupQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MemberViewTemp"));
                groupQuery.addSelectColumn(Column.getColumn("MemberViewTemp", "MEMBER_RESOURCE_ID"));
                final Criteria groupTempCri = new Criteria(Column.getColumn("MemberViewTemp", "USER_ID"), (Object)userId, 0);
                groupQuery.setCriteria(groupTempCri);
                final DerivedColumn dResCol = new DerivedColumn("MEMBER_RESOURCE_ID", groupQuery);
                final Criteria deviceCri2 = cri = new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)dResCol, 8);
                final String viewName = viewCtx.getUniqueId();
                if (!viewName.toLowerCase().contains("user")) {
                    cri = cri.and(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
                }
            }
            final int memberCount = MDMCustomGroupUtil.getInstance().getGroupMemberCount(groupId);
            groupMemberViewDetails.put("memberCount", memberCount);
            final String modelTypeStr = request.getParameter("modelType");
            if (modelTypeStr != null && !modelTypeStr.equalsIgnoreCase("all")) {
                modelType = Integer.valueOf(modelTypeStr);
                groupMemberViewDetails.put("modelType", modelType);
                request.setAttribute("modelType", (Object)modelTypeStr);
                final Criteria modelNameCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)modelType, 0);
                cri = cri.and(modelNameCri);
                final String multiUser = request.getParameter("isMultiUser");
                if (modelType == 2 && multiUser != null && !multiUser.equalsIgnoreCase("all")) {
                    cri = cri.and(new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)multiUser, 0));
                }
            }
            if (platform != null && !platform.equalsIgnoreCase("all")) {
                final Integer platformType = Integer.valueOf(platform);
                groupMemberViewDetails.put("platform", platformType);
                request.setAttribute("platform", (Object)platformType);
                final Criteria platformCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
                cri = cri.and(platformCri);
            }
            final String isEditable = request.getParameter("isEditable");
            if (isEditable != null && !isEditable.trim().isEmpty()) {
                request.setAttribute("isEditable", (Object)Boolean.valueOf(isEditable));
            }
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
            request.setAttribute("groupMemberViewDetails", (Object)groupMemberViewDetails);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupMembersRetrieverAction...", e);
        }
    }
    
    private boolean containsCGRelJoin(final SelectQuery selectQuery) {
        final List tables = selectQuery.getTableList();
        if (tables != null) {
            for (int i = 0; i < tables.size(); ++i) {
                final Table table = tables.get(i);
                final String tableName = table.getTableName();
                if (tableName != null && tableName.equalsIgnoreCase("CustomGroupMemberRel")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final String unique = viewCtx.getUniqueId();
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        if (!selectQuery.containsSubQuery()) {
            new MDMDeviceRecentUserViewHandler().addRecentUsersTableJoin(selectQuery, unique);
        }
        return selectQuery;
    }
}
