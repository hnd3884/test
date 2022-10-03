package com.adventnet.client.components.table.web;

import java.util.Iterator;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.model.table.CVTableModel;
import com.adventnet.persistence.DataAccessException;
import java.util.Properties;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import java.util.Map;
import java.util.HashMap;
import javax.swing.table.TableModel;
import com.adventnet.client.components.web.DefaultTransformerContext;

public class TableTransformerContext extends DefaultTransformerContext
{
    private TableModel tableModel;
    private TableViewModel tableViewModel;
    private HashMap<String, Integer> columnNameToIndexMapping;
    private HashMap<String, Class<?>> columnNameToTypeMapping;
    private HashMap<String, String> columnNameToSQLTypeMapping;
    private HashMap<String, String> columnNameToTransformedTypeMapping;
    protected Map<String, Long> columnAliasVsWidth;
    protected Map<String, Boolean> columnAliasVsCanAutoReize;
    protected HashMap<String, String> selectCriteria;
    protected HashMap<String, String> selectComboCriteria;
    protected String searchValue;
    protected String searchComboValue;
    protected String configuredColStyle;
    protected boolean enableRowHover;
    protected boolean isSortEnabled;
    protected boolean isSearchEnabled;
    protected boolean isAdvancedSearch;
    protected boolean hlsearch;
    protected boolean isSliderTable;
    protected String headerCss;
    protected int searchComparator;
    protected Map<String, Integer> selectComparators;
    private static Logger out;
    
