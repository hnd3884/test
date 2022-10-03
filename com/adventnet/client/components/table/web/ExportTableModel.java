package com.adventnet.client.components.table.web;

import java.util.Properties;
import javax.swing.event.TableModelListener;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.ds.query.DataSet;
import com.adventnet.beans.xtable.SortColumn;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.swing.table.TableModel;

public class ExportTableModel implements TableModel
{
    private static Logger out;
    protected HashMap totalSum;
    protected HashMap viewSum;
    protected SortColumn[] modelSortColumns;
    private String encryptCol;
    long totalRows;
    DataSet ds;
    
    public ExportTableModel() {
        this.totalSum = new HashMap();
        this.viewSum = new HashMap();
        this.encryptCol = null;
        this.totalRows = 0L;
        this.ds = null;
    }
    
    public ExportTableModel(final DataSet ds) {
        this.totalSum = new HashMap();
        this.viewSum = new HashMap();
        this.encryptCol = null;
        this.totalRows = 0L;
        this.ds = null;
        this.ds = ds;
    }
    
    public void updateModel(final DataSet ds) {
        this.ds = ds;
    }
    
    @Override
    public int getRowCount() {
        return 0;
    }
    
    @Override
    public int getColumnCount() {
        try {
            return this.ds.getColumnCount();
        }
        catch (final Exception e) {
            ExportTableModel.out.warning("Problem occured while fetching number of columns in the dataset");
            ExportTableModel.out.warning(e.getMessage());
            return 0;
        }
    }
    
    @Override
    public String getColumnName(final int columnIndex) {
        try {
            return this.ds.getColumnName(columnIndex + 1);
        }
        catch (final Exception e) {
            ExportTableModel.out.warning("Problem occured while fetching column name for the given column index");
            ExportTableModel.out.warning(e.getMessage());
            return "";
        }
    }
    
    @Override
    public Class getColumnClass(final int columnIndex) {
        try {
            final String className = this.ds.getColumnClassName(columnIndex + 1);
            return Class.forName(className);
        }
        catch (final Exception e) {
            ExportTableModel.out.warning("Problem occured while fetching column class instance for the given column index");
            ExportTableModel.out.warning(e.getMessage());
            return String.class;
        }
    }
    
    public String getColumnDataType(final int columnIndex) {
        try {
            return this.getSQLTypeAsString(this.ds.getColumnType(columnIndex + 1));
        }
        catch (final Exception e) {
            ExportTableModel.out.warning("Problem occured while fetching datatype for the given column index");
            ExportTableModel.out.warning(e.getMessage());
            return "CHAR";
        }
    }
    
    private String getSQLTypeAsString(final int sqlTypeVal) throws IllegalArgumentException {
        switch (sqlTypeVal) {
            case 1:
            case 12: {
                return "CHAR";
            }
            case -6: {
                return "TINYINT";
            }
            case 2:
            case 4:
            case 5: {
                return "INTEGER";
            }
            case -5: {
                return "BIGINT";
            }
            case -7:
            case 16: {
                return "BOOLEAN";
            }
            case 6:
            case 7: {
                return "FLOAT";
            }
            case 3: {
                return "DECIMAL";
            }
            case 8: {
                return "DOUBLE";
            }
            case 91: {
                return "DATE";
            }
            case 92: {
                return "TIME";
            }
            case 93: {
                return "TIMESTAMP";
            }
            case 2004: {
                return "BLOB";
            }
            default: {
                throw new IllegalArgumentException("Invalid SQL type : " + sqlTypeVal);
            }
        }
    }
    
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        throw new UnsupportedOperationException("isCellEditable method not supported");
    }
    
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        Object value = null;
        try {
            value = this.ds.getValue(columnIndex + 1);
            if (this.encryptCol != null) {
                final String[] columnList = this.encryptCol.split(",");
                int i = 0;
                while (i < columnList.length) {
                    if (columnList[i].equals(this.getColumnName(columnIndex))) {
                        if (WebClientUtil.isNewAuthPropertySet()) {
                            return CryptoUtil.encrypt((String)value);
                        }
                        return WebClientUtil.getAuthImpl().encrypt(value);
                    }
                    else {
                        ++i;
                    }
                }
            }
        }
        catch (final Exception ex) {}
        return value;
    }
    
    public void setEncryption(final String colList) {
        this.encryptCol = colList;
    }
    
    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
    }
    
    @Override
    public void addTableModelListener(final TableModelListener l) {
    }
    
    @Override
    public void removeTableModelListener(final TableModelListener l) {
        throw new UnsupportedOperationException("removeTableModelListener method not supported");
    }
    
    public boolean moveNextRow() throws Exception {
        try {
            if (this.ds.next()) {
                return true;
            }
            if (this.ds != null) {
                this.ds.close();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (this.ds != null) {
                this.ds.close();
            }
        }
        return false;
    }
    
    public void setModelSortColumns(final SortColumn[] modelSortColumns) {
        this.modelSortColumns = modelSortColumns;
    }
    
    public HashMap getTotalSumMap() {
        return this.totalSum;
    }
    
    public void setTotalSumMap(final HashMap map) {
        this.totalSum = map;
    }
    
    public HashMap getViewSumMap() {
        return this.viewSum;
    }
    
    public void setViewSumMap(final HashMap map) {
        this.viewSum = map;
    }
    
    public void setPIIColumnConfig(final Properties maskingConfigMap) {
        ExportTableModel.out.warning("Redaction is not supported in ExportTableModel");
    }
    
    public Properties getPIIColumnConfig() {
        ExportTableModel.out.warning("Redaction is not supported in ExportTableModel");
        return null;
    }
    
    static {
        ExportTableModel.out = Logger.getLogger(ExportTableModel.class.getName());
    }
}
