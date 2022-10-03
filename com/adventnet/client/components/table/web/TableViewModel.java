package com.adventnet.client.components.table.web;

import com.zoho.authentication.AuthenticationUtil;
import com.adventnet.beans.xtable.SortColumn;
import javax.swing.table.TableModel;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.json.JSONException;
import java.util.Properties;
import org.json.JSONObject;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.client.util.DataUtils;
import com.adventnet.persistence.WritableDataObject;
import java.util.ArrayList;
import java.util.Iterator;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;

public class TableViewModel implements TableConstants
{
    protected Integer dummyIndex;
    protected Object tableModel;
    protected Object[] viewColumns;
    protected HashMap<String, Integer> columnNameVsIndex;
    protected String sortedColumn;
    private boolean sortedOrder;
    protected DataObject viewConfigDO;
    protected TableTransformerContext transformerContext;
    protected ViewContext viewContext;
    protected Row tableViewConfigDO;
    protected NavigationConfig navigConfig;
    protected TableIterator iter;
    private RowTransformer rowTrans;
    private String menuid;
    private List<Row> actableColumnList;
    private static Logger log;
    public TreeMap<String, DataObject> viewList;
    
    public TableViewModel(final Object tableModel, final ViewContext viewContext) throws Exception {
        this(viewContext);
        this.tableModel = tableModel;
    }
    
    public TableViewModel(final ViewContext viewContext) throws Exception {
        this.dummyIndex = -1;
        this.tableModel = null;
        this.viewColumns = null;
        this.columnNameVsIndex = null;
        this.sortedColumn = null;
        this.sortedOrder = true;
        this.viewConfigDO = null;
        this.transformerContext = null;
        this.viewContext = null;
        this.tableViewConfigDO = null;
        this.navigConfig = null;
        this.menuid = null;
        this.actableColumnList = null;
        this.viewList = null;
        this.viewContext = viewContext;
        getColumnConfigDO(this.viewConfigDO = viewContext.getModel().getViewConfiguration());
    }
    
    public void init() throws Exception {
        this.setTableProperties();
        this.setColumnConfigurations();
        this.setNavigationProperties();
        this.transformerContext = new TableTransformerContext(this, this.viewContext);
        this.rowTrans = (RowTransformer)WebClientUtil.createInstance((String)this.viewConfigDO.getFirstValue("ACTableViewConfig", "ROWTRANSFORMER"));
    }
    
    public TableIterator getTableIterator() {
        if (this.iter == null) {
            this.iter = new TableIterator(this.viewContext);
        }
        return this.iter;
    }
    
    public TableIterator getNewTableIterator() {
        return new TableIterator(this.viewContext);
    }
    
    public TableTransformerContext getTableTransformerContext() {
        return this.transformerContext;
    }
    
    public Object getTableModel() {
        return this.tableModel;
    }
    
    public void setTableModel(final Object newModel) {
        this.tableModel = newModel;
    }
    