    public TableTransformerContext(final TableViewModel tableViewModel, final ViewContext viewContext) {
        super(viewContext);
        this.tableModel = null;
        this.tableViewModel = null;
        this.columnNameToIndexMapping = null;
        this.columnNameToTypeMapping = null;
        this.columnNameToSQLTypeMapping = null;
        this.columnNameToTransformedTypeMapping = null;
        this.selectCriteria = null;
        this.selectComboCriteria = null;
        this.searchValue = "";
        this.searchComboValue = "";
        this.configuredColStyle = "";
        this.enableRowHover = false;
        this.isSortEnabled = false;
        this.isSearchEnabled = false;
        this.isAdvancedSearch = false;
        this.hlsearch = false;
        this.isSliderTable = false;
        this.headerCss = "";
        this.selectComparators = null;
        this.tableModel = (TableModel)tableViewModel.getTableModel();
        TableTransformerContext.out.log(Level.FINER, "TableModel instance is of type {0} ", this.tableModel.getClass().getName());
        this.tableViewModel = tableViewModel;
        final DataObject dataObject = viewContext.getModel().getViewConfiguration();
        try {
            this.enableRowHover = (boolean)dataObject.getFirstValue("ACTableViewConfig", "ENABLEROWHOVER");
            this.hlsearch = (boolean)dataObject.getFirstValue("ACTableViewConfig", "HLSEARCH");
            this.isAdvancedSearch = (boolean)dataObject.getFirstValue("ACTableViewConfig", "ISADVANCEDSEARCH");
            this.isSliderTable = (boolean)dataObject.getFirstValue("ACTableViewConfig", "ISSCROLLENABLED");
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        this.columnNameToIndexMapping = new HashMap<String, Integer>();
        this.columnNameToTypeMapping = new HashMap<String, Class<?>>();
        this.columnNameToSQLTypeMapping = new HashMap<String, String>();
        this.columnNameToTransformedTypeMapping = new HashMap<String, String>();
        this.setColumnNameToIndexMapping();
        this.setColumnNameToTypeMapping();
        this.setColumnNameToSQLTypeMapping();
        this.setColumnNameToTransformedTypeMapping();
        this.setSelectCriteria();
        this.columnAliasVsWidth = new HashMap<String, Long>();
        this.columnAliasVsCanAutoReize = new HashMap<String, Boolean>();
        this.setColumnWidthAndCanAutoResize();
    }
    
    public boolean isExportable() throws DataAccessException {
        boolean isExportable = true;
        if (this.columnConfiguration.containsTable("ACColumnConfiguration")) {
            isExportable = (boolean)this.columnConfiguration.getValue("ACColumnConfiguration", 37, (Row)null);
        }
        if (isExportable && this.tableModel instanceof ExportTableViewModel) {
            final Properties redactConfig = ((ExportTableModel)this.tableModel).getPIIColumnConfig();
            isExportable = !"SKIP".equals(redactConfig.getProperty(this.getPropertyName()));
        }
        return isExportable;
    }
    
    public void setSelectCriteria() {
        this.selectCriteria = new HashMap<String, String>();
        this.selectComboCriteria = new HashMap<String, String>();
        final String searchColumns = (String)this.viewContext.getStateOrURLStateParameter("SEARCH_COLUMN");
        final String searchValues = (String)this.viewContext.getStateOrURLStateParameter("SEARCH_VALUE");
        if (searchColumns != null) {
            final String[] columnNames = searchColumns.split(",");
            String[] columnValues = null;
            if (searchValues != null) {
                columnValues = searchValues.split(",");
            }
            for (int colSize = columnNames.length, count = 0; count < colSize; ++count) {
                if ((columnValues != null && columnNames.length == columnValues.length) || (columnValues != null && count <= columnValues.length - 1)) {
                    if (columnValues[count].equals("")) {
                        this.selectCriteria.put(columnNames[count], null);
                    }
                    else {
                        this.selectCriteria.put(columnNames[count], columnValues[count]);
                    }
                }
                else {
                    this.selectCriteria.put(columnNames[count], null);
                }
            }
        }
        final String searchComboValues = (String)this.viewContext.getStateOrURLStateParameter("SEARCHVAL_COMB");
        String[] columnComboValues = null;
        if (searchColumns != null) {
            final String[] columnNames2 = searchColumns.split(",");
            if (searchComboValues != null) {
                columnComboValues = searchComboValues.split(",");
            }
            for (int colSize2 = columnNames2.length, count2 = 0; count2 < colSize2; ++count2) {
                if (this.selectComboCriteria != null && columnComboValues != null) {
                    this.selectComboCriteria.put(columnNames2[count2], columnComboValues[count2]);
                }
            }
        }
    }
    
    @Override
    public Object getDataModel() {
        return this.tableModel;
    }
    
    @Override
    public String getPropertyName() {
        if (this.columnIndex >= 0) {
            return this.tableModel.getColumnName(this.columnIndex);
        }
        return (String)this.getColumnConfigRow().get("COLUMNALIAS");
    }
    
    @Override
    public Object getPropertyValue() {
        if (this.columnIndex >= 0) {
            return this.tableModel.getValueAt(this.rowIndex, this.columnIndex);
        }
        return null;
    }
    
    @Override
    public Object getAssociatedPropertyValue(final String propertyName) {
        final Integer index = this.columnNameToIndexMapping.get(propertyName);
        if (index == null) {
            return null;
        }
        return this.tableModel.getValueAt(this.rowIndex, index);
    }
    
    @Override
    public Object getAssociatedPropertyValue(final String propertyName, final boolean encrypted) {
        final Integer index = this.columnNameToIndexMapping.get(propertyName);
        if (index == null) {
            return null;
        }
        if (!(this.tableModel.getValueAt(this.rowIndex, index) instanceof HashMap)) {
            return this.tableModel.getValueAt(this.rowIndex, index);
        }
        final HashMap<String, Object> map = (HashMap<String, Object>)this.tableModel.getValueAt(this.rowIndex, index);
        if (encrypted) {
            return map.get("encrypted");
        }
        return map.get("actual");
    }
    
    @Override
    public Object getPropertyIndex(final String propertyName) {
        return this.columnNameToIndexMapping.get(propertyName);
    }
    
    @Override
    public Object getAssociatedIndexedValue(final Object columnIndex) {
        return this.tableModel.getValueAt(this.rowIndex, (int)columnIndex);
    }
    
    public Class<?> getDataType() {
        return this.columnNameToTypeMapping.get(this.getPropertyName());
    }
    
    public String getSQLDataType() {
        final String defaultSQLDataType = "CHAR";
        try {
            final String actualSQLType = this.columnNameToSQLTypeMapping.get(this.getPropertyName());
            return (actualSQLType != null) ? actualSQLType : defaultSQLDataType;
        }
        catch (final Exception e) {
            TableTransformerContext.out.warning("Problem occurred while getting SQL type of column");
            e.printStackTrace();
            return defaultSQLDataType;
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        this.searchValue = "";
        this.searchComboValue = null;
        try {
            this.isSortEnabled = (boolean)this.configRow.get("SORTENABLED");
            this.isSearchEnabled = (boolean)this.configRow.get("SEARCHENABLED");
            this.configuredColStyle = (String)this.configRow.get("CSSCLASS");
            this.headerCss = (String)this.configRow.get("HEADERCSS");
            if (this.isSearchEnabled) {
                final String propertyName = this.getPropertyName();
                if (this.selectCriteria.containsKey(propertyName) || this.selectComboCriteria.containsKey(propertyName)) {
                    this.searchValue = this.selectCriteria.get(propertyName);
                    if (this.selectComboCriteria != null) {
                        this.searchComboValue = this.selectComboCriteria.get(propertyName);
                    }
                    final StringBuffer charBuffer = new StringBuffer();
                    if (this.searchValue != null) {
                        for (int length = 0; length < this.searchValue.length(); ++length) {
                            final char ch = this.searchValue.charAt(length);
                            if (ch == '\'') {
                                charBuffer.append("&#39;");
                            }
                            else {
                                charBuffer.append(ch);
                            }
                        }
                        this.searchValue = charBuffer.toString();
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    @Deprecated
    public String getColumnCSS() {
        return this.getColumnCSS(false);
    }
    
    @Deprecated
    public String getColumnCSS(final boolean addAlternateRow) {
        final String colCSS = this.renderedProperties.get("CSSCLASS");
        String css = "";
        if (colCSS != null) {
            css = colCSS;
        }
        else if (this.configuredColStyle != null) {
            css = this.configuredColStyle;
        }
        if (addAlternateRow && !this.isSliderTable) {
            if (this.rowIndex % 2 == 0) {
                css += " evenRow";
            }
            else {
                css += " oddRow";
            }
        }
        else if (addAlternateRow) {
            if (this.rowIndex % 2 == 0) {
                css += " evenRowSliderTable";
            }
            else {
                css += " oddRowSliderTable";
            }
        }
        if (colCSS == null && this.configuredColStyle == null) {
            final String propertyName = this.getPropertyName();
            if (propertyName != null && propertyName.equals(this.tableViewModel.getSortedColumn())) {
                css += " sortedColumn";
            }
        }
        return css;
    }
    
    public boolean isSortEnabled() {
        return this.isSortEnabled;
    }
    
    public boolean isSearchEnabled() {
        return this.isSearchEnabled;
    }
    
    public boolean isAdvancedSearch() {
        return this.isAdvancedSearch;
    }
    
    public boolean isSliderTable() {
        return this.isSliderTable;
    }
    
    public String getHeaderColumnClass() {
        final String propertyName = this.getPropertyName();
        if (propertyName != null && propertyName.equals(this.tableViewModel.getSortedColumn())) {
            return "sortedTableHeader";
        }
        return "tableHeader";
    }
    
    private void setColumnNameToIndexMapping() {
        for (int columnCount = this.tableModel.getColumnCount(), count = 0; count < columnCount; ++count) {
            final String columnName = this.tableModel.getColumnName(count);
            this.columnNameToIndexMapping.put(columnName, new Integer(count));
        }
    }
    
    private void setColumnNameToTypeMapping() {
        for (int columnCount = this.tableModel.getColumnCount(), count = 0; count < columnCount; ++count) {
            this.columnNameToTypeMapping.put(this.tableModel.getColumnName(count), this.tableModel.getColumnClass(count));
        }
    }
    
    private void setColumnNameToSQLTypeMapping() {
        for (int columnCount = this.tableModel.getColumnCount(), count = 0; count < columnCount; ++count) {
            if (this.tableModel instanceof TableDatasetModel) {
                this.columnNameToSQLTypeMapping.put(this.tableModel.getColumnName(count), ((TableDatasetModel)this.tableModel).getColumnDataType(count));
            }
            if (this.tableModel instanceof CVTableModel) {
                this.columnNameToSQLTypeMapping.put(this.tableModel.getColumnName(count), ((CVTableModel)this.tableModel).getColSQLClass(count));
            }
            if (this.tableModel instanceof ExportTableModel) {
                this.columnNameToSQLTypeMapping.put(this.tableModel.getColumnName(count), ((ExportTableModel)this.tableModel).getColumnDataType(count));
            }
        }
    }
    
    @Deprecated
    public String getSortButtonClass() {
        final String order = (String)this.viewContext.getStateOrURLStateParameter("_SO");
        if (order != null && order.equals("D")) {
            return "sortButtonDESC";
        }
        return "sortButtonASC";
    }
    
    public String getSearchValue() {
        return this.searchValue;
    }
    
    public String getSearchComboValue() {
        return this.searchComboValue;
    }
    
    public boolean isEnableRowHover() {
        return this.enableRowHover;
    }
    
    public boolean isHlSearch() {
        return this.hlsearch;
    }
    
    public String getHeaderCssClass() {
        return this.headerCss;
    }
    
    public boolean isSearchValuePresent() {
        return !this.selectCriteria.isEmpty();
    }
    
    private void setColumnWidthAndCanAutoResize() {
        final DataObject viewObject = this.getViewContext().getModel().getViewConfiguration();
        try {
            final DataObject columnConfigDO = TableViewModel.getColumnConfigDO(viewObject.getValue("ACTableViewConfig", 4, (Criteria)null), viewObject);
            final Iterator<Row> acColConfigRows = columnConfigDO.getRows("ACColumnConfiguration");
            while (acColConfigRows.hasNext()) {
                final String columnAlias = (String)acColConfigRows.next().get(3);
                Long width = (Long)viewObject.getValue("ACTableColumns", 6, new Criteria(Column.getColumn("ACTableColumns", "COLUMNALIAS"), (Object)columnAlias, 0));
                this.columnAliasVsCanAutoReize.put(columnAlias, width == null);
                if (width == null) {
                    width = (Long)columnConfigDO.getValue("ACColumnConfiguration", 36, new Criteria(Column.getColumn("ACColumnConfiguration", "COLUMNALIAS"), (Object)columnAlias, 0));
                }
                this.columnAliasVsWidth.put(columnAlias, width);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public long getColumnWidth() {
        final Long width = this.columnAliasVsWidth.get(this.getPropertyName());
        return (width != null) ? width : -1L;
    }
    
    public boolean canAutoResize() {
        final Boolean bool = this.columnAliasVsCanAutoReize.get(this.getPropertyName());
        return bool == null || bool;
    }
    
    private void setColumnNameToTransformedTypeMapping() {
        final DataObject viewObject = this.getViewContext().getModel().getViewConfiguration();
        try {
            final DataObject columnConfigDO = TableViewModel.getColumnConfigDO(viewObject.getValue("ACTableViewConfig", 4, (Criteria)null), viewObject);
            final Iterator<Row> acColConfigRows = columnConfigDO.getRows("ACColumnConfiguration");
            while (acColConfigRows.hasNext()) {
                final Row columnConfigRow = acColConfigRows.next();
                String colType;
                if (columnConfigRow.get("COLUMN_TYPE") != null) {
                    colType = (String)columnConfigRow.get("COLUMN_TYPE");
                }
                else {
                    colType = this.columnNameToSQLTypeMapping.get(columnConfigRow.get("COLUMNALIAS"));
                    if (!"CHAR".equals(colType) && (columnConfigRow.get("PREFIX_TEXT") != null || columnConfigRow.get("SUFFIX_TEXT") != null || columnConfigRow.get("REPLACE_TEXT") != null || columnConfigRow.get("STATIC_TEXT") != null || columnConfigRow.get("DEFAULT_TEXT") != null)) {
                        colType = "CHAR";
                    }
                    if (columnConfigRow.get("DATE_FORMAT") != null) {
                        colType = "DATETIME";
                    }
                }
                this.columnNameToTransformedTypeMapping.put((String)columnConfigRow.get("COLUMNALIAS"), colType);
            }
        }
        catch (final Exception e) {
            TableTransformerContext.out.severe("Exception occurred while manipulating the transformed type of the column");
            throw new RuntimeException(e);
        }
    }
    
    public boolean isPII() {
        return this.tableModel instanceof ExportTableViewModel && ((ExportTableViewModel)this.tableModel).getPIIColumnConfig().containsKey(this.getPropertyName());
    }
    
    public String getTransformedType() {
        return this.columnNameToTransformedTypeMapping.get(this.getPropertyName());
    }
    
    static {
        TableTransformerContext.out = Logger.getLogger(TableTransformerContext.class.getName());
    }
}
