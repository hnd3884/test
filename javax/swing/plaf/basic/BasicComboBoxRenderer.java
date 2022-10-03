package javax.swing.plaf.basic;

import javax.swing.plaf.UIResource;
import javax.swing.border.EmptyBorder;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JList;
import java.awt.Dimension;
import javax.swing.border.Border;
import java.io.Serializable;
import javax.swing.ListCellRenderer;
import javax.swing.JLabel;

public class BasicComboBoxRenderer extends JLabel implements ListCellRenderer, Serializable
{
    protected static Border noFocusBorder;
    private static final Border SAFE_NO_FOCUS_BORDER;
    
    public BasicComboBoxRenderer() {
        this.setOpaque(true);
        this.setBorder(getNoFocusBorder());
    }
    
    private static Border getNoFocusBorder() {
        if (System.getSecurityManager() != null) {
            return BasicComboBoxRenderer.SAFE_NO_FOCUS_BORDER;
        }
        return BasicComboBoxRenderer.noFocusBorder;
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension dimension;
        if (this.getText() == null || this.getText().equals("")) {
            this.setText(" ");
            dimension = super.getPreferredSize();
            this.setText("");
        }
        else {
            dimension = super.getPreferredSize();
        }
        return dimension;
    }
    
    @Override
    public Component getListCellRendererComponent(final JList list, final Object o, final int n, final boolean b, final boolean b2) {
        if (b) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
        }
        else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }
        this.setFont(list.getFont());
        if (o instanceof Icon) {
            this.setIcon((Icon)o);
        }
        else {
            this.setText((o == null) ? "" : o.toString());
        }
        return this;
    }
    
    static {
        BasicComboBoxRenderer.noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        SAFE_NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
    }
    
    public static class UIResource extends BasicComboBoxRenderer implements javax.swing.plaf.UIResource
    {
    }
}
