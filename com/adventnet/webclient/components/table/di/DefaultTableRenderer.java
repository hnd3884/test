package com.adventnet.webclient.components.table.di;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.webclient.components.table.ViewColumn;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;
import com.adventnet.webclient.components.table.TableRenderer;

public class DefaultTableRenderer extends TableRenderer
{
    public Properties renderCell(final TableNavigatorModel tableModel, final int rowIndex, final int columnIndex, final ViewColumn viewColumn) {
        Properties cellProperties = null;
        final String headerName = tableModel.getColumnName(columnIndex);
        final Object value = tableModel.getValueAt(rowIndex, columnIndex);
        if (cellProperties == null) {
            cellProperties = new Properties();
        }
        if (value == null) {
            ((Hashtable<String, String>)cellProperties).put("VALUE", "");
        }
        else {
            ((Hashtable<String, Object>)cellProperties).put("VALUE", value);
        }
        return cellProperties;
    }
    
    public Properties renderHeader(final ViewColumn viewColumn) {
        Object transformedHeader = null;
        final String columnName = (String)(transformedHeader = viewColumn.getColumnName());
        Properties headerProperties = null;
        if (headerProperties == null) {
            headerProperties = new Properties();
        }
        ((Hashtable<String, Object>)headerProperties).put("HEADER", transformedHeader);
        return headerProperties;
    }
}
