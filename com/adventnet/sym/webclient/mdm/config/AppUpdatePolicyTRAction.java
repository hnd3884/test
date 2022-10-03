package com.adventnet.sym.webclient.mdm.config;

import java.util.logging.Level;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMDBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import java.util.List;
import com.me.mdm.server.role.RBDAUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class AppUpdatePolicyTRAction extends MDMEmberTableRetrieverAction
{
    private static Logger logger;
    public static final String APP_COUNT_IN_POLICY_COL = "APP_COUNT_IN_POLICY_COL";
    public static final String POLICY_TO_APP_COUNT_TABLE = "POLICY_TO_APP_COUNT_TABLE";
    public static final String ASSOCIATED_GROUP_COUNT_COL = "ASSOCIATED_GROUP_COUNT_COL";
    public static final String POLICY_TO_GROUP_COUNT_TABLE = "POLICY_TO_GROUP_COUNT_TABLE";
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        final SelectQuery appCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("AutoAppUpdatePackageList"));
        final Column appUpdateConfigCol = new Column("AutoAppUpdatePackageList", "APP_UPDATE_CONF_ID");
        final List groupByColList = new ArrayList();
        groupByColList.add(appUpdateConfigCol);
        Column appCountInPolicy = new Column("AutoAppUpdatePackageList", "PACKAGE_ID");
        appCountInPolicy = appCountInPolicy.distinct().count();
        appCountInPolicy.setColumnAlias("APP_COUNT_IN_POLICY_COL");
        appCountQuery.addSelectColumn(appUpdateConfigCol);
        appCountQuery.addSelectColumn(appCountInPolicy);
        appCountQuery.setGroupByClause(new GroupByClause(groupByColList));
        final DerivedTable derivedTable = new DerivedTable("POLICY_TO_APP_COUNT_TABLE", (Query)appCountQuery);
        selectQuery.addJoin(new Join(Table.getTable("AutoAppUpdateConfigDetails"), (Table)derivedTable, new String[] { "APP_UPDATE_CONF_ID" }, new String[] { "APP_UPDATE_CONF_ID" }, 1));
        selectQuery.addSelectColumn(new Column("POLICY_TO_APP_COUNT_TABLE", "APP_COUNT_IN_POLICY_COL"));
        final SelectQuery groupCountQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("RecentProfileForGroup"));
        groupCountQuery.addJoin(new Join("RecentProfileForGroup", "CustomGroup", new String[] { "GROUP_ID" }, new String[] { "RESOURCE_ID" }, 2));
        groupCountQuery.addJoin(new Join("RecentProfileForGroup", "Profile", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
        final Column profileCol = Column.getColumn("RecentProfileForGroup", "PROFILE_ID");
        final List grpByColList = new ArrayList();
        grpByColList.add(profileCol);
        Column associatedGroupCountCol = new Column("RecentProfileForGroup", "GROUP_ID");
        associatedGroupCountCol = associatedGroupCountCol.distinct().count();
        associatedGroupCountCol.setColumnAlias("ASSOCIATED_GROUP_COUNT_COL");
        groupCountQuery.setCriteria(new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)12, 0).and(new Criteria(Column.getColumn("RecentProfileForGroup", "MARKED_FOR_DELETE"), (Object)false, 0)));
        RBDAUtil.getInstance().getRBDAQuery(groupCountQuery);
        groupCountQuery.addSelectColumn(associatedGroupCountCol);
        groupCountQuery.addSelectColumn(profileCol);
        groupCountQuery.setGroupByClause(new GroupByClause(grpByColList));
        final DerivedTable derivedTable2 = new DerivedTable("POLICY_TO_GROUP_COUNT_TABLE", (Query)groupCountQuery);
        selectQuery.addJoin(new Join(Table.getTable("Profile"), (Table)derivedTable2, new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 1));
        selectQuery.addSelectColumn(new Column("POLICY_TO_GROUP_COUNT_TABLE", "ASSOCIATED_GROUP_COUNT_COL"));
        return selectQuery;
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        final Long customerId = CustomerInfoUtil.getInstance().getCustomerId();
        final Criteria customerCriteria = new Criteria(Column.getColumn("AutoAppUpdateConfigDetails", "CUSTOMER_ID"), (Object)customerId, 0);
        query.setCriteria(customerCriteria);
        super.setCriteria(query, viewCtx);
    }
    
    public void postModelFetch(final ViewContext viewContext) {
        try {
            Integer totalAppCountInAppRepo = 0;
            final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Profile"));
            selectQuery.addJoin(new Join("Profile", "ProfileToCollection", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("Profile", "ProfileToCustomerRel", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToCollection", "AppGroupToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
            selectQuery.addJoin(new Join("AppGroupToCollection", "MdPackageToAppGroup", new String[] { "APP_GROUP_ID" }, new String[] { "APP_GROUP_ID" }, 2));
            final Criteria profileTypeCriteria = new Criteria(Column.getColumn("Profile", "PROFILE_TYPE"), (Object)2, 0);
            final Criteria trashCriteria = new Criteria(Column.getColumn("Profile", "IS_MOVED_TO_TRASH"), (Object)false, 0);
            final Criteria appTypeCriteria = new Criteria(Column.getColumn("MdPackageToAppGroup", "IS_PURCHASED_FROM_PORTAL"), (Object)true, 0);
            final Criteria customerCriteria = new Criteria(Column.getColumn("ProfileToCustomerRel", "CUSTOMER_ID"), (Object)CustomerInfoUtil.getInstance().getCustomerId(), 0);
            selectQuery.setCriteria(profileTypeCriteria.and(trashCriteria).and(appTypeCriteria).and(customerCriteria));
            totalAppCountInAppRepo = MDMDBUtil.getCount(selectQuery, "Profile", "PROFILE_ID");
            final HashMap map = new HashMap();
            map.put("TOTAL_APP_COUNT", totalAppCountInAppRepo);
            viewContext.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)map);
        }
        catch (final Exception ex) {
            AppUpdatePolicyTRAction.logger.log(Level.SEVERE, "Exception in post model fetch of AppUpdatePolicyTraction");
        }
    }
    
    static {
        AppUpdatePolicyTRAction.logger = Logger.getLogger("MDMConfigLogger");
    }
}
