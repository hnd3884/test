package com.me.devicemanagement.framework.webclient.reports;

import com.me.devicemanagement.framework.webclient.reportcriteria.ReportCriteriaUtil;
import com.adventnet.i18n.I18N;
import java.util.List;
import com.me.devicemanagement.framework.server.reportcriteria.CriteriaColumnValueUtil;
import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Join;
import com.me.devicemanagement.framework.server.dcViewFilter.DCViewFilterUtil;
import java.util.ArrayList;
import com.adventnet.ds.query.Query;
import com.adventnet.db.util.SQLGeneratorForMSP;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import org.json.JSONObject;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import org.json.JSONArray;
import java.util.logging.Logger;

public class ReportBIUtil
{
    protected static String className;
    protected static Logger out;
    public static final long USER_RESOURCE_COLUMN_TYPE = 1L;
    public static final long MODIFIED_TIME_COLUMN_TYPE = 2L;
    public static final long PRIMARY_COLUMN_TYPE = 3L;
    public static final long TRANSFORM_VALUE_COLUMN_TYPE = 4L;
    public static final long I18N_VALUE_COLUMN_TYPE = 5L;
    public static final long CLASS_TRANSFORM_COLUMN_TYPE = 6L;
    
    public static JSONArray getModuleDetailsForUser(final Long userID) {
        final JSONArray availableModules = new JSONArray();
        try {
            final DataObject moduleDO = getAvailableModulesForUser(userID);
            if (moduleDO != null) {
                final Iterator availableModuleIterator = moduleDO.getRows("DCUserModule");
                final HashMap<Long, String> addedModules = new HashMap<Long, String>();
                while (availableModuleIterator.hasNext()) {
                    final Row moduleRow = availableModuleIterator.next();
                    final Long moduleID = (Long)moduleRow.get("MODULE_ID");
                    final String moduleName = (String)moduleRow.get("MODULE_NAME");
                    final JSONObject individualModules = new JSONObject();
                    individualModules.put("moduleID", (Object)moduleID);
                    individualModules.put("moduleName", (Object)moduleName);
                    availableModules.put((Object)individualModules);
                    addedModules.put(moduleID, moduleName);
                }
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception in getting Available Report Modules for the user" + e);
        }
        return availableModules;
    }
    
    public static DataObject getTableDetailsForModule(final Long moduleID, final String apiVersion) {
        try {
            final Criteria reportQueryCriteria = new Criteria(new Column("ReportBIQuery", "MODULE_ID"), (Object)moduleID, 0);
            final Criteria apiVersionCriteria = new Criteria(new Column("ReportBIQuery", "API_VERSION"), (Object)apiVersion, 0);
            final DataObject versionSpecificDO = getReportBITableDetails("ReportBIQuery", reportQueryCriteria.and(apiVersionCriteria));
            if (versionSpecificDO == null || versionSpecificDO.isEmpty()) {
                return getReportBITableDetails("ReportBIQuery", reportQueryCriteria);
            }
            return versionSpecificDO;
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception in getTableDetailsForModule() method" + e);
            return null;
        }
    }
    
    public static SelectQuery getSelectQueryForModuleTable(final Long tableID, final Boolean isAdmin, final Long[] customers, final Long userID, final Long modifiedTime, final Integer offset, final String apiVersion, final Integer rowCount) {
        SelectQuery selectQuery = null;
        try {
            selectQuery = getSelectQueryFromTableID(tableID, apiVersion);
            if (selectQuery != null) {
                selectQuery = transformSelectQuery(tableID, selectQuery);
                selectQuery = appendCustomerCriteriaToSelectQuery(selectQuery, customers);
                selectQuery = appendOffsetToSelectQuery(selectQuery, offset, rowCount);
                final Boolean modifiedTimeApplicable = isModifiedTimeApplicableToTableID(tableID);
                final Boolean setDistinct = returnsDistinctValues(tableID);
                if (setDistinct) {
                    final Column distinctColumn = (Column)selectQuery.getSelectColumns().get(0).clone();
                    selectQuery.removeSelectColumn(distinctColumn);
                    String columnAlias = distinctColumn.getColumnAlias();
                    columnAlias = columnAlias.replace(' ', '_');
                    distinctColumn.setColumnAlias(columnAlias);
                    selectQuery.addSelectColumn(distinctColumn, 0);
                    selectQuery.setDistinct(true);
                }
                if (!isAdmin) {
                    selectQuery = appendRBCACriteriaToSelectQuery(selectQuery, userID, tableID);
                }
                if (modifiedTime != null && modifiedTimeApplicable) {
                    selectQuery = appendModifiedTimeToSelectQuery(selectQuery, modifiedTime, tableID);
                }
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting SelectQuery for the Table" + e);
        }
        return selectQuery;
    }
    
    private static SelectQuery getSelectQueryFromTableID(final Long tableID, final String apiVersion) {
        final Criteria tableIDCriteria = new Criteria(new Column("ReportBIQuery", "TABLE_ID"), (Object)tableID, 0);
        final Criteria apiVersionCriteria = new Criteria(new Column("ReportBIQuery", "API_VERSION"), (Object)apiVersion, 0);
        final DataObject reportTableIDDO = getReportBITableDetails("ReportBIQuery", tableIDCriteria.and(apiVersionCriteria));
        try {
            if (reportTableIDDO != null && !reportTableIDDO.isEmpty()) {
                final Row reportQueryRow = reportTableIDDO.getFirstRow("ReportBIQuery");
                final Long selectQueryID = (Long)reportQueryRow.get("QUERY_ID");
                if (selectQueryID != null) {
                    return QueryUtil.getSelectQuery((long)selectQueryID);
                }
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting SelectQuery for Table ID" + e);
        }
        return null;
    }
    
    private static SelectQuery appendOffsetToSelectQuery(final SelectQuery selectQuery, final Integer offset, final Integer rowCount) {
        try {
            final Range queryRange = new Range((int)offset, (int)rowCount);
            final Column columnSort = selectQuery.getSelectColumns().get(0);
            final SortColumn sortColumn = new SortColumn(columnSort, true);
            selectQuery.addSortColumn(sortColumn);
            selectQuery.setRange(queryRange);
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while appending Offset Criteria to SelectQuery" + e);
        }
        return selectQuery;
    }
    
    private static SelectQuery appendCustomerCriteriaToSelectQuery(final SelectQuery selectQuery, final Long[] customers) {
        try {
            final SelectQuery customerAppendedSelectQuery = (SelectQuery)SQLGeneratorForMSP.getInstance().appendCustomerCriteriaToQuery((Query)selectQuery, customers);
            return customerAppendedSelectQuery;
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while appending Customer Criteria to SelectQuery" + e);
            return selectQuery;
        }
    }
    
    private static SelectQuery transformSelectQuery(final Long tableID, final SelectQuery tableSelect) {
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBIQuery", "TABLE_ID"), (Object)tableID, 0);
            final DataObject transformSelectDO = getReportBITableDetails("ReportBIQuery", tableIDCriteria);
            if (transformSelectDO != null && !transformSelectDO.isEmpty()) {
                final Row transformRow = transformSelectDO.getFirstRow("ReportBIQuery");
                final String tableName = (String)transformRow.get("TABLE_NAME");
                final String transformationClass = (String)transformRow.get("TRANSFORMER_CLASS");
                if (transformationClass != null) {
                    final ReportBISelectQueryTransformer reportBISelectQueryTransformer = (ReportBISelectQueryTransformer)Class.forName(transformationClass).newInstance();
                    return reportBISelectQueryTransformer.transformSelectQuery(tableName, tableSelect);
                }
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while transforming SelectQuery " + e);
        }
        return tableSelect;
    }
    
    private static SelectQuery appendRBCACriteriaToSelectQuery(SelectQuery selectQuery, final Long userID, final Long tableID) {
        Long columnID = null;
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBITableColumn", "TABLE_ID"), (Object)tableID, 0);
            final Criteria joinType = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)1L, 0);
            final Criteria tableColumnCriteria = tableIDCriteria.and(joinType);
            final DataObject tableColumnDO = getReportBITableColumnID(tableColumnCriteria);
            if (tableColumnDO != null) {
                final Row joinColumnRow = tableColumnDO.getFirstRow("ReportBITableColumn");
                columnID = (Long)joinColumnRow.get("COLUMN_ID");
            }
            Criteria baseCriteria = selectQuery.getCriteria();
            final ArrayList tableList = (ArrayList)selectQuery.getTableList();
            final ArrayList tableNameList = DCViewFilterUtil.getInstance().fetchTableNamesFromList(tableList);
            if (columnID != null) {
                final Criteria columnDetailsCrit = new Criteria(new Column("CRColumns", "COLUMN_ID"), (Object)columnID, 0);
                final DataObject columnDetailsDO = getColumnDetailsForCriteria(columnDetailsCrit);
                if (columnDetailsDO != null && !columnDetailsDO.isEmpty()) {
                    final Row columnRow = columnDetailsDO.getFirstRow("CRColumns");
                    final String tableName = (String)columnRow.get("TABLE_NAME_ALIAS");
                    final String columnName = (String)columnRow.get("COLUMN_NAME_ALIAS");
                    final Long subModuleId = (Long)columnRow.get("SUB_MODULE_ID");
                    final Criteria subModuleCriteria = new Criteria(Column.getColumn("CRJoinRelation", "SUB_MODULE_ID"), (Object)subModuleId, 0);
                    final DataObject joinDO = DCViewFilterUtil.getInstance().getJoinDO(subModuleCriteria);
                    final DataObject joinCriteriaDO = DCViewFilterUtil.getInstance().getjoinCriteriaDO(subModuleCriteria);
                    final ArrayList joinList = new ArrayList();
                    selectQuery = DCViewFilterUtil.getInstance().checkAndAddJoin(selectQuery, tableNameList, joinList, tableName, joinDO, joinCriteriaDO);
                    final Criteria userCriteria = new Criteria(new Column(tableName, columnName), (Object)userID, 0);
                    if (baseCriteria != null) {
                        baseCriteria = baseCriteria.and(userCriteria);
                        selectQuery.setCriteria(baseCriteria);
                    }
                    else {
                        selectQuery.setCriteria(userCriteria);
                    }
                }
                return selectQuery;
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while appending RBCA Criteria to SelectQuery" + e);
        }
        return selectQuery;
    }
    
    private static SelectQuery appendModifiedTimeToSelectQuery(SelectQuery selectQuery, final Long modifiedTime, final Long tableID) {
        Long columnID = null;
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBITableColumn", "TABLE_ID"), (Object)tableID, 0);
            final Criteria joinType = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)2L, 0);
            final Criteria tableColumnCriteria = tableIDCriteria.and(joinType);
            final DataObject tableColumnDO = getReportBITableColumnID(tableColumnCriteria);
            if (tableColumnDO != null) {
                final Row joinColumnRow = tableColumnDO.getFirstRow("ReportBITableColumn");
                columnID = (Long)joinColumnRow.get("COLUMN_ID");
            }
            Criteria baseCriteria = selectQuery.getCriteria();
            final ArrayList tableList = (ArrayList)selectQuery.getTableList();
            final ArrayList tableNameList = DCViewFilterUtil.getInstance().fetchTableNamesFromList(tableList);
            if (columnID != null) {
                final Criteria columnDetailsCrit = new Criteria(new Column("CRColumns", "COLUMN_ID"), (Object)columnID, 0);
                final DataObject columnDetailsDO = getColumnDetailsForCriteria(columnDetailsCrit);
                if (columnDetailsDO != null && !columnDetailsDO.isEmpty()) {
                    final Row columnRow = columnDetailsDO.getFirstRow("CRColumns");
                    final String tableName = (String)columnRow.get("TABLE_NAME_ALIAS");
                    final String columnName = (String)columnRow.get("COLUMN_NAME_ALIAS");
                    final Long subModuleId = (Long)columnRow.get("SUB_MODULE_ID");
                    final Criteria subModuleCriteria = new Criteria(Column.getColumn("CRJoinRelation", "SUB_MODULE_ID"), (Object)subModuleId, 0);
                    final DataObject joinDO = DCViewFilterUtil.getInstance().getJoinDO(subModuleCriteria);
                    final DataObject joinCriteriaDO = DCViewFilterUtil.getInstance().getjoinCriteriaDO(subModuleCriteria);
                    final ArrayList joinList = new ArrayList();
                    selectQuery = DCViewFilterUtil.getInstance().checkAndAddJoin(selectQuery, tableNameList, joinList, tableName, joinDO, joinCriteriaDO);
                    final Criteria modifiedCriteria = new Criteria(new Column(tableName, columnName), (Object)modifiedTime, 4);
                    if (baseCriteria != null) {
                        baseCriteria = baseCriteria.and(modifiedCriteria);
                        selectQuery.setCriteria(baseCriteria);
                    }
                    else {
                        selectQuery.setCriteria(modifiedCriteria);
                    }
                }
                return selectQuery;
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while appending Modified Time to SelectQuery" + e);
        }
        return null;
    }
    
    public static DataObject getReportBITableColumnID(final Criteria tableColumnCriteria) {
        return getReportBITableColumnID(tableColumnCriteria, null);
    }
    
    public static DataObject getReportBITableColumnID(final Criteria tableColumnCriteria, final Join tableColumnJoin) {
        DataObject joinColumnDO = null;
        try {
            final SelectQuery joinColumnSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ReportBITableColumn"));
            joinColumnSelectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            joinColumnSelectQuery.setCriteria(tableColumnCriteria);
            if (tableColumnJoin != null) {
                joinColumnSelectQuery.addJoin(tableColumnJoin);
            }
            joinColumnDO = SyMUtil.getPersistence().get(joinColumnSelectQuery);
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting DataObject in getDOForModuleTable" + e);
        }
        return joinColumnDO;
    }
    
    public static DataObject getColumnDetailsForCriteria(final Criteria columnCriteria) {
        DataObject columnDetailsDO = null;
        try {
            final SelectQuery columnDetailsSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
            columnDetailsSelect.addSelectColumn(new Column((String)null, "*"));
            columnDetailsSelect.setCriteria(columnCriteria);
            columnDetailsDO = SyMUtil.getPersistence().get(columnDetailsSelect);
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting column Details for columnID" + e);
        }
        return columnDetailsDO;
    }
    
    public static DataObject getAvailableModulesForUser(final Long userID) {
        DataObject moduleDO = null;
        try {
            final SelectQuery availableModuleSelect = (SelectQuery)new SelectQueryImpl(Table.getTable("UsersRoleMapping"));
            availableModuleSelect.addSelectColumn(Column.getColumn((String)null, "*"));
            availableModuleSelect.setDistinct(true);
            final Criteria userCriteria = new Criteria(Column.getColumn("AaaLogin", "USER_ID"), (Object)userID, 0);
            availableModuleSelect.setCriteria(userCriteria);
            availableModuleSelect.addJoin(new Join("UsersRoleMapping", "UMRole", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
            availableModuleSelect.addJoin(new Join("UMRole", "UMRoleModuleRelation", new String[] { "UM_ROLE_ID" }, new String[] { "UM_ROLE_ID" }, 2));
            availableModuleSelect.addJoin(new Join("UMRoleModuleRelation", "UMModule", new String[] { "UM_MODULE_ID" }, new String[] { "UM_MODULE_ID" }, 2));
            availableModuleSelect.addJoin(new Join("UMModule", "DCUserModule", new String[] { "DC_MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            availableModuleSelect.addJoin(new Join("DCUserModule", "ReportBIQuery", new String[] { "MODULE_ID" }, new String[] { "MODULE_ID" }, 2));
            availableModuleSelect.addJoin(new Join("UsersRoleMapping", "AaaLogin", new String[] { "LOGIN_ID" }, new String[] { "LOGIN_ID" }, 2));
            moduleDO = SyMUtil.getPersistence().get(availableModuleSelect);
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting Available Report Modules for the user" + e);
        }
        return moduleDO;
    }
    
    public static DataObject getReportBITableDetails(final String tableName, final Criteria criteria) {
        final SelectQuery reportSelectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
        reportSelectQuery.addSelectColumn(Column.getColumn((String)null, "*"));
        if (criteria != null) {
            reportSelectQuery.setCriteria(criteria);
        }
        try {
            return SyMUtil.getPersistence().get(reportSelectQuery);
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while retrieving ReportBIQuery " + e);
            return null;
        }
    }
    
    public static Boolean moduleApplicableForUser(final Long moduleID, final Long userID) {
        try {
            final JSONArray availableModules = getModuleDetailsForUser(userID);
            for (int i = 0; i < availableModules.length(); ++i) {
                final JSONObject moduleDetail = (JSONObject)availableModules.get(i);
                final Long moduleId = moduleDetail.getLong("moduleID");
                if (moduleId.equals(moduleID)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while authorising module for User" + e);
        }
        return false;
    }
    
    private static Boolean isModifiedTimeApplicableToTableID(final Long tableID) {
        Boolean isModifiedSyncApplicable = true;
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBIQuery", "TABLE_ID"), (Object)tableID, 0);
            final DataObject modifiedTimeApplicableDO = getReportBITableDetails("ReportBIQuery", tableIDCriteria);
            if (modifiedTimeApplicableDO != null) {
                final Row tableRow = modifiedTimeApplicableDO.getFirstRow("ReportBIQuery");
                isModifiedSyncApplicable = (Boolean)tableRow.get("MODIFIED_SYNC_AVAILABLE");
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting Modified time applicable for Table ID" + e);
        }
        return isModifiedSyncApplicable;
    }
    
    private static Boolean isReportBITableInUse(final Long tableID, final String apiVersion) {
        Boolean tableIsInUse = true;
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBIQuery", "TABLE_ID"), (Object)tableID, 0);
            final Criteria apiVersionCriteria = new Criteria(new Column("ReportBIQuery", "API_VERSION"), (Object)apiVersion, 0);
            final DataObject tableIsInUseDO = getReportBITableDetails("ReportBIQuery", tableIDCriteria.and(apiVersionCriteria));
            if (tableIsInUseDO != null) {
                final Row tableRow = tableIsInUseDO.getFirstRow("ReportBIQuery");
                tableIsInUse = (Boolean)tableRow.get("IS_IN_USE");
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting Modified time applicable for Table ID" + e);
        }
        return tableIsInUse;
    }
    
    private static Boolean returnsDistinctValues(final Long tableID) {
        Boolean setDistinct = false;
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBIQuery", "TABLE_ID"), (Object)tableID, 0);
            final DataObject distinctApplicableDO = getReportBITableDetails("ReportBIQuery", tableIDCriteria);
            if (distinctApplicableDO != null && !distinctApplicableDO.isEmpty()) {
                final Row tableRow = distinctApplicableDO.getFirstRow("ReportBIQuery");
                setDistinct = (Boolean)tableRow.get("SET_DISTINCT");
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting is Distinct flag for Table ID" + e);
        }
        return setDistinct;
    }
    
    public static Object getTransformedDataValue(final Long tableID, final String columnName, final Object dataValue) {
        LinkedHashMap<Object, String> transformValue = new LinkedHashMap<Object, String>();
        try {
            final Criteria tableIDCriteria = new Criteria(new Column("ReportBITableColumn", "TABLE_ID"), (Object)tableID, 0);
            final Criteria browseValueTypeCriteria = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)4L, 0);
            final Criteria i18NTypeCriteria = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)5L, 0);
            final Criteria classTransformTypeCriteria = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)6L, 0);
            final Criteria tableColumnCriteria = tableIDCriteria.and(browseValueTypeCriteria.or(i18NTypeCriteria).or(classTransformTypeCriteria));
            final Join tableColumnDetailsJoin = new Join("ReportBITableColumn", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            final DataObject tableColumnDO = getReportBITableColumnID(tableColumnCriteria, tableColumnDetailsJoin);
            final Criteria columnNameCriteria = new Criteria(new Column("CRColumns", "COLUMN_NAME_ALIAS"), (Object)columnName, 0);
            if (tableColumnDO != null) {
                Long columnType = null;
                final Row columnTypeRow = tableColumnDO.getRow("ReportBITableColumn", columnNameCriteria);
                if (columnTypeRow != null) {
                    columnType = (Long)columnTypeRow.get("TYPE");
                }
                final Row columnNameRow = tableColumnDO.getRow("CRColumns", columnNameCriteria);
                if (columnNameRow != null && columnType != null) {
                    final Long columnID = (Long)columnNameRow.get("COLUMN_ID");
                    if (columnType.equals(4L)) {
                        transformValue = CriteriaColumnValueUtil.getInstance().getTranformValueList(columnID, null);
                        if (transformValue != null && !transformValue.isEmpty()) {
                            return transformValue.get(String.valueOf(dataValue));
                        }
                    }
                    else {
                        if (columnType.equals(5L)) {
                            return I18N.getMsg(String.valueOf(dataValue), new Object[0]);
                        }
                        if (columnType.equals(6L)) {
                            final String className = ReportCriteriaUtil.getInstance().hasSpecialHandlerClass(columnID);
                            if (className != null) {
                                final ReportBIDataValueTransformer reportBIDataValueTransformer = (ReportBIDataValueTransformer)Class.forName(className).newInstance();
                                return reportBIDataValueTransformer.transformValue(columnName, dataValue);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, " Exception while getting Modified time applicable for Table ID" + e);
        }
        return dataValue;
    }
    
    public static DataObject getTransformedDataDO(final Long tableID, final String[] columnNames) {
        final Criteria tableIDCriteria = new Criteria(new Column("ReportBITableColumn", "TABLE_ID"), (Object)tableID, 0);
        final Criteria browseValueTypeCriteria = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)4L, 0);
        final Criteria i18NTypeCriteria = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)5L, 0);
        final Criteria classTransformTypeCriteria = new Criteria(new Column("ReportBITableColumn", "TYPE"), (Object)6L, 0);
        final Criteria tableColumnCriteria = tableIDCriteria.and(browseValueTypeCriteria.or(i18NTypeCriteria).or(classTransformTypeCriteria));
        final Criteria columnNameCriteria = new Criteria(new Column("CRColumns", "COLUMN_NAME_ALIAS"), (Object)columnNames, 8);
        final Join tableColumnDetailsJoin = new Join("ReportBITableColumn", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
        return getReportBITableColumnID(tableColumnCriteria.and(columnNameCriteria), tableColumnDetailsJoin);
    }
    
    public static Object getTransformDataValue(final String columnName, final Object dataValue, final HashMap transformValueForColumns) {
        Object transformedData = null;
        final LinkedHashMap<Object, String> transformValue = transformValueForColumns.get(columnName);
        if (transformValue != null && !transformValue.isEmpty()) {
            transformedData = transformValue.get(String.valueOf(dataValue));
        }
        return transformedData;
    }
    
    public static Object getClassTransformDataValue(final String columnName, final Object dataValue, final HashMap handlerClassForColumns) {
        Object transformedData = null;
        try {
            final String className = handlerClassForColumns.get(columnName);
            if (className != null) {
                final ReportBIDataValueTransformer reportBIDataValueTransformer = (ReportBIDataValueTransformer)Class.forName(className).newInstance();
                transformedData = reportBIDataValueTransformer.transformValue(columnName, dataValue);
            }
        }
        catch (final Exception e) {
            ReportBIUtil.out.log(Level.WARNING, "Exception in ReportBIUtil.getClassTransformDataValue().columnName: " + columnName, e);
        }
        return transformedData;
    }
    
    static {
        ReportBIUtil.className = ReportBIUtil.class.getName();
        ReportBIUtil.out = Logger.getLogger(ReportBIUtil.className);
    }
}
