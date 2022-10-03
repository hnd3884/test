package com.adventnet.client.components.table.web;

import java.util.logging.Level;
import com.adventnet.client.cache.StaticCache;
import org.json.JSONArray;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.view.UserPersonalizationAPI;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.List;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TablePersonalizationUtil
{
    private static Logger out;
    
    @Deprecated
    public static void setColumnsListForView(final String viewName, final long accountId, final ArrayList<String[]> displayList, final ArrayList<String[]> hideList, final HttpServletRequest request) {
        final ViewContext viewCtx = ViewContext.getViewContext((Object)viewName, request);
        setColumnsListForView(viewCtx, displayList, hideList);
    }
    
    public static void setColumnsListForView(final ViewContext viewCtx, final ArrayList<String[]> displayList, final ArrayList<String[]> hideList) {
        try {
            final TableViewModel tableViewModel = (TableViewModel)viewCtx.getViewModel();
            final TableTransformerContext transContext = tableViewModel.getTableTransformerContext();
            final List<Row> columnList = tableViewModel.getColumnList();
            final DataObject viewConfigDO = viewCtx.getModel().getViewConfiguration();
            final Long columnConfigName = (Long)viewConfigDO.getFirstValue("ACTableViewConfig", 4);
            final DataObject configDO = TableViewModel.getColumnConfigDO(columnConfigName, viewConfigDO);
            final ArrayList<String> tableNames = new ArrayList<String>();
            tableNames.add("ACColumnConfiguration");
            tableNames.add("ACRendererConfiguration");
            final Row criteriaRow = new Row("ACColumnConfiguration");
            criteriaRow.set(1, (Object)columnConfigName);
            final Row emberConfigRow = new Row("ACColumnConfiguration");
            emberConfigRow.set(1, (Object)columnConfigName);
            for (final Row actablecolumn : columnList) {
                final Boolean visible = (Boolean)actablecolumn.get(5);
                final String alias = (String)actablecolumn.get(3);
                final String displayName = (String)actablecolumn.get(4);
                criteriaRow.set(3, actablecolumn.get(3));
                final DataObject ccDO = configDO.getDataObject((List)tableNames, criteriaRow);
                final String transformerName = (String)ccDO.getFirstValue("ACColumnConfiguration", 7);
                final ColumnTransformer transformer = (ColumnTransformer)WebClientUtil.createInstance(transformerName);
                transContext.setColumnConfiguration(ccDO);
                transContext.setDisplayName(displayName);
                final Integer origIdx = tableViewModel.columnNameVsIndex.get(alias);
                transContext.setColumnIndex((origIdx != null) ? ((int)origIdx) : -1);
                if (!transformer.canRenderColumn(transContext)) {
                    continue;
                }
                emberConfigRow.set(3, actablecolumn.get(3));
                final Row emberCCRow = configDO.findRow(emberConfigRow);
                boolean isColumnSortable = true;
                if (emberCCRow != null && emberCCRow.get(39) != null) {
                    final JSONObject properties = new JSONObject((String)emberCCRow.get(39));
                    isColumnSortable = (properties.has("isSortable") && Boolean.TRUE.equals(properties.get("isSortable")));
                }
                final String[] values = { alias, displayName, (emberCCRow != null) ? String.valueOf(emberCCRow.get(38)) : "true", String.valueOf(isColumnSortable) };
                if (visible) {
                    displayList.add(values);
                }
                else {
                    hideList.add(values);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void updateColumnsListForView(final String viewName, final long accountId, final String[] showList, final String[] hideList) {
        try {
            final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
            TableViewModel.updateTableWithColumns(dataObject);
            if (showList == null || showList.length <= 0) {
                throw new Exception("No column selected for showing");
            }
            int size = showList.length;
            final Iterator<Row> iter = dataObject.getRows("ACTableColumns");
            final ArrayList<String> list = new ArrayList<String>();
            while (iter.hasNext()) {
                final Row curRow = iter.next();
                list.add((String)curRow.get(3));
            }
            final Row row = new Row("ACTableColumns");
            row.set(1, dataObject.getFirstValue("ViewConfiguration", 1));
            for (int i = 0; i < size; ++i) {
                row.set(3, (Object)showList[i]);
                final Row completeRow = dataObject.getRow("ACTableColumns", row);
                final Integer columnIndex = (Integer)completeRow.get(2);
                final Boolean visible = (Boolean)completeRow.get(5);
                boolean changed = false;
                if (!visible) {
                    completeRow.set(5, (Object)Boolean.TRUE);
                    changed = true;
                }
                if (columnIndex != i) {
                    completeRow.set(2, (Object)i);
                    changed = true;
                }
                if (changed) {
                    dataObject.updateRow(completeRow);
                }
                list.remove(showList[i]);
            }
            if (hideList != null) {
                for (int hideSize = hideList.length, j = 0; j < hideSize; ++j) {
                    row.set(3, (Object)hideList[j]);
                    final Row completeRow2 = dataObject.getRow("ACTableColumns", row);
                    if (completeRow2.get(5)) {
                        completeRow2.set(5, (Object)Boolean.FALSE);
                    }
                    completeRow2.set(2, (Object)(size + j));
                    dataObject.updateRow(completeRow2);
                    list.remove(hideList[j]);
                }
                size += hideList.length;
            }
            for (int hideSize = list.size(), j = 0; j < hideSize; ++j) {
                row.set(3, (Object)list.get(j));
                final Row completeRow2 = dataObject.getRow("ACTableColumns", row);
                final Boolean visible = (Boolean)completeRow2.get(5);
                if (visible) {
                    completeRow2.set(5, (Object)Boolean.FALSE);
                }
                completeRow2.set(2, (Object)(size + j));
                dataObject.updateRow(completeRow2);
            }
            UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void updateSortForView(final String viewName, final long accountId, final String sortColumn, String order) {
        try {
            final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
            final Row row = dataObject.getFirstRow("ACTableViewConfig");
            if ("D".equals(order)) {
                order = "DESC";
            }
            else {
                order = "ASC";
            }
            row.set("SORTCOLUMN", (Object)sortColumn);
            row.set("SORTORDER", (Object)order);
            dataObject.updateRow(row);
            UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void updatePageLengthForView(final String viewName, final long accountId, final Integer pageLength) {
        try {
            final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
            final Row row = dataObject.getFirstRow("ACTableViewConfig");
            row.set("PAGELENGTH", (Object)pageLength);
            dataObject.updateRow(row);
            UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void updateColumnWidthForUser(final String viewName, final long accountId, final JSONObject columns) {
        try {
            final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
            TableViewModel.updateTableWithColumns(dataObject);
            final JSONArray colArray = columns.names();
            final Row tempRow = new Row("ACTableColumns");
            tempRow.set(1, dataObject.getFirstValue("ACTableColumns", 1));
            for (int i = 0, len = colArray.length(); i < len; ++i) {
                final String columnName = (String)colArray.get(i);
                tempRow.set(3, (Object)columnName);
                final Row curRow = dataObject.findRow(tempRow);
                curRow.set(6, (Object)new Long(columns.get(columnName).toString()));
                dataObject.updateRow(curRow);
            }
            UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static void updateExportMaskingConfig(final String viewName, final long accountId, final JSONObject configMap) {
        try {
            final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
            TableViewModel.updateTableWithColumns(dataObject);
            final JSONArray colArray = configMap.names();
            final Row tempRow = new Row("ACTableColumns");
            tempRow.set(1, dataObject.getFirstValue("ACTableColumns", 1));
            for (int i = 0, len = colArray.length(); i < len; ++i) {
                final String columnName = (String)colArray.get(i);
                tempRow.set(3, (Object)columnName);
                final Row curRow = dataObject.findRow(tempRow);
                if (curRow != null) {
                    curRow.set(7, configMap.get(columnName));
                    dataObject.updateRow(curRow);
                }
            }
            UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
            final String cacheKey = (String)dataObject.getFirstValue("ViewConfiguration", 2) + "_REDACT_CONFIG";
            StaticCache.removeFromCache((Object)cacheKey);
        }
        catch (final Exception e) {
            TablePersonalizationUtil.out.log(Level.SEVERE, "Exception occurred while updating export mask configuration for view {0}", viewName);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public static JSONObject getExportMaskingConfig(final String viewName, final long accountId, final HttpServletRequest request) {
        String personalizedViewName = UserPersonalizationAPI.getPersonalizedViewName((Object)viewName, accountId);
        if (personalizedViewName == null) {
            personalizedViewName = viewName;
        }
        final String cacheKey = personalizedViewName + "_REDACT_CONFIG";
        if (StaticCache.getFromCache((Object)cacheKey) != null) {
            return (JSONObject)StaticCache.getFromCache((Object)cacheKey);
        }
        final JSONObject redactConfig = new JSONObject();
        try {
            final ViewContext viewCtx = ViewContext.getViewContext((Object)personalizedViewName, (Object)personalizedViewName, request);
            final TableViewModel tableViewModel = (TableViewModel)viewCtx.getViewModel();
            final TableTransformerContext transContext = tableViewModel.getTableTransformerContext();
            final DataObject viewConfigDO = viewCtx.getModel().getViewConfiguration();
            TableViewModel.updateTableWithColumns(viewConfigDO);
            final Long columnConfigName = (Long)viewConfigDO.getFirstValue("ACTableViewConfig", 4);
            final DataObject configDO = TableViewModel.getColumnConfigDO(columnConfigName, viewConfigDO);
            final ArrayList<String> tableNames = new ArrayList<String>();
            tableNames.add("ACColumnConfiguration");
            final Row criteriaRow = new Row("ACColumnConfiguration");
            criteriaRow.set(1, (Object)columnConfigName);
            final JSONArray exportMaskColumns = new JSONArray();
            final Row maskConfigRow = new Row("PIIRedactConfig");
            maskConfigRow.set(1, viewConfigDO.getFirstValue("ViewConfiguration", 1));
            Row tableColumnRow = new Row("ACTableColumns");
            tableColumnRow.set(1, viewConfigDO.getFirstValue("ViewConfiguration", 1));
            criteriaRow.set(1, (Object)columnConfigName);
            final Iterator<Row> redactConfigRows = viewConfigDO.getRows("PIIRedactConfig");
            while (redactConfigRows.hasNext()) {
                final JSONObject exportMaskColumn = new JSONObject();
                final Row redactConfigRow = redactConfigRows.next();
                final String alias = (String)redactConfigRow.get(2);
                tableColumnRow.set(3, (Object)alias);
                tableColumnRow = viewConfigDO.findRow(tableColumnRow);
                final Boolean visible = (Boolean)tableColumnRow.get(5);
                final String displayName = (String)tableColumnRow.get(4);
                criteriaRow.set(3, tableColumnRow.get(3));
                final DataObject ccDO = configDO.getDataObject((List)tableNames, criteriaRow);
                final String transformerName = (String)ccDO.getFirstValue("ACColumnConfiguration", 7);
                final ColumnTransformer transformer = (ColumnTransformer)WebClientUtil.createInstance(transformerName);
                transContext.setColumnConfiguration(ccDO);
                transContext.setDisplayName(displayName);
                final Integer origIdx = tableViewModel.columnNameVsIndex.get(alias);
                transContext.setColumnIndex((origIdx != null) ? ((int)origIdx) : -1);
                if (!transformer.canRenderColumn(transContext)) {
                    continue;
                }
                exportMaskColumn.put("DISPLAYNAME", (Object)displayName);
                exportMaskColumn.put("COLUMNALIAS", (Object)alias);
                exportMaskColumn.put("VISIBLE", (boolean)visible);
                exportMaskColumn.put("REDACT_TYPE", tableColumnRow.get("REDACT_TYPE"));
                exportMaskColumns.put((Object)exportMaskColumn);
            }
            if (exportMaskColumns.length() > 0) {
                redactConfig.put(viewName, (Object)exportMaskColumns);
                StaticCache.addToCache((Object)cacheKey, (Object)redactConfig);
            }
        }
        catch (final Exception e) {
            TablePersonalizationUtil.out.log(Level.SEVERE, "Exception occurred while fetching export mask configuration for view {0}", viewName);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return redactConfig;
    }
    
    static {
        TablePersonalizationUtil.out = Logger.getLogger(TablePersonalizationUtil.class.getName());
    }
}
