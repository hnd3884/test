package com.adventnet.client.components.table.web;

import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import com.adventnet.ds.query.QueryConstructionException;
import java.sql.SQLException;
import com.adventnet.ds.query.Query;
import java.util.ArrayList;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.client.cache.web.ClientDataObjectCache;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Persistence;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.client.util.DataUtils;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import com.adventnet.client.components.table.TableViewState;
import java.util.List;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.util.HashMap;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;

public class TableUtil implements WebConstants, JavaScriptConstants, TableConstants
{
    private static Logger out;
    
    @Deprecated
    public static String getSortOrderNavigURL(final ViewContext viewContext, final String uniqueId, final String columnName) throws Exception {
        String sortorder = (String)viewContext.getStateOrURLStateParameter("_SO");
        if (sortorder != null && sortorder.equals("D")) {
            sortorder = "A";
        }
        else if (sortorder != null && sortorder.equals("A")) {
            sortorder = "D";
        }
        if (sortorder == null) {
            final Row tableRow = viewContext.getModel().getViewConfiguration().getFirstRow("ACTableViewConfig");
            sortorder = (String)tableRow.get("SORTORDER");
            if (sortorder == null) {
                sortorder = "A";
            }
            else {
                sortorder = ("DESC".equals(sortorder) ? "A" : "D");
            }
        }
        final String navigurl = viewContext.getRequest().getContextPath() + "/" + uniqueId + ".cc?";
        viewContext.setURLStateParameter("_SB", (Object)String.valueOf(columnName));
        viewContext.setURLStateParameter("_SO", (Object)String.valueOf(sortorder));
        final String rootview = WebViewAPI.getRootView(viewContext.getRequest(), viewContext);
        viewContext.setURLStateParameter("rootview", (Object)rootview);
        return navigurl + WebViewAPI.getAsURLStateParameters((HashMap)viewContext.getURLState());
    }
    
