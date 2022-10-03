package com.me.devicemanagement.framework.server.reportcriteria;

import java.util.Set;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import java.util.Iterator;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.LinkedHashMap;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.Join;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.logging.Level;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class CriteriaColumnValueUtil
{
    private static Logger logger;
    private static CriteriaColumnValueUtil criteriaColumnValueUtilBase;
    
    public static CriteriaColumnValueUtil getInstance() {
        if (CriteriaColumnValueUtil.criteriaColumnValueUtilBase == null) {
            CriteriaColumnValueUtil.criteriaColumnValueUtilBase = new CriteriaColumnValueUtil();
        }
        return CriteriaColumnValueUtil.criteriaColumnValueUtilBase;
    }
    
    public String getDataType(final DataObject dataObject) throws DataAccessException {
        String dataType = null;
        dataType = String.valueOf(dataObject.getFirstRow("CRColumns").get("DATA_TYPE"));
        return dataType;
    }
    
    public DataObject crcolumnDetails(final Long columnId) {
        CriteriaColumnValueUtil.logger.log(Level.FINEST, "Entered into CriteriaColumnValueUtil.crcolumnDetails");
        DataObject dataObject = null;
        try {
            final SelectQuery crcolumnQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CRColumns"));
            final Column idColumn = new Column("CRColumns", "COLUMN_ID");
            final Column tableNameColumn = new Column("CRColumns", "TABLE_NAME");
            final Column columnNameColumn = new Column("CRColumns", "COLUMN_NAME");
            final Column dataTypeColumn = new Column("CRColumns", "DATA_TYPE");
            final Criteria columnIdCriteria = new Criteria(idColumn, (Object)columnId, 0);
            crcolumnQuery.addSelectColumn(idColumn);
            crcolumnQuery.addSelectColumn(tableNameColumn);
            crcolumnQuery.addSelectColumn(columnNameColumn);
            crcolumnQuery.addSelectColumn(dataTypeColumn);
            crcolumnQuery.setCriteria(columnIdCriteria);
            dataObject = SyMUtil.getPersistence().get(crcolumnQuery);
            CriteriaColumnValueUtil.logger.log(Level.INFO, "Report Criteria Browse: crcolumn query string: {0}", RelationalAPI.getInstance().getSelectSQL((Query)crcolumnQuery));
        }
        catch (final Exception ex) {
            CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while fetching Criteria Column details from CRColumns", ex);
        }
        return dataObject;
    }
    
    public SelectQuery customBrowseValuesFetchQuery(final Long columnId, final Long viewId, final String filter, final Boolean isI18N, final List customSearchValuesList) {
        CriteriaColumnValueUtil.logger.log(Level.FINEST, "Entered into CriteriaColumnValueUtil.customBrowseValuesFetchQuery");
        SelectQuery customBrowseQuery = null;
        try {
            final SelectQuery customQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("BrowseValueFetchQuery"));
            final Column idColumn = new Column("BrowseValueFetchQuery", "COLUMN_ID");
            final Column viewIdColumn = new Column("BrowseValueFetchQuery", "VIEW_ID");
            final Criteria columnIdCriteria = new Criteria(idColumn, (Object)columnId, 0);
            final Criteria viewIdcriteria = new Criteria(viewIdColumn, (Object)viewId, 0);
            final Join queryJoin = new Join("BrowseValueFetchQuery", "SelectQuery", new String[] { "QUERYID" }, new String[] { "QUERYID" }, 2);
            customQuery.addSelectColumn(Column.getColumn((String)null, "*"));
            customQuery.addJoin(queryJoin);
            customQuery.setCriteria(columnIdCriteria.and(viewIdcriteria));
            final DataObject customQueryObject = SyMUtil.getPersistence().get(customQuery);
            CriteriaColumnValueUtil.logger.log(Level.INFO, "Custom query id fetch query: {0}", RelationalAPI.getInstance().getSelectSQL((Query)customQuery));
            if (!customQueryObject.isEmpty()) {
                final Row row = customQueryObject.getFirstRow("SelectQuery");
                final Long queryId = (Long)row.get("QUERYID");
                customBrowseQuery = QueryUtil.getSelectQuery((long)queryId);
                List<Column> selectColumns = new ArrayList<Column>();
                selectColumns = customBrowseQuery.getSelectColumns();
                final int columnCount = selectColumns.size();
                customBrowseQuery.setDistinct(true);
                customBrowseQuery.addSortColumn(new SortColumn((Column)selectColumns.get(columnCount - 1), true));
                Column selectColumn = null;
                if (!customBrowseQuery.getSelectColumns().isEmpty() && customBrowseQuery.getSelectColumns() != null) {
                    selectColumn = customBrowseQuery.getSelectColumns().get(0);
                }
                if (selectColumn != null) {
                    if (customSearchValuesList != null && !customSearchValuesList.isEmpty()) {
                        final Criteria customSearchCriteria = new Criteria(selectColumn, (Object)customSearchValuesList.toArray(), 8, false);
                        Criteria customCriteria = customBrowseQuery.getCriteria();
                        customCriteria = ((customCriteria != null) ? customCriteria.and(customSearchCriteria) : customSearchCriteria);
                        customBrowseQuery.setCriteria(customCriteria);
                    }
                    else if (filter != null && !filter.isEmpty() && !isI18N) {
                        final Criteria filterCriteria = new Criteria(selectColumn, (Object)filter, 12, false);
                        Criteria customCriteria = customBrowseQuery.getCriteria();
                        customCriteria = ((customCriteria != null) ? customCriteria.and(filterCriteria) : filterCriteria);
                        customBrowseQuery.setCriteria(customCriteria);
                    }
                }
                CriteriaColumnValueUtil.logger.log(Level.INFO, "Custom query: {0}", RelationalAPI.getInstance().getSelectSQL((Query)customBrowseQuery));
            }
        }
        catch (final Exception ex) {
            CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while fetching custom browse values fetch query", ex);
        }
        return customBrowseQuery;
    }
    
    public SelectQuery defaultBrowseValuesFetchQuery(final DataObject browseColumnDetails, final String filter, final List customSearchValuesList) {
        CriteriaColumnValueUtil.logger.log(Level.FINEST, "Entered into CriteriaColumnValueUtil.defaultBrowseValuesFetchQuery");
        SelectQuery browseValueFetchQuery = null;
        if (!browseColumnDetails.isEmpty()) {
            try {
                final Row columnDetails = browseColumnDetails.getFirstRow("CRColumns");
                final String tableName = String.valueOf(columnDetails.get("TABLE_NAME"));
                final String dataType = String.valueOf(columnDetails.get("DATA_TYPE"));
                final String columnName = String.valueOf(columnDetails.get("COLUMN_NAME"));
                final Column selectColumn = new Column(tableName, columnName);
                browseValueFetchQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tableName));
                if (customSearchValuesList != null && !customSearchValuesList.isEmpty()) {
                    final Criteria filterCriteria = new Criteria(Column.getColumn(tableName, columnName), (Object)customSearchValuesList.toArray(), 8, false);
                    browseValueFetchQuery.setCriteria(filterCriteria);
                }
                else if (filter != null && !filter.isEmpty() && !dataType.equalsIgnoreCase("I18N")) {
                    final Criteria filterCriteria = new Criteria(Column.getColumn(tableName, columnName), (Object)filter, 12, false);
                    browseValueFetchQuery.setCriteria(filterCriteria);
                }
                browseValueFetchQuery.addSelectColumn(Column.getColumn(tableName, columnName));
                browseValueFetchQuery.setDistinct(true);
                browseValueFetchQuery.addSortColumn(new SortColumn(selectColumn, true));
            }
            catch (final Exception ex) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while fetchinf default browse values fetch query", ex);
            }
        }
        return browseValueFetchQuery;
    }
    
    public List getBrowseValueList(final SelectQuery searchValueQuery, final String dataType, final String filter) {
        CriteriaColumnValueUtil.logger.log(Level.FINEST, "Entered into CriteriaColumnValueUtil.getBrowseValueList");
        final LinkedHashMap<Object, Object> browseValues = new LinkedHashMap<Object, Object>();
        browseValues.putAll(this.getBrowseValuesFromDB(searchValueQuery));
        final List searchValues = this.getBrowseValues(browseValues, dataType, filter);
        CriteriaColumnValueUtil.logger.log(Level.INFO, "searchValues: ", searchValues);
        return searchValues;
    }
    
    public JSONArray getBrowseValuesDefault(final LinkedHashMap valuesFromDB) {
        CriteriaColumnValueUtil.logger.log(Level.FINEST, "Entered into CriteriaColumnValueUtil.getBrowseValuesDefault");
        final JSONArray searchValues = new JSONArray();
        JSONObject searchData = new JSONObject();
        final LinkedHashMap<Object, Object> browseValues = new LinkedHashMap<Object, Object>();
        browseValues.putAll(valuesFromDB);
        final Iterator iterator = browseValues.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                searchData = new JSONObject();
                final Object key = iterator.next();
                searchData.put("searchValue", key);
                searchData.put("displayValue", browseValues.get(key));
            }
            catch (final Exception ex) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while fetching browse values in default method", ex);
            }
            searchValues.put((Object)searchData);
        }
        CriteriaColumnValueUtil.logger.log(Level.FINE, "getTranformValueList: searchValues--: {0}", searchValues);
        return searchValues;
    }
    
    public List getBrowseValuesWithTransformation(final LinkedHashMap transformValues, final String filter) {
        CriteriaColumnValueUtil.logger.log(Level.FINEST, "Entered into CriteriaColumnValueUtil.getBrowseValuesWithTransformation");
        final List searchValues = new ArrayList();
        final LinkedHashMap<Object, Object> transformValuesObject = new LinkedHashMap<Object, Object>();
        transformValuesObject.putAll(transformValues);
        final LinkedHashMap<String, String> transformValuesString = new LinkedHashMap<String, String>();
        for (final Map.Entry<Object, Object> entry : transformValuesObject.entrySet()) {
            try {
                transformValuesString.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }
            catch (final ClassCastException cce) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while covnerting transformation values to string", cce);
            }
        }
        final Iterator iterator = transformValuesString.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                final Map searchData = new LinkedHashMap();
                final Object key = iterator.next();
                searchData.put("searchValue", key);
                final String value = String.valueOf(transformValuesString.get(key));
                searchData.put("displayValue", value);
                if (filter != null && !filter.isEmpty() && !value.toLowerCase().contains(filter.toLowerCase())) {
                    continue;
                }
                searchValues.add(searchData);
            }
            catch (final Exception ex) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while fetching browse value transformation object", ex);
            }
        }
        CriteriaColumnValueUtil.logger.log(Level.INFO, "getBrowseValuesWithTransformation: Transformed searchValues--: {0}", searchValues);
        return searchValues;
    }
    
    public JSONArray getBrowseValuesForBoolean(final LinkedHashMap valuesFromDB) {
        final JSONArray searchValues = new JSONArray();
        JSONObject searchData = new JSONObject();
        final LinkedHashMap<Object, Object> browseValues = new LinkedHashMap<Object, Object>();
        browseValues.putAll(valuesFromDB);
        final Iterator iterator = browseValues.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                searchData = new JSONObject();
                final boolean boolValue = iterator.next();
                searchData.put("searchValue", boolValue);
                if (boolValue) {
                    searchData.put("displayValue", (Object)"True");
                }
                else if (!boolValue) {
                    searchData.put("displayValue", (Object)"False");
                }
            }
            catch (final Exception ex) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while generating boolean browse values", ex);
            }
            searchValues.put((Object)searchData);
        }
        CriteriaColumnValueUtil.logger.log(Level.INFO, "getBrowseValuesForBoolean: searchValues--: {0}", searchValues);
        return searchValues;
    }
    
    public List getBrowseValues(final LinkedHashMap browseValues, final String dataType, final String filter) {
        final List searchValues = new ArrayList();
        Map searchData = new HashMap();
        final Iterator iterator = browseValues.keySet().iterator();
        while (iterator.hasNext()) {
            try {
                searchData = new HashMap();
                final Object key = iterator.next();
                String displayValue = "";
                if (dataType.equalsIgnoreCase("I18N")) {
                    displayValue = I18N.getMsg(key.toString(), new Object[0]);
                    searchData.put("searchValue", key);
                    searchData.put("displayValue", displayValue);
                }
                else if (dataType.equalsIgnoreCase("BOOLEAN")) {
                    displayValue = (key ? "True" : "False");
                    searchData.put("searchValue", (boolean)key);
                    searchData.put("displayValue", displayValue);
                }
                else {
                    displayValue = ((browseValues.get(key) != null) ? browseValues.get(key).toString() : "");
                    searchData.put("searchValue", key);
                    searchData.put("displayValue", displayValue);
                }
                if (filter != null && !filter.isEmpty() && !displayValue.isEmpty() && !displayValue.toLowerCase().contains(filter.toLowerCase())) {
                    continue;
                }
                searchValues.add(searchData);
            }
            catch (final Exception ex) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while generating I18N browse values", ex);
            }
        }
        CriteriaColumnValueUtil.logger.log(Level.INFO, "getBrowseValuesForI18n: searchValues--: {0}", searchValues);
        return searchValues;
    }
    
    public LinkedHashMap getBrowseValuesFromDB(final SelectQuery searchValueQuery) {
        searchValueQuery.setDistinct(true);
        final LinkedHashMap<Object, Object> browseValues = new LinkedHashMap<Object, Object>();
        final RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relApi.getConnection();
            ds = relApi.executeQuery((Query)searchValueQuery, conn);
            CriteriaColumnValueUtil.logger.log(Level.INFO, "getBrowseValuesFromDB: search values query string: {0}", RelationalAPI.getInstance().getSelectSQL((Query)searchValueQuery));
            while (ds.next()) {
                final int columns = ds.getColumnCount();
                final Object key = ds.getValue(1);
                Object value = null;
                if (columns > 1) {
                    value = ds.getValue(2);
                }
                else {
                    value = key;
                }
                browseValues.put(key, value);
            }
        }
        catch (final Exception ex) {
            CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while fetching browse values from DB", ex);
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final SQLException ex2) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while closing dataset while fetching browse values from DB", ex2);
            }
            try {
                conn.close();
            }
            catch (final SQLException ex2) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while closing connection", ex2);
            }
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final SQLException ex3) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while closing dataset while fetching browse values from DB", ex3);
            }
            try {
                conn.close();
            }
            catch (final SQLException ex3) {
                CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while closing connection", ex3);
            }
        }
        CriteriaColumnValueUtil.logger.log(Level.FINE, "getBrowseValuesFromDB: browseValues--: {0}", browseValues);
        return browseValues;
    }
    
    public LinkedHashMap getTranformValueList(final Long columnId, final List customSearchValuesList) {
        LinkedHashMap<Object, String> transformValue = new LinkedHashMap<Object, String>();
        final ArrayList columnIDs = new ArrayList();
        columnIDs.add(columnId);
        final HashMap transformValueForcolumn = this.getTransformValues(columnIDs);
        final Set<String> keys = transformValueForcolumn.keySet();
        for (final String columnName : keys) {
            transformValue = transformValueForcolumn.get(columnName);
        }
        if (customSearchValuesList != null && !customSearchValuesList.isEmpty()) {
            final Set browseKeys = transformValue.keySet();
            browseKeys.removeIf(delKey -> !list.contains(delKey));
        }
        CriteriaColumnValueUtil.logger.log(Level.INFO, "getTranformValueList: transformValue--: {0}", transformValue);
        return transformValue;
    }
    
    public HashMap getTransformValues(final ArrayList columnIDs) {
        final HashMap transformValueForColumn = new HashMap();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("BrowseValueTransformation"));
        try {
            final Join crColumnJoin = new Join("BrowseValueTransformation", "CRColumns", new String[] { "COLUMN_ID" }, new String[] { "COLUMN_ID" }, 2);
            final Criteria columnIdCriteria = new Criteria(new Column("BrowseValueTransformation", "COLUMN_ID"), (Object)columnIDs.toArray(), 8);
            query.addSelectColumn(Column.getColumn((String)null, "*"));
            query.addJoin(crColumnJoin);
            query.addSortColumn(new SortColumn(Column.getColumn("BrowseValueTransformation", "BROWSE_VALUE"), true));
            query.setCriteria(columnIdCriteria);
            final DataObject transformValuesDO = SyMUtil.getPersistence().get(query);
            CriteriaColumnValueUtil.logger.log(Level.INFO, "Transform value query: {0}", RelationalAPI.getInstance().getSelectSQL((Query)query));
            if (!transformValuesDO.isEmpty()) {
                for (final Object columnID : columnIDs) {
                    final LinkedHashMap<Object, String> transformValue = new LinkedHashMap<Object, String>();
                    final Criteria columnIDCrit = new Criteria(new Column("BrowseValueTransformation", "COLUMN_ID"), columnID, 0);
                    final Iterator iterator = transformValuesDO.getRows("BrowseValueTransformation", columnIDCrit);
                    while (iterator.hasNext()) {
                        final Row row = iterator.next();
                        final boolean isI18n = (boolean)row.get("IS_I18N");
                        String value = (String)row.get("BROWSE_VALUE");
                        if (isI18n) {
                            value = I18N.getMsg(value, new Object[0]);
                        }
                        transformValue.put(row.get("BROWSE_KEY"), value);
                    }
                    final String columnName = (String)transformValuesDO.getValue("CRColumns", "COLUMN_NAME_ALIAS", columnIDCrit);
                    if (columnName != null) {
                        transformValueForColumn.put(columnName, transformValue);
                    }
                }
            }
        }
        catch (final Exception ex) {
            CriteriaColumnValueUtil.logger.log(Level.SEVERE, "Report Criteria Browse: Exception while fetching tranformation value list", ex);
        }
        return transformValueForColumn;
    }
    
    static {
        CriteriaColumnValueUtil.logger = Logger.getLogger("ScheduleReportLogger");
        CriteriaColumnValueUtil.criteriaColumnValueUtilBase = null;
    }
}
