package com.adventnet.sym.webclient.mdm.config;

import java.util.logging.Level;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.me.mdm.server.msp.sync.SyncConfigurationsUtil;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import java.util.List;
import com.adventnet.ds.query.GroupByClause;
import java.util.Arrays;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class AppVersionsTRAction extends MDMEmberTableRetrieverAction
{
    public static final String PUB_APP_TO_DEVICE_COUNT_COL_NAME = "PublishedAppVersionToDeviceCount";
    public static final String PUB_APP_TO_DEVICE_COUNT_TAB_NAME = "PublishedAppVersionToDeviceCountTable";
    public static final String PUB_APP_TO_GROUP_COUNT_COL_NAME = "PublishedAppVersionToGroupCount";
    public static final String PUB_APP_TO_GROUP_COUNT_TAB_NAME = "PublishedAppVersionToGroupCountTable";
    private Logger logger;
    
    public AppVersionsTRAction() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        final Criteria appGroupCriteria = new Criteria(Column.getColumn("InstallAppPolicy", "APP_GROUP_ID"), (Object)Column.getColumn("AppGroupToCollection", "APP_GROUP_ID"), 0);
        final Criteria collectionCriteria = new Criteria(Column.getColumn("ProfileToCollection", "COLLECTION_ID"), (Object)Column.getColumn("AppGroupToCollection", "COLLECTION_ID"), 0);
        selectQuery.addJoin(new Join("InstallAppPolicy", "AppGroupToCollection", appGroupCriteria.and(collectionCriteria), 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "AppReleaseLabel", new String[] { "RELEASE_LABEL_ID" }, new String[] { "RELEASE_LABEL_ID" }, 2));
        selectQuery.addJoin(new Join("AppGroupToCollection", "Collection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToResource"));
        subQuery.addJoin(new Join("MdAppCatalogToResource", "ManagedDevice", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        subQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0));
        RBDAUtil.getInstance().getRBDAQuery(subQuery);
        final Column publishedAppIdToDeviceCol = new Column("MdAppCatalogToResource", "APPROVED_APP_ID");
        final Column publishedAppIdToDeviceColCount = publishedAppIdToDeviceCol.count();
        publishedAppIdToDeviceColCount.setColumnAlias("PublishedAppVersionToDeviceCount");
        subQuery.addSelectColumn(publishedAppIdToDeviceCol);
        subQuery.addSelectColumn(publishedAppIdToDeviceColCount);
        final GroupByClause groupByPubAppIdToDevice = new GroupByClause((List)Arrays.asList(publishedAppIdToDeviceCol));
        subQuery.setGroupByClause(groupByPubAppIdToDevice);
        final DerivedTable publishedAppVersionToDevice = new DerivedTable("PublishedAppVersionToDeviceCountTable", (Query)subQuery);
        selectQuery.addJoin(new Join(Table.getTable("MdAppDetails"), (Table)publishedAppVersionToDevice, new String[] { "APP_ID" }, new String[] { "APPROVED_APP_ID" }, 1));
        selectQuery.addSelectColumn(new Column("PublishedAppVersionToDeviceCountTable", "PublishedAppVersionToDeviceCount"));
        final SelectQuery subQuery2 = (SelectQuery)new SelectQueryImpl(Table.getTable("MdAppCatalogToGroup"));
        subQuery2.addJoin(new Join("MdAppCatalogToGroup", "CustomGroup", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        RBDAUtil.getInstance().getRBDAQuery(subQuery2);
        final Column publishedAppIdToGroupCol = new Column("MdAppCatalogToGroup", "APPROVED_APP_ID");
        final Column publishedAppIdToGroupColCount = publishedAppIdToGroupCol.count();
        publishedAppIdToGroupColCount.setColumnAlias("PublishedAppVersionToGroupCount");
        subQuery2.addSelectColumn(publishedAppIdToGroupCol);
        subQuery2.addSelectColumn(publishedAppIdToGroupColCount);
        final GroupByClause groupByPubAppIdToGroup = new GroupByClause((List)Arrays.asList(publishedAppIdToGroupCol));
        subQuery2.setGroupByClause(groupByPubAppIdToGroup);
        final DerivedTable publishedAppVersionToGroup = new DerivedTable("PublishedAppVersionToGroupCountTable", (Query)subQuery2);
        selectQuery.addJoin(new Join(Table.getTable("MdAppDetails"), (Table)publishedAppVersionToGroup, new String[] { "APP_ID" }, new String[] { "APPROVED_APP_ID" }, 1));
        selectQuery.addSelectColumn(new Column("PublishedAppVersionToGroupCountTable", "PublishedAppVersionToGroupCount"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "COLLECTION_ID", "AppGroupToCollection.COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_GROUP_ID", "AppGroupToCollection.APP_GROUP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "APP_VERSION_STATUS", "AppGroupToCollection.APP_VERSION_STATUS"));
        selectQuery.addSelectColumn(Column.getColumn("AppGroupToCollection", "RELEASE_LABEL_ID", "AppGroupToCollection.RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_ID", "AppReleaseLabel.RELEASE_LABEL_ID"));
        selectQuery.addSelectColumn(Column.getColumn("AppReleaseLabel", "RELEASE_LABEL_DISPLAY_NAME", "AppReleaseLabel.RELEASE_LABEL_DISPLAY_NAME"));
        selectQuery.addSelectColumn(Column.getColumn("Collection", "COLLECTION_ID", "Collection.COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("Collection", "CREATION_TIME", "Collection.CREATION_TIME"));
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        final HttpServletRequest httpServletRequest = viewCtx.getRequest();
        final Long packageId = Long.parseLong(httpServletRequest.getParameter("packageId"));
        final Long customerID = MSPWebClientUtil.getCustomerID(httpServletRequest);
        final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)customerID, 0);
        final Criteria packageIdCriteria = new Criteria(Column.getColumn("MdPackage", "PACKAGE_ID"), (Object)packageId, 0);
        query.setCriteria(customerCriteria.and(packageIdCriteria));
        super.setCriteria(query, viewCtx);
    }
    
    public void postModelFetch(final ViewContext viewContext) {
        try {
            final HttpServletRequest httpServletRequest = viewContext.getRequest();
            final String packageIdStr = httpServletRequest.getParameter("packageId");
            final Long packageId = Long.parseLong(packageIdStr);
            final Boolean isForAllCustomers = SyncConfigurationsUtil.checkIfAppIsForAllCustomers(packageId);
            Boolean isUserInRole = Boolean.TRUE;
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("SyncConfigurationsForAllCustomers")) {
                final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                isUserInRole = DMUserHandler.isUserInAdminRole(loginId);
            }
            final HashMap map = new HashMap();
            map.put("is_for_all_customers", isForAllCustomers);
            map.put("isUserInRole", isUserInRole);
            viewContext.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)map);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in MDMAppToGroupsTRAction postModelFetch", ex);
        }
    }
}