    public Row getTableViewConfigRow() {
        try {
            return this.viewConfigDO.getRow("ACTableViewConfig");
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public Object[] getViewColumns() {
        return this.viewColumns;
    }
    
    public String getSortedColumn() {
        return this.sortedColumn;
    }
    
    public boolean getSortOrder() {
        return this.sortedOrder;
    }
    
    public void setNavigationConfig(final NavigationConfig config) {
        this.navigConfig = config;
    }
    
    public NavigationConfig getNavigationConfig() {
        return this.navigConfig;
    }
    
    public void addCompiledData(final String key, final Object data) {
        this.viewContext.getModel().addCompiledData((Object)key, data);
    }
    
    public Object getCompiledData(final String key) {
        return this.viewContext.getModel().getCompiledData((Object)key);
    }
    
    private void setColumnConfigurations() throws Exception {
        this.viewColumns = (Object[])this.viewContext.getModel().getCompiledData((Object)"COLUMN_OBJECTS");
        if (this.viewColumns == null) {
            updateTableWithColumns(this.viewConfigDO);
            final Long columnConfigNameNo = (Long)this.viewConfigDO.getFirstValue("ACTableViewConfig", "COLUMNCONFIGLIST");
            final Iterator<Row> rows = this.viewConfigDO.getRows("ACTableColumns");
            this.setColumnConfigMap(columnConfigNameNo, rows);
            this.addCompiledData("COLUMN_OBJECTS", this.viewColumns);
            this.addCompiledData("TABLE_COLUMNS", this.actableColumnList);
        }
    }
    
    public DataObject getColumnConfig(final String columnAlias) {
        return this.viewList.get(columnAlias);
    }
    
    private void setColumnConfigMap(final Object columnConfigName, final Iterator<Row> rows) throws Exception {
        final Row accList = new Row("ACColumnConfigurationList");
        accList.set(1, columnConfigName);
        final DataObject colConfig = getColumnConfigDO(columnConfigName, this.viewConfigDO);
        final Row criteriaRow = new Row("ACColumnConfiguration");
        criteriaRow.set("CONFIGNAME", columnConfigName);
        this.viewList = new TreeMap<String, DataObject>();
        this.actableColumnList = new ArrayList<Row>();
        final TreeMap<Integer, Object[]> viewColumnList = new TreeMap<Integer, Object[]>();
        Object[] viewObj = null;
        final DataObject actableColumns = (DataObject)new WritableDataObject();
        while (rows.hasNext()) {
            final Row columnRow = rows.next();
            final String columnName = (String)columnRow.get("COLUMNALIAS");
            final boolean isVisible = (boolean)columnRow.get("VISIBLE");
            final Integer columnIndex = (Integer)columnRow.get("COLUMNINDEX");
            criteriaRow.set("COLUMNALIAS", (Object)columnName);
            try {
                final DataObject dataObject = colConfig.getDataObject(colConfig.getTableNames(), criteriaRow);
                viewObj = this.getViewObject(dataObject, columnRow);
                if (isVisible) {
                    viewColumnList.put(columnIndex, viewObj);
                    this.viewList.put(columnName, dataObject);
                }
                actableColumns.addRow(columnRow);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                throw new Exception("ACColumnConfiguration vs ACTableColumns mismatch. Column name is " + columnName);
            }
        }
        this.viewColumns = viewColumnList.values().toArray();
        this.actableColumnList = DataUtils.getSortedList(actableColumns, "ACTableColumns", "COLUMNINDEX");
    }
    
    public Object[] getViewObject(final DataObject columnConfigDO, final Row columnRow) throws Exception {
        final Object[] columnObject = new Object[6];
        final String columnName = (String)columnRow.get("COLUMNALIAS");
        String displayName = (String)columnRow.get("DISPLAYNAME");
        final boolean isVisible = (boolean)columnRow.get("VISIBLE");
        if (isVisible) {
            final String transformer = (String)columnConfigDO.getFirstValue("ACColumnConfiguration", "TRANSFORMER");
            final boolean isHeaderVisible = (boolean)columnConfigDO.getFirstValue("ACColumnConfiguration", "ISHEADERVISIBLE");
            if (!isHeaderVisible) {
                displayName = "&nbsp;";
            }
            columnObject[0] = columnName;
            final Integer origIdx = this.columnNameVsIndex.get(columnName);
            if (origIdx != null) {
                columnObject[1] = origIdx;
            }
            else {
                final Object[] array = columnObject;
                final int n = 1;
                final Integer dummyIndex = this.dummyIndex;
                --this.dummyIndex;
                array[n] = dummyIndex;
            }
            columnObject[2] = WebClientUtil.createInstance(transformer);
            columnObject[3] = getRendererConfigProps(columnConfigDO);
            columnObject[4] = columnConfigDO;
            columnObject[5] = displayName;
        }
        return columnObject;
    }
    
    public List<Row> getColumnList() throws Exception {
        return (List)this.getCompiledData("TABLE_COLUMNS");
    }
    
    private void setNavigationProperties() throws Exception {
        if (!(this.tableModel instanceof TableNavigatorModel) || this.viewContext.isExportType()) {
            return;
        }
        final TableNavigatorModel tnModel = (TableNavigatorModel)this.tableModel;
        final Long navigConfigName = (Long)this.viewConfigDO.getFirstValue("ACTableViewConfig", "NAVIGATIONCONFIG");
        if (navigConfigName == null) {
            return;
        }
        this.navigConfig = NavigationConfig.createNavigationConfig(this.viewContext, tnModel, navigConfigName);
    }
    
    public static HashMap<String, String> getRendererConfigProps(final DataObject columnConfigDO) throws DataAccessException {
        HashMap<String, String> propHash = null;
        final Iterator<Row> iterator = columnConfigDO.getRows("ACRendererConfiguration");
        if (iterator != null) {
            propHash = new HashMap<String, String>();
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final String key = (String)row.get("PROPERTYNAME");
                final String val = (String)row.get("PROPERTYVALUE");
                if (val != null) {
                    propHash.put(key.intern(), val);
                }
            }
        }
        return propHash;
    }
    
    public static void updateTableWithColumns(final DataObject viewConfigDO) throws Exception {
        if (!viewConfigDO.containsTable("ACTableColumns")) {
            synchronized (viewConfigDO) {
                if (viewConfigDO.containsTable("ACTableColumns")) {
                    return;
                }
                final Long columnConfigName_NO = (Long)viewConfigDO.getFirstValue("ACTableViewConfig", 4);
                final DataObject configDO = getColumnConfigDO(viewConfigDO);
                final Iterator<Row> columnRows = configDO.getRows("ACColumnConfiguration");
                final Object viewName = viewConfigDO.getFirstValue("ACTableViewConfig", 1);
                final Row emberConfigRow = new Row("ACColumnConfiguration");
                emberConfigRow.set(1, (Object)columnConfigName_NO);
                while (columnRows.hasNext()) {
                    final Row currentRow = columnRows.next();
                    final Row acTableColumn = new Row("ACTableColumns");
                    acTableColumn.set(1, viewName);
                    final String columnAlias = (String)currentRow.get(3);
                    emberConfigRow.set(3, (Object)columnAlias);
                    final Row currentRowInEmberColConfig = configDO.findRow(emberConfigRow);
                    acTableColumn.set(3, (Object)columnAlias);
                    acTableColumn.set(2, currentRow.get(2));
                    acTableColumn.set(4, currentRow.get(4));
                    acTableColumn.set(5, currentRow.get(5));
                    if (currentRowInEmberColConfig != null) {
                        acTableColumn.set(6, currentRowInEmberColConfig.get(36));
                    }
                    Row maskingConfigRow = new Row("PIIRedactConfig");
                    maskingConfigRow.set(1, viewName);
                    maskingConfigRow.set(2, (Object)columnAlias);
                    maskingConfigRow = viewConfigDO.findRow(maskingConfigRow);
                    acTableColumn.set(7, (maskingConfigRow != null) ? maskingConfigRow.get(3) : "");
                    viewConfigDO.addRow(acTableColumn);
                }
            }
        }
        else {
            final DataObject configDO2 = getColumnConfigDO(viewConfigDO);
            final Object viewName2 = viewConfigDO.getFirstValue("ACTableViewConfig", 1);
            final Iterator<Row> columnRows2 = configDO2.getRows("ACColumnConfiguration", new Criteria(Column.getColumn("ACColumnConfiguration", "ISDYNAMIC"), (Object)true, 0));
            while (columnRows2.hasNext()) {
                final Row currentRow2 = columnRows2.next();
                final Row acTableColumn2 = new Row("ACTableColumns");
                acTableColumn2.set(1, viewName2);
                acTableColumn2.set(3, currentRow2.get(3));
                if (viewConfigDO.findRow(acTableColumn2) == null) {
                    acTableColumn2.set(2, currentRow2.get(2));
                    acTableColumn2.set(4, currentRow2.get(4));
                    acTableColumn2.set(5, (Object)false);
                    viewConfigDO.addRow(acTableColumn2);
                }
            }
        }
    }
    
    private static DataObject getColumnConfigDO(final DataObject viewConfigDO) throws Exception {
        return getColumnConfigDO(viewConfigDO.getFirstValue("ACTableViewConfig", 4), viewConfigDO);
    }
    
    public Row getEmptyTableMessageRow() throws DataAccessException {
        final Object msgId = this.getTableViewConfigRow().get("EMPTY_MESSAGE_ID");
        final Row emptyTableMessageRow = new Row("EmptyTableMessage");
        emptyTableMessageRow.set("EMPTY_MESSAGE_ID", msgId);
        final DataObject dob = LookUpUtil.getCachedPersistence().get("EmptyTableMessage", emptyTableMessageRow);
        final Row empty_message_row = dob.getRow("EmptyTableMessage");
        final Object menu_id = empty_message_row.get("MENU_ID");
        if (menu_id != null) {
            Row menuRow = new Row("Menu");
            menuRow.set("MENUID_NO", menu_id);
            final DataObject dobj = LookUpUtil.getCachedPersistence().get("Menu", menuRow);
            menuRow = dobj.getRow("Menu");
            this.menuid = (String)menuRow.get("MENUID");
        }
        return empty_message_row;
    }
    
    public String getMenuNameForEmptyTableMessage() throws DataAccessException {
        this.getEmptyTableMessageRow();
        return this.menuid;
    }
    
    public static DataObject getColumnConfigDO(final Object columnConfigName, final DataObject viewConfigDO) throws Exception {
        DataObject columnConfigDO = null;
        if (columnConfigName instanceof String) {
            columnConfigDO = DataUtils.getFromCache("ColumnConfiguration", "ACColumnConfigurationList", "NAME", columnConfigName);
        }
        else {
            columnConfigDO = DataUtils.getFromCache("ColumnConfiguration", "ACColumnConfigurationList", "NAME_NO", columnConfigName);
        }
        return handleRowSelection(columnConfigDO, viewConfigDO);
    }
    
    private static DataObject handleRowSelection(DataObject columnConfigDO, final DataObject viewConfigDO) throws Exception {
        final Long componentNameNo = (Long)viewConfigDO.getRow("ViewConfiguration").get(3);
        final boolean isCSRComp = componentNameNo != null && (boolean)WebViewAPI.getUIComponentConfig((Object)componentNameNo).getValue("WebUIComponent", "ISCSRCOMPONENT", (Criteria)null);
        if (isCSRComp) {
            final Criteria c1 = new Criteria(Column.getColumn("ACColumnConfiguration", "COLUMN_TYPE"), (Object)"rowSel", 0);
            final Row rowSelColumnConfig = columnConfigDO.getRow("ACColumnConfiguration", c1);
            if (rowSelColumnConfig != null) {
                final Row emberConfigRow = new Row("ACColumnConfiguration");
                emberConfigRow.set(1, rowSelColumnConfig.get(1));
                final String columnAlias = (String)rowSelColumnConfig.get(3);
                emberConfigRow.set(3, (Object)columnAlias);
                Row tempRow = columnConfigDO.findRow(emberConfigRow);
                if (tempRow != null) {
                    columnConfigDO = (DataObject)columnConfigDO.clone();
                    tempRow = columnConfigDO.findRow(emberConfigRow);
                    updateGivenRowSelColumn(columnConfigDO, tempRow);
                }
            }
            else {
                final String enableRowSel = (String)viewConfigDO.getRow("ACTableViewConfig").get(9);
                if (!enableRowSel.equals("NONE")) {
                    columnConfigDO = (DataObject)columnConfigDO.clone();
                    addNewRowSelectionColumn(columnConfigDO, enableRowSel);
                }
            }
        }
        return columnConfigDO;
    }
    
    private static void updateGivenRowSelColumn(final DataObject columnConfigDO, final Row tempRow) throws DataAccessException, JSONException {
        final Properties defaultRowSelColumnProps = getDefaultRowSelColumnProps();
        final String defaultProps = (defaultRowSelColumnProps != null) ? defaultRowSelColumnProps.getProperty("renderprops") : null;
        final String props = (String)tempRow.get("PROPERTIES");
        JSONObject properties;
        if (props != null) {
            if (defaultProps == null) {
                properties = new JSONObject(props);
                setMissingColumnRenderProps(properties);
            }
            else {
                final JSONObject defaultProperties = new JSONObject(defaultProps);
                properties = new JSONObject(props);
                setWithDefault(properties, defaultProperties, "isSortable", false);
                setWithDefault(properties, defaultProperties, "isResizable", false);
                setWithDefault(properties, defaultProperties, "borderEnabled", false);
                setWithDefault(properties, defaultProperties, "maxWidth", 50);
                setWithDefault(properties, defaultProperties, "textAlign", "text-align-center");
            }
        }
        else if (defaultProps == null) {
            properties = new JSONObject();
            setDefaultRenderingProperties(properties);
        }
        else {
            properties = new JSONObject(defaultProps);
            setMissingColumnRenderProps(properties);
        }
        tempRow.set("PROPERTIES", (Object)properties.toString());
        if (tempRow.get("ISEXPORTABLE") == null) {
            tempRow.set("ISEXPORTABLE", (Object)false);
        }
        if (tempRow.get("WIDTH") == null) {
            if (defaultRowSelColumnProps != null) {
                tempRow.set("WIDTH", (Object)Long.valueOf(defaultRowSelColumnProps.getProperty("width")));
            }
            else {
                tempRow.set("WIDTH", (Object)50L);
            }
        }
        if (tempRow.getChangedColumnIndex() != null) {
            columnConfigDO.updateRow(tempRow);
        }
    }
    
    private static void setWithDefault(final JSONObject properties, final JSONObject defaultProperties, final String key, final Object defaultVal) throws JSONException {
        if (!properties.has(key)) {
            if (!defaultProperties.has(key)) {
                properties.put(key, defaultVal);
            }
            else {
                properties.put(key, defaultProperties.get(key));
            }
        }
    }
    
    private static void setDefaultRenderingProperties(final JSONObject properties) throws JSONException {
        properties.put("isSortable", false);
        properties.put("isResizable", false);
        properties.put("borderEnabled", false);
        properties.put("maxWidth", 50);
        properties.put("textAlign", (Object)"text-align-center");
    }
    
    private static void setMissingColumnRenderProps(final JSONObject properties) throws JSONException {
        if (!properties.has("isSortable")) {
            properties.put("isSortable", false);
        }
        if (!properties.has("isResizable")) {
            properties.put("isResizable", false);
        }
        if (!properties.has("borderEnabled")) {
            properties.put("borderEnabled", false);
        }
        if (!properties.has("maxWidth")) {
            properties.put("maxWidth", 50);
        }
        if (!properties.has("textAlign")) {
            properties.put("textAlign", (Object)"text-align-center");
        }
    }
    
    private static Properties getDefaultRowSelColumnProps() {
        Properties defaultRowSelColumnProps = null;
        final File propFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "ClientComponents" + File.separator + "default-rowsel-column.props");
        if (propFile.exists()) {
            try {
                defaultRowSelColumnProps = new Properties();
                defaultRowSelColumnProps.load(new FileInputStream(propFile));
            }
            catch (final IOException e) {
                TableViewModel.log.fine(e.getMessage());
            }
        }
        return defaultRowSelColumnProps;
    }
    
