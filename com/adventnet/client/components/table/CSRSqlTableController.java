package com.adventnet.client.components.table;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.util.Locale;
import com.adventnet.beans.xtable.SortColumn;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.client.components.table.web.TableUtil;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.components.table.web.CSRTableDataUtil;
import org.json.JSONObject;
import com.adventnet.client.view.State;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.components.table.web.TablePersonalizationUtil;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.ds.query.Range;
import java.util.Properties;
import com.adventnet.client.components.table.web.ExportTableModel;
import java.util.List;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import java.util.Map;
import com.adventnet.client.view.common.ExportUtils;
import java.util.logging.Level;
import com.adventnet.client.components.table.web.TableDatasetModel;
import com.adventnet.client.components.sql.SQLQueryAPI;
import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.TableViewController;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.view.web.DefaultViewController;

public class CSRSqlTableController extends DefaultViewController implements TemplateAPI.VariableHandler, TableViewController
{
    private static final Logger OUT;
    protected HashMap<String, String> sqlTypes;
    
    public CSRSqlTableController() {
        this.sqlTypes = null;
    }
    
    protected String initializeSQL(final ViewContext viewCtx) throws Exception {
        final String sql = this.getSQLString(viewCtx);
        final String orderByString = this.getSorting(viewCtx);
        final int orderByIdx = sql.toUpperCase().lastIndexOf("ORDER BY");
        String limitedSql;
        if (!orderByString.isEmpty() && orderByIdx != -1) {
            limitedSql = sql.substring(0, orderByIdx) + " " + orderByString;
        }
        else {
            limitedSql = sql + " " + orderByString;
        }
        return limitedSql;
    }
    
    public long getCount(final ViewContext viewCtx) throws Exception {
        viewCtx.setTransientState("fetchCountOnly", (Object)true);
        final TableDatasetModel tdm = SQLQueryAPI.getAsTableModel(null, this.getCountSQL(viewCtx, this.initializeSQL(viewCtx)), viewCtx, true);
        return tdm.getTotalRecordsCount();
    }
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        this.preModelFetch(viewCtx);
        final String limitedSql = this.initializeSQL(viewCtx);
        String sumsql = null;
        if (viewCtx.getModel().getFeatureValue("SUMCOLS") != null) {
            sumsql = this.getSumSQL(viewCtx, limitedSql);
        }
        CSRSqlTableController.OUT.log(Level.FINER, "sql constructed is {0}", limitedSql);
        CSRTableViewModel viewModel;
        if (viewCtx.isExportType()) {
            viewModel = new CSRTableViewModel(viewCtx);
            final ExportTableModel expModel = SQLQueryAPI.getExportTableModel(limitedSql, viewCtx, sumsql);
            final Properties redactConfigProps = ExportUtils.getExportMaskingConfig(viewCtx.getModel().getViewConfiguration());
            final Properties extendedProps = this.getCustomRedactConfiguration(viewCtx);
            if (extendedProps != null) {
                redactConfigProps.putAll(extendedProps);
            }
            CSRSqlTableController.OUT.log(Level.FINE, "redactConfig Properties configured for view {0} is {1} ", new Object[] { viewCtx.getModel().getViewName(), redactConfigProps });
            expModel.setPIIColumnConfig(redactConfigProps);
            viewModel.setTableModel(expModel);
        }
        else {
            final boolean isUnionQuery = (boolean)SQLQueryAPI.getACSQLDO(viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig").get("CVNAME")).getRow("ACSQLString").get("ISUNION");
            final Range r = this.getNavigation(viewCtx);
            final TableDatasetModel tdm = SQLQueryAPI.getAsTableModel(limitedSql, isUnionQuery, this.getCountSQL(viewCtx, limitedSql), viewCtx, !NavigationConfig.getNocount(viewCtx).equals("true"), r, sumsql);
            this.sqlTypes = new HashMap<String, String>();
            for (int size = tdm.getColumnCount(), i = 0; i < size; ++i) {
                this.sqlTypes.put(tdm.getColumnName(i), tdm.getColumnDataType(i));
            }
            StaticCache.addToCache((Object)(viewCtx.getModel().getViewName() + "_SQLTYPES"), (Object)this.sqlTypes, (List)null);
            this.setSortColumn(tdm, viewCtx);
            viewModel = new CSRTableViewModel(viewCtx);
            viewModel.setTableModel(tdm);
        }
        viewModel.init();
        viewCtx.setViewModel((Object)viewModel);
        this.postModelFetch(viewCtx);
    }
    
