package javax.swing;

import javax.swing.plaf.UIResource;
import javax.swing.border.EmptyBorder;
import java.awt.Rectangle;
import java.awt.Container;
import java.awt.Color;
import java.awt.Component;
import sun.swing.DefaultLookup;
import javax.swing.border.Border;
import java.io.Serializable;

public class DefaultListCellRenderer extends JLabel implements ListCellRenderer<Object>, Serializable
{
    private static final Border SAFE_NO_FOCUS_BORDER;
    private static final Border DEFAULT_NO_FOCUS_BORDER;
    protected static Border noFocusBorder;
    
    public DefaultListCellRenderer() {
        this.setOpaque(true);
        this.setBorder(this.getNoFocusBorder());
        this.setName("List.cellRenderer");
    }
    
    private Border getNoFocusBorder() {
        final Border border = DefaultLookup.getBorder(this, this.ui, "List.cellNoFocusBorder");
        if (System.getSecurityManager() != null) {
            if (border != null) {
                return border;
            }
            return DefaultListCellRenderer.SAFE_NO_FOCUS_BORDER;
        }
        else {
            if (border != null && (DefaultListCellRenderer.noFocusBorder == null || DefaultListCellRenderer.noFocusBorder == DefaultListCellRenderer.DEFAULT_NO_FOCUS_BORDER)) {
                return border;
            }
            return DefaultListCellRenderer.noFocusBorder;
        }
    }
    
    @Override
    public Component getListCellRendererComponent(final JList<?> list, final Object o, final int n, boolean b, final boolean b2) {
        this.setComponentOrientation(list.getComponentOrientation());
        Color color = null;
        Color color2 = null;
        final JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsert() && dropLocation.getIndex() == n) {
            color = DefaultLookup.getColor(this, this.ui, "List.dropCellBackground");
            color2 = DefaultLookup.getColor(this, this.ui, "List.dropCellForeground");
            b = true;
        }
        if (b) {
            this.setBackground((color == null) ? list.getSelectionBackground() : color);
            this.setForeground((color2 == null) ? list.getSelectionForeground() : color2);
        }
        else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        if (o instanceof Icon) {
            this.setIcon((Icon)o);
            this.setText("");
        }
        else {
            this.setIcon(null);
            this.setText((o == null) ? "" : o.toString());
        }
        this.setEnabled(list.isEnabled());
        this.setFont(list.getFont());
        Border border = null;
        if (b2) {
            if (b) {
                border = DefaultLookup.getBorder(this, this.ui, "List.focusSelectedCellHighlightBorder");
            }
            if (border == null) {
                border = DefaultLookup.getBorder(this, this.ui, "List.focusCellHighlightBorder");
            }
        }
        else {
            border = this.getNoFocusBorder();
        }
        this.setBorder(border);
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
    public void validate() {
    }
    
    @Override
    public void invalidate() {
    }
    
    @Override
    public void repaint() {
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
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        if (s == "text" || ((s == "font" || s == "foreground") && o != o2 && this.getClientProperty("html") != null)) {
            super.firePropertyChange(s, o, o2);
        }
    }
    
    @Override
    public void firePropertyChange(final String s, final byte b, final byte b2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final char c, final char c2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final short n, final short n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final int n, final int n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final long n, final long n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final float n, final float n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final double n, final double n2) {
    }
    
    @Override
    public void firePropertyChange(final String s, final boolean b, final boolean b2) {
    }
    
    static {
        SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
        DEFAULT_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
        DefaultListCellRenderer.noFocusBorder = DefaultListCellRenderer.DEFAULT_NO_FOCUS_BORDER;
    }
    
    public static class UIResource extends DefaultListCellRenderer implements javax.swing.plaf.UIResource
    {
    }
}
