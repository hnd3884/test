package com.adventnet.beans.xtable;

import java.awt.Dimension;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.JTableHeader;

public class XTableHeader extends JTableHeader
{
    public XTableHeader(final TableColumnModel tableColumnModel) {
        super(tableColumnModel);
    }
    
    public void setStandStill(final int n, final boolean standStill) {
        if (this.getReorderingAllowed()) {
            ((XTableColumn)this.getColumnModel().getColumn(n)).setStandStill(standStill);
        }
        this.repaint();
    }
    
    public boolean isStandStill(final int n) {
        return ((XTableColumn)this.getColumnModel().getColumn(n)).isStandStill();
    }
    
    public void setDraggedColumn(TableColumn draggedColumn) {
        if (draggedColumn != null && ((XTableColumn)draggedColumn).isStandStill()) {
            draggedColumn = null;
        }
        super.setDraggedColumn(draggedColumn);
    }
    
    public int getDraggedDistance() {
        int draggedDistance = super.getDraggedDistance();
        final int columnIndex = this.columnModel.getColumnIndex(this.getDraggedColumn().getIdentifier());
        if (draggedDistance > 0) {
            if (columnIndex + 1 < this.columnModel.getColumnCount() && ((XTableColumn)this.columnModel.getColumn(columnIndex + 1)).isStandStill()) {
                this.setDraggedColumn(null);
                draggedDistance = 0;
            }
        }
        else if (columnIndex - 1 >= 0 && ((XTableColumn)this.columnModel.getColumn(columnIndex - 1)).isStandStill()) {
            this.setDraggedColumn(null);
            draggedDistance = 0;
        }
        return draggedDistance;
    }
    
    public void setHeight(final int n) {
        this.setPreferredSize(new Dimension((int)this.getPreferredSize().getWidth(), n));
        this.updateUI();
    }
    
    public int getHeight() {
        return (int)this.getPreferredSize().getHeight();
    }
}
