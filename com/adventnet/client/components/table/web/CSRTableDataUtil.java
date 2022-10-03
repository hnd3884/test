package com.adventnet.client.components.table.web;

import com.adventnet.client.components.filter.web.FilterModel;
import com.adventnet.client.components.table.template.CSRCellRenderer;
import java.util.Collection;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import com.adventnet.client.components.web.SearchOperator;
import java.util.HashMap;
import com.adventnet.client.components.web.TransformerContext;
import java.util.Locale;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.ViewModel;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.components.action.web.MenuDataUtil;
import java.util.Arrays;
import com.adventnet.client.components.table.TableViewState;
import javax.swing.table.TableModel;
import org.json.JSONException;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import org.json.JSONObject;
import java.util.ArrayList;
import org.json.JSONArray;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.util.web.JavaScriptConstants;

public class CSRTableDataUtil implements TableConstants, JavaScriptConstants
{
    private static Logger log;
    
    private static JSONArray getColChooserList(final Long ccMenuItem, final ViewContext viewContext) throws JSONException, Exception {
        if (ccMenuItem != null) {
            final ArrayList<String[]> showList = new ArrayList<String[]>();
            final ArrayList<String[]> hideList = new ArrayList<String[]>();
            TablePersonalizationUtil.setColumnsListForView(viewContext, showList, hideList);
            final JSONArray colList = new JSONArray();
            for (final String[] values : showList) {
                final String[] strings = values;
                final JSONObject column = new JSONObject();
                column.put("name", (Object)values[0]);
                column.put("display", (Object)I18N.getMsg(values[1], new Object[0]));
                column.put("isChoosable", (Object)Boolean.valueOf(values[2]));
                column.put("isVisible", true);
                column.put("isSortable", (Object)Boolean.valueOf(values[3]));
                colList.put((Object)column);
            }
            for (final String[] values : hideList) {
                final String[] strings = values;
                final JSONObject column = new JSONObject();
                column.put("name", (Object)values[0]);
                column.put("display", (Object)I18N.getMsg(values[1], new Object[0]));
                column.put("isChoosable", (Object)Boolean.valueOf(values[2]));
                column.put("isVisible", false);
                column.put("isSortable", (Object)Boolean.valueOf(values[3]));
                colList.put((Object)column);
            }
            return colList;
        }
        return null;
    }
    
    public static JSONObject getTableViewModelAsJson(final ViewContext viewContext) throws Exception {
        final String viewName = viewContext.getModel().getViewName();
        final TableViewModel tableViewModel = (TableViewModel)viewContext.getViewModel();
        final TableModel tableModel = (TableModel)tableViewModel.getTableModel();
        final TableTransformerContext transContext = tableViewModel.getTableTransformerContext();
        final JSONObject tableData = new JSONObject();
        tableData.put("name", (Object)viewName);
        final ViewModel viewModel = (ViewModel)viewContext.getModel();
        final DataObject viewConfigDO = viewModel.getViewConfiguration();
        final Row tableConfigRow = tableViewModel.getTableViewConfigRow();
        tableData.put("colList", (Object)getColChooserList((Long)tableConfigRow.get(8), viewContext));
        tableData.put("filterConfig", (Object)getFilterConfig(viewContext));
        tableData.put("TableModel", (Object)generateTableModelJson(tableModel, viewContext));
        tableData.put("SQLTable", tableViewModel.getTableModel() instanceof TableDatasetModel);
        tableData.put("headers", (Object)getHeaders(tableViewModel));
        tableData.put("isAdvancedSearch", transContext.isAdvancedSearch());
        tableData.put("isSearchPresent", transContext.isSearchValuePresent());
        tableData.put("isExportEnabled", tableConfigRow.get(16));
        tableData.put("showNavig", tableConfigRow.get("SHOWNAVIG"));
        tableData.put("showHeader", tableConfigRow.get(17));
        final String rowSelectionMode = (String)tableConfigRow.get(9);
        tableData.put("rowSelection", (Object)(rowSelectionMode.contains("CHECKBOX") ? "multiple" : (rowSelectionMode.contains("RADIO") ? "single" : "none")));
        tableData.put("numFixedColumns", (int)(tableData.getString("rowSelection").equals("none") ? 0 : 1));
        tableData.put("rowHover", transContext.isEnableRowHover());
        tableData.put("isScrollTable", transContext.isSliderTable());
        tableData.put("sortBy", (Object)tableViewModel.getSortedColumn());
        tableData.put("sortOrder", tableViewModel.getSortOrder());
        tableData.put("reqParams", viewContext.getTransientState("_D_RP"));
        tableData.put("navigation", (Object)getNavigData(tableViewModel.getNavigationConfig(), tableModel));
        String noRowMsg = (String)tableConfigRow.get(6);
        if (noRowMsg == null) {
            noRowMsg = "mc.component.table.No_Rows_found";
        }
        tableData.put("noRowMsg", (Object)I18N.getMsg(noRowMsg, new Object[0]));
        final RowTransformer rowtrans = tableViewModel.getRowTransformer();
        if (rowtrans != null) {
            final String[] rowSelectionList = ((TableViewState)viewContext.getViewState()).getSelectedRowIndices();
            if (rowSelectionList != null) {
                rowtrans.setRowSelectionDetails(Arrays.asList(rowSelectionList), transContext);
            }
        }
        tableData.put("data", (Object)getData(tableViewModel));
        final String menuId = (String)viewConfigDO.getFirstValue("ViewConfiguration", 9);
        if (menuId != null) {
            final Boolean renderMenu = (Boolean)viewConfigDO.getFirstValue("ACTableViewConfig", 10);
            tableData.put("showMenu", (Object)(renderMenu ? MenuDataUtil.getMenuData(menuId, viewContext) : null));
        }
        tableData.put("templateName", (Object)WebViewAPI.getViewTemplateName(viewContext));
        tableData.put("pkcol", (Object)viewModel.getFeatureValue("PKCOL"));
        return tableData;
    }
    
