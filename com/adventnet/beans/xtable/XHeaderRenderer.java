package com.adventnet.beans.xtable;

import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.Icon;
import javax.swing.table.TableCellRenderer;

public class XHeaderRenderer extends HeaderPanel implements TableCellRenderer
{
    private boolean pushed;
    private Icon modelSortUp;
    private Icon modelSortDown;
    private Icon viewSortUp;
    private Icon viewSortDown;
    private int lastVisibleCol;
    private int oldMinValueOfLastVisibleCol;
    private XTableColumn lvc;
    private Border br;
    private RightCornerHeaderComponent rchc;
    
    public XHeaderRenderer() {
        this.lastVisibleCol = -1;
        this.init();
    }
    
    private void init() {
        this.setHeaderBorder(new BevelBorder(0));
        this.rchc = new RightCornerHeaderComponent();
        this.setAscendingIconForModelSort(new ImageIcon(this.getClass().getResource("server_up.png")));
        this.setDescendingIconForModelSort(new ImageIcon(this.getClass().getResource("server_down.png")));
        this.setAscendingIconForViewSort(new ImageIcon(this.getClass().getResource("client_up.png")));
        this.setDescendingIconForViewSort(new ImageIcon(this.getClass().getResource("client_down.png")));
    }
    
    void setHeaderBorder(final Border br) {
        this.br = br;
    }
    
    RightCornerHeaderComponent getRightCornerHeaderComponent() {
        return this.rchc;
    }
    
    void setPushed(final boolean pushed) {
        this.pushed = pushed;
    }
    
    void setAscendingIconForModelSort(final Icon modelSortUp) {
        if (modelSortUp != null) {
            final Icon modelSortUp2 = this.modelSortUp;
            this.modelSortUp = modelSortUp;
            if (modelSortUp2 != this.modelSortUp) {
                this.repaint();
            }
        }
    }
    
    Icon getAscendingIconForModelSort() {
        return this.modelSortUp;
    }
    
    void setDescendingIconForModelSort(final Icon modelSortDown) {
        if (modelSortDown != null) {
            final Icon modelSortDown2 = this.modelSortDown;
            this.modelSortDown = modelSortDown;
            if (modelSortDown2 != this.modelSortDown) {
                this.repaint();
            }
        }
    }
    
    Icon getDescendingIconForModelSort() {
        return this.modelSortDown;
    }
    
    void setAscendingIconForViewSort(final Icon viewSortUp) {
        if (viewSortUp != null) {
            final Icon viewSortUp2 = this.viewSortUp;
            this.viewSortUp = viewSortUp;
            if (viewSortUp2 != this.viewSortUp) {
                this.repaint();
            }
        }
    }
    
    Icon getAscendingIconForViewSort() {
        return this.viewSortUp;
    }
    
    void setDescendingIconForViewSort(final Icon viewSortDown) {
        if (viewSortDown != null) {
            final Icon viewSortDown2 = this.viewSortDown;
            this.viewSortDown = viewSortDown;
            if (viewSortDown2 != this.viewSortDown) {
                this.repaint();
            }
        }
    }
    
    Icon getDescendingIconForViewSort() {
        return this.viewSortDown;
    }
    
    private void refreshColumn(final XTableColumn xTableColumn) {
        if (xTableColumn != null) {
            xTableColumn.setResizable(true);
            xTableColumn.setMinWidth(this.oldMinValueOfLastVisibleCol);
        }
    }
    
    private int findLastVisibleColumn(final XTable xTable) {
        int n;
        for (n = xTable.getColumnCount() - 1; n >= 0 && xTable.getColumn(n).isHidden(); --n) {}
        return n;
    }
    
    private void setParameters(final XTableColumn xTableColumn) {
        xTableColumn.setResizable(false);
        this.oldMinValueOfLastVisibleCol = xTableColumn.getMinWidth();
        xTableColumn.setMinWidth(75);
    }
    
    public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
        if (n == -1 && n2 == 0) {
            this.refreshColumn(this.lvc);
            this.lastVisibleCol = this.findLastVisibleColumn((XTable)table);
            if (this.lastVisibleCol > 0) {
                this.setParameters(this.lvc = ((XTable)table).getColumn(this.lastVisibleCol));
            }
        }
        final XTableHeader xTableHeader = ((XTable)table).getXTableHeader();
        ((XTable)table).getXColumnModel();
        final XTableColumn column = ((XTable)table).getColumn(n2);
        this.header.setHorizontalAlignment(0);
        this.header.setEnabled(true);
        this.header.setBackground(xTableHeader.getBackground());
        this.header.setForeground(xTableHeader.getForeground());
        this.header.setFont(xTableHeader.getFont());
        this.header.setText(o.toString());
        this.setBorder(this.br);
        final Font font = this.header.getFont();
        final int size = this.viewSort.getFont().getSize();
        if (column.isStandStill()) {
            this.header.setEnabled(false);
        }
        switch (column.getViewClickCount()) {
            case 0: {
                this.viewSort.setIcon(null);
                this.viewSort.setText("");
                break;
            }
            case 1: {
                this.viewSort.setIcon(this.viewSortUp);
                if (column.getViewSortOrder() > 0) {
                    this.viewSort.setText("<html><b><font size=" + (size - 10) + "><sup>" + column.getViewSortOrder() + "</sup></font></b></html>");
                }
                this.header.setFont(new Font(font.getName(), 1, font.getSize()));
                break;
            }
            case 2: {
                this.viewSort.setIcon(this.viewSortDown);
                if (column.getViewSortOrder() > 0) {
                    this.viewSort.setText("<html><b><font size=" + (size - 10) + "><sup>" + column.getViewSortOrder() + "</sup></font></b></html>");
                }
                this.header.setFont(new Font(font.getName(), 1, font.getSize()));
                break;
            }
        }
        final int size2 = this.modelSort.getFont().getSize();
        switch (column.getModelClickCount()) {
            case 0: {
                this.modelSort.setIcon(null);
                this.modelSort.setText("");
                break;
            }
            case 1: {
                this.modelSort.setIcon(this.modelSortUp);
                if (column.getModelSortOrder() > 0) {
                    this.modelSort.setText("<html><b><font size=" + (size2 - 10) + "><sup>" + column.getModelSortOrder() + "</sup></font></b></html>");
                }
                this.header.setFont(new Font(font.getName(), 1, font.getSize()));
                break;
            }
            case 2: {
                this.modelSort.setIcon(this.modelSortDown);
                if (column.getModelSortOrder() > 0) {
                    this.modelSort.setText("<html><font size=" + (size2 - 10) + "><sup>" + column.getModelSortOrder() + "</sup></font></html>");
                }
                this.header.setFont(new Font(font.getName(), 1, font.getSize()));
                break;
            }
        }
        if (n2 == this.lastVisibleCol && this.rchc.isVisible()) {
            final JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(this);
            BevelBorder border;
            if (this.pushed) {
                border = new BevelBorder(1);
            }
            else {
                border = new BevelBorder(0);
            }
            this.rchc.setBorder(border);
            panel.add("East", this.rchc);
            return panel;
        }
        return this;
    }
}
