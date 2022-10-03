package javax.swing.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.CellEditor;

public interface TableCellEditor extends CellEditor
{
    Component getTableCellEditorComponent(final JTable p0, final Object p1, final boolean p2, final int p3, final int p4);
}