    private static JSONArray getColumnsList(final TableModel model) {
        final JSONArray colList = new JSONArray();
        for (int i = 0, count = model.getColumnCount(); i < count; ++i) {
            colList.put(i, (Object)model.getColumnName(i));
        }
        return colList;
    }
    
    public static JSONObject generateTableModelJson(final TableModel model, final ViewContext viewContext) throws Exception {
        final String uniqueId = viewContext.getUniqueId();
        final String rowSelection = (String)viewContext.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", "ENABLEROWSELECTION");
        final TableViewModel viewModel = (TableViewModel)viewContext.getViewModel();
        final JSONObject tableModel = new JSONObject();
        tableModel.put("uniqueId", (Object)uniqueId);
        tableModel.put("columnNames", (Object)getColumnsList(model));
        final Object[] viewColumns = viewModel.getViewColumns();
        final JSONArray viewCols = new JSONArray();
        for (int i = 0; i < viewColumns.length; ++i) {
            viewCols.put(i, ((Object[])viewColumns[i])[0]);
        }
        tableModel.put("viewColumns", (Object)viewCols);
        tableModel.put("rowSelectionType", (Object)rowSelection);
        final int rowCnt = model.getRowCount();
        final JSONArray rowVals = new JSONArray();
        for (int j = 0; j < rowCnt; ++j) {
            final JSONArray colVals = new JSONArray();
            for (int columnCount = model.getColumnCount(), count = 0; count < columnCount; ++count) {
                final Object obj = model.getValueAt(j, count);
                colVals.put(count, (Object)((obj != null) ? String.valueOf(obj) : null));
            }
            rowVals.put(j, (Object)colVals);
        }
        tableModel.put("tableModelRows", (Object)rowVals);
        return tableModel;
    }
    