    private static void addNewRowSelectionColumn(final DataObject columnConfigDO, final String colName) throws DataAccessException, JSONException {
        JSONObject properties = null;
        long defaultRowSelColumnWidth = 0L;
        final Properties defaultRowSelColumnProps = getDefaultRowSelColumnProps();
        if (defaultRowSelColumnProps != null) {
            if (defaultRowSelColumnProps.getProperty("renderprops") != null) {
                properties = new JSONObject(defaultRowSelColumnProps.getProperty("renderprops"));
                setMissingColumnRenderProps(properties);
            }
            if (defaultRowSelColumnProps.getProperty("width") != null) {
                try {
                    defaultRowSelColumnWidth = Long.valueOf(defaultRowSelColumnProps.getProperty("width"));
                }
                catch (final NumberFormatException nfe) {
                    nfe.printStackTrace();
                    TableViewModel.log.severe("The given width should be a long type");
                }
            }
        }
        if (properties == null) {
            properties = new JSONObject();
            setDefaultRenderingProperties(properties);
        }
        final Long configName = (Long)columnConfigDO.getRow("ACColumnConfiguration").get(1);
        final Row rowSelColumn = new Row("ACColumnConfiguration");
        rowSelColumn.set(32, (Object)"rowSel");
        rowSelColumn.set(3, (Object)colName);
        rowSelColumn.set(2, (Object)(-999));
        rowSelColumn.set(1, (Object)configName);
        rowSelColumn.set(9, (Object)false);
        rowSelColumn.set(6, (Object)false);
        rowSelColumn.set(5, (Object)true);
        if (colName.equals("CHECKBOX")) {
            rowSelColumn.set(4, (Object)"rowSel");
        }
        else {
            rowSelColumn.set(4, (Object)"");
        }
        rowSelColumn.set("ISCHOOSABLE", (Object)false);
        rowSelColumn.set("ISEXPORTABLE", (Object)false);
        rowSelColumn.set("WIDTH", (Object)((defaultRowSelColumnWidth != 0L) ? defaultRowSelColumnWidth : 50L));
        rowSelColumn.set("PROPERTIES", (Object)properties.toString());
        columnConfigDO.addRow(rowSelColumn);
    }
    
