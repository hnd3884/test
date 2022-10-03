package com.me.mdm.files.doc;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.List;

public class MDMDocDeviceAuthorizer
{
    private static final int MODULE_INDEX = 0;
    private static final int CUSTOMER_ID_INDEX = 1;
    
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
        return splitRequestUri[2].substring(0, splitRequestUri[2].indexOf("."));
    }
    
    public SelectQuery getAgentDocAuthorizationQuery(final List<String> deviceUDIDs, final List<Long> fileIDs) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ManagedDevice"));
        selectQuery.addJoin(new Join("ManagedDevice", "DocumentManagedDeviceRel", new String[] { "RESOURCE_ID" }, new String[] { "MANAGEDDEVICE_ID" }, 2));
        selectQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentDetails", new String[] { "DOC_ID" }, new String[] { "DOC_ID" }, 2));
        selectQuery.addJoin(new Join("DocumentManagedDeviceRel", "DocumentManagedDeviceInfo", new String[] { "DOC_MD_ID" }, new String[] { "DOC_MD_ID" }, 2));
        selectQuery.setCriteria(new Criteria(Column.getColumn("DocumentManagedDeviceInfo", "ASSOCIATE"), (Object)Boolean.TRUE, 0).and(new Criteria(Column.getColumn("DocumentDetails", "DOC_ID"), (Object)fileIDs.toArray(new Long[fileIDs.size()]), 8)).and(new Criteria(Column.getColumn("ManagedDevice", "UDID"), (Object)deviceUDIDs.toArray(new String[deviceUDIDs.size()]), 8)).and(new Criteria(Column.getColumn("DocumentDetails", "REPOSITORY_TYPE"), (Object)0, 1)));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "UDID"));
        selectQuery.addSelectColumn(Column.getColumn("ManagedDevice", "RESOURCE_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "DOC_MD_ID"));
        selectQuery.addSelectColumn(Column.getColumn("DocumentManagedDeviceRel", "MANAGEDDEVICE_ID"));
        return selectQuery;
    }
}
