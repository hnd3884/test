package com.adventnet.client.components.table.web;

import com.adventnet.customview.service.SQTemplateValuesServiceConfiguration;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.db.persistence.metadata.PrimaryKeyDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaModel;
import com.adventnet.model.table.CVTableModelImpl;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import java.util.List;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.client.ClientException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.client.util.LookUpUtil;
import java.util.ArrayList;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import java.util.Properties;
import com.adventnet.ds.query.DataSet;
import java.util.logging.Level;
import java.util.Map;
import com.adventnet.client.view.common.ExportUtils;
import com.adventnet.ds.query.Query;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Range;
import com.adventnet.mfw.bean.BeanUtil;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import com.adventnet.model.table.CVTableModel;
import com.adventnet.customview.service.ServiceConfiguration;
import com.adventnet.customview.CustomViewRequest;
import javax.swing.table.TableModel;
import com.adventnet.customview.ViewData;
import com.adventnet.ds.query.SelectQuery;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class TableRetrieverAction extends DefaultViewController implements WebConstants, TemplateAPI.VariableHandler, QueryUtil.TemplateVariableHandler, TableViewController
{
    protected static CustomViewManager cvMgr;
    private static Logger out;
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        this.preModelFetch(viewCtx);
        initializeReferences();
        final DataObject tableViewDO = viewCtx.getModel().getViewConfiguration();
        final HttpServletRequest request = viewCtx.getRequest();
        final HashMap<String, String> criteriaMap = this.getCustomCriteria(tableViewDO, request);
        final SelectQuery query = this.initializeSQL(viewCtx);
        ViewData viewData = null;
        TableViewModel viewModel = null;
        if (viewCtx.isExportType()) {
            viewModel = new TableViewModel(viewCtx);
            final TableModel expModel = this.getViewExportModel(query, viewCtx, criteriaMap);
            viewModel.setTableModel(expModel);
        }
        else {
            viewData = this.getViewData(query, viewCtx, criteriaMap);
            viewModel = new TableViewModel(viewData.getModel(), viewCtx);
        }
        viewModel.init();
        viewModel.getTableTransformerContext().setRequest(viewCtx.getRequest());
        viewCtx.setViewModel((Object)viewModel);
        this.setStateParameters(viewCtx, viewModel);
        this.updateDCAIfNecessary(viewCtx);
        this.postModelFetch(viewCtx);
    }
    
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final DataObject tableViewDO = viewCtx.getModel().getViewConfiguration();
        SelectQuery query = (SelectQuery)viewCtx.getModel().getCompiledData((Object)"SELECTQUERY");
        if (query == null) {
            final Object customViewId = tableViewDO.getFirstValue("ACTableViewConfig", 2);
            query = this.getSelectQuery(customViewId, viewCtx);
            viewCtx.getModel().addCompiledData((Object)"SELECTQUERY", (Object)query);
        }
        return query;
    }
    
    public long getCount(final ViewContext viewCtx) throws Exception {
        initializeReferences();
        final SelectQuery query = this.initializeSQL(viewCtx);
        this.setCriteria(query, viewCtx);
        final HashMap<String, String> criteriaMap = this.getCustomCriteria(viewCtx.getModel().getViewConfiguration(), viewCtx.getRequest());
        final CustomViewRequest cvRequest = new CustomViewRequest(query);
        cvRequest.set("fetchCountOnly", (Object)true);
        cvRequest.putServiceConfiguration((ServiceConfiguration)this.getServiceConfig(criteriaMap, viewCtx));
        final ViewData viewData = this.sendCVRequest(viewCtx, cvRequest);
        return ((CVTableModel)viewData.getModel()).getTotalRecordsCount();
    }
    
    protected HashMap<String, String> getCustomCriteria(final DataObject tableViewDO, final HttpServletRequest request) throws Exception {
        HashMap<String, String> criteriaMap = null;
        if (tableViewDO.containsTable("TemplateViewParams")) {
            criteriaMap = new HashMap<String, String>();
            final Iterator<Row> iterator = tableViewDO.getRows("TemplateViewParams");
            while (iterator.hasNext()) {
                final Row tempRow = iterator.next();
                final String paramName = (String)tempRow.get(2);
                final String paramValue = request.getParameter(paramName);
                if (paramValue != null) {
                    criteriaMap.put(paramName, paramValue);
                }
            }
        }
        return criteriaMap;
    }
    
    protected static void initializeReferences() throws Exception {
        if (TableRetrieverAction.cvMgr == null) {
            TableRetrieverAction.cvMgr = (CustomViewManager)BeanUtil.lookup("TableViewManager");
        }
    }
    
    public ExportTableModel getViewExportModel(final SelectQuery query, final ViewContext viewCtx, final HashMap<String, String> criteriaMap) throws Exception {
        this.setSortColumn(query, viewCtx);
        this.replaceTemplates(query, viewCtx, criteriaMap);
        this.setCriteria(query, viewCtx);
        DataSet ds = null;
        query.setRange((Range)null);
        ds = RelationalAPI.getInstance().executeReadOnlyQuery((Query)query, (Connection)viewCtx.getTransientState("CONNECTION"));
        final ExportTableViewModel expModel = new ExportTableViewModel();
        expModel.updateModel(ds);
        final Properties redactConfigProps = ExportUtils.getExportMaskingConfig(viewCtx.getModel().getViewConfiguration());
        final Properties extendedProps = this.getCustomRedactConfiguration(viewCtx);
        if (extendedProps != null) {
            redactConfigProps.putAll(extendedProps);
        }
        TableRetrieverAction.out.log(Level.FINE, "redactConfig Properties configured for view {0} is {1} ", new Object[] { viewCtx.getModel().getViewName(), redactConfigProps });
        expModel.setPIIColumnConfig(redactConfigProps);
        return expModel;
    }
    
    public ViewData getViewData(final SelectQuery query, final ViewContext viewCtx, final HashMap<String, String> criteriaMap) throws Exception {
        this.setRange(query, viewCtx);
        this.setSortColumn(query, viewCtx);
        this.setCriteria(query, viewCtx);
        final CustomViewRequest cvRequest = new CustomViewRequest(query);
        if (viewCtx.getModel().getFeatureValue("no_of_attempt_to_fetchPrevPage") != null) {
            cvRequest.set("no_of_times_to_fetch_prevPage", (Object)Integer.parseInt(viewCtx.getModel().getFeatureValue("no_of_attempt_to_fetchPrevPage")));
        }
        if (viewCtx.getRequest().getParameter("fetchPrevPage") != null) {
            cvRequest.set("fetchPrevPage", (Object)"true".equalsIgnoreCase(viewCtx.getRequest().getParameter("fetchPrevPage")));
        }
        if (NavigationConfig.getNocount(viewCtx).equals("true")) {
            cvRequest.set("TOTAL", (Object)(-1L));
        }
        cvRequest.putServiceConfiguration((ServiceConfiguration)this.getServiceConfig(criteriaMap, viewCtx));
        return this.sendCVRequest(viewCtx, cvRequest);
    }
    
    protected ViewData sendCVRequest(final ViewContext viewCtx, final CustomViewRequest cvRequest) throws Exception {
        final String dsname = (String)viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 11);
        if (dsname != null) {
            final ArrayList<String> dsList = new ArrayList<String>();
            dsList.add(dsname);
            cvRequest.set("DATASOURCE_LIST", (Object)dsList);
            return LookUpUtil.getCVManagerForMDS().getData(cvRequest);
        }
        return TableRetrieverAction.cvMgr.getData(cvRequest);
    }
    
    public SelectQuery getSelectQuery(final Object customViewId, final ViewContext viewCtx) throws Exception {
        return CustomViewUtils.getSelectQuery(customViewId, viewCtx.getModel().getViewConfiguration());
    }
    
    public void setRange(final SelectQuery query, final ViewContext viewCtx) throws DataAccessException, ClientException {
        if ("true".equals(viewCtx.getRequest().getParameter("TOTALROWS"))) {
            query.setRange(new Range(1, Integer.MAX_VALUE));
            return;
        }
        if (this.isSliderEnabledTable(viewCtx)) {
            this.handleSettingRangeForSliderTable(query, viewCtx);
            return;
        }
        final String viewNameWithUrlPattern = viewCtx.getRequest().getServletPath();
        if (viewNameWithUrlPattern != null) {
            final int urlPatternIndex = viewNameWithUrlPattern.indexOf(".");
            if (viewNameWithUrlPattern.substring(urlPatternIndex + 1).equals("/exporthtml")) {
                query.setRange(new Range(1, -1));
                return;
            }
        }
        int fromIndex = (query.getRange() != null) ? query.getRange().getStartIndex() : 1;
        int pageLength = this.getDefaultPageLength(viewCtx, query);
        int pageNo = 0;
        if (viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 3) != null) {
            final String fromIndexStr = (String)viewCtx.getStateOrURLStateParameter("_FI");
            final String pageLengthStr = (String)viewCtx.getStateOrURLStateParameter("_PL");
            final String pageNumberStr = (String)viewCtx.getStateOrURLStateParameter("_PN");
            if (pageNumberStr != null && (pageNo = Integer.parseInt(pageNumberStr)) > -1 && pageLengthStr != null) {
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
        query.setRange(new Range(fromIndex, pageLength));
    }
    
    private int getDefaultPageLength(final ViewContext viewCtx, final SelectQuery query) throws DataAccessException {
        Integer pageLen = (Integer)viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 12);
        if (pageLen == null) {
            pageLen = ((query.getRange() != null) ? new Integer(query.getRange().getNumberOfObjects()) : new Integer(10));
        }
        return pageLen;
    }
    
    public void setSortColumn(final SelectQuery query, final ViewContext viewCtx) throws Exception {
        String columnName = (String)viewCtx.getStateOrURLStateParameter("_SB");
        String sortOrder = (String)viewCtx.getStateOrURLStateParameter("_SO");
        if (columnName == null) {
            final Row tableRow = viewCtx.getModel().getViewConfiguration().getFirstRow("ACTableViewConfig");
            columnName = (String)tableRow.get(13);
            sortOrder = (String)tableRow.get(14);
            sortOrder = ("DESC".equals(sortOrder) ? "D" : "A");
        }
        if (sortOrder == null) {
            sortOrder = "A";
        }
        if (columnName != null) {
            final boolean isAscending = !sortOrder.equals("D");
            final List<SortColumn> sortColumns = query.getSortColumns();
            for (int i = sortColumns.size() - 1; i >= 0; --i) {
                query.removeSortColumn(i);
            }
            final List<Column> selectColumns = query.getSelectColumns();
            for (int j = 0, size = selectColumns.size(); j < size; ++j) {
                final Column column = selectColumns.get(j);
                String columnAlias = column.getColumnAlias();
                if (columnAlias == null) {
                    columnAlias = column.getColumnName();
                }
                if (columnAlias.equals(columnName)) {
                    final SortColumn sortColumn = new SortColumn(column, isAscending);
                    query.addSortColumn(sortColumn, 0);
                    break;
                }
            }
        }
    }
    
    public void setStateParameters(final ViewContext viewContext, final TableViewModel viewModel) {
        if (viewContext.isExportType()) {
            return;
        }
        final TableNavigatorModel tableModel = (TableNavigatorModel)viewModel.getTableModel();
        final com.adventnet.beans.xtable.SortColumn[] sortColumns = tableModel.getModelSortedColumns();
        if (sortColumns != null && sortColumns.length > 0) {
            final boolean isAscending = sortColumns[0].isAscending();
            viewContext.setStateOrURLStateParam("_SO", (Object)(isAscending ? "A" : "D"));
            final int columnIndex = sortColumns[0].getColumnIndex();
            final String columnName = tableModel.getColumnName(columnIndex);
            viewContext.updateStateParameter("_SB", (Object)columnName);
            viewContext.setURLStateParameter("_SB", (Object)columnName);
        }
        this.setNavigationParameters(viewContext);
    }
    
    public void setNavigationParameters(final ViewContext viewCtx) {
        final Object model = ((TableViewModel)viewCtx.getViewModel()).getTableModel();
        if (!(model instanceof TableNavigatorModel)) {
            return;
        }
        final TableNavigatorModel tableModel = (TableNavigatorModel)model;
        int pageLength = (int)tableModel.getPageLength();
        viewCtx.setStateOrURLStateParam("_FI", (Object)(tableModel.getStartIndex() + ""));
        viewCtx.setStateOrURLStateParam("_TI", (Object)(tableModel.getEndIndex() + ""));
        if (NavigationConfig.getNocount(viewCtx).equals("true")) {
            --pageLength;
        }
        viewCtx.setStateOrURLStateParam("_PL", (Object)(pageLength + ""));
        viewCtx.setStateOrURLStateParam("_TL", (Object)(tableModel.getTotalRecordsCount() + ""));
    }
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        Criteria criteria = query.getCriteria();
        try {
            final List<Column> selectColumns = query.getSelectColumns();
            final int size = selectColumns.size();
            final Column[] selColumn = new Column[size];
            for (int i = 0; i < size; ++i) {
                selColumn[i] = selectColumns.get(i);
            }
            criteria = TableUtil.spotSearchCriteria(viewCtx, selColumn, criteria);
            query.setCriteria(criteria);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateDCAIfNecessary(final ViewContext viewContext) throws Exception {
        final DataObject viewModel = viewContext.getModel().getViewConfiguration();
        if (((TableModel)((TableViewModel)viewContext.getViewModel()).getTableModel()).getRowCount() == 0) {
            return;
        }
        if (WebViewAPI.isAjaxRequest(viewContext.getRequest())) {
            return;
        }
        if (viewModel.containsTable("UINavigationConfig")) {
            final Row uiNavConfig = viewModel.getRow("UINavigationConfig");
            final String contentAreaName = (String)uiNavConfig.get(2);
            final HttpServletRequest request = viewContext.getRequest();
            final DynamicContentAreaModel model = DynamicContentAreaAPI.getDynamicContentAreaModel(request, contentAreaName);
            final ViewContext curCtx = model.getCurrentItem();
            if (curCtx == null) {
                final CVTableModelImpl cvModel = (CVTableModelImpl)((TableViewModel)viewContext.getViewModel()).getTableModel();
                final String parameters = this.getPKValuesAsParams(cvModel, (String)uiNavConfig.get(7));
                final Long viewNo = (Long)uiNavConfig.get(4);
                DynamicContentAreaAPI.handleNavigationAction(viewContext, viewContext.getRequest(), (Object)viewNo, parameters);
            }
        }
    }
    
    private String getPKValuesAsParams(final CVTableModelImpl model, final String tableAlias) throws Exception {
        final Column[] columns = model.getColumns();
        final int count = columns.length;
        final StringBuffer buffer = new StringBuffer();
        final String tableName = model.getTableNameForAlias(tableAlias);
        final TableDefinition tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
        final PrimaryKeyDefinition pkdef = tableDef.getPrimaryKey();
        final List<String> pkList = pkdef.getColumnList();
        for (int j = 0; j < pkList.size(); ++j) {
            for (int i = 0; i < count; ++i) {
                final String currentTableAlias = columns[i].getTableAlias();
                final String colName = pkList.get(j);
                if (tableAlias.equals(currentTableAlias) && columns[i].getColumnName().equals(colName)) {
                    buffer.append(colName);
                    buffer.append("=");
                    buffer.append(model.getValueAt(0, i));
                    buffer.append("&");
                }
            }
        }
        return buffer.toString();
    }
    
    public void savePreferences(final ViewContext viewCtx) throws Exception {
        final String mpstate = (String)viewCtx.getStateOrURLStateParameter("_MP");
        if ("_SB".equals(mpstate)) {
            this.saveSortPerference(viewCtx);
        }
        else if ("_PL".equals(mpstate)) {
            this.savePageLength(viewCtx);
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
    
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) throws Exception {
        final ViewContext viewCtx = (ViewContext)handlerContext;
        final String reqValue = viewCtx.getRequest().getParameter(variableName);
        return reqValue;
    }
    
    public String getValue(final String variableName, final Object handlerContext) {
        final HashMap<String, String> criteriaMap = (HashMap<String, String>)((ViewContext)handlerContext).getRequest().getAttribute("criteriaMap");
        if (criteriaMap != null && criteriaMap.containsKey(variableName)) {
            return criteriaMap.get(variableName);
        }
        try {
            return TemplateAPI.getVariableValue(TemplateAPI.getVariableDef(variableName), 0, (TemplateAPI.VariableHandler)this, handlerContext);
        }
        catch (final Exception exp) {
            throw new RuntimeException(exp.getMessage());
        }
    }
    
    public void handleSettingRangeForSliderTable(final SelectQuery query, final ViewContext viewCtx) {
        int length = -1;
        try {
            final Integer initialFetchedRows = (Integer)viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig").get("INITIALFETCHEDROWS");
            length = initialFetchedRows;
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
            final Range range = new Range(_FI, _TI);
            query.setRange(range);
        }
        else {
            final Range range2 = new Range(1, length);
            query.setRange(range2);
        }
    }
    
    public boolean isSliderEnabledTable(final ViewContext viewCtx) {
        try {
            return (boolean)viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig").get("ISSCROLLENABLED");
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) throws Exception {
        return null;
    }
    
    private void replaceTemplates(final SelectQuery query, final ViewContext viewCtx, final HashMap<String, String> criteriaMap) {
        Criteria criteria = query.getCriteria();
        if (criteriaMap != null) {
            viewCtx.getRequest().setAttribute("criteriaMap", (Object)criteriaMap);
        }
        try {
            if (criteria != null) {
                criteria = QueryUtil.getTemplateReplacedCriteria(criteria, (Object)this, (Object)viewCtx);
            }
            query.setCriteria(criteria);
        }
        catch (final QueryConstructionException qce) {
            throw new IllegalArgumentException("Exception occurred while replacing template values in criteria");
        }
    }
    
    private SelectQuery initializeSQL(final ViewContext viewCtx) throws Exception {
        SelectQuery query = this.fetchAndCacheSelectQuery(viewCtx);
        query = (SelectQuery)query.clone();
        query = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery((Query)query);
        TableRetrieverAction.out.log(Level.FINER, "constructed query : {0}", query);
        RelationalAPI.getInstance().setDataType((Query)query);
        return query;
    }
    
    private SQTemplateValuesServiceConfiguration getServiceConfig(final HashMap<String, String> criteriaMap, final ViewContext viewCtx) throws Exception {
        if (criteriaMap != null) {
            viewCtx.getRequest().setAttribute("criteriaMap", (Object)criteriaMap);
        }
        final SQTemplateValuesServiceConfiguration serConfig = new SQTemplateValuesServiceConfiguration((Object)this, (Object)viewCtx);
        if (criteriaMap != null) {
            for (final Object key : criteriaMap.keySet()) {
                final Map<Object, String> map = serConfig.getValuesFromHashMap();
                if (map != null) {
                    map.put(key, criteriaMap.get(key));
                }
            }
        }
        return serConfig;
    }
    
    static {
        TableRetrieverAction.cvMgr = null;
        TableRetrieverAction.out = Logger.getLogger(TableRetrieverAction.class.getName());
    }
}
