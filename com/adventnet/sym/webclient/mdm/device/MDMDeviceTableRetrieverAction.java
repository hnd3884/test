package com.adventnet.sym.webclient.mdm.device;

import com.me.mdm.server.customgroup.GroupFacade;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.logging.Level;
import com.adventnet.sym.webclient.mdm.encryption.ios.MDMDeviceRecentUserViewHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMDeviceTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMDeviceTableRetrieverAction() {
        this.logger = Logger.getLogger(MDMDeviceTableRetrieverAction.class.getName());
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext arg0) throws Exception {
        SelectQuery selectQuery = super.fetchAndCacheSelectQuery(arg0);
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("temporary.selectqueryfix")) {
            selectQuery = MDMApiFactoryProvider.getMDMUtilAPI().deepCloneQuery(selectQuery);
        }
        if (!selectQuery.containsSubQuery()) {
            try {
                final Table resourceTable = Table.getTable("Resource");
                final SelectQuery profileSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForResource"));
                profileSQ.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
                final Column profile_data = Column.getColumn("RecentProfileForResource", "PROFILE_ID").count();
                profile_data.setColumnAlias("PROFILE_ID");
                profileSQ.addSelectColumn(profile_data);
                final List profileGrouplist = new ArrayList();
                final Column profileGroupByCol = Column.getColumn("RecentProfileForResource", "RESOURCE_ID");
                profileGrouplist.add(profileGroupByCol);
                final GroupByClause profileGroupBy = new GroupByClause(profileGrouplist);
                profileSQ.setGroupByClause(profileGroupBy);
                final DerivedTable profileDerievedTab = new DerivedTable("RecentProfileForResource", (Query)profileSQ);
                selectQuery.addJoin(new Join(resourceTable, (Table)profileDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                new MDMDeviceRecentUserViewHandler().addRecentUsersTableJoin(selectQuery, arg0.getUniqueId());
            }
            catch (final Exception e) {
                this.logger.log(Level.WARNING, "Exception while dynamic join ---- :", e);
            }
        }
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            Criteria cri;
            final Criteria enrolledCriteria = cri = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0, false);
            final HttpServletRequest request = viewCtx.getRequest();
            final String modelName = request.getParameter("modelName");
            final String modelType = request.getParameter("modelType");
            final String platformType = request.getParameter("platformType");
            final String unAssingedDeviceOnly = request.getParameter("showUnassignedOnly");
            final HashMap deviceTypeMap = MDMUtil.getInstance().getMDMDeviceTypeMap();
            final HashMap platformTypeListMap = MDMUtil.getInstance().getPlatformTypeMap();
            request.setAttribute("deviceTypeMap", (Object)deviceTypeMap);
            request.setAttribute("modelType", (Object)modelType);
            request.setAttribute("platformType", (Object)platformType);
            request.setAttribute("platformTypeListMap", (Object)platformTypeListMap);
            if (modelName != null && !modelName.equalsIgnoreCase("all")) {
                final Criteria modelNameCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_NAME"), (Object)modelName, 0);
                cri = cri.and(modelNameCri);
            }
            if (modelType != null && !modelType.equalsIgnoreCase("all")) {
                final Criteria modelTypeCri = new Criteria(Column.getColumn("MdModelInfo", "MODEL_TYPE"), (Object)modelType, 0);
                cri = cri.and(modelTypeCri);
                final String multiUser = request.getParameter("isMultiUser");
                if (Integer.parseInt(modelType) == 2 && multiUser != null && !multiUser.equalsIgnoreCase("all")) {
                    cri = cri.and(new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)multiUser, 0));
                }
            }
            if (platformType != null && !platformType.equalsIgnoreCase("all")) {
                final Criteria platformTypeCri = new Criteria(Column.getColumn("ManagedDevice", "PLATFORM_TYPE"), (Object)platformType, 0);
                cri = cri.and(platformTypeCri);
            }
            selectQuery.setCriteria(cri);
            final Table resourceTable = Table.getTable("Resource");
            SelectQuery subSQ = (SelectQuery)new SelectQueryImpl(Table.getTable("CustomGroupMemberRel"));
            subSQ.addSelectColumn(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"));
            final Column column_config_data = Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID").count();
            column_config_data.setColumnAlias("GROUP_RESOURCE_ID");
            final Join customGroupJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
            subSQ.addJoin(customGroupJoin);
            subSQ.setCriteria(new Criteria(Column.getColumn("CustomGroup", "GROUP_TYPE"), (Object)MDMGroupHandler.getMDMGroupType().toArray(), 8));
            subSQ = RBDAUtil.getInstance().getRBDAQuery(subSQ);
            subSQ.addSelectColumn(column_config_data);
            final List list = new ArrayList();
            final Column groupByCol = Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID");
            list.add(groupByCol);
            final GroupByClause memberGroupBy = new GroupByClause(list);
            subSQ.setGroupByClause(memberGroupBy);
            final DerivedTable groupDerievedTab = new DerivedTable("CustomGroupMemberRel", (Query)subSQ);
            selectQuery.addJoin(new Join(resourceTable, (Table)groupDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 1));
            final Column maxCountCol = new Column("CustomGroupMemberRel", "GROUP_RESOURCE_ID");
            maxCountCol.setColumnAlias("GROUP_COUNT");
            selectQuery.addSelectColumn(maxCountCol);
            if (unAssingedDeviceOnly != null && unAssingedDeviceOnly.equals("true")) {
                final Criteria criteria = selectQuery.getCriteria().and(new Criteria(Column.getColumn("CustomGroupMemberRel", "MEMBER_RESOURCE_ID"), (Object)null, 0));
                selectQuery.setCriteria(criteria);
            }
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupTableRetrieverAction...", e);
        }
        ProfileAssociateHandler.getInstance().updateDeviceProfileSummary();
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        try {
            final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
            final ArrayList<Long> list = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "Resource.RESOURCE_ID");
            final HashMap hashMap = new GroupFacade().getAssociatedGroupsForResList(list);
            viewCtx.getRequest().setAttribute("ASSOCIATED_GROUP_NAMES", (Object)hashMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while Add Group Names..", e);
        }
    }
}
