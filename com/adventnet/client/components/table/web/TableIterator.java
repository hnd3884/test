package com.adventnet.client.components.table.web;

import java.util.Iterator;
import com.adventnet.client.view.web.WebViewModel;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import com.adventnet.client.components.web.TransformerContext;
import java.util.ArrayList;
import com.adventnet.client.components.rangenavigator.web.NavigationConfig;
import com.adventnet.client.view.web.ViewContext;
import java.util.Map;
import javax.swing.table.TableModel;

public class TableIterator implements TableConstants
{
    private TableModel tableModel;
    private TableTransformerContext transContext;
    private TableViewModel viewModel;
    private Object[] viewColumns;
    private Map colToObjMap;
    private int currentRow;
    private long totalRowCount;
    private int currentColumn;
    private int totalColumns;
    Object[] viewColumn;
    boolean nextFlag;
    boolean hasNextFlag;
    
    public TableIterator(final ViewContext viewCtx) {
        this.tableModel = null;
        this.transContext = null;
        this.currentRow = -1;
        this.totalRowCount = 0L;
        this.currentColumn = -1;
        this.nextFlag = false;
        this.hasNextFlag = false;
        this.viewModel = (TableViewModel)viewCtx.getViewModel();
        this.tableModel = (TableModel)this.viewModel.getTableModel();
        this.transContext = this.viewModel.getTableTransformerContext();
        if (this.viewModel.getNavigationConfig() != null && "true".equals(NavigationConfig.getNocount(viewCtx)) && this.tableModel.getRowCount() > this.viewModel.getNavigationConfig().getPageLength()) {
            this.totalRowCount = this.tableModel.getRowCount() - 1;
        }
        else {
            this.totalRowCount = this.tableModel.getRowCount();
        }
        this.viewColumns = this.viewModel.getViewColumns();
        try {
            final ArrayList validColumns = new ArrayList();
            for (int i = 0; i < this.viewColumns.length; ++i) {
                this.setCurrentColumn(i);
                final Object[] colProps = (Object[])this.viewColumns[this.currentColumn];
                final ColumnTransformer transformer = (ColumnTransformer)colProps[2];
                if (transformer.canRenderColumn(this.transContext)) {
                    validColumns.add(colProps);
                }
            }
            this.viewColumns = validColumns.toArray();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        this.totalColumns = this.viewColumns.length;
        this.reset();
    }
    
    public void reset() {
        this.currentRow = -1;
        this.currentColumn = -1;
    }
    
    public boolean hasNextRow() {
        if (this.tableModel instanceof ExportTableModel) {
            try {
                this.nextFlag = ((ExportTableModel)this.tableModel).moveNextRow();
                this.hasNextFlag = true;
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            return this.nextFlag;
        }
        return this.currentRow + 1 < this.totalRowCount;
    }
    
    public boolean nextRow() {
        if (this.tableModel instanceof ExportTableModel) {
            try {
                if (!this.hasNextFlag) {
                    this.nextFlag = ((ExportTableModel)this.tableModel).moveNextRow();
                }
                this.hasNextFlag = false;
                return this.nextFlag;
            }
            catch (final Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        if (this.currentRow + 1 < this.totalRowCount) {
            this.setCurrentRow(this.currentRow + 1);
            return true;
        }
        return false;
    }
    
    public boolean hasNextColumn() {
        return this.currentColumn + 1 < this.totalColumns;
    }
    
    public boolean isLastColumn() {
        return !this.hasNextColumn();
    }
    
    public boolean nextColumn() {
        try {
            if (this.hasNextColumn()) {
                this.setCurrentColumn(this.currentColumn + 1);
                return true;
            }
            return false;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void setCurrentColumn(final int colIndex) {
        this.currentColumn = colIndex;
        if (this.currentColumn > -1) {
            this.viewColumn = (Object[])this.viewColumns[this.currentColumn];
            this.transContext.setRendererConfigProps((HashMap<String, String>)this.viewColumn[3]);
            this.transContext.setViewIndexForCol(this.currentColumn);
            this.transContext.setColumnIndex((int)this.viewColumn[1]);
            try {
                this.transContext.setColumnConfiguration((DataObject)this.viewColumn[4]);
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
            this.transContext.setDisplayName((String)this.viewColumn[5]);
        }
    }
    
    public int getCurrentColumn() {
        return this.currentColumn;
    }
    
    public void setCurrentRow(final int rowIndex) {
        this.currentRow = rowIndex;
        if (this.currentRow > -1) {
            this.transContext.setRowIndex(this.currentRow);
        }
    }
    
    public int getCurrentRow() {
        return this.currentRow;
    }
    
    public Object[] getCurrentViewColumn() {
        return this.viewColumn;
    }
    
    public void initTransCtxForCurrentCell(final String type) throws Exception {
        final ColumnTransformer transformer = (ColumnTransformer)this.viewColumn[2];
        final TableViewModel viewmodel = (TableViewModel)this.transContext.getViewContext().getViewModel();
        final WebViewModel model = this.transContext.getViewContext().getModel();
        final boolean sumUsed = model.getFeatureValue("SUMCOLS") != null || model.getFeatureValue("VIEWSUMCOLS") != null;
        if (type.equals("Cell")) {
            if (this.transContext.getRowIndex() == 0) {
                transformer.initCellRendering(this.transContext);
            }
            if (sumUsed && this.transContext.getRowIndex() == 0 && viewmodel.getTableModel() instanceof TableDatasetModel) {
                this.formatSum(this.transContext, transformer);
            }
            try {
                transformer.renderCell(this.transContext);
            }
            catch (final Exception e) {
                e.printStackTrace();
                this.transContext.getRenderedAttributes().put("VALUE", "[CELL Rendering Exception]");
            }
        }
        else {
            try {
                transformer.renderHeader(this.transContext);
            }
            catch (final Exception e) {
                e.printStackTrace();
                this.transContext.getRenderedAttributes().put("VALUE", "[HEADER Rendering Exception]");
            }
        }
    }
    
    public void setCurrentColumn(final String colName) {
        if (this.colToObjMap == null) {
            this.colToObjMap = new HashMap();
            for (int i = 0; i < this.viewColumns.length; ++i) {
                this.colToObjMap.put(((Object[])this.viewColumns[i])[0], new Integer(i));
            }
        }
        this.setCurrentColumn(this.colToObjMap.get(colName));
    }
    
    public ColumnTransformer getColumnTransformer() {
        return (ColumnTransformer)this.viewColumn[2];
    }
    
    public void formatSum(final TableTransformerContext context, final ColumnTransformer transformer) {
        final TableViewModel viewmodel = (TableViewModel)this.transContext.getViewContext().getViewModel();
        final WebViewModel model = this.transContext.getViewContext().getModel();
        final String columnAlias = this.transContext.getColumnConfigRow().get("COLUMNALIAS").toString();
        HashMap map = null;
        boolean flag = true;
        if (model.getFeatureValue("VIEWSUMCOLS") != null) {
            map = ((TableDatasetModel)viewmodel.getTableModel()).getViewSumMap();
            flag = false;
            if (map.get(columnAlias) == null) {
                return;
            }
            this.formatMap(map, transformer, flag);
        }
        if (model.getFeatureValue("SUMCOLS") != null) {
            map = ((TableDatasetModel)viewmodel.getTableModel()).getTotalSumMap();
            flag = true;
            if (map.get(columnAlias) == null) {
                return;
            }
            this.formatMap(map, transformer, flag);
        }
    }
    
    public void formatMap(final Map map1, final ColumnTransformer transformer, final boolean flag) {
        final Map map2 = new HashMap(map1);
        String keys = "";
        if (map2.get("isformatted") != null) {
            keys = map2.get("isformatted").toString();
        }
        for (final Object key : map2.keySet()) {
            if ("isformatted".equals(key)) {
                continue;
            }
            String formattedSum;
            final String sum = formattedSum = map2.get(key).toString();
            try {
                if (!keys.contains(key.toString())) {
                    formattedSum = transformer.formatSumValue(this.transContext.getViewContext().getUniqueId(), key.toString(), sum, flag);
                    keys = keys + "," + key;
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            map1.put(key, formattedSum);
            map1.put("isformatted", keys);
        }
    }
    
    public int getTotalColumns() {
        return this.totalColumns;
    }
}
