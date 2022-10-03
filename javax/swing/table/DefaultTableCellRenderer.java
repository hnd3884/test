package javax.swing.table;

import javax.swing.border.EmptyBorder;
import java.awt.Rectangle;
import java.awt.Container;
import javax.swing.plaf.UIResource;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JComponent;
import sun.swing.DefaultLookup;
import java.awt.Color;
import javax.swing.border.Border;
import java.io.Serializable;
import javax.swing.JLabel;

public class DefaultTableCellRenderer extends JLabel implements TableCellRenderer, Serializable
{
    private static final Border SAFE_NO_FOCUS_BORDER;
    private static final Border DEFAULT_NO_FOCUS_BORDER;
    protected static Border noFocusBorder;
    private Color unselectedForeground;
    private Color unselectedBackground;
    
    public DefaultTableCellRenderer() {
        this.setOpaque(true);
        this.setBorder(this.getNoFocusBorder());
        this.setName("Table.cellRenderer");
    }
    
    private Border getNoFocusBorder() {
        final Border border = DefaultLookup.getBorder(this, this.ui, "Table.cellNoFocusBorder");
        if (System.getSecurityManager() != null) {
            if (border != null) {
                return border;
            }
            return DefaultTableCellRenderer.SAFE_NO_FOCUS_BORDER;
        }
        else {
            if (border != null && (DefaultTableCellRenderer.noFocusBorder == null || DefaultTableCellRenderer.noFocusBorder == DefaultTableCellRenderer.DEFAULT_NO_FOCUS_BORDER)) {
                return border;
            }
            return DefaultTableCellRenderer.noFocusBorder;
        }
    }
    
    @Override
    public void setForeground(final Color color) {
        super.setForeground(color);
        this.unselectedForeground = color;
    }
    
    @Override
    public void setBackground(final Color color) {
        super.setBackground(color);
        this.unselectedBackground = color;
    }
    
    @Override
    public void updateUI() {
        super.updateUI();
        this.setForeground(null);
        this.setBackground(null);
    }
    
    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, boolean b, final boolean b2, final int n, final int n2) {
        if (table == null) {
            return this;
        }
        Color color = null;
        Color color2 = null;
        final JTable.DropLocation dropLocation = table.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsertRow() && !dropLocation.isInsertColumn() && dropLocation.getRow() == n && dropLocation.getColumn() == n2) {
            color = DefaultLookup.getColor(this, this.ui, "Table.dropCellForeground");
            color2 = DefaultLookup.getColor(this, this.ui, "Table.dropCellBackground");
            b = true;
        }
        if (b) {
            super.setForeground((color == null) ? table.getSelectionForeground() : color);
            super.setBackground((color2 == null) ? table.getSelectionBackground() : color2);
        }
        else {
            Color background = (this.unselectedBackground != null) ? this.unselectedBackground : table.getBackground();
            if (background == null || background instanceof javax.swing.plaf.UIResource) {
                final Color color3 = DefaultLookup.getColor(this, this.ui, "Table.alternateRowColor");
                if (color3 != null && n % 2 != 0) {
                    background = color3;
                }
            }
            super.setForeground((this.unselectedForeground != null) ? this.unselectedForeground : table.getForeground());
            super.setBackground(background);
        }
        this.setFont(table.getFont());
        if (b2) {
            Border border = null;
            if (b) {
                border = DefaultLookup.getBorder(this, this.ui, "Table.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = DefaultLookup.getBorder(this, this.ui, "Table.focusCellHighlightBorder");
            }
            this.setBorder(border);
            if (!b && table.isCellEditable(n, n2)) {
                final Color color4 = DefaultLookup.getColor(this, this.ui, "Table.focusCellForeground");
                if (color4 != null) {
                    super.setForeground(color4);
                }
                final Color color5 = DefaultLookup.getColor(this, this.ui, "Table.focusCellBackground");
                if (color5 != null) {
                    super.setBackground(color5);
                }
            }
        }
        else {
            this.setBorder(this.getNoFocusBorder());
        }
        this.setValue(value);
        return this;
    }
    
    @Override
    public boolean isOpaque() {
        final Color background = this.getBackground();
        Container container = this.getParent();
        if (container != null) {
            container = container.getParent();
        }
        return (background == null || container == null || !background.equals(container.getBackground()) || !container.isOpaque()) && super.isOpaque();
    }
    
    @Override
    public void invalidate() {
    }
    
    @Override
    public void validate() {
    }
    
    @Override
    public void revalidate() {
    }
    
    @Override
    public void repaint(final long n, final int n2, final int n3, final int n4, final int n5) {
    }
    
    @Override
    public void repaint(final Rectangle rectangle) {
    }
    
    @Override
    public void repaint() {
    }
    
    @Override
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        if (s == "text" || s == "labelFor" || s == "displayedMnemonic" || ((s == "font" || s == "foreground") && o != o2 && this.getClientProperty("html") != null)) {
            super.firePropertyChange(s, o, o2);
        }
    }
    
    @Override
    public void firePropertyChange(final String s, final boolean b, final boolean b2) {
    }
    
    protected void setValue(final Object o) {
        this.setText((o == null) ? "" : o.toString());
    }
    
    static {
        SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
        DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
        DefaultTableCellRenderer.noFocusBorder = DefaultTableCellRenderer.DEFAULT_NO_FOCUS_BORDER;
    }
    
    public static class UIResource extends DefaultTableCellRenderer implements javax.swing.plaf.UIResource
    {
    }
}
