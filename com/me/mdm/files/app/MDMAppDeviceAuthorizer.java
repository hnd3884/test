package com.me.mdm.files.app;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;

public class MDMAppDeviceAuthorizer
{
    private static final int MODULE_INDEX = 0;
    private static final int CUSTOMER_ID_INDEX = 1;
    private static final int APP_ID_INDEX = 4;
    
    public static String[] getSplitStr(final String requestPath) {
        final String baseSplitStr = "MDM/";
        final String baseStr = requestPath.substring(requestPath.indexOf(baseSplitStr) + baseSplitStr.length());
        return baseStr.split("/");
    }
    
    public static String getModule(final String requestPath) {
        final String[] splitRequestUri = getSplitStr(requestPath);
        return splitRequestUri[0];
    }
    
    public static Long getCustomerID(final String requestPath) {
        final String[] splitRequestUri = getSplitStr(requestPath);
        return Long.parseLong(splitRequestUri[1]);
    }
    
    public static String getFileIDhint(final String requestPath) {
        final String[] splitRequestUri = getSplitStr(requestPath);
        return splitRequestUri[4];
    }
    
    public SelectQuery getAgentAppAuthorizationQuery(final List<String> deviceUDIDs, final List<Long> fileIDs) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "RecentProfileForResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
        selectQuery.addJoin(new Join("RecentProfileForResource", "MdAppToCollection", new String[] { "COLLECTION_ID" }, new String[] { "COLLECTION_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)deviceUDIDs.toArray(), 8));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("MdAppToCollection", "APP_ID"), (Object)fileIDs.toArray(), 8)));
        selectQuery.setCriteria(selectQuery.getCriteria().and(new Criteria(Column.getColumn("RecentProfileForResource", "MARKED_FOR_DELETE"), (Object)false, 0)));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "APP_ID"));
        selectQuery.addSelectColumn(Column.getColumn("MdAppToCollection", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "COLLECTION_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("RecentProfileForResource", "PROFILE_ID"));
        return selectQuery;
    }
}
