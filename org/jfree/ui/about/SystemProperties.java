package org.jfree.ui.about;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.jfree.ui.SortableTableModel;
import org.jfree.ui.SortableTable;

public class SystemProperties
{
    private SystemProperties() {
    }
    
    public static SortableTable createSystemPropertiesTable() {
        final SystemPropertiesTableModel properties = new SystemPropertiesTableModel();
        final SortableTable table = new SortableTable(properties);
        final TableColumnModel model = table.getColumnModel();
        TableColumn column = model.getColumn(0);
        column.setPreferredWidth(200);
        column = model.getColumn(1);
        column.setPreferredWidth(350);
        table.setAutoResizeMode(2);
        return table;
    }
}