    private static JSONArray getHeaders(final TableViewModel viewModel) throws Exception {
        final CSRTableTransformerContext transContext = (CSRTableTransformerContext)viewModel.getTableTransformerContext();
        final TableIterator tableIter = viewModel.getNewTableIterator();
        tableIter.reset();
        final JSONArray headers = new JSONArray();
        boolean sqlTable = false;
        final boolean isAdvSearch = transContext.isAdvancedSearch();
        if (viewModel.getTableModel() instanceof TableDatasetModel) {
            sqlTable = true;
        }
        while (tableIter.nextColumn()) {
            final JSONObject header = new JSONObject();
            tableIter.initTransCtxForCurrentCell("HEADER");
            final HashMap<String, Object> props = transContext.getRenderedAttributes();
            final String properties = (String)transContext.getColumnConfiguration().getValue("ACColumnConfiguration", "PROPERTIES", (Row)null);
            if (properties != null) {
                final JSONObject json = new JSONObject(properties);
                final Iterator<String> keys = json.keys();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    header.put(key, json.get(key));
                }
            }
            final Row columnConfigRow = transContext.getColumnConfigRow();
            final String actionName = (String)columnConfigRow.get(8);
            if (actionName != null) {
                try {
                    final DataObject menuItemObj = MenuVariablesGenerator.getCompleteMenuItemData((Object)actionName);
                    header.put("menuScriptInclusion", (Object)MenuDataUtil.getScriptInclusion(menuItemObj, transContext.getViewContext().getContextPath()));
                    header.put("createMenuScript", (Object)MenuVariablesGenerator.generateMenuVariableJSON(menuItemObj, transContext.getViewContext(), true));
                }
                catch (final AuthorizationException ae) {
                    ae.printStackTrace();
                }
            }
            final String menuID = (String)columnConfigRow.get(24);
            if (menuID != null) {
                final DataObject menuObj = MenuVariablesGenerator.getCompleteMenuData((Object)menuID);
                final JSONArray createMenuScripts = new JSONArray();
                final JSONArray jsFuncArray = new JSONArray();
                final Iterator<Long> iter = menuObj.get("MenuAndMenuItem", 2);
                while (iter.hasNext()) {
                    final Long menuItemID_NO = iter.next();
                    DataObject menuItemIdObj;
                    try {
                        menuItemIdObj = MenuVariablesGenerator.getCompleteMenuItemData((Object)menuItemID_NO);
                    }
                    catch (final AuthorizationException ae2) {
                        ae2.printStackTrace();
                        continue;
                    }
                    final JSONObject menuScript = MenuVariablesGenerator.generateMenuVariableJSON(menuItemIdObj, transContext.getViewContext(), true);
                    createMenuScripts.put((Object)menuScript);
                    final String script = MenuDataUtil.getScriptInclusion(menuItemIdObj, transContext.getViewContext().getContextPath());
                    if (script != null) {
                        jsFuncArray.put((Object)script);
                    }
                }
                header.put("createMenuScripts", (Object)createMenuScripts);
                header.put("jsFuncArray", (Object)jsFuncArray);
            }
            final String columnName = transContext.getPropertyName();
            header.put("colIndex", tableIter.getCurrentColumn());
            header.put("sortEnabled", transContext.isSortEnabled());
            header.put("columnName", (Object)columnName);
            final String value = props.get("VALUE");
            header.put("displayName", (Object)((value != null) ? value : ""));
            final ColumnTransformer ctObj = tableIter.getColumnTransformer();
            if (sqlTable) {
                final TableDatasetModel tdm = (TableDatasetModel)viewModel.getTableModel();
                final int columnIndex = tdm.getColumnIndex(columnName);
                if (columnIndex >= 0) {
                    header.put("sqlTblColindex", (Object)String.valueOf(columnIndex + 1));
                }
            }
            final boolean isSearchEnabled = transContext.isSearchEnabled();
            header.put("headerCss", (Object)transContext.getHeaderCssClass());
            header.put("columnType", columnConfigRow.get(32));
            header.put("columnCss", (Object)transContext.getColumnCSS().trim());
            header.put("sqlType", (Object)transContext.getSQLDataType().toUpperCase(Locale.ENGLISH));
            final int comparator = transContext.getSearchComparator();
            header.put("disabled", "BOOLEAN".equalsIgnoreCase(transContext.getSQLDataType()) || 16 == comparator || 17 == comparator);
            header.put("isSearchEnabled", isSearchEnabled);
            header.put("savedWidth", transContext.getColumnWidth());
            header.put("canAutoResize", transContext.canAutoResize());
            if (isSearchEnabled && isAdvSearch) {
                final SearchOperator[] criOps = ctObj.getSearchOperators(transContext);
                final JSONArray options = new JSONArray();
                for (final SearchOperator criOp : criOps) {
                    final JSONObject option = new JSONObject();
                    option.put("value", criOp.getValue());
                    option.put("criteria", (Object)criOp.getDisplayString());
                    option.put("selected", criOp.getValue() == comparator);
                    option.put("noOfArgs", criOp.getNumberOfArguments());
                    options.put((Object)option);
                }
                header.put("advanceSearchOptions", (Object)options);
            }
            header.put("searchValue", (Object)transContext.getSearchValue());
            if (isSearchEnabled) {
                header.put("errorMsg", (Object)ctObj.getErrorMsg(transContext));
                header.put("validateMethod", (Object)ctObj.getValidateJSFunction(transContext));
                header.put("format", (Object)ctObj.getInputFormat(transContext));
            }
            headers.put((Object)header);
        }
        return headers;
    }
    
    private static JSONObject getNavigData(final NavigationConfig navConfig, final TableModel tableModel) {
        if (navConfig == null) {
            return null;
        }
        final JSONObject navigConf = new JSONObject();
        final long totalRecords = navConfig.getTotalRecords();
        navigConf.put("total", totalRecords);
        final int pageLength = navConfig.getPageLength();
        navigConf.put("itemsPerPage", pageLength);
        navigConf.put("from", navConfig.getFromIndex());
        navigConf.put("to", navConfig.getToIndex());
        final int pages = navConfig.getTotalPages();
        navigConf.put("pages", pages);
        final int pageNum = navConfig.getPageNumber();
        navigConf.put("currentPage", pageNum);
        navigConf.put("prevPageIndex", navConfig.getPreviousPageIndex());
        final boolean isNoCount = navConfig.isNoCount();
        final String orientation = navConfig.getOrientation();
        navigConf.put("hasPaginationTop", true);
        navigConf.put("hasPaginationBottom", true);
        if (!orientation.equals("BOTH")) {
            navigConf.put("hasPaginationTop", orientation.equals("TOP"));
            navigConf.put("hasPaginationBottom", orientation.equals("BOTTOM"));
        }
        final String type = navConfig.getNavigationType();
        navigConf.put("type", (Object)type);
        navigConf.put("naviglayout", (Object)navConfig.getNavigTemplateName());
        final int rowCount = tableModel.getRowCount();
        navigConf.put("range", (Collection)navConfig.getRangeList());
        navigConf.put("startLinkIndex", navConfig.getStartLinkIndex());
        navigConf.put("endLinkIndex", navConfig.getEndLinkIndex());
        navigConf.put("isNoCount", isNoCount);
        navigConf.put("showPrevPage", pageNum > 1);
        if (isNoCount) {
            navigConf.put("showNextPage", pageLength < rowCount);
        }
        else {
            navigConf.put("showFirstPage", pageNum > 1);
            navigConf.put("showNextPage", pageNum != pages);
            navigConf.put("showLastPage", pageNum != pages);
        }
        return navigConf;
    }
    
    private static JSONArray getData(final TableViewModel viewModel) throws Exception {
        final TableTransformerContext transContext = viewModel.getTableTransformerContext();
        final TableIterator iterator = viewModel.getTableIterator();
        iterator.setCurrentRow(-1);
        final JSONArray rows = new JSONArray();
        final RowTransformer rowTransformer = viewModel.getRowTransformer();
        while (iterator.nextRow()) {
            final JSONObject row = new JSONObject();
            iterator.setCurrentColumn(-1);
            final JSONArray cells = new JSONArray();
            row.put("rowIdx", transContext.getRowIndex());
            while (iterator.nextColumn()) {
                iterator.initTransCtxForCurrentCell("Cell");
                final HashMap<String, Object> props = transContext.getRenderedAttributes();
                cells.put((Object)CSRCellRenderer.getData(props, transContext));
            }
            row.put("cells", (Object)cells);
            if (rowTransformer != null) {
                row.put("isSelected", rowTransformer.canSelectRow(transContext));
            }
            rows.put((Object)row);
        }
        return rows;
    }
    
    private static JSONObject getFilterConfig(final ViewContext viewContext) throws Exception {
        final JSONObject filterObject = new JSONObject();
        final FilterModel fm = (FilterModel)viewContext.getTransientState("FILTERMODEL");
        if (fm == null) {
            return null;
        }
        final DataObject viewConfigDO = viewContext.getModel().getViewConfiguration();
        final Row filterRow = viewConfigDO.getRow("ACTableFilterListRel");
        final boolean customFilter = (boolean)filterRow.get(5);
        filterObject.put("linkedViewName", filterRow.get(7));
        filterObject.put("uiType", filterRow.get(6));
        filterObject.put("isCustomFilter", customFilter);
        final JSONArray filters = new JSONArray();
        while (fm.next()) {
            final JSONObject filter = new JSONObject();
            final String groupTitle = fm.getGroupTitle();
            filter.put("groupTitle", (Object)((groupTitle != null) ? I18N.getMsg(groupTitle, new Object[0]) : ""));
            filter.put("name", (Object)I18N.getMsg(fm.getFilterName(), new Object[0]));
            filter.put("title", (Object)I18N.getMsg(fm.getFilterTitle(), new Object[0]));
            filters.put((Object)filter);
        }
        filterObject.put("LISTID", (Object)fm.getListId());
        filterObject.put("isDeletable", fm.isDeleteable());
        filterObject.put("isEditable", fm.isEditable());
        filterObject.put("SELFILTER", (Object)fm.getSelectedFilter());
        filterObject.put("filters", (Object)filters);
        return filterObject;
    }
    
    static {
        CSRTableDataUtil.log = Logger.getLogger(CSRTableDataUtil.class.getName());
    }
}