    public static Criteria spotSearchCriteria(final ViewContext viewCtx, final Column[] column, Criteria criteria) throws Exception {
        final String searchColumns = (String)viewCtx.getStateOrURLStateParameter("SEARCH_COLUMN");
        final String searchValues = (String)viewCtx.getStateOrURLStateParameter("SEARCH_VALUE");
        String[] columnNames = null;
        int colSize = 0;
        int valSize = 0;
        String[] columnValues = null;
        String[] comboValues = null;
        String[] transformerClassNames = null;
        if (searchColumns == null || searchColumns.isEmpty()) {
            return criteria;
        }
        columnNames = searchColumns.split(",");
        colSize = columnNames.length;
        if (searchValues != null) {
            columnValues = searchValues.split(",");
        }
        if (columnValues != null) {
            valSize = columnValues.length;
        }
        if (valSize == 0) {
            columnValues = new String[colSize];
            valSize = colSize;
        }
        transformerClassNames = new String[colSize];
        String searchValuesCombo = "";
        String searchValCombo = "";
        final DataObject viewDataObject = viewCtx.getModel().getViewConfiguration();
        final Row acTableViewRow = viewDataObject.getRow("ACTableViewConfig");
        final DataObject columnConfigDO = TableViewModel.getColumnConfigDO(acTableViewRow.get(4), viewDataObject);
        final Iterator<Row> columnConfigRows = columnConfigDO.getRows("ACColumnConfiguration");
        final boolean isAdvancedSearch = (boolean)viewDataObject.getFirstValue("ACTableViewConfig", 20);
        if (isAdvancedSearch) {
            searchValuesCombo = (String)viewCtx.getStateOrURLStateParameter("SEARCHCOMBO_VALUE");
            searchValCombo = (String)viewCtx.getStateOrURLStateParameter("SEARCHVAL_COMB");
            if (searchValuesCombo != null) {
                comboValues = searchValuesCombo.split(",");
            }
            if (comboValues == null) {
                throw new IllegalArgumentException("The state 'SEARCHVAL_COMB' cannot be null for Advanced Search.");
            }
        }
        for (int incr = 0; columnConfigRows.hasNext() && incr < colSize; ++incr) {
            final Row columnConfigRow = columnConfigRows.next();
            final String colAlias = (String)columnConfigRow.get(3);
            if (colAlias.equals(columnNames[incr])) {
                transformerClassNames[incr] = (String)columnConfigRow.get(7);
            }
        }
        viewCtx.updateStateOrURLStateParam("SEARCH_COLUMN", (Object)searchColumns);
        viewCtx.updateStateOrURLStateParam("SEARCH_VALUE", (Object)searchValues);
        if (isAdvancedSearch) {
            viewCtx.updateStateOrURLStateParam("SEARCHCOMBO_VALUE", (Object)searchValuesCombo);
            viewCtx.updateStateOrURLStateParam("SEARCHVAL_COMB", (Object)searchValCombo);
        }
        final int size = column.length;
        for (int count = 0; count < colSize; ++count) {
            String value = "";
            String comboVal = "";
            if (isAdvancedSearch) {
                comboVal = comboValues[count];
            }
            String columnVal = null;
            if (count < valSize) {
                columnVal = columnValues[count];
            }
            for (int ct = 0; ct < size; ++ct) {
                String columnAlias = column[ct].getColumnAlias();
                final String columnName = column[ct].getColumnName();
                if (columnAlias == null) {
                    columnAlias = columnName;
                }
                if (columnAlias.equals(columnNames[count])) {
                    final int type = column[ct].getType();
                    if (columnVal != null) {
                        if (colSize == valSize || count <= valSize - 1) {
                            value = columnVal.replaceAll("&#44;", ",");
                        }
                        else {
                            value = "";
                        }
                    }
                    Criteria crit = null;
                    if (!isAdvancedSearch) {
                        int compare;
                        if (type == 12) {
                            value = escapeSpecialCharacters(value);
                            compare = 12;
                        }
                        else {
                            compare = 0;
                        }
                        crit = new Criteria(column[ct], (Object)value, compare, false);
                    }
                    else {
                        try {
                            final int compare = Integer.parseInt(comboVal);
                            value = ((type == 12 && compare != 0) ? escapeSpecialCharacters(value) : value);
                            transformerClassNames[count] = ((transformerClassNames[count] == null) ? "com.adventnet.client.components.table.web.DefaultTransformer" : transformerClassNames[count]);
                            final Object transformerClass = WebClientUtil.createInstance(transformerClassNames[count]);
                            if (ColumnTransformer.class.isAssignableFrom(transformerClass.getClass())) {
                                final ColumnTransformer ctObj = (ColumnTransformer)transformerClass;
                                crit = ctObj.formCriteria(column[ct], value, compare, type);
                            }
                            else {
                                TableUtil.out.log(Level.WARNING, "Incompatible class {0} specified as ColumnTransformer", transformerClassNames[count]);
                            }
                        }
                        catch (final Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (criteria != null) {
                        criteria = ((crit != null) ? criteria.and(crit) : criteria);
                    }
                    else {
                        criteria = crit;
                    }
                }
            }
        }
        return criteria;
    }
    
    public static Criteria getSearchCriteria(final ViewContext viewContext, final List<Column> columns) throws Exception {
        final TableViewState tableViewState = (TableViewState)viewContext.getViewState();
        final String[] searchColumns = tableViewState.getSearchColumns();
        if (searchColumns.length == 0) {
            return null;
        }
        final int[] comparators = tableViewState.getSearchComparators();
        final String[] searchValues = tableViewState.getSearchValues();
        if (searchValues.length < searchColumns.length) {
            Arrays.fill(searchValues, searchValues.length, searchColumns.length, "");
        }
        final DataObject viewDataObject = viewContext.getModel().getViewConfiguration();
        final Row acTableViewRow = viewDataObject.getRow("ACTableViewConfig");
        final DataObject columnConfigDO = TableViewModel.getColumnConfigDO(acTableViewRow.get(4), viewDataObject);
        final boolean isAdvancedSearch = (boolean)viewDataObject.getFirstValue("ACTableViewConfig", 20);
        final Map<String, Column> columnNameVsColumnObj = new LinkedHashMap<String, Column>();
        Arrays.stream(searchColumns).forEach(columnName -> list.stream().filter(columnObj -> columnObj.getColumnAlias().equals(s)).forEach(columnObj -> {
            final Column column2 = map.put(s2, columnObj);
        }));
        int idx = 0;
        Criteria searchCriteria = null;
        for (final Column column : columnNameVsColumnObj.values()) {
            final Criteria columnAliasCriteria = new Criteria(Column.getColumn("ACColumnConfiguration", "COLUMNALIAS"), (Object)column.getColumnAlias(), 0);
            final ColumnTransformer columnTransformer = (ColumnTransformer)WebClientUtil.createInstance((String)columnConfigDO.getValue("ACColumnConfiguration", 7, columnAliasCriteria));
            final int comparator = isAdvancedSearch ? comparators[idx] : ((column.getType() == 12) ? 12 : 0);
            final String value = (column.getType() == 12 && comparator != 0) ? escapeSpecialCharacters(searchValues[idx]) : searchValues[idx];
            final Criteria crit = columnTransformer.formCriteria(column, value, comparator, column.getType());
            searchCriteria = ((searchCriteria == null) ? crit : searchCriteria.and(crit));
            ++idx;
        }
        return searchCriteria;
    }
    
    public static String escapeSpecialCharacters(String value) {
        if (value.contains("\\")) {
            value = value.replaceAll("\\\\", "\\\\\\\\");
        }
        if (value.contains("\"")) {
            value = value.replaceAll("\"", "\\\\\"");
        }
        if (value.contains("*")) {
            value = value.replaceAll("\\*", "\\\\*");
        }
        if (value.contains("?")) {
            value = value.replaceAll("\\?", "\\\\?");
        }
        return value;
    }
    
    public static void addDynamicColumnInView(final String viewName, final ColumnDefinition columnDef, final Row columnConfigurationRow) throws Exception {
        final String displayName = columnDef.getDisplayName();
        final String columnName = columnDef.getColumnName();
        final String tableName = columnDef.getTableName();
        final String tableAlias = columnConfigurationRow.getString("TABLEALIAS");
        final String columnAlias = columnConfigurationRow.getString("COLUMNALIAS");
        if (!columnDef.isDynamic()) {
            throw new Exception("cannot add physical column in view, should be a dynamic column  : " + columnDef);
        }
        if (tableAlias == null || columnAlias == null) {
            TableUtil.out.log(Level.SEVERE, "tableAlias or columnAlias should not be null for dynamic column row : " + columnConfigurationRow);
            throw new Exception("tableAlias or columnAlias should not be null  for dynamic column row : " + columnConfigurationRow);
        }
        try {
            final Persistence pers = LookUpUtil.getPersistence();
            DataObject viewConfigDO = (DataObject)WebViewAPI.getViewConfiguration((Object)viewName).clone();
            if (!viewConfigDO.containsTable("ACTableViewConfig")) {
                throw new Exception("Cannot add a column for the given view, should be a table view : " + viewName);
            }
            final Long columnConfigList = viewConfigDO.getFirstRow("ACTableViewConfig").getLong(4);
            final DataObject columnConfigDO = TableViewModel.getColumnConfigDO(columnConfigList, viewConfigDO);
            int maxColIdx = DataUtils.getMaxIndex(columnConfigDO, "ACColumnConfiguration", 2);
            maxColIdx = ((maxColIdx < 1000) ? 1000 : (maxColIdx + 1));
            columnConfigurationRow.set(2, (Object)maxColIdx);
            columnConfigurationRow.set(1, viewConfigDO.getRow("ACTableViewConfig").get(4));
            columnConfigurationRow.set(32, (Object)columnDef.getDataType());
            if (columnConfigurationRow.get(4) == null) {
                columnConfigurationRow.set(4, (Object)displayName);
            }
            columnConfigurationRow.set(33, (Object)true);
            viewConfigDO.addRow(columnConfigurationRow);
            final Row dynamicColumnConfig = new Row("DynamicColumnConfiguration");
            dynamicColumnConfig.set(2, (Object)tableAlias);
            dynamicColumnConfig.set(4, (Object)columnName);
            final String dcColumnName = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().getDCSQLGeneratorForTable(tableName).getDCSpecificColumnName(columnName);
            dynamicColumnConfig.set(5, (Object)dcColumnName);
            dynamicColumnConfig.set(3, (Object)columnAlias);
            final List<String> persviewnames = UserPersonalizationAPI.getPersonalizedViewNames(viewName);
            if (persviewnames != null) {
                for (final String persView : persviewnames) {
                    final Row personalizedDynamicColumnConfig = (Row)dynamicColumnConfig.clone();
                    personalizedDynamicColumnConfig.set(1, (Object)WebViewAPI.getViewNameNo((Object)persView));
                    viewConfigDO.addRow(personalizedDynamicColumnConfig);
                }
            }
            dynamicColumnConfig.set(1, (Object)WebViewAPI.getViewNameNo((Object)viewName));
            viewConfigDO.addRow(dynamicColumnConfig);
            pers.update(viewConfigDO);
            final Row actableColumn = new Row("ACTableColumns");
            actableColumn.set(3, (Object)columnAlias);
            actableColumn.set(2, (Object)maxColIdx);
            actableColumn.set(5, columnConfigurationRow.get("VISIBLE"));
            actableColumn.set(4, (Object)displayName);
            final String persviewname = UserPersonalizationAPI.getPersonalizedViewName((Object)viewName, WebClientUtil.getAccountId());
            if (persviewname != null) {
                final DataObject personalizedViewDO = getPersonalisedDOForView(persviewname);
                if (personalizedViewDO.containsTable("ACTableColumns")) {
                    actableColumn.set(1, (Object)WebViewAPI.getViewNameNo((Object)persviewname));
                    personalizedViewDO.addRow(actableColumn);
                    pers.update(personalizedViewDO);
                    clearCacheForView(persviewname);
                }
            }
            viewConfigDO = getPersonalisedDOForView(viewName);
            if (viewConfigDO.containsTable("ACTableColumns")) {
                actableColumn.set(1, (Object)WebViewAPI.getViewNameNo((Object)viewName));
                viewConfigDO.addRow(actableColumn);
                pers.update(viewConfigDO);
            }
        }
        catch (final Exception ex) {
            TableUtil.out.log(Level.SEVERE, "Exception occurred while adding dynamic column for the view  ::" + viewName);
            ex.printStackTrace();
            throw new Exception("Exception occurred while adding dynamic column for the view  ::" + viewName, ex);
        }
        finally {
            clearCacheForView(viewName);
        }
    }
    
    private static DataObject getPersonalisedDOForView(final String viewname) throws DataAccessException {
        final Row viewConfigRow = new Row("ViewConfiguration");
        viewConfigRow.set("VIEWNAME", (Object)viewname);
        return LookUpUtil.getPersistence().getForPersonality("TableViewConfig", viewConfigRow);
    }
    
    public static void removeDynamicColumnFromView(final String viewName, final String tableAlias, final String columnAlias) throws Exception {
        if (tableAlias == null || columnAlias == null) {
            TableUtil.out.log(Level.SEVERE, "tableAlias : {0} or columnAlias :{1} should not be null ", new Object[] { tableAlias, columnAlias });
            throw new Exception("Dynamic column removal failed for view : " + viewName);
        }
        try {
            final Persistence pers = LookUpUtil.getPersistence();
            final DataObject viewConfigDO = (DataObject)WebViewAPI.getViewConfiguration((Object)viewName).clone();
            if (!viewConfigDO.containsTable("ACTableViewConfig")) {
                throw new Exception("Cannot remove a column for the given view, should be a table view : " + viewName);
            }
            final Object configName = viewConfigDO.getFirstRow("ACTableViewConfig").get(4);
            Criteria columnConfigCriteria = new Criteria(Column.getColumn("ACColumnConfiguration", "CONFIGNAME"), configName, 0);
            columnConfigCriteria = columnConfigCriteria.and(new Criteria(Column.getColumn("ACColumnConfiguration", "COLUMNALIAS"), (Object)columnAlias, 0, true));
            final DataObject dob = pers.get("ACColumnConfiguration", columnConfigCriteria);
            final Row columnConfigRow = dob.getFirstRow("ACColumnConfiguration");
            if (!columnConfigRow.getBoolean("ISDYNAMIC")) {
                throw new RuntimeException("Static column: [" + columnAlias + "] cannot be deleted");
            }
            pers.delete(columnConfigRow);
            final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("ViewConfiguration"));
            subQuery.addSelectColumn(Column.getColumn("ViewConfiguration", "VIEWNAME_NO"));
            final Column viewnameColumn = Column.getColumn("ViewConfiguration", "VIEWNAME");
            Criteria cri = new Criteria(viewnameColumn, (Object)viewName, 0);
            cri = cri.or(viewnameColumn, (Object)(viewName + "_PERSVIEW*"), 2);
            subQuery.setCriteria(cri);
            final DerivedColumn dc = new DerivedColumn("VIEWNAME", subQuery);
            final DeleteQuery deleteACTableColumn = (DeleteQuery)new DeleteQueryImpl("ACTableColumns");
            Criteria acTableColumnCriteria = new Criteria(Column.getColumn("ACTableColumns", "VIEWNAME"), (Object)dc, 8);
            acTableColumnCriteria = acTableColumnCriteria.and(Column.getColumn("ACTableColumns", "COLUMNALIAS"), (Object)columnAlias, 0);
            deleteACTableColumn.setCriteria(acTableColumnCriteria);
            TableUtil.out.log(Level.FINE, "DeleteQuery for the column {0} is : {1}", new Object[] { columnAlias, deleteACTableColumn });
            pers.delete(deleteACTableColumn);
            final DeleteQuery deleteDynamicColumnConfig = (DeleteQuery)new DeleteQueryImpl("DynamicColumnConfiguration");
            Criteria dynamicColumnConfigCriteria = new Criteria(Column.getColumn("DynamicColumnConfiguration", "VIEWNAME"), (Object)dc, 8);
            dynamicColumnConfigCriteria = dynamicColumnConfigCriteria.and(Column.getColumn("DynamicColumnConfiguration", "COLUMNALIAS"), (Object)columnAlias, 0);
            deleteDynamicColumnConfig.setCriteria(dynamicColumnConfigCriteria);
            TableUtil.out.log(Level.FINE, "DeleteQuery for the column {0} is : {1}", new Object[] { columnAlias, deleteDynamicColumnConfig });
            pers.delete(deleteDynamicColumnConfig);
        }
        finally {
            clearCacheForView(viewName);
        }
    }
    
    private static void clearCacheForView(final String origviewname) {
        ClientDataObjectCache.clearCacheForView(origviewname);
        final List<String> perviewnames = UserPersonalizationAPI.getPersonalizedViewNames(origviewname);
        if (perviewnames != null) {
            for (final String viewname : perviewnames) {
                ClientDataObjectCache.clearCacheForView(viewname);
            }
        }
        StaticCache.removeFromCache((Object)(origviewname + "_SQL"));
        StaticCache.removeFromCache((Object)(origviewname + "_COLUMNNAMES"));
    }
    
    public static List<DynamicColumn> getDynamicColumns(final long viewNameNo) throws SQLException {
        final String cacheKey = viewNameNo + "_DYNAMIC_COLUMNS";
        List<DynamicColumn> dynamicColumns = (List<DynamicColumn>)StaticCache.getFromCache((Object)cacheKey);
        if (dynamicColumns != null) {
            return dynamicColumns;
        }
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DynamicColumnConfiguration"));
        sq.addSelectColumn(Column.getColumn("DynamicColumnConfiguration", "*"));
        sq.setCriteria(new Criteria(Column.getColumn("DynamicColumnConfiguration", "VIEWNAME"), (Object)viewNameNo, 0));
        dynamicColumns = new ArrayList<DynamicColumn>();
        try (final Connection conn = RelationalAPI.getInstance().getConnection();
             final DataSet ds = RelationalAPI.getInstance().executeQuery((Query)sq, conn)) {
            while (ds.next()) {
                final String columnAlias = ds.getAsString("COLUMNALIAS");
                final String columnName = ds.getAsString("COLUMNNAME");
                final String dyColName = ds.getAsString("DYCOLUMNNAME");
                final String tableAlias = ds.getAsString("TABLEALIAS");
                dynamicColumns.add(new DynamicColumn(columnAlias, columnName, dyColName, tableAlias));
            }
        }
        catch (final QueryConstructionException qce) {
            throw new SQLException("Problem while fetching DynamicColumnConfiguration for the view: " + viewNameNo, (Throwable)qce);
        }
        StaticCache.addToCache((Object)cacheKey, (Object)dynamicColumns, (List)new ArrayList(Collections.singleton("DynamicColumnConfiguration")));
        return dynamicColumns;
    }
    
    static {
        TableUtil.out = Logger.getLogger(TableUtil.class.getName());
    }
    
    public static class DynamicColumn
    {
        private String columnAlias;
        private String columnName;
        private String tableAlias;
        private String dyColName;
        
        public DynamicColumn(final String columnAlias, final String columnName, final String dyColName, final String tableAlias) {
            this.columnAlias = columnAlias;
            this.columnName = columnName;
            this.dyColName = dyColName;
            this.tableAlias = tableAlias;
        }
        
        public String getColumnAlias() {
            return this.columnAlias;
        }
        
        public String getColumnName() {
            return this.columnName;
        }
        
        public String getDCSpecificColumnName() {
            return this.dyColName;
        }
        
        public String getTableAlias() {
            return this.tableAlias;
        }
        
        @Override
        public String toString() {
            return "columnAlias:" + this.columnAlias + ", columnName:" + this.columnName + ", dcSpecificColumnName:" + this.dyColName + ", tableAlias:" + this.tableAlias;
        }
    }
}
