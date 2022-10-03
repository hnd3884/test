package com.adventnet.client.components.table.web;

import com.zoho.framework.utils.crypto.CryptoUtil;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.util.Locale;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.beans.xtable.SortColumn;
import com.adventnet.persistence.Row;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.ds.query.Range;
import java.util.Properties;
import java.util.List;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import java.util.Map;
import com.adventnet.client.view.common.ExportUtils;
import java.util.logging.Level;
import com.adventnet.client.components.sql.SQLQueryAPI;
import com.adventnet.client.view.web.ViewContext;
import java.util.HashMap;
import java.util.logging.Logger;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class SqlViewController extends DefaultViewController implements WebConstants, TemplateAPI.VariableHandler, TableViewController
{
    private static Logger out;
    protected HashMap<String, String> sqlTypes;
    
    public SqlViewController() {
        this.sqlTypes = null;
    }
    
    protected String initializeSQL(final ViewContext viewCtx) throws Exception {
        String limitedSql;
        final String sql = limitedSql = this.getSQLString(viewCtx);
        final String orderByString = this.getSorting(viewCtx);
        final int orderByIdx = sql.toUpperCase().lastIndexOf("ORDER BY");
        if (orderByIdx != -1 && !orderByString.isEmpty()) {
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
        viewCtx.setURLStateParameters();
        this.preModelFetch(viewCtx);
        final String limitedSql = this.initializeSQL(viewCtx);
        String reccount = (String)viewCtx.getURLStateParameter("_TL");
        if (reccount == null) {
            reccount = (String)viewCtx.getStateParameter("_TL");
        }
        String docount = (String)viewCtx.getURLStateParameter("_CO");
        if (docount == null) {
            docount = (String)viewCtx.getStateParameter("_CO");
        }
        String countsql = null;
        if (docount == null) {
            countsql = this.getCountSQL(viewCtx, limitedSql);
        }
        String sumsql = null;
        if (viewCtx.getModel().getFeatureValue("SUMCOLS") != null) {
            sumsql = this.getSumSQL(viewCtx, limitedSql);
        }
        SqlViewController.out.log(Level.FINER, "sql constructed is {0}", limitedSql);
        TableViewModel viewModel = null;
        if (viewCtx.isExportType()) {
            viewModel = new TableViewModel(viewCtx);
            final ExportTableModel expModel = SQLQueryAPI.getExportTableModel(limitedSql, viewCtx, sumsql);
            final Properties redactConfigProps = ExportUtils.getExportMaskingConfig(viewCtx.getModel().getViewConfiguration());
            final Properties extendedProps = this.getCustomRedactConfiguration(viewCtx);
            if (extendedProps != null) {
                redactConfigProps.putAll(extendedProps);
            }
            SqlViewController.out.log(Level.FINE, "redactConfig Properties configured for view {0} is {1} ", new Object[] { viewCtx.getModel().getViewName(), redactConfigProps });
            expModel.setPIIColumnConfig(redactConfigProps);
            this.encryptForExport(expModel, viewCtx);
            viewModel.setTableModel(expModel);
        }
        else {
            final boolean isUnionQuery = (boolean)SQLQueryAPI.getACSQLDO(viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig").get("CVNAME")).getRow("ACSQLString").get("ISUNION");
            final Range r = this.getNavigation(viewCtx);
            final TableDatasetModel tdm = SQLQueryAPI.getAsTableModel(limitedSql, isUnionQuery, countsql, viewCtx, !NavigationConfig.getNocount(viewCtx).equals("true"), r, sumsql);
            if (docount != null && reccount != null) {
                tdm.setTotalRecordsCount(Integer.parseInt(reccount));
            }
            this.sqlTypes = new HashMap<String, String>();
            for (int size = tdm.getColumnCount(), i = 0; i < size; ++i) {
                this.sqlTypes.put(tdm.getColumnName(i), tdm.getColumnDataType(i));
            }
            StaticCache.addToCache((Object)(viewCtx.getModel().getViewName() + "_SQLTYPES"), (Object)this.sqlTypes, (List)null);
            this.updateNavigationState(tdm, viewCtx);
            this.setSortColumn(tdm, viewCtx);
            viewModel = new TableViewModel(viewCtx);
            this.encryptPkFk(tdm, viewCtx);
            viewModel.setTableModel(tdm);
        }
        viewModel.init();
        viewModel.getTableTransformerContext().setRequest(viewCtx.getRequest());
        viewCtx.setViewModel((Object)viewModel);
        this.postModelFetch(viewCtx);
    }
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final Object cvId = viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 2);
        return SQLQueryAPI.getSQLString(cvId, (TemplateAPI.VariableHandler)this, viewCtx);
    }
    
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) {
        String result = null;
        try {
            result = this.getVariableValue((ViewContext)handlerContext, variableName);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String reqValue = null;
        try {
            if (variableName.equals("SCRITERIA")) {
                reqValue = this.getSearchCriteria(viewCtx);
            }
            else if (variableName.equals("DYNAMIC_COLUMN")) {
                reqValue = getDynamicColumns(viewCtx);
            }
            else if (variableName.equalsIgnoreCase("global_start_id") || variableName.equalsIgnoreCase("global_end_id") || variableName.equalsIgnoreCase("sas_start_id") || variableName.equalsIgnoreCase("sas_end_id")) {
                reqValue = "${" + variableName + "}";
            }
            else {
                reqValue = viewCtx.getRequest().getParameter(variableName);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return reqValue;
    }
    
    public static String getDynamicColumns(final ViewContext viewCtx) throws DataAccessException {
        final long viewNameNo = WebViewAPI.getViewNameNo((Object)viewCtx.getModel().getViewName());
        final StringBuilder dy_column = new StringBuilder();
        try {
            TableUtil.getDynamicColumns(viewNameNo).forEach(dyCol -> sb.append(", ").append(dyCol.getTableAlias()).append(".").append(dyCol.getDCSpecificColumnName()).append(" AS \"").append(dyCol.getColumnAlias()).append("\" "));
        }
        catch (final SQLException sqe) {
            throw new DataAccessException((Throwable)sqe);
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
    
    public void updateNavigationState(final TableDatasetModel dataSetModel, final ViewContext viewCtx) throws Exception {
        viewCtx.setStateOrURLStateParam("_FI", (Object)(dataSetModel.getStartIndex() + ""), true);
        viewCtx.setStateOrURLStateParam("_TI", (Object)(dataSetModel.getEndIndex() + ""), true);
        viewCtx.setStateOrURLStateParam("_TL", (Object)(dataSetModel.getTotalRecordsCount() + ""), true);
        viewCtx.setStateParameter("_PN", viewCtx.getStateOrURLStateParameter("_PN"));
        long pageLen = dataSetModel.getPageLength();
        if ("true".equals(NavigationConfig.getNocount(viewCtx))) {
            --pageLen;
        }
        viewCtx.setStateOrURLStateParam("_PL", (Object)(pageLen + ""), true);
    }
    
    public String getSearchCriteria(final ViewContext viewCtx) {
        Criteria crit = null;
        final String searchColumns = (String)viewCtx.getStateOrURLStateParameter("SEARCH_COLUMN");
        int count = 0;
        int colSize = 0;
        String[] columnNames = null;
        if (searchColumns == null) {
            return "(1 = 1)";
        }
        columnNames = searchColumns.split(",");
        colSize = columnNames.length;
        if (this.sqlTypes == null) {
            this.sqlTypes = (HashMap)StaticCache.getFromCache((Object)(viewCtx.getModel().getViewName() + "_SQLTYPES"));
        }
        final Column[] selColumns = new Column[colSize];
        for (count = 0; count < colSize; ++count) {
            final String colName = columnNames[count];
            final Column col = new Column((String)null, colName);
            final Object dataType = this.sqlTypes.get(colName);
            col.setDataType(dataType.toString().toUpperCase());
            selColumns[count] = col;
        }
        try {
            crit = TableUtil.spotSearchCriteria(viewCtx, selColumns, null);
        }
        catch (final Exception e) {
            e.getStackTrace();
        }
        String res = "(1 = 1)";
        if (crit != null) {
            res = this.replaceColumnAliasWithName(viewCtx, crit, columnNames);
        }
        return res;
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) throws Exception {
        return null;
    }
    
    private boolean isSliderEnabledTable(final ViewContext viewCtx) {
        try {
            return (boolean)viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig").get("ISSCROLLENABLED");
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public Range getRangeForSliderTable(final ViewContext viewCtx) {
        int length = -1;
        try {
            length = (int)viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig").get("INITIALFETCHEDROWS");
        }
        catch (final Exception e) {
            length = -1;
        }
        viewCtx.getRequest().setAttribute("initialFetchedRows", (Object)(length + ""));
        final String fromIndex = (String)viewCtx.getStateOrURLStateParameter("_FI");
        final String toIndex = (String)viewCtx.getStateOrURLStateParameter("_TI");
        if ("true".equals(viewCtx.getRequest().getParameter("ajaxTableUpdate")) && fromIndex != null && toIndex != null) {
            final int _FI = Integer.parseInt(fromIndex);
            final int _TI = Integer.parseInt(toIndex);
            return new Range(_FI, _TI);
        }
        return new Range(1, length);
    }
    
    public Range getNavigation(final ViewContext viewCtx) throws Exception {
        if ("true".equals(viewCtx.getRequest().getParameter("TOTALROWS"))) {
            return new Range(1, Integer.MAX_VALUE);
        }
        if (this.isSliderEnabledTable(viewCtx)) {
            return this.getRangeForSliderTable(viewCtx);
        }
        int fromIndex = 1;
        int pageNo = 0;
        int pageLength = this.getDefaultPageLength(viewCtx);
        if (viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 3) != null) {
            final String fromIndexStr = (String)viewCtx.getStateOrURLStateParameter("_FI");
            final String pageLengthStr = (String)viewCtx.getStateOrURLStateParameter("_PL");
            final String pageNumStr = (String)viewCtx.getStateOrURLStateParameter("_PN");
            if (pageNumStr != null && (pageNo = Integer.parseInt(pageNumStr)) > -1 && pageLengthStr != null) {
                pageLength = Integer.parseInt(pageLengthStr);
                fromIndex = Math.max(pageNo - 1, 0) * pageLength + 1;
            }
            else if (fromIndexStr != null && pageLengthStr != null) {
                fromIndex = Integer.parseInt(fromIndexStr);
                pageLength = Integer.parseInt(pageLengthStr);
            }
            if (NavigationConfig.getNocount(viewCtx).equals("true") && pageLength != -1) {
                ++pageLength;
            }
        }
        return new Range(fromIndex, pageLength);
    }
    
    private int getDefaultPageLength(final ViewContext viewCtx) throws DataAccessException {
        final Integer pageLen = (Integer)viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 12);
        return (pageLen != null) ? pageLen : 10;
    }
    
    public String getSorting(final ViewContext viewCtx) throws Exception {
        String columnName = (String)viewCtx.getStateOrURLStateParameter("_SB");
        String sortOrder = (String)viewCtx.getStateOrURLStateParameter("_SO");
        if (columnName == null) {
            final Row tableRow = viewCtx.getModel().getViewConfiguration().getFirstRow("ACTableViewConfig");
            columnName = (String)tableRow.get(13);
            sortOrder = (String)tableRow.get(14);
            sortOrder = ("DESC".equals(sortOrder) ? "D" : "A");
        }
        if (columnName != null) {
            final boolean orderType = !"D".equals(sortOrder);
            viewCtx.setStateOrURLStateParam("_SB", (Object)columnName);
            viewCtx.setStateOrURLStateParam("_SO", (Object)sortOrder);
            return RelationalAPI.getInstance().getDBAdapter().getSQLGenerator().formOrderByString(new String[] { columnName }, new boolean[] { orderType });
        }
        return "";
    }
    
    public void setSortColumn(final TableDatasetModel tdm, final ViewContext viewCtx) {
        String columnName = (String)viewCtx.getURLStateParameter("_SB");
        if (columnName == null) {
            columnName = (String)viewCtx.getStateParameter("_SB");
        }
        if (columnName == null) {
            return;
        }
        String sortOrder = (String)viewCtx.getURLStateParameter("_SO");
        if (sortOrder == null) {
            sortOrder = (String)viewCtx.getStateParameter("_SO");
        }
        final boolean orderType = "A".equalsIgnoreCase(sortOrder);
        int columnIndex = tdm.getColumnIndex(columnName);
        if (columnIndex < 0) {
            try {
                columnIndex = Integer.parseInt(columnName) - 1;
            }
            catch (final NumberFormatException ex) {}
        }
        if (columnIndex >= 0) {
            tdm.setModelSortColumns(new SortColumn[] { new SortColumn(columnIndex, orderType) });
        }
    }
    
    public void savePreferences(final ViewContext viewCtx) throws Exception {
        final String mpstate = (String)viewCtx.getStateOrURLStateParameter("_MP");
        if (mpstate != null) {
            final String viewName = viewCtx.getModel().getViewName();
            StaticCache.addToCache((Object)(viewName + "_SQLTYPES"), (Object)this.sqlTypes, (List)null);
            if ("_SB".equals(mpstate)) {
                this.saveSortPerference(viewCtx);
            }
            else if ("_PL".equals(mpstate)) {
                this.savePageLength(viewCtx);
            }
        }
    }
    
    public void saveSortPerference(final ViewContext viewCtx) throws Exception {
        final String sortCol = (String)viewCtx.getStateOrURLStateParameter("_SB");
        final String sortOrder = (String)viewCtx.getStateOrURLStateParameter("_SO");
        if (sortCol != null && sortOrder != null) {
            final Row r = viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig");
            if (!sortCol.equals(r.get(13)) || !sortOrder.equals(r.get(14))) {
                TablePersonalizationUtil.updateSortForView(viewCtx.getModel().getViewName(), WebClientUtil.getAccountId(), sortCol, sortOrder);
            }
        }
    }
    
    public void savePageLength(final ViewContext viewCtx) throws Exception {
        final String pageLength = (String)viewCtx.getStateOrURLStateParameter("_PL");
        final Row r = viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig");
        if (pageLength != null && !Integer.valueOf(pageLength).equals(r.get(12))) {
            TablePersonalizationUtil.updatePageLengthForView(viewCtx.getModel().getViewName(), WebClientUtil.getAccountId(), Integer.parseInt(pageLength));
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
            SqlViewController.out.log(Level.FINE, "ColumnName and columnAlias MAP :" + columnNameVSAliasMap);
        }
        catch (final Exception e) {
            SqlViewController.out.log(Level.SEVERE, "Exception occurred while forming spot search Criteria for View {0} : {1}" + new Object[] { viewCtx.getUniqueId(), e.getMessage() });
            e.printStackTrace();
        }
        SqlViewController.out.log(Level.FINE, "final criteria String is : {0}", criteriaString);
        return criteriaString;
    }
    
    private void encryptForExport(final ExportTableModel expModel, final ViewContext vc) {
        final String colList = vc.getModel().getFeatureValue("EncryptColumn");
        if (colList != null) {
            expModel.setEncryption(colList);
        }
    }
    
    private void encryptPkFk(final TableDatasetModel model, final ViewContext vc) {
        try {
            final String colList = vc.getModel().getFeatureValue("EncryptColumn");
            if (colList == null) {
                return;
            }
            if (model.tableData == null || model.totalRecords == 0L) {
                return;
            }
            final String[] columnNames = colList.split(",");
            final int rowcount = model.getRowCount();
            for (int i = 0; i < columnNames.length; ++i) {
                final String colName = columnNames[i];
                final int colIndex = model.getColumnIndex(colName);
                for (int j = 0; j < rowcount; ++j) {
                    final Object val = model.getValueAt(j, colIndex);
                    final Object enc = CryptoUtil.encrypt((String)val);
                    model.setValueAt(enc, j, colIndex);
                }
            }
        }
        catch (final Exception e) {
            SqlViewController.out.log(Level.SEVERE, "could'nt encrypt pk of :" + vc.getUniqueId());
        }
    }
    
    private static int getOpenBracketIndex(final String sql, final int closeBracketIdx) throws Exception {
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
        SqlViewController.out = Logger.getLogger(SqlViewController.class.getName());
    }
}
