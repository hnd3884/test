package com.adventnet.sym.webclient.mdm.group;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.customgroup.CustomGroupUtil;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMGroupProfileExecutionStatusRetrieverAction extends MDMEmberTableRetrieverAction
{
    private Logger logger;
    
    public MDMGroupProfileExecutionStatusRetrieverAction() {
        this.logger = Logger.getLogger(MDMGroupProfileRetrieverAction.class.getName());
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery sQuery = super.fetchAndCacheSelectQuery(viewCtx);
        final List<Table> tableList = sQuery.getTableList();
        if (!tableList.contains(Table.getTable("MdAppCatalogToResource"))) {
            final Join mdAppCollnJoin = new Join("CollnToResources", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 1);
            final Criteria appGroupCri = new Criteria(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "APP_GROUP_ID"), 0);
            final Criteria appResCri = new Criteria(Column.getColumn("CollnToResources", "RESOURCE_ID"), (Object)Column.getColumn("MdAppCatalogToResource", "RESOURCE_ID"), 0);
            final Join mdAppCatalogToResource = new Join("AppGroupToCollection", "MdAppCatalogToResource", appGroupCri.and(appResCri), 1);
            final Join mdAppCatalogToResourceScope = new Join("MdAppCatalogToResource", "MdAppCatalogToResourceScope", new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, new String[] { "RESOURCE_ID", "APP_GROUP_ID" }, 1);
            final Column scopeColumn = Column.getColumn("MdAppCatalogToResourceScope", "SCOPE", "SCOPE");
            sQuery.addJoin(mdAppCollnJoin);
            sQuery.addJoin(mdAppCatalogToResource);
            sQuery.addJoin(mdAppCatalogToResourceScope);
            sQuery.addSelectColumn(scopeColumn);
        }
        return sQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        try {
            final HttpServletRequest request = viewCtx.getRequest();
            final long updateResourceId = Long.parseLong(request.getParameter("resourceId"));
            final long profileId = Long.parseLong(request.getParameter("profileId"));
            final long collectionId = Long.parseLong(request.getParameter("collectionId"));
            final String status = request.getParameter("status");
            final String installedInVal = request.getParameter("installedIn");
            final Criteria criGroup = new Criteria(Column.getColumn("CustomGroup", "RESOURCE_ID"), (Object)updateResourceId, 0);
            final Criteria collectionCriteria = new Criteria(Column.getColumn("CollnToResources", "COLLECTION_ID"), (Object)collectionId, 0);
            final Criteria deviceAwaitingLicense = new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0);
            Criteria cri = criGroup.and(collectionCriteria).and(deviceAwaitingLicense);
            final HashMap profileMap = MDMUtil.getInstance().getProfileDetails(profileId);
            request.setAttribute("profileType", profileMap.get("PROFILE_TYPE"));
            request.setAttribute("platform", profileMap.get("PLATFORM_TYPE"));
            final List<Table> tableList = selectQuery.getTableList();
            final HashMap groupMap = CustomGroupUtil.getInstance().getResourceProperties(Long.valueOf(updateResourceId));
            final Integer groupType = groupMap.get("GROUP_TYPE");
            if (groupType == 7) {
                final Join resToManagedUserJoin = new Join("ManagedUserToDevice", "Resource", new String[] { "MANAGED_USER_ID" }, new String[] { "RESOURCE_ID" }, 2);
                selectQuery.addJoin(resToManagedUserJoin);
            }
            else {
                final Join resToManagedUserJoin = new Join("ManagedDevice", "Resource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                selectQuery.addJoin(resToManagedUserJoin);
            }
            if (!tableList.contains(Table.getTable("CustomGroupMemberRel"))) {
                final Join resToCustomerUserJoinJoin = new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2);
                selectQuery.addJoin(resToCustomerUserJoinJoin);
            }
            if (!tableList.contains(Table.getTable("CustomGroup"))) {
                final Join customerGroupMemJoin = new Join("CustomGroupMemberRel", "CustomGroup", new String[] { "GROUP_RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2);
                selectQuery.addJoin(customerGroupMemJoin);
            }
            if (status != null && !status.equalsIgnoreCase("all")) {
                final Long lstatus = Long.parseLong(status);
                request.setAttribute("status", (Object)status);
                final Criteria cStatus = new Criteria(new Column("CollnToResources", "STATUS"), (Object)lstatus, 0);
                cri = cri.and(cStatus);
            }
            if (installedInVal != null) {
                request.setAttribute("instalAppScope", (Object)installedInVal);
            }
            if (installedInVal != null && !installedInVal.equals("-1") && !installedInVal.equals("0") && !installedInVal.equalsIgnoreCase("all")) {
                final Integer instalAppScope = Integer.parseInt(installedInVal);
                final Criteria cStatus = new Criteria(new Column("MdAppCatalogToResourceScope", "SCOPE"), (Object)instalAppScope, 0);
                if (cri != null) {
                    cri = cri.and(cStatus);
                }
                else {
                    cri = cStatus;
                }
            }
            selectQuery.setCriteria(cri);
            super.setCriteria(selectQuery, viewCtx);
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in MDMGroupProfileExecutionStatusRetrieverAction...", e);
        }
    }
}
