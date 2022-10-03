package org.jfree.ui;

import javax.swing.table.JTableHeader;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;

public class SortableTableHeaderListener implements MouseListener, MouseMotionListener
{
    private SortableTableModel model;
    private SortButtonRenderer renderer;
    private int sortColumnIndex;
    
    public SortableTableHeaderListener(final SortableTableModel model, final SortButtonRenderer renderer) {
        this.model = model;
        this.renderer = renderer;
    }
    
    public void mouseClicked(final MouseEvent e) {
    }
    
    public void mouseDragged(final MouseEvent e) {
        final JTableHeader header = (JTableHeader)e.getComponent();
        if (header.getDraggedDistance() > 0 || header.getResizingColumn() != null) {
            this.renderer.setPressedColumn(-1);
            this.sortColumnIndex = -1;
        }
    }
    
    public void mouseEntered(final MouseEvent e) {
    }
    
    public void mouseExited(final MouseEvent e) {
    }
    
    public void mouseMoved(final MouseEvent e) {
    }
    
    public void mousePressed(final MouseEvent e) {
        final JTableHeader header = (JTableHeader)e.getComponent();
        if (header.getResizingColumn() == null && header.getDraggedDistance() < 1) {
            final int columnIndex = header.columnAtPoint(e.getPoint());
            final int modelColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
            if (this.model.isSortable(modelColumnIndex)) {
                this.sortColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
                this.renderer.setPressedColumn(this.sortColumnIndex);
                header.repaint();
                if (header.getTable().isEditing()) {
                    header.getTable().getCellEditor().stopCellEditing();
                }
            }
            else {
                this.sortColumnIndex = -1;
            }
        }
    }
    
    public void mouseReleased(final MouseEvent e) {
        final JTableHeader header = (JTableHeader)e.getComponent();
        if (header.getResizingColumn() == null && this.sortColumnIndex != -1) {
            final SortableTableModel model = (SortableTableModel)header.getTable().getModel();
            final boolean ascending = model.isAscending() ^ true;
            model.setAscending(ascending);
            model.sortByColumn(this.sortColumnIndex, ascending);
            this.renderer.setPressedColumn(-1);
            header.repaint();
        }
    }
    
    public void setTableModel(final SortableTableModel model) {
        this.model = model;
    }
}