    private void setTableProperties() {
        this.columnNameVsIndex = new HashMap<String, Integer>();
        final TableModel model = (TableModel)this.tableModel;
        for (int count = model.getColumnCount(), i = 0; i < count; ++i) {
            final String columnName = model.getColumnName(i);
            this.columnNameVsIndex.put(columnName, i);
        }
        if (model instanceof TableNavigatorModel) {
            final SortColumn[] sortColumns = ((TableNavigatorModel)model).getModelSortedColumns();
            if (sortColumns != null && sortColumns.length > 0) {
                this.sortedColumn = model.getColumnName(sortColumns[0].getColumnIndex());
                this.sortedOrder = sortColumns[0].isAscending();
            }
        }
    }
    
    public RowTransformer getRowTransformer() {
        return this.rowTrans;
    }
    
    private boolean isRoleMatched(final String columnName) throws Exception {
        final Long columnConfigName_NO = (Long)this.viewConfigDO.getFirstValue("ACTableViewConfig", "COLUMNCONFIGLIST");
        final DataObject configDO = getColumnConfigDO(columnConfigName_NO, this.viewConfigDO);
        final Row row = configDO.getRow("ACColumnConfiguration", new Criteria(new Column("ACColumnConfiguration", "COLUMNALIAS"), (Object)columnName, 0));
        final String rolename = (String)row.get("ROLENAME");
        return rolename == null || rolename.isEmpty() || (WebClientUtil.isNewAuthPropertySet() ? AuthenticationUtil.isUserExists(rolename) : WebClientUtil.getAuthImpl().userExists(rolename));
    }
    
    static {
        TableViewModel.log = Logger.getLogger(TableViewModel.class.getName());
    }
}
