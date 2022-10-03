package javax.swing.table;

import java.awt.Component;
import javax.swing.JTable;

public interface TableCellRenderer
{
    Component getTableCellRendererComponent(final JTable p0, final Object p1, final boolean p2, final boolean p3, final int p4, final int p5);
}
