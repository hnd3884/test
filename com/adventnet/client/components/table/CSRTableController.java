package com.adventnet.client.components.table;

import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.client.tpl.TemplateAPI;
import com.adventnet.client.components.tpl.HttpRequestHandler;
import com.adventnet.ds.query.Criteria;
import com.adventnet.client.components.table.web.TableUtil;
import java.util.List;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.table.web.CustomViewUtils;
import com.adventnet.client.util.LookUpUtil;
import java.util.ArrayList;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import com.adventnet.ds.query.DataSet;
import java.util.logging.Level;
import java.util.Map;
import com.adventnet.client.view.common.ExportUtils;
import com.adventnet.client.components.table.web.ExportTableViewModel;
import com.adventnet.ds.query.Query;
import java.sql.Connection;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Range;
import com.adventnet.client.components.table.web.ExportTableModel;
import com.adventnet.mfw.bean.BeanUtil;
import java.util.Properties;
import com.adventnet.model.table.CVTableModel;
import com.adventnet.customview.service.ServiceConfiguration;
import com.adventnet.customview.service.SQTemplateValuesServiceConfiguration;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.table.web.CSRTableDataUtil;
import org.json.JSONObject;
import com.adventnet.client.view.State;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.components.table.web.TablePersonalizationUtil;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.customview.ViewData;
import javax.swing.table.TableModel;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.customview.CustomViewManager;
import com.adventnet.client.components.table.web.TableViewController;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.client.view.web.DefaultViewController;