    private void refreshViewModelIfNeeded(final ViewContext viewContext) throws Exception {
        final TableViewState state = (TableViewState)viewContext.getViewState();
        if (state == null) {
            return;
        }
        boolean refreshViewModel = false;
        if (state.getColumnAliasVSColumnWidth() != null) {
            TablePersonalizationUtil.updateColumnWidthForUser(viewContext.getModel().getViewName(), WebClientUtil.getAccountId(), state.getColumnAliasVSColumnWidth());
            refreshViewModel = true;
        }
        if (state.getVisibleColumns() != null) {
            TablePersonalizationUtil.updateColumnsListForView(viewContext.getModel().getViewName(), WebClientUtil.getAccountId(), state.getVisibleColumns(), null);
            refreshViewModel = true;
        }
        if ("PERSONALIZE".equals(viewContext.getModel().getViewConfiguration().getFirstValue("ViewConfiguration", 10)) && state.getModifiedParam() != null) {
            this.savePreferences(viewContext);
            refreshViewModel = true;
        }
        if (refreshViewModel) {
            viewContext.setModel(WebViewAPI.getConfigModel((Object)viewContext.getModel().getViewName(), true));
            viewContext.getModel().setState((State)state);
            viewContext.setViewModel((Object)null);
        }
    }
    
