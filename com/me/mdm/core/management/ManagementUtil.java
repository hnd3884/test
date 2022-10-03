package com.me.mdm.core.management;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.Join;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;

public class ManagementUtil
{
    public static void addManagementType(final int type, final Long uvh, final Integer managementType) throws Exception {
        final String tableName = getTableNameBasedOnEntityType(type);
        final String UVHColumn = getUVHColumnName(type);
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(new Table(tableName));
        selectQuery.addSelectColumn(Column.getColumn(tableName, "*"));
        selectQuery.setCriteria(new Criteria(Column.getColumn(tableName, UVHColumn), (Object)uvh, 0));
        final DataObject dataObject = MDMUtil.getPersistenceLite().get(selectQuery);
        if (dataObject.isEmpty()) {
            final Row row = new Row(tableName);
            row.set(UVHColumn, (Object)uvh);
            row.set("MANAGEMENT_ID", (Object)getManagementIDForType(managementType));
            dataObject.addRow(row);
            MDMUtil.getPersistenceLite().update(dataObject);
        }
    }
    
    public static void modifySelectQueryForManagement(final SelectQuery selectQuery) {
        if (selectQuery.getTableList().contains(new Table("Profile")) && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableModernMgmt")) {
            selectQuery.addJoin(new Join("Profile", "ProfileToManagement", new String[] { "PROFILE_ID" }, new String[] { "PROFILE_ID" }, 2));
            selectQuery.addJoin(new Join("ProfileToManagement", "ServerManagementSettings", new String[] { "MANAGEMENT_ID" }, new String[] { "MANAGEMENT_ID" }, 2));
        }
    }
    
    public static Criteria generateCriteria(Criteria criteria, final int entity) {
        if (entity == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableModernMgmt")) {
            SelectQuery selectQuery = null;
            selectQuery = (SelectQuery)new SelectQueryImpl(new Table("ProfileToManagement"));
            selectQuery.addJoin(new Join("ProfileToManagement", "ServerManagementSettings", new String[] { "MANAGEMENT_ID" }, new String[] { "MANAGEMENT_ID" }, 2));
            selectQuery.addSelectColumn(Column.getColumn("ProfileToManagement", "PROFILE_ID"));
            final DerivedColumn derivedColumn = new DerivedColumn("ManagementColumn", selectQuery);
            if (criteria == null) {
                criteria = new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)derivedColumn, 8);
            }
            else {
                criteria = criteria.and(new Criteria(Column.getColumn("Profile", "PROFILE_ID"), (Object)derivedColumn, 8));
            }
        }
        return criteria;
    }
    
    public static Long getManagementIDForType(final Integer managementType) throws Exception {
        return (Long)DBUtil.getValueFromDB("ManagementModel", "MANAGEMENT_IDENTIFIER", (Object)managementType, "MANAGEMENT_ID");
    }
    
    private static String getUVHColumnName(final int entityType) {
        if (entityType == 1) {
            return "PROFILE_ID";
        }
        return null;
    }
    
    private static String getTableNameBasedOnEntityType(final int entityType) {
        if (entityType == 1) {
            return "ProfileToManagement";
        }
        return null;
    }
    
    public static String getManagementJoinAsString(final int type) {
        if (type == 1 && MDMFeatureParamsHandler.getInstance().isFeatureEnabled("EnableModernMgmt")) {
            return "INNER JOIN PROFILETOMANAGEMENT ON PROFILE.PROFILE_ID = PROFILETOMANAGEMENT.PROFILE_ID INNER JOIN SERVERMANAGEMENTSETTINGS ON SERVERMANAGEMENTSETTINGS.MANAGEMENT_ID=PROFILETOMANAGEMENT.MANAGEMENT_ID";
        }
        return "";
    }
}
