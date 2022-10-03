package org.jfree.ui;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.JTable;

public class SortableTable extends JTable
{
    private SortableTableHeaderListener headerListener;
    
    public SortableTable(final SortableTableModel model) {
        super(model);
        final SortButtonRenderer renderer = new SortButtonRenderer();
        final TableColumnModel cm = this.getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); ++i) {
            cm.getColumn(i).setHeaderRenderer(renderer);
        }
        final JTableHeader header = this.getTableHeader();
        header.addMouseListener(this.headerListener = new SortableTableHeaderListener(model, renderer));
        header.addMouseMotionListener(this.headerListener);
        model.sortByColumn(0, true);
    }
    
    public void setSortableModel(final SortableTableModel model) {
        super.setModel(model);
        this.headerListener.setTableModel(model);
        final SortButtonRenderer renderer = new SortButtonRenderer();
        final TableColumnModel cm = this.getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); ++i) {
            cm.getColumn(i).setHeaderRenderer(renderer);
        }
        model.sortByColumn(0, true);
    }
}
