package sun.swing;

import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.Icon;
import java.awt.Insets;
import javax.swing.plaf.UIResource;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.LayoutStyle;

public class DefaultLayoutStyle extends LayoutStyle
{
    private static final DefaultLayoutStyle INSTANCE;
    
    public static LayoutStyle getInstance() {
        return DefaultLayoutStyle.INSTANCE;
    }
    
    @Override
    public int getPreferredGap(final JComponent component, final JComponent component2, final ComponentPlacement componentPlacement, final int n, final Container container) {
        if (component == null || component2 == null || componentPlacement == null) {
            throw new NullPointerException();
        }
        this.checkPosition(n);
        if (componentPlacement == ComponentPlacement.INDENT && (n == 3 || n == 7)) {
            final int indent = this.getIndent(component, n);
            if (indent > 0) {
                return indent;
            }
        }
        return (componentPlacement == ComponentPlacement.UNRELATED) ? 12 : 6;
    }
    
    @Override
    public int getContainerGap(final JComponent component, final int n, final Container container) {
        if (component == null) {
            throw new NullPointerException();
        }
        this.checkPosition(n);
        return 6;
    }
    
    protected boolean isLabelAndNonlabel(final JComponent component, final JComponent component2, final int n) {
        if (n == 3 || n == 7) {
            final boolean b = component instanceof JLabel;
            final boolean b2 = component2 instanceof JLabel;
            return (b || b2) && b != b2;
        }
        return false;
    }
    
    protected int getButtonGap(final JComponent component, final JComponent component2, final int n, int n2) {
        n2 -= this.getButtonGap(component, n);
        if (n2 > 0) {
            n2 -= this.getButtonGap(component2, this.flipDirection(n));
        }
        if (n2 < 0) {
            return 0;
        }
        return n2;
    }
    
    protected int getButtonGap(final JComponent component, final int n, int n2) {
        n2 -= this.getButtonGap(component, n);
        return Math.max(n2, 0);
    }
    
    public int getButtonGap(final JComponent component, final int n) {
        final String uiClassID = component.getUIClassID();
        if ((uiClassID == "CheckBoxUI" || uiClassID == "RadioButtonUI") && !((AbstractButton)component).isBorderPainted() && component.getBorder() instanceof UIResource) {
            return this.getInset(component, n);
        }
        return 0;
    }
    
    private void checkPosition(final int n) {
        if (n != 1 && n != 5 && n != 7 && n != 3) {
            throw new IllegalArgumentException();
        }
    }
    
    protected int flipDirection(final int n) {
        switch (n) {
            case 1: {
                return 5;
            }
            case 5: {
                return 1;
            }
            case 3: {
                return 7;
            }
            case 7: {
                return 3;
            }
            default: {
                assert false;
                return 0;
            }
        }
    }
    
    protected int getIndent(final JComponent component, final int n) {
        final String uiClassID = component.getUIClassID();
        if (uiClassID == "CheckBoxUI" || uiClassID == "RadioButtonUI") {
            final AbstractButton abstractButton = (AbstractButton)component;
            final Insets insets = component.getInsets();
            final Icon icon = this.getIcon(abstractButton);
            final int iconTextGap = abstractButton.getIconTextGap();
            if (this.isLeftAligned(abstractButton, n)) {
                return insets.left + icon.getIconWidth() + iconTextGap;
            }
            if (this.isRightAligned(abstractButton, n)) {
                return insets.right + icon.getIconWidth() + iconTextGap;
            }
        }
        return 0;
    }
    
    private Icon getIcon(final AbstractButton abstractButton) {
        final Icon icon = abstractButton.getIcon();
        if (icon != null) {
            return icon;
        }
        Object o = null;
        if (abstractButton instanceof JCheckBox) {
            o = "CheckBox.icon";
        }
        else if (abstractButton instanceof JRadioButton) {
            o = "RadioButton.icon";
        }
        if (o != null) {
            final Object value = UIManager.get(o);
            if (value instanceof Icon) {
                return (Icon)value;
            }
        }
        return null;
    }
    
    private boolean isLeftAligned(final AbstractButton abstractButton, final int n) {
        if (n == 7) {
            final boolean leftToRight = abstractButton.getComponentOrientation().isLeftToRight();
            final int horizontalAlignment = abstractButton.getHorizontalAlignment();
            return (leftToRight && (horizontalAlignment == 2 || horizontalAlignment == 10)) || (!leftToRight && horizontalAlignment == 11);
        }
        return false;
    }
    
    private boolean isRightAligned(final AbstractButton abstractButton, final int n) {
        if (n == 3) {
            final boolean leftToRight = abstractButton.getComponentOrientation().isLeftToRight();
            final int horizontalAlignment = abstractButton.getHorizontalAlignment();
            return (leftToRight && (horizontalAlignment == 4 || horizontalAlignment == 11)) || (!leftToRight && horizontalAlignment == 10);
        }
        return false;
    }
    
    private int getInset(final JComponent component, final int n) {
        return this.getInset(component.getInsets(), n);
    }
    
    private int getInset(final Insets insets, final int n) {
        if (insets == null) {
            return 0;
        }
        switch (n) {
            case 1: {
                return insets.top;
            }
            case 5: {
                return insets.bottom;
            }
            case 3: {
                return insets.right;
            }
            case 7: {
                return insets.left;
            }
            default: {
                assert false;
                return 0;
            }
        }
    }
    
    static {
        INSTANCE = new DefaultLayoutStyle();
    }
}
