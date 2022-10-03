package sun.swing.table;

import java.io.Serializable;
import java.awt.Insets;
import java.awt.FontMetrics;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Graphics;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.border.Border;
import javax.swing.SortOrder;
import java.awt.Color;
import javax.swing.table.JTableHeader;
import javax.swing.JComponent;
import sun.swing.DefaultLookup;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;

public class DefaultTableCellHeaderRenderer extends DefaultTableCellRenderer implements UIResource
{
    private boolean horizontalTextPositionSet;
    private Icon sortArrow;
    private EmptyIcon emptyIcon;
    
    public DefaultTableCellHeaderRenderer() {
        this.emptyIcon = new EmptyIcon();
        this.setHorizontalAlignment(0);
    }
    
    @Override
    public void setHorizontalTextPosition(final int horizontalTextPosition) {
        this.horizontalTextPositionSet = true;
        super.setHorizontalTextPosition(horizontalTextPosition);
    }
    
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object o, final boolean b, final boolean b2, final int n, final int n2) {
        Icon icon = null;
        boolean paintingForPrint = false;
        if (table != null) {
            final JTableHeader tableHeader = table.getTableHeader();
            if (tableHeader != null) {
                Color foreground = null;
                Color background = null;
                if (b2) {
                    foreground = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellForeground");
                    background = DefaultLookup.getColor(this, this.ui, "TableHeader.focusCellBackground");
                }
                if (foreground == null) {
                    foreground = tableHeader.getForeground();
                }
                if (background == null) {
                    background = tableHeader.getBackground();
                }
                this.setForeground(foreground);
                this.setBackground(background);
                this.setFont(tableHeader.getFont());
                paintingForPrint = tableHeader.isPaintingForPrint();
            }
            if (!paintingForPrint && table.getRowSorter() != null) {
                if (!this.horizontalTextPositionSet) {
                    this.setHorizontalTextPosition(10);
                }
                final SortOrder columnSortOrder = getColumnSortOrder(table, n2);
                if (columnSortOrder != null) {
                    switch (columnSortOrder) {
                        case ASCENDING: {
                            icon = DefaultLookup.getIcon(this, this.ui, "Table.ascendingSortIcon");
                            break;
                        }
                        case DESCENDING: {
                            icon = DefaultLookup.getIcon(this, this.ui, "Table.descendingSortIcon");
                            break;
                        }
                        case UNSORTED: {
                            icon = DefaultLookup.getIcon(this, this.ui, "Table.naturalSortIcon");
                            break;
                        }
                    }
                }
            }
        }
        this.setText((o == null) ? "" : o.toString());
        this.setIcon(icon);
        this.sortArrow = icon;
        Border border = null;
        if (b2) {
            border = DefaultLookup.getBorder(this, this.ui, "TableHeader.focusCellBorder");
        }
        if (border == null) {
            border = DefaultLookup.getBorder(this, this.ui, "TableHeader.cellBorder");
        }
        this.setBorder(border);
        return this;
    }
    
    public static SortOrder getColumnSortOrder(final JTable table, final int n) {
        SortOrder sortOrder = null;
        if (table == null || table.getRowSorter() == null) {
            return sortOrder;
        }
        final List<? extends RowSorter.SortKey> sortKeys = table.getRowSorter().getSortKeys();
        if (sortKeys.size() > 0 && ((RowSorter.SortKey)sortKeys.get(0)).getColumn() == table.convertColumnIndexToModel(n)) {
            sortOrder = ((RowSorter.SortKey)sortKeys.get(0)).getSortOrder();
        }
        return sortOrder;
    }
    
    public void paintComponent(final Graphics graphics) {
        if (DefaultLookup.getBoolean(this, this.ui, "TableHeader.rightAlignSortArrow", false) && this.sortArrow != null) {
            this.emptyIcon.width = this.sortArrow.getIconWidth();
            this.emptyIcon.height = this.sortArrow.getIconHeight();
            this.setIcon(this.emptyIcon);
            super.paintComponent(graphics);
            final Point computeIconPosition = this.computeIconPosition(graphics);
            this.sortArrow.paintIcon(this, graphics, computeIconPosition.x, computeIconPosition.y);
        }
        else {
            super.paintComponent(graphics);
        }
    }
    
    private Point computeIconPosition(final Graphics graphics) {
        final FontMetrics fontMetrics = graphics.getFontMetrics();
        final Rectangle rectangle = new Rectangle();
        final Rectangle rectangle2 = new Rectangle();
        final Rectangle rectangle3 = new Rectangle();
        final Insets insets = this.getInsets();
        rectangle.x = insets.left;
        rectangle.y = insets.top;
        rectangle.width = this.getWidth() - (insets.left + insets.right);
        rectangle.height = this.getHeight() - (insets.top + insets.bottom);
        SwingUtilities.layoutCompoundLabel(this, fontMetrics, this.getText(), this.sortArrow, this.getVerticalAlignment(), this.getHorizontalAlignment(), this.getVerticalTextPosition(), this.getHorizontalTextPosition(), rectangle, rectangle3, rectangle2, this.getIconTextGap());
        return new Point(this.getWidth() - insets.right - this.sortArrow.getIconWidth(), rectangle3.y);
    }
    
    private class EmptyIcon implements Icon, Serializable
    {
        int width;
        int height;
        
        private EmptyIcon() {
            this.width = 0;
            this.height = 0;
        }
        
        @Override
        public void paintIcon(final Component component, final Graphics graphics, final int n, final int n2) {
        }
        
        @Override
        public int getIconWidth() {
            return this.width;
        }
        
        @Override
        public int getIconHeight() {
            return this.height;
        }
    }
}