public class CSRTableController extends DefaultViewController implements QueryUtil.TemplateVariableHandler, TableViewController
{
    protected static CustomViewManager cvMgr;
    private static final Logger OUT;
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        this.preModelFetch(viewCtx);
        initializeReferences();
        final SelectQuery query = this.initializeSQL(viewCtx);
        CSRTableViewModel viewModel;
        if (viewCtx.isExportType()) {
            viewModel = new CSRTableViewModel(viewCtx);
            final TableModel expModel = this.getViewExportModel(query, viewCtx);
            viewModel.setTableModel(expModel);
        }
        else {
            final ViewData viewData = this.getViewData(query, viewCtx);
            viewModel = new CSRTableViewModel(viewData.getModel(), viewCtx);
        }
        viewModel.init();
        viewModel.getTableTransformerContext().setRequest(viewCtx.getRequest());
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
        final CustomViewRequest cvRequest = new CustomViewRequest(query);
        cvRequest.set("fetchCountOnly", (Object)true);
        cvRequest.putServiceConfiguration((ServiceConfiguration)new SQTemplateValuesServiceConfiguration((Object)this, (Object)viewCtx));
        final ViewData viewData = this.sendCVRequest(viewCtx, cvRequest);
        return ((CVTableModel)viewData.getModel()).getTotalRecordsCount();
    }
    
    public Properties getCustomRedactConfiguration(final ViewContext vc) {
        return null;
    }
    
    protected static void initializeReferences() throws Exception {
        if (CSRTableController.cvMgr == null) {
            CSRTableController.cvMgr = (CustomViewManager)BeanUtil.lookup("TableViewManager");
        }
    }
    
    public ExportTableModel getViewExportModel(final SelectQuery query, final ViewContext viewCtx) throws Exception {
        this.setSortColumn(query, viewCtx);
        this.replaceTemplates(query, viewCtx);
        this.setCriteria(query, viewCtx);
        query.setRange((Range)null);
        final DataSet ds = RelationalAPI.getInstance().executeReadOnlyQuery((Query)query, (Connection)viewCtx.getTransientState("CONNECTION"));
        final ExportTableViewModel expModel = new ExportTableViewModel();
        expModel.updateModel(ds);
        final Properties redactConfigProps = ExportUtils.getExportMaskingConfig(viewCtx.getModel().getViewConfiguration());
        final Properties extendedProps = this.getCustomRedactConfiguration(viewCtx);
        if (extendedProps != null) {
            redactConfigProps.putAll(extendedProps);
        }
        CSRTableController.OUT.log(Level.FINE, "redactConfig Properties configured for view {0} is {1} ", new Object[] { viewCtx.getModel().getViewName(), redactConfigProps });
        expModel.setPIIColumnConfig(redactConfigProps);
        return expModel;
    }
    
    public ViewData getViewData(final SelectQuery query, final ViewContext viewCtx) throws Exception {
        this.setRange(query, viewCtx);
        this.setSortColumn(query, viewCtx);
        this.setCriteria(query, viewCtx);
        final CustomViewRequest cvRequest = new CustomViewRequest(query);
        cvRequest.set("fetchPrevPage", (Object)((TableViewState)viewCtx.getViewState()).isPrevPageToBeFetched());
        if (viewCtx.getModel().getFeatureValue("no_of_attempt_to_fetchPrevPage") != null) {
            cvRequest.set("no_of_times_to_fetch_prevPage", (Object)Integer.parseInt(viewCtx.getModel().getFeatureValue("no_of_attempt_to_fetchPrevPage")));
        }
        if (NavigationConfig.getNocount(viewCtx).equals("true")) {
            cvRequest.set("TOTAL", (Object)(-1L));
        }
        cvRequest.putServiceConfiguration((ServiceConfiguration)new SQTemplateValuesServiceConfiguration((Object)this, (Object)viewCtx));
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
        return CSRTableController.cvMgr.getData(cvRequest);
    }
    
    public SelectQuery getSelectQuery(final Object customViewId, final ViewContext viewCtx) throws Exception {
        return CustomViewUtils.getSelectQuery(customViewId, viewCtx.getModel().getViewConfiguration());
    }
    
    public void setRange(final SelectQuery query, final ViewContext viewCtx) throws DataAccessException {
        int fromIndex = Integer.MIN_VALUE;
        int pageLength = Integer.MIN_VALUE;
        final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
        if (tableViewState != null && viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 3) != null) {
            fromIndex = tableViewState.getFromIndex();
            pageLength = tableViewState.getPageLength();
            final int pageNo = tableViewState.getPageNumber();
            if (pageNo > -1 && pageLength != Integer.MIN_VALUE) {
                fromIndex = Math.max(pageNo - 1, 0) * pageLength + 1;
            }
        }
        if (fromIndex == Integer.MIN_VALUE) {
            fromIndex = ((query.getRange() != null) ? query.getRange().getStartIndex() : 1);
        }
        if (pageLength == Integer.MIN_VALUE) {
            final Row r = viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig");
            if (r.get("ISSCROLLENABLED")) {
                pageLength = (int)r.get("INITIALFETCHEDROWS");
            }
            else {
                pageLength = this.getDefaultPageLength(viewCtx, query);
            }
        }
        if (NavigationConfig.getNocount(viewCtx).equals("true") && pageLength != -1) {
            ++pageLength;
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
        final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
        if (tableViewState == null) {
            return;
        }
        String columnName = tableViewState.getSortBy();
        boolean isAscending = tableViewState.getSortOrder();
        if (columnName == null) {
            final Row tableRow = viewCtx.getModel().getViewConfiguration().getFirstRow("ACTableViewConfig");
            columnName = (String)tableRow.get(13);
            isAscending = !"DESC".equals(tableRow.get(14));
        }
        if (columnName != null) {
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
    
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        Criteria criteria = query.getCriteria();
        try {
            final Criteria searchCriteria = TableUtil.getSearchCriteria(viewCtx, query.getSelectColumns());
            if (searchCriteria != null) {
                criteria = ((criteria == null) ? searchCriteria : criteria.and(searchCriteria));
            }
            query.setCriteria(criteria);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void savePreferences(final ViewContext viewCtx) throws Exception {
        final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
        if (tableViewState == null) {
            return;
        }
        final String mpstate = tableViewState.getModifiedParam();
        if ("_SB".equals(mpstate)) {
            this.saveSortPerference(viewCtx);
        }
        else if ("_PL".equals(mpstate)) {
            this.savePageLength(viewCtx);
        }
    }
    
    public void saveSortPerference(final ViewContext viewCtx) throws Exception {
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
        final TableViewState tableViewState = (TableViewState)viewCtx.getViewState();
        if (tableViewState == null) {
            return;
        }
        final int pageLength = tableViewState.getPageLength();
        final Row r = viewCtx.getModel().getViewConfiguration().getRow("ACTableViewConfig");
        if (pageLength != Integer.MIN_VALUE && Integer.valueOf(pageLength) != r.get(12)) {
            TablePersonalizationUtil.updatePageLengthForView(viewCtx.getModel().getViewName(), WebClientUtil.getAccountId(), pageLength);
        }
    }
    
    public String getValue(final String variableName, final Object handlerContext) {
        try {
            final HttpRequestHandler defaultTemplateHandler = new HttpRequestHandler();
            return TemplateAPI.getVariableValue(TemplateAPI.getVariableDef(variableName), 0, (TemplateAPI.VariableHandler)defaultTemplateHandler, handlerContext);
        }
        catch (final Exception exp) {
            throw new RuntimeException(exp.getMessage());
        }
    }
    
    private void replaceTemplates(final SelectQuery query, final ViewContext viewCtx) {
        Criteria criteria = query.getCriteria();
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
        final SelectQuery query = (SelectQuery)RelationalAPI.getInstance().getModifiedQuery((Query)this.fetchAndCacheSelectQuery(viewCtx).clone());
        CSRTableController.OUT.log(Level.FINER, "constructed query : {0}", query);
        RelationalAPI.getInstance().setDataType((Query)query);
        return query;
    }
    
    static {
        CSRTableController.cvMgr = null;
        OUT = Logger.getLogger(CSRTableController.class.getName());
    }
}