    public JSONObject getModelAsJSON(final ViewContext viewContext) throws Exception {
        this.refreshViewModelIfNeeded(viewContext);
        return CSRTableDataUtil.getTableViewModelAsJson(viewContext);
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final Object cvId = viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 2);
        return SQLQueryAPI.getSQLString(cvId, (TemplateAPI.VariableHandler)this, viewCtx);
    }
    
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) {
        return this.getVariableValue((ViewContext)handlerContext, variableName);
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String reqValue = null;
        try {
            if (variableName.equals("SCRITERIA")) {
                reqValue = this.getSearchCriteria(viewCtx);
            }
            else if (variableName.equalsIgnoreCase("global_start_id") || variableName.equalsIgnoreCase("global_end_id") || variableName.equalsIgnoreCase("sas_start_id") || variableName.equalsIgnoreCase("sas_end_id")) {
                reqValue = "${" + variableName + "}";
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return reqValue;
    }
    
    public static String getDynamicColumns(final ViewContext viewCtx) throws DataAccessException {
        final String viewName = viewCtx.getModel().getViewName();
        final DataObject dynamic_columns = LookUpUtil.getPersistence().get("DynamicColumnConfiguration", new Criteria(Column.getColumn("DynamicColumnConfiguration", "VIEWNAME"), (Object)WebViewAPI.getViewNameNo((Object)viewName), 0));
        final StringBuilder dy_column = new StringBuilder();
        Row dynamicColumnRow = null;
        final Iterator<Row> iter = dynamic_columns.getRows("DynamicColumnConfiguration");
        while (iter.hasNext()) {
            dynamicColumnRow = iter.next();
            dy_column.append(", ");
            dy_column.append(dynamicColumnRow.get("TABLEALIAS"));
            dy_column.append(".");
            dy_column.append(dynamicColumnRow.get("DYCOLUMNNAME"));
            dy_column.append(" AS \"");
            dy_column.append((String)dynamicColumnRow.get("COLUMNALIAS"));
            dy_column.append("\" ");
        }
        return dy_column.toString();
    }
    
    public String getCountSQL(final ViewContext viewCtx, final String sql) throws Exception {
        final Object cvId = viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 2);
        return SQLQueryAPI.getCountSQL(cvId, sql, (TemplateAPI.VariableHandler)this, viewCtx);
    }
    
    public String getSumSQL(final ViewContext viewCtx, final String sql) throws Exception {
        final Object cvId = viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 2);
        return SQLQueryAPI.getSumSQL(cvId, sql, (TemplateAPI.VariableHandler)this, viewCtx);
    }
    
    public String getSearchCriteria(final ViewContext viewCtx) {
        final String alwaysTrueCriteria = "(1 = 1)";
        final TableViewState state = (TableViewState)viewCtx.getViewState();
        final String[] columnNames = state.getSearchColumns();
        if (columnNames == null) {
            return alwaysTrueCriteria;
        }
        if (this.sqlTypes == null) {
            this.sqlTypes = (HashMap)StaticCache.getFromCache((Object)(viewCtx.getModel().getViewName() + "_SQLTYPES"));
        }
        final List<Column> selColumns = new ArrayList<Column>();
        for (final String columnName : columnNames) {
            final Column col = new Column((String)null, columnName);
            col.setDataType(this.sqlTypes.get(columnName).toUpperCase());
            selColumns.add(col);
        }
        Criteria crit = null;
        try {
            crit = TableUtil.getSearchCriteria(viewCtx, selColumns);
        }
        catch (final Exception e) {
            e.getStackTrace();
        }
        if (crit != null) {
            return this.replaceColumnAliasWithName(viewCtx, crit, columnNames);
        }
        return alwaysTrueCriteria;
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) throws Exception {
        return null;
    }
    
    public Range getNavigation(final ViewContext viewCtx) throws Exception {
        int _FI = 1;
        int _PL = this.getDefaultPageLength(viewCtx);
        if (viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 3) != null) {
            final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
            _FI = tableViewState.getFromIndex();
            final int _PN = tableViewState.getPageNumber();
            _PL = tableViewState.getPageLength();
            if (_PN > Integer.MIN_VALUE && _PN != -1 && _PL > Integer.MIN_VALUE) {
                _FI = Math.max(_PN - 1, 0) * _PL + 1;
            }
            if (_FI == Integer.MIN_VALUE) {
                _FI = 1;
            }
            if (_PL == Integer.MIN_VALUE) {
                final Row r = viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig");
                if (r.get("ISSCROLLENABLED")) {
                    _PL = (int)r.get("INITIALFETCHEDROWS");
                }
                else {
                    _PL = this.getDefaultPageLength(viewCtx);
                }
            }
            if (NavigationConfig.getNocount(viewCtx).equals("true") && _PL != -1) {
                ++_PL;
            }
        }
        return new Range(_FI, _PL);
    }
    
    private int getDefaultPageLength(final ViewContext viewCtx) throws DataAccessException {
        final Integer pageLen = (Integer)viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 12);
        return (pageLen != null) ? pageLen : 10;
    }
    
    public String getSorting(final ViewContext viewCtx) throws Exception {
        final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
        String columnName = tableViewState.getSortBy();
        boolean orderType = tableViewState.getSortOrder();
        if (columnName == null) {
            final Row tableRow = viewCtx.getModel().getViewConfiguration().getFirstRow("ACTableViewConfig");
            columnName = (String)tableRow.get(13);
            final String defaultSortOrder = (String)tableRow.get(14);
            orderType = !"DESC".equals(defaultSortOrder);
        }
        if (columnName != null) {
            return RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().formOrderByString(new String[] { columnName }, new boolean[] { orderType });
        }
        return "";
    }
    
    public void setSortColumn(final TableDatasetModel tdm, final ViewContext viewCtx) {
        final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
        String columnName = tableViewState.getSortBy();
        boolean orderType = tableViewState.getSortOrder();
        if (columnName == null) {
            try {
                final Row tableRow = viewCtx.getModel().getViewConfiguration().getFirstRow("ACTableViewConfig");
                columnName = (String)tableRow.get(13);
                final String defaultSortOrder = (String)tableRow.get(14);
                orderType = !"DESC".equals(defaultSortOrder);
            }
            catch (final DataAccessException e) {
                throw new RuntimeException("Problem occured while fetching sortcolumn details for the view : " + viewCtx.getUniqueId(), (Throwable)e);
            }
        }
        if (columnName == null) {
            return;
        }
        int columnIndex = tdm.getColumnIndex(columnName);
        if (columnIndex < 0) {
            try {
                columnIndex = Integer.parseInt(columnName) - 1;
            }
            catch (final NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }
        if (columnIndex >= 0) {
            tdm.setModelSortColumns(new SortColumn[] { new SortColumn(columnIndex, orderType) });
        }
    }
    
    public void savePreferences(final ViewContext viewCtx) throws Exception {
        final String mpstate = ((TableViewState)viewCtx.getViewState()).getModifiedParam();
        if (mpstate != null) {
            final String viewName = viewCtx.getModel().getViewName();
            StaticCache.addToCache((Object)(viewName + "_SQLTYPES"), (Object)this.sqlTypes, (List)null);
            if ("_SB".equals(mpstate)) {
                this.saveSortPreference(viewCtx);
            }
            else if ("_PL".equals(mpstate)) {
                this.savePageLength(viewCtx);
            }
        }
    }
    
    public void saveSortPreference(final ViewContext viewCtx) throws Exception {
        final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
        if (tableViewState == null) {
            return;
        }
        final String sortCol = tableViewState.getSortBy();
        final String sortOrder = tableViewState.getSortOrder() ? "ASC" : "DESC";
        if (sortCol != null) {
            final Row r = viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig");
            if (!sortCol.equals(r.get(13)) || !sortOrder.equals(r.get(14))) {
                TablePersonalizationUtil.updateSortForView(viewCtx.getModel().getViewName(), WebClientUtil.getAccountId(), sortCol, tableViewState.getSortOrder() ? "A" : "D");
            }
        }
    }
    
    public void savePageLength(final ViewContext viewCtx) throws Exception {
        final int pageLength = ((TableViewState)viewCtx.getViewState()).getPageLength();
        final Row r = viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig");
        if (pageLength != Integer.MIN_VALUE && !Integer.valueOf(pageLength).equals(r.get(12))) {
            TablePersonalizationUtil.updatePageLengthForView(viewCtx.getModel().getViewName(), WebClientUtil.getAccountId(), pageLength);
        }
    }
    
    protected String replaceColumnAliasWithName(final ViewContext viewCtx, final Criteria criteria, final String[] columnNames) {
        String criteriaString = null;
        try (final Connection conn = RelationalAPI.getInstance().getConnection()) {
            criteriaString = RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().formWhereClause(criteria);
            final DatabaseMetaData metadata = conn.getMetaData();
            final String quoteStr = metadata.getIdentifierQuoteString();
            final String viewName = viewCtx.getModel().getViewName();
            String sql = (String)StaticCache.getFromCache((Object)(viewName + "_SQL"));
            final String selString = sql.toUpperCase(Locale.ENGLISH).startsWith("SELECT DISTINCT") ? "SELECT DISTINCT" : "SELECT";
            sql = sql.substring(selString.length()).trim();
            HashMap<String, String> columnNameVSAliasMap = (HashMap<String, String>)StaticCache.getFromCache((Object)(viewName + "_COLUMNNAMES"));
            columnNameVSAliasMap = ((columnNameVSAliasMap == null) ? new HashMap<String, String>() : columnNameVSAliasMap);
            for (final String columnAlias : columnNames) {
                final int indexOfAlias = sql.indexOf(" \"" + columnAlias + "\"");
                if (indexOfAlias != -1 && !columnNameVSAliasMap.containsKey(columnAlias)) {
                    final int indexOfCloseBracket = sql.lastIndexOf(")", indexOfAlias);
                    int indexOfColumn = sql.lastIndexOf(",", indexOfAlias);
                    if (indexOfCloseBracket > 0 && indexOfCloseBracket > indexOfColumn) {
                        final int functionIndex = getOpenBracketIndex(sql, indexOfCloseBracket);
                        indexOfColumn = sql.lastIndexOf(",", functionIndex);
                        String columnString = sql.substring(indexOfColumn + 1, indexOfAlias).trim();
                        columnString = (columnString.toUpperCase(Locale.ENGLISH).endsWith(" AS") ? columnString.substring(0, columnString.length() - 2) : columnString);
                        columnNameVSAliasMap.put(columnAlias, columnString);
                    }
                    else {
                        String columnString2 = sql.substring(indexOfColumn + 1, indexOfAlias).trim();
                        columnString2 = (columnString2.toUpperCase(Locale.ENGLISH).endsWith(" AS") ? columnString2.substring(0, columnString2.length() - 2) : columnString2);
                        columnNameVSAliasMap.put(columnAlias, columnString2);
                    }
                }
                if (criteriaString.contains(quoteStr + columnAlias + quoteStr)) {
                    criteriaString = criteriaString.replaceFirst(quoteStr + columnAlias + quoteStr, columnNameVSAliasMap.containsKey(columnAlias) ? columnNameVSAliasMap.get(columnAlias) : columnAlias);
                }
                else {
                    criteriaString = criteriaString.replaceFirst(columnAlias, columnNameVSAliasMap.containsKey(columnAlias) ? columnNameVSAliasMap.get(columnAlias) : columnAlias);
                }
            }
            if (StaticCache.getFromCache((Object)(viewName + "_COLUMNNAMES")) == null) {
                StaticCache.addToCache((Object)(viewName + "_COLUMNNAMES"), (Object)columnNameVSAliasMap, (List)null);
            }
            CSRSqlTableController.OUT.log(Level.FINE, "ColumnName and columnAlias MAP :" + columnNameVSAliasMap);
        }
        catch (final Exception e) {
            CSRSqlTableController.OUT.log(Level.SEVERE, "Exception occurred while forming spot search Criteria for View {0} : {1}" + new Object[] { viewCtx.getUniqueId(), e.getMessage() });
            e.printStackTrace();
        }
        CSRSqlTableController.OUT.log(Level.FINE, "final criteria String is : {0}", criteriaString);
        return criteriaString;
    }
    
    private static int getOpenBracketIndex(final String sql, final int closeBracketIdx) {
        int braceCnt = 1;
        for (int idx = closeBracketIdx - 1; idx > 0; --idx) {
            final char charAtIdx = sql.charAt(idx);
            if (charAtIdx == ')') {
                ++braceCnt;
            }
            if (charAtIdx == '(' && --braceCnt == 0) {
                return idx;
            }
        }
        return -1;
    }
    
    static {
        OUT = Logger.getLogger(CSRSqlTableController.class.getName());
    }
}
